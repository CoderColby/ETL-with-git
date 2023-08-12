import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.lang.IndexOutOfBoundsException;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Scanner;
import java.lang.Thread;

public class GameBoard extends JLayeredPane {
  
  public static final byte MOVE_RIGHT = 0;
  public static final byte MOVE_DOWN = 1;
  public static final byte MOVE_LEFT = 2;
  public static final byte MOVE_UP = 3;
  private static final int ROOM_HEIGHT = 49;
  private static final int WALL_THICKNESS = 10;
  private static final int BOARD_DIMENSION = 10 * ROOM_HEIGHT + 11 * WALL_THICKNESS;
  private static final byte FLOOR_LEVEL = 0;
  private static final byte ROOMOBJECT_LEVEL = 1;
  private static final byte ITEM_LEVEL = 2;
  private static final byte ENTITY_LEVEL = 3;
  private static final byte WALL_LEVEL = 4;

  private String[][] fileContents;
  private GridCell[][] board;
  private GridCell playerLocation;
  private GridCell[] zombieLocations;
  private int startEnergy;
  private int remainingEnergy;
  private boolean hasDied;


  ////////////////////////////////////////////////////////////////////////////////////////// Constructors

  public GameBoard(String[][] fileContents) {
    
  }

  public static void initializeBoard() {
    display = new JLayeredPane();
    display.setSize(BOARD_DIMENSION, BOARD_DIMENSION);
    display.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
    display.setBackground(Data.Colors.levelBackground);
  }
  

  public static JLayeredPane makeLevel(File levelFile) throws FileNotFoundException {
    /* levelFile Contents
      title
      details
      initial energy
      least moves
      wallID wallID ; RoomObjectID RoomObjectID RoomObjectID
      wallID wallID ; 
      filled
      ---------------------------------------------------------
      Default Level
      This is a default level
      100
      2
      filled
      filled
      wall hallway ; zombie key1 generator
      wall hallway ; - key1 -
  
    */

    board = new GridCell[10][10];
    
    Scanner fileIn = new Scanner(levelFile);
    String title = fileIn.nextLine().trim();
    String details = fileIn.nextLine().trim();
    startEnergy = fileIn.nextInt(); fileIn.nextLine();
    leastMoves = fileIn.nextInt(); fileIn.nextLine();
    
    // String[][] mockBoard = new String[10][10];
    
    for (int i = 99; i > -1; i--) {
      
      int row = i / 10;
      int column = i % 10;
      String line = fileIn.nextLine().trim();

      if (line.isEmpty()) {
        board[row][column] = new GridCell(new int[] {row, column});
        continue;
      }

      String[] temp = line.split(";")[0].split(" ");
      Wall[] walls = new Wall[2];

      if (column == 9 || board[row][column + 1].isFilled())
        walls[0] = Wall.makeNewWall("regularWall");
      else
        walls[0] = Wall.makeNewWall(temp[0]);
      
      if (row == 9 || board[row + 1][column].isFilled())
        walls[1] = Wall.makeNewWall("regularWall");
      else
        walls[1] = Wall.makeNewWall(temp[1]);

      temp = line.split(";")[1].split(" ");
      for (int j = 0; j < 3; j++) {
        if (temp[j].equals("-"))
          temp[j] = null;
      }

      board[row][column] = new GridCell(new int[] {row, column}, walls, Entity.makeNewEntity(temp[0]), Item.makeNewItem(temp[1]), RoomObject.makeNewRoomObject(temp[2]));

      board[row][column].draw(display);
      
    }

    
  }

  ////////////////////////////////////////////////////////////////////////////////////////// Setters and Getters
  

  public static GridCell getCell(int row, int column) {
    return board[row][column];
  }

  public static GridCell getCell(int row, int column, byte direction) {
    return board[row + getRowShift(direction)][column + getColumnShift(direction)];
  }

  public static int getRowShift(byte direction) {
    return direction - 2 + 2 * ((3 - direction) / 3); // 0:0; 1:-1; 2:0; 3:1
  }

  public static int getColumnShift(byte direction) {
    return 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
  }

  public static void setScreenCoordinates(Point r) {
    display.setLocation(r);
  }

  ////////////////////////////////////////////////////////////////////////////////////////// Core Functionality
  

  public static void movePlayer(byte direction) {
    if (direction < 0 || direction > 4)
      throw new IndexOutOfBoundsException();

    Entity human = playerLocation.getEntity();
    
    human.turn(direction);

    if (!playerLocation.isLegalMove(direction, this))
      return;
    
    Wall passWall = playerLocation.getWall(direction, this);
    remainingEnergy = passWall.takeEnergy(remainingEnergy);

    // Right 0; (row) (column) getWall(0)
    // Down 1; (row) (column) getWall(1)
    // Left 2; (row) (column-1) getWall(0)
    // Up 3; (row-1) (column) getWall(1)
    // Wall passWall = board[playerLocation.getRow() - direction / 3][playerLocation.getColumn() - direction / 2 + direction / 3].getWall((byte) (direction % 2), this);
    AnimationList queue = new AnimationList(this);

    ///////////////////////////////////// ANIMATION IS NOT IN RIGHT ORDER
    
    queue.addAnimation(playerLocation, human, direction, passWall, 0);
    playerLocation = getCell(playerLocation.getRow(), playerLocation.getColumn(), direction);

    for (int b = 0; b < 2; b++) {
      Arrays.sort(zombieLocations);
  
      // Add zombie movement animation for all, twice
      for (int i = 0; i < zombieLocations.length; i++) {
        GridCell zombieCell = zombieLocations[i];
  
        // Find direction of travel
        int rowDifference = zombieCell.getRow() - playerLocation.getRow();
        int columnDifference = zombieCell.getColumn() - playerLocation.getColumn();
        byte zombieDirection = (byte) ((Math.abs(rowDifference) < Math.abs(columnDifference))? Math.signum(columnDifference) + 1 : Math.signum(rowDifference) + 2);
        // Direction might be messed up
        
        if (!zombieCell.isLegalMove(zombieDirection, this)) {
          if (zombieDirection % 2 == 0 && columnDifference != 0)
            zombieDirection = (byte) (Math.signum(rowDifference) + 2);
          else if (zombieDirection % 2 == 1 && rowDifference != 0)
            zombieDirection = (byte) (Math.signum(columnDifference) + 1);

          if (!zombieCell.isLegalMove(zombieDirection, this))
            continue;
        }

        Entity zombie = zombieCell.getEntity();
          
        queue.addAnimation(zombieCell, zombie, zombieDirection, zombieCell.getWall(zombieDirection));

        if (zombieCell.hasOpenSequence(zombie))
          zombieCell.getWall(zombieDirection, this).getOpenSequence(zombie);
        
        GridCell newZombieCell = getCell(zombieCell.getRow(), zombieCell.getColumn(), zombieDirection);
        newZombieCell.setEntity(zombieCell.removeEntity());
        zombieLocations[i] = newZombieCell;
        
      }
    }
    

    // Animate

    playerLocation = newLocation;
    
  }


  public void deactivateSecurity() {
    
  }

  public void keyFound(byte keyID) {
    
  }

}





class GridCell {

  private int[] row_column;
  private Wall[] walls;
  private Entity entity;
  private Item item;
  private RoomObject roomObject;
  private boolean isFilled;


  public GridCell(int[] row_column) {
    this.isFilled = true;
    this.row_column = row_column;
    this.walls = new Wall[] {new RegularWall(), new RegularWall()};
  }

  public GridCell(int[] row_column, Wall[] walls, Entity entity, Item item, RoomObject roomObject) {
    this.isFilled = false;
    this.row_column = row_column;
    this.walls = walls;
    this.entity = entity;
    this.item = item;
    this.roomObject = roomObject;
  }

  public void setEntity(Entity newEntity) {
    entity = newEntity;
    if (entity.getClass() == Human.class && item.getClass() == key.class) {
      
    }
  }

  public void setWall(byte i, Wall newWall) {
    walls[i] = newWall;
  }

  public Entity removeEntity() {
    Entity oldEntity = entity;
    entity = null;
    return oldEntity;
  }

  public void removeItem() {
    item = null;
  }

  public int getRow() {
    return row_column[0];
  }

  public int getColumn() {
    return row_column[1];
  }

  public Wall getWall(byte direction) {
    if (direction < 2)
      return walls[direction];
    else if ((direction == 2 && row_column[1] == 0) || (direction == 3 && row_column[0] == 0))
      return GameBoard.getCell(row_column[0], row_column[1], direction).getWall(direction % 2);
    return new RegularWall();
  }

  public Entity getEntity() {
    return entity;
  }

  public boolean hasEntity() {
    return entity != null;
  }

  public Item getItem() {
    return item;
  }
  
  public RoomObject getRoomObject() {
    return roomObject;
  }

  public boolean isFilled() {
    return isFilled;
  }

  public boolean isLegalMove(byte direction) {
    Entity neighbor = GameBoard.getCell(this.row_column[0], this.row_column[1], direction).getEntity();
    if (isFilled || neighbor.getClass() == Zombie.class)
      return false;
    return this.getWall(direction).canPass(entity, board.getRemainingEnergy());
  }

  public void draw(JLayeredPane display) {
    
  }
}