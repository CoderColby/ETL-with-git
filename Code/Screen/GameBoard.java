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
  public static final int ROOM_HEIGHT = 49;
  public static final int WALL_THICKNESS = 10;
  public static final int BOARD_DIMENSION = 10 * ROOM_HEIGHT + 11 * WALL_THICKNESS;
  public static final byte ROOMTYPE_LAYER = 0;
  public static final byte ITEM_LAYER = 1;
  public static final byte ENTITY_LAYER = 2;
  public static final byte WALL_LAYER = 3;

  private Level level;

  private String[][] fileContents;
  private GridCell[][] board;
  private Human player;
  private Zombie[] zombies;
  private SmartZombie[] smartZombies;
  private Target[] targets;
  private KeyDoor[] keyDoors;
  private Star[] stars;
  private int startEnergy;
  private int remainingEnergy;
  private boolean hasLost;
  private boolean hasWon;
  

  public GameBoard(String[][] fileContents, int startEnergy, Level level) { // For reading level file
    this.level = level;
    this.startEnergy = startEnergy;
    this.fileContents = fileContents;
    board = new GridCell[10][10];
    super.setSize(BOARD_DIMENSION, BOARD_DIMENSION);
    super.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
    super.setBackground(Data.Colors.levelBackground);
    super.setFocusable(false);
    reset();
  }

  
  public void reset() {
    ArrayList<Zombie> zombieTracker = new ArrayList<>();
    ArrayList<SmartZombie> smartZombieTracker = new ArrayList<>();
    ArrayList<Target> targetTracker = new ArrayList<>();
    ArrayList<KeyDoor> keyDoorTracker = new ArrayList<>();
    ArrayList<Star> starTracker = new ArrayList<>();
    
    for (int i = 0; i < 100; i++) {
      GridCell temp = new GridCell(fileContents[i], i, this);
      
      if (temp.hasEntity(Zombie.TAG))
        zombieTracker.add((Zombie) temp.getEntity());
      else if (temp.hasEntity(SmartZombie.TAG))
        smartZombieTracker.add((SmartZombie) temp.getEntity());
      else if (temp.hasEntity(Human.TAG))
        player = (Human) temp.getEntity();

      if (temp.hasRoomType(Target.TAG))
        targetTracker.add((Target) temp.getRoomType());
      else if (temp.hasRoomType(Star.TAG))
        starTracker.add((Star) temp.getRoomType());

      for (byte wallNum = 0; wallNum < 2; wallNum++) {
        if (temp.hasWall(wallNum, KeyDoor.TAG))
          keyDoorTracker.add((KeyDoor) temp.getWall(wallNum));
      }
      
      board[i/10][i%10] = temp;
    }

    zombies = zombieTracker.toArray();
    smartZombies = smartZombieTracker.toArray();
    targets = targetTracker.toArray();
    keyDoors = keyDoorTracker.toArray();
    stars = starTracker.toArray();
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

  
  public void move(byte direction) { // direction represents the direction of the arrow key that was pressed

    ArrayList<Animation> animations = new ArrayList<>();

    int timer = 0;

    player.turn(direction);

    if (!player.canMove(direction))
      return animations;

    animations.addAll(player.move(direction));
    timer = player.getTimeOfMovement();

    if (hasWon) {
      Level.goToNextLevel = true;
      return animations;
    }


    Arrays.sort(zombies);
    for (Zombie z : zombies)
      animations.addAll(z.move(getPlayerLocation(), timer));

    Arrays.sort(smartZombies);
    for (SmartZombie s : smartZombies)
      animations.addAll(s.move(getPlayerLocation(), timer));

    Arrays.sort(zombies);
    for (Zombie z : zombies)
      animations.addAll(z.move(getPlayerLocation(), timer));

    playAnimations(new PriorityQueue(animations));

    if (hasLost)
      player.infect();
    
  }



  public void playAnimations(PriorityQueue<AnimationEvent> queue) {
    long baseTime = System.currentTimeMillis();
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
    return player.getGridCell().getCoordinates();
  }












  

  ////////////////////////////////////////////////////////////////////////////////////////// Setters and Getters
  

  public GridCell getGridCell(int[] rowColumn) throws IndexOutOfBoundsException {
    return board[rowColumn[0]][rowColumn[1]];
  }

  public int getRemainingEnergy() {
    return remainingEnergy;
  }

  public void decrementEnergy() {
    level.setEnergy(--remainingEnergy);
  }

  public void addEnergy(int energyGain) {
    remainingEnergy += energyGain;
    level.setEnergy(remainingEnergy);
  }

  public void infectPlayer() {
    hasLost = true;
  }

  public void hasReturned() {
    hasWon = true;
    for (Target t : targets)
      hasWon = hasWon && t.isGood();
  }

  ////////////////////////////////////////////////////////////////////////////////////////// Core Functionality
  


  public void ToggleTarget() {
    if (Target.isOngoing) {
      ((Target) player.getGridCell().getRoomType()).cancelFix();
      return;
    }
    
    if (!player.getGridCell().hasRoomType(Target.TAG) || ((Target) player.getGridCell().getRoomType()).isGood())
      return;

    JPanel targetPanel = ((Target) player.getGridCell().getRoomType()).fixPanel();
    level.add(targetPanel);

    while (!Target.hasFinished)
      wait();

    level.remove(targetPanel);

    boolean targetsGood = true;
    for (int i = 0; i < targets.length && targetsGood; i++)
      targetsGood = targets[i].isGood();
    if (targetsGood)
      playAnimations(new PriorityQueue<Animation>(setPower(true, 0)));
  }

  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      animations.addAll(board[i/10][i%10].getWall(0).setPower(isPowered, delay));
      animations.addAll(board[i/10][i%10].getWall(1).setPower(isPowered, delay));
    }

    return animations;
  }

  public ArrayList<Animation> unlockDoorsWithKey(Key key, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    
    for (KeyDoor k : keyDoors)
      animations.addAll(k.tryUnlock(key, delay));

    return animations;
  }


  public String[] stringify() {
    String[] gridCellStrings = new String[100];

    for (int i = 0; i < 100; i++)
      gridCellStrings[i] = board[i/10][i%10].stringify();

    return gridCellStrings;
  }

}




class GridCell {

  public static final byte RIGHT_WALL = 0;
  public static final byte BOTTOM_WALL = 1;
  public static final byte LEFT_WALL = 2;
  public static final byte TOP_WALL = 4;

  private static final int[] EntityXYPos = new int[] {}; /////////// What position is entity pic?
  private static final int[] ItemXYPos = new int[] {}; /////////// What position is item pic?
  private static final int[] RoomTypeXYPos = new int[] {}; /////////// What position is roomtype pic?
  private static final int[] WallXYPos = new int[] {}; /////////// What position is wall pic? can swap index 0 with 1 for walls 0 and 1



  private RoomType roomType;
  private Item item;
  private Entity entity;
  private Wall[] walls;
  private int[] rowColumn;
  private GameBoard board;

  public GridCell(String[] fileContents, int coordinates, GameBoard board) {
    rowColumn = new int[] {coordinates / 10, coordinates % 10};
    this.board = board;
    walls = new Wall[2];
    walls[0] = AbstractWall.getWallByTag(fileContents[0], this);
    walls[1] = AbstractWall.getWallByTag(fileContents[1], this);
    roomType = AbstractRoomType.getRoomTypeByTag(fileContents[2], this);
    item = AbstractItem.getItemByTag(fileContents[3], this);
    entity = AbstractEntity.getEntityByTag(fileContents[4], this);
  }

  public char getRoomType() {
    return interior;
  }

  public char getItem() {
    return item;
  }

  public Entity getEntity() {
    return entity;
  }

  public Wall getWall(byte direction) {
    if (direction < 2)
      return walls[direction];
    else
      try {
        return getNeighbor(direction).getWall(direction % 2);
      } catch (IndexOutOfBoundsException e) {
        return new Wall(this);
      }
  }

  public void setRoomType(RoomType interior) {
    this.interior = interior;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  public void setWall(Wall wall, byte direction) {
    if (direction < 2)
      this.walls[direction] = wall;
    else
      try {
        getNeighbor(direction).setWall(direction % 2);
      } catch (IndexOutOfBoundsException e) {/* do nothing */}
  }

  public GridCell getNeighbor(byte direction) throws IndexOutOfBoundsException {
    int rowShift = direction - 2 + 2 * ((3 - direction) / 3); // 0:0; 1:-1; 2:0; 3:1
    int columnShift = direction - 2 + 2 * ((3 - direction) / 3); // 0:0; 1:-1; 2:0; 3:1
    return board.getGridCell(new int[] {rowColumn[0] + rowShift, rowColumn[1] + columnShift});
  }

  public int[] getCoordinates() {
    return rowColumn;
  }

  public String stringify() {
    String s = "";
    AbstractGameObject[] objects = new AbstractGameObject[] {this.walls[0], this.walls[1], this.roomType, this.item, this.entity};
    for (AbstractGameObject o : objects)
      s += ((o != null)? o.getIdentifier() : "-") + " ";

    return s.trim();
  }
}