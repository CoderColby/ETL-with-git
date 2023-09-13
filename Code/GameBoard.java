import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
// import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.lang.IndexOutOfBoundsException;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
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
  private Player player;
  private Zombie[] zombies;
  private SmartZombie[] smartZombies;
  private Target[] targets;
  private LockedDoor[] lockedDoors;
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
    ArrayList<LockedDoor> lockedDoorTracker = new ArrayList<>();
    ArrayList<Star> starTracker = new ArrayList<>();
    
    for (int i = 0; i < 100; i++) {
      GridCell temp = new GridCell(fileContents[i], i, this);
      
      if (temp.hasEntity(Zombie.TAG))
        zombieTracker.add((Zombie) temp.getEntity());
      else if (temp.hasEntity(SmartZombie.TAG))
        smartZombieTracker.add((SmartZombie) temp.getEntity());
      else if (temp.hasEntity(Player.TAG))
        player = (Player) temp.getEntity();

      if (temp.hasRoomType(Target.TAG))
        targetTracker.add((Target) temp.getRoomType());
      else if (temp.hasRoomType(Star.TAG))
        starTracker.add((Star) temp.getRoomType());

      for (byte wallNum = 0; wallNum < 2; wallNum++) {
        if (temp.hasWall(wallNum, LockedDoor.TAG))
          lockedDoorTracker.add((LockedDoor) temp.getWall(wallNum));
      }
      
      board[i/10][i%10] = temp;
    }

    if (!zombieTracker.isEmpty())
      zombieTracker.toArray(zombies);
    if (!smartZombieTracker.isEmpty())
      smartZombieTracker.toArray(smartZombies);
    if (!targetTracker.isEmpty())
      targetTracker.toArray(targets);
    if (!lockedDoorTracker.isEmpty())
      lockedDoorTracker.toArray(lockedDoors);
    if (!starTracker.isEmpty())
      starTracker.toArray(stars);
    remainingEnergy = startEnergy;
    hasWon = false;
    hasLost = false;

    // for (int i = 0; i < 100; i++) {
    //   super.add(new JLabel(board[i/10][i%10].getRoomType()), ROOMTYPE_LAYER);
    //   super.add(new JLabel(board[i/10][i%10].getItem()), ITEM_LAYER);
    //   super.add(new JLabel(board[i/10][i%10].getEntity()), ENTITY_LAYER);
    //   if (i % 10 < 9)
    //     super.add(new JLabel(board[i/10][i%10].getWall((byte) 0)), WALL_LAYER);
    //   if (i / 10 < 9)
    //     super.add(new JLabel(board[i/10][i%10].getWall((byte) 1)), WALL_LAYER);
    // }
    
  }

  
  public void move(byte direction) { // direction represents the direction of the arrow key that was pressed

    ArrayList<Animation> animations = new ArrayList<>();

    int timer = 0;

    player.turn(direction);

    if (player.canMove(direction)) {

      animations.addAll(player.move(direction));
      timer = player.getTimeOfMovement();
  
      if (hasWon) {
        Level.goToNextLevel = true;
        level.getUser().incrementLevels();
        if (stars.length == 0 && !level.isCustom())
          level.getUser().addPerfectLevel(level.getNum());
      } else {
  
  
        Arrays.sort(zombies);
        for (Zombie z : zombies)
          animations.addAll(z.move(getPlayerLocation(), timer));
    
        Arrays.sort(smartZombies);
        for (SmartZombie s : smartZombies)
          animations.addAll(s.move(timer));
    
        Arrays.sort(zombies);
        for (Zombie z : zombies)
          animations.addAll(z.move(getPlayerLocation(), timer));
      }
    }

    playAnimations(new PriorityQueue(animations));

    if (hasLost) {
      player.infect();
      level.playerDeath();
    }
    
  }



  public void playAnimations(PriorityQueue<Animation> queue) {
    long baseTime = System.currentTimeMillis();
    Animation animation;
    long animationStartTime;
    long timeUntilAnimationStart;

    while (!queue.isEmpty()) {
      animation = queue.poll();
      animationStartTime = baseTime + animation.getStartTime();

      timeUntilAnimationStart = animationStartTime - System.currentTimeMillis();

      if (timeUntilAnimationStart > 0) {
          try {
              Thread.sleep(timeUntilAnimationStart); // Sleep until animation start time
          } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
          }
      }

      Thread animationThread = new Thread(animation::run);
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
  

  private JPanel targetPanel;

  public void ToggleTarget() {
    if (Target.isOngoing) {
      Target.isOngoing = false;
      notifyAll();
      return;
    }
    
    if (!player.getGridCell().hasRoomType(Target.TAG) || ((Target) player.getGridCell().getRoomType()).isGood())
      return;

    targetPanel = ((Target) player.getGridCell().getRoomType()).fixPanel();
    level.add(targetPanel);

    while (Target.isOngoing) {
      try {
        wait();
      } catch (InterruptedException e) {
        // nothing
      }
    }

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
      animations.addAll(board[i/10][i%10].getWall((byte)0).setPower(isPowered, delay));
      animations.addAll(board[i/10][i%10].getWall((byte)1).setPower(isPowered, delay));
    }

    return animations;
  }

  public ArrayList<Animation> unlockDoorsWithKey(Key key, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    
    for (LockedDoor k : lockedDoors)
      animations.addAll(k.openWithKey(key, delay));

    return animations;
  }

  public int getNumOfTargets() {
    return targets.length;
  }

  public int getNumOfGoodTargets() {
    int count = 0;
    for (Target t : targets)
      if (t.isGood())
        count++;
    return count;
  }

  public void removeStar(Star star) {
    ArrayList<Star> otherStars = new ArrayList<>(Arrays.asList(stars));
    otherStars.remove(star);
    star.getGridCell().setRoomType(null);
    stars = (Star[]) otherStars.toArray();
    super.repaint();
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
  public static final byte TOP_WALL = 3;

  // private static final int[] EntityXYPos = new int[] {}; /////////// What position is entity pic?
  // private static final int[] ItemXYPos = new int[] {}; /////////// What position is item pic?
  // private static final int[] RoomTypeXYPos = new int[] {}; /////////// What position is roomtype pic?
  // private static final int[] WallXYPos = new int[] {}; /////////// What position is wall pic? can swap index 0 with 1 for walls 0 and 1



  private AbstractRoomType roomType;
  private AbstractItem item;
  private AbstractEntity entity;
  private AbstractWall[] walls;
  private int[] rowColumn;
  private GameBoard board;

  public GridCell() {
    System.out.println("HIE");
  }

  public GridCell(String[] fileContents, int coordinates, GameBoard board) {
    rowColumn = new int[] {coordinates / 10, coordinates % 10};
    this.board = board;
    walls = new AbstractWall[2];
    walls[0] = AbstractWall.getWallByTag(fileContents[0], this, (byte) 0);
    walls[1] = AbstractWall.getWallByTag(fileContents[1], this, (byte) 1);
    roomType = AbstractRoomType.getRoomTypeByTag(fileContents[2], this);
    item = AbstractItem.getItemByTag(fileContents[3], this);
    entity = AbstractEntity.getEntityByTag(fileContents[4], this);
  }

  public boolean hasRoomType(String tag) {
    return roomType != null && tag.equals(roomType.getIdentifier().split(":")[0]);
  }

  public boolean hasItem(String tag) {
    return item != null && tag.equals(item.getIdentifier().split(":")[0]);
  }

  public boolean hasEntity(String tag) {
    return entity != null && tag.equals(entity.getIdentifier().split(":")[0]);
  }

  public boolean hasWall(byte orientation, String tag) {
    return walls[orientation] != null && tag.equals(walls[orientation].getIdentifier().split(":")[0]);
  }

  public AbstractRoomType getRoomType() {
    return roomType;
  }

  public AbstractItem getItem() {
    return item;
  }

  public AbstractEntity getEntity() {
    return entity;
  }

  public AbstractWall getWall(byte direction) {
    AbstractWall wall;
    if (direction < 2)
      wall = walls[direction];
    else
      try {
        return getNeighbor(direction).getWall((byte) (direction % 2));
      } catch (IndexOutOfBoundsException e) {
        wall = AbstractWall.getWallByTag(Wall.TAG + ":" + Wall.DEFAULT, this, (byte) (direction % 2));
      }
    // if (direction == 1)
    //   wall.setImage(Data.Images.rotateIcon(wall, 90).getImage());
    return wall;
  }

  public int[] getPixelOffset() {
    return new int[] {rowColumn[0] * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), rowColumn[1] * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS)};
  }

  public void setRoomType(AbstractRoomType roomType) {
    this.roomType = roomType;
  }

  public void setItem(AbstractItem item) {
    this.item = item;
  }

  public void setEntity(AbstractEntity entity) {
    this.entity = entity;
  }

  public void setWall(AbstractWall wall, byte direction) {
    if (direction < 2)
      this.walls[direction] = wall;
    else
      try {
        getNeighbor(direction).setWall(wall, (byte) (direction % 2));
      } catch (IndexOutOfBoundsException e) {/* do nothing */}
  }

  public GridCell getNeighbor(byte direction) throws IndexOutOfBoundsException {
    int rowShift = 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
    int columnShift = 2 - direction - 2 * ((5 - direction) / 5); // 0:0; 1:1; 2:0; 3:-1
    return board.getGridCell(new int[] {rowColumn[0] + rowShift, rowColumn[1] + columnShift});
  }

  public int[] getCoordinates() {
    return rowColumn;
  }

  public GameBoard getGameBoard() {
    return board;
  }

  public String stringify() {
    String s = "";
    AbstractGameObject[] objects = new AbstractGameObject[] {this.walls[0], this.walls[1], this.roomType, this.item, this.entity};
    for (AbstractGameObject o : objects)
      s += ((o != null)? o.getIdentifier() : "-:") + " ";

    return s.trim();
  }
}










abstract class Animation implements Runnable, Comparable {
  protected int startTimeInMillis;

  protected Animation(int startTimeInMillis) {
    this.startTimeInMillis = startTimeInMillis;
  }

  public int compareTo(Object other) {
    return Integer.compare(this.startTimeInMillis, ((Animation) other).startTimeInMillis);
  }

  public abstract void run();

  public int getStartTime() {
    return startTimeInMillis;
  }
}








class EntityAnimation extends Animation {

  private static final int timeBetweenTicksInMillis = 20;

  private AbstractEntity entity;
  private byte direction;
  private int[] startPosition;
  private int[] endPosition;
  private float[] currentPosition;
  private float[] distancePerTick;

  private Timer timer;
  private int timeElapsedInMillis;

  public EntityAnimation(int startTimeInMillis, AbstractEntity entity, byte direction) {
    super(startTimeInMillis);
    this.entity = entity;
    this.direction = direction;
  }

  public void run() {
    startPosition = new int[] {entity.getLabel().getX(), entity.getLabel().getY()};
    int rowShift = 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
    int columnShift = 2 - direction - 2 * ((5 - direction) / 5); // 0:0; 1:1; 2:0; 3:-1
    endPosition = new int[] {startPosition[0] + rowShift * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), startPosition[1] + columnShift * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS)};
    
    currentPosition = new float[] {startPosition[0], startPosition[1]};
    distancePerTick = new float[] {(endPosition[0] - currentPosition[0]) / (entity.getAnimationDuration() / timeBetweenTicksInMillis), (endPosition[1] - currentPosition[1]) / (entity.getAnimationDuration() / timeBetweenTicksInMillis)};
    timeElapsedInMillis = 0;
    
    timer = new Timer(timeBetweenTicksInMillis, event -> {
      EntityAnimation.this.currentPosition[0] += EntityAnimation.this.distancePerTick[0];
      EntityAnimation.this.currentPosition[1] += EntityAnimation.this.distancePerTick[1];
      EntityAnimation.this.entity.getLabel().setLocation(Math.round(EntityAnimation.this.currentPosition[0]), Math.round(EntityAnimation.this.currentPosition[1]));

      EntityAnimation.this.timeElapsedInMillis += EntityAnimation.timeBetweenTicksInMillis;
      if (EntityAnimation.this.timeElapsedInMillis >= EntityAnimation.this.entity.getAnimationDuration()) {
        EntityAnimation.this.timer.stop();
        notifyAll();
      }

      EntityAnimation.this.entity.getGridCell().getGameBoard().repaint();
    });
    timer.start();

    while (timer.isRunning()) {
      try {
        wait();
      } catch (InterruptedException e) {
        // nothing
      }
    }

    entity.getLabel().setLocation(endPosition[0], endPosition[1]);
    
    entity.getGridCell().getGameBoard().repaint();
  }
}







class WallAnimation extends Animation {

  private AbstractWall wall;
  private byte transformationType;

  public WallAnimation(int startTimeInMillis, AbstractWall wall, byte transformationType) {
    super(startTimeInMillis);
    this.wall = wall;
    this.transformationType = transformationType;
  }

  public void run() {
    wall.transform(transformationType);
    wall.getGridCell().getGameBoard().repaint();
  }
}