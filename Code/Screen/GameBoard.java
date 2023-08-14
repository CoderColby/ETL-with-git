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
import java.util.ArrayList;
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
  private static final byte FLOOR_LAYER = 0;
  private static final byte ROOMTYPE_LAYER = 1;
  private static final byte ITEM_LAYER = 2;
  private static final byte ENTITY_LAYER = 3;
  private static final byte WALL_LAYER = 4;

  private String[][] fileContents;
  private GridCell[][] board;
  private Human player;
  private Zombie[] zombies;
  private SecuritySystem[] securitySystems;
  private KeyDoor[] keyDoors;
  private int startEnergy;
  private int remainingEnergy;
  private boolean hasLost;
  private boolean hasWon;



  public GameBoard(String[][] fileContents, int startEnergy) {
    this.startEnergy = startEnergy;
    this.fileContents = fileContents;
    board = new GridCell[10][10];
    super.setSize(BOARD_DIMENSION, BOARD_DIMENSION);
    super.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
    super.setBackground(Data.Colors.levelBackground);
    super.setFocusable(false);
  }

  public void reset() {
    ArrayList<Zombie> zombieTracker = new ArrayList<>();
    ArrayList<SecuritySystem> securityTracker = new ArrayList<>();
    ArrayList<KeyDoor> keyDoorTracker = new Arraylist<>();
    
    for (int i = 0; i < 100; i++) {
      GridCell temp = new GridCell(fileContents[i], i, this);
      
      if (temp.hasEntity(Zombie.TAG))
        zombieTracker.add((Zombie) temp.getEntity());
      else if (temp.hasEntity(Human.TAG))
        player = (Human) temp.getEntity();

      if (temp.hasRoomType(SecuritySystem.TAG))
        securityTracker.add((SecuritySystem) temp.getRoomType());

      for (byte wallNum = 0; wallNum < 2; wallNum++) {
        if (temp.hasWall(wallNum, KeyDoor.TAG))
          keyDoorTracker.add((KeyDoor) temp.getWall(wallNum));
      }
      
      board[i/10][i%10] = temp;
    }

    zombies = zombieTracker.toArray();
    securitySystems = securityTracker.toArray();
    keyDoors = keyDoorTracker.toArray();
    remainingEnergy = startEnergy;
    hasDied = false;

    for (int i = 0; i < 100; i++) {
      super.add(board[i/10][i%10].getJLabelImageOfRoomType(), ROOMTYPE_LAYER);
      super.add(board[i/10][i%10].getJLabelImageOfItem(), ITEM_LAYER);
      super.add(board[i/10][i%10].getJLabelImageOfEntity(), ENTITY_LAYER);
      if (i % 10 < 9)
        super.add(board[i/10][i%10].getJLabelImageOfWall(0), WALL_LAYER);
      if (i / 10 < 9)
        super.add(board[i/10][i%10].getJLabelImageOfWall(1), WALL_LAYER);
    }
    
  }


  public void move(byte direction) {
    PriorityQueue<Animation> animations = new PriorityQueue(moveAndAnimate(direction));

    
  }

  public ArrayList<Animation> moveAndAnimate(byte direction) { // direction represents the direction of the arrow key that was pressed

    ArrayList<Animation> animations = new ArrayList<>();

    player.turn(direction);

    if (!player.canMove(direction))
      return animations;

    animations.addAll(player.move(direction));
    // if (player.getGridCell().hasRoomType(Elevator.TAG)) {
    //   hasWon = true;
    //   return animations;
    // }
    // if (player.getGridCell().hasItem(Key.TAG)) {
    //   for (KeyDoor kd : keyDoors) {
    //     if (kd.goesWithKey((Key) player.getGridCell().getItem()))
    //       animations.addAll(kd.unlock());
    //   }
    // }

    if (hasWon) {
      Level.goToNextLevel = true;
      return animations;
    }

    for (int i = 0; i < 2 && !hasLost; i++) {
      Arrays.sort(zombies);
      for (Zombie z : zombies)
        animations.addAll(z.move(getPlayerLocation()));
  
        // if (z.willOverlapPlayer(zombieDirection)) {
        //   animations.addAll(player.becomeInfected());
        //   hasLost = true;
        //   return animations;
        // }
    }
      
  }



  public void playAnimations(PriorityQueue<AnimationEvent> queue) {
    long baseTime = SystemcurrentTimeMillis();
    AnimationEvent animation;
    long animationStartTime;
    long timeUntilAnimationStart;

    while (!queue.isEmpty()) {
      animation = queue.poll();
      animationStartTime = baseTime + animationEvent.getStartTime();

      timeUntilAnimationStart = animationStartTime - System.currentTimeMillis();

      if (timeUntilAnimationStart > 0) {
          try {
              Thread.sleep(timeUntilAnimationStart); // Sleep until animation start time
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
          }
      }

      Thread animationThread = new Thread(animationEvent::run);
      animationThread.start();
    }
  }



  public int[] getPlayerLocation() {
    return player.getGridCell().getCoords();
  }












  

  ////////////////////////////////////////////////////////////////////////////////////////// Setters and Getters
  

  public GridCell getGridCell(int[] rowColumn) {
    return board[rowColumn[0]][rowColumn[1]];
  }

  public GridCell getGridCell(int[] rowColumn, byte direction) throws IndexOutOfBoundsException {
    return board[rowColumn[0] + getRowShift(direction)][rowColumn[1] + getColumnShift(direction)];
  }

  public int getRowShift(byte direction) {
    return direction - 2 + 2 * ((3 - direction) / 3); // 0:0; 1:-1; 2:0; 3:1
  }

  public int getColumnShift(byte direction) {
    return 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
  }

  public int getRemainingEnergy() {
    return remainingEnergy;
  }

  public void decrementEnergy() {
    remainingEnergy--;
  }

  public void setScreenCoordinates(Point r) {
    display.setLocation(r);
  }

  public void isPlayerInfected

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




public class GridCell {

  public static final byte RIGHT_WALL = 0;
  public static final byte BOTTOM_WALL = 1;
  public static final byte LEFT_WALL = 2;
  public static final byte TOP_WALL = 4;

  private RoomType roomType;
  private Item item;
  private Entity entity;
  private Wall[] walls;
  private int[] rowColumn;
  private GameBoard board;

  public GridCell(String[] fileContents, int coordinates, GameBoard board) {
    rowColumn = int[2] {coordinates / 10, coordinates % 10};
    this.board = board;
    walls = new Wall[2];
    walls[0] = Wall.getWallByTag(fileContents[0]);
    walls[1] = Wall.getWallByTag(fileContents[1]);
    roomType = RoomType.getRoomTypeByTag(fileContents[2]);
    item = Item.getItemByTag(fileContents[3]);
    entity = Entity.getEntityByTag(fileContents[4]);
  }

  public char getRoomType() {
    return interior;
  }

  public char getItem() {
    return item;
  }

  public Entity getEntity() {
    return occupant;
  }

  public Wall getWall(byte direction) {
    if (direction < 2)
      return walls[direction];
    else if ((direction == 2 && rowColumn[1] == 0) || (direction == 3 && row_column[0] == 0))
      return board.getCell(row_column[0], row_column[1], direction).getWall(direction % 2);
    return new RegularWall();
  }

  public int[] getCoordinates() {
    return rowColumn;
  }
}