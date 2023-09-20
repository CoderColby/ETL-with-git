import java.util.Arrays;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

import java.lang.IndexOutOfBoundsException;
import java.lang.Thread;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.SwingWorker;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;


public class GameBoard extends JLayeredPane {
  
  public static final byte MOVE_RIGHT = 0;
  public static final byte MOVE_DOWN = 1;
  public static final byte MOVE_LEFT = 2;
  public static final byte MOVE_UP = 3;
  public static final int ROOM_HEIGHT = 49;
  public static final int WALL_THICKNESS = 10;
  public static final int BOARD_DIMENSION = 10 * ROOM_HEIGHT + 11 * WALL_THICKNESS;
  public static final byte BASE_LAYER = 0;
  public static final byte ROOMTYPE_LAYER = 1;
  public static final byte ITEM_LAYER = 2;
  public static final byte ENTITY_LAYER = 3;
  public static final byte WALL_LAYER = 4;

  private Level level;

  private String[][] fileContents;
  private GridCell[][] board;
  private Player player;
  private Player[] players;
  private Elevator[] elevators;
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
    super.setSize(GameBoard.BOARD_DIMENSION, BOARD_DIMENSION);
    super.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
    super.setBackground(Data.Colors.levelBackground);
    super.setBorder(BorderFactory.createLineBorder(Color.BLACK, GameBoard.WALL_THICKNESS));
    super.setOpaque(true);
    super.setFocusable(false);
    reset();
  }

  
  public void reset() {
    super.removeAll();
    for (int i = 0; i < 100; i++)
      board[i/10][i%10] = new GridCell(fileContents[i], i, this);
    scanGridCells();
    remainingEnergy = startEnergy;
    hasWon = false;
    hasLost = false;

    for (int i = 0; i < 100; i++)
      board[i/10][i%10].draw();
  }

  
  private void scanGridCells() {
    ArrayList<Player> playerTracker = new ArrayList<>();
    ArrayList<Elevator> elevatorTracker = new ArrayList<>();
    ArrayList<Zombie> zombieTracker = new ArrayList<>();
    ArrayList<SmartZombie> smartZombieTracker = new ArrayList<>();
    ArrayList<Target> targetTracker = new ArrayList<>();
    ArrayList<LockedDoor> lockedDoorTracker = new ArrayList<>();
    ArrayList<Star> starTracker = new ArrayList<>();
    player = null;
    players = new Player[0];
    elevators = new Elevator[0];
    zombies = new Zombie[0];
    smartZombies = new SmartZombie[0];
    targets = new Target[0];
    lockedDoors = new LockedDoor[0];
    stars = new Star[0];
    
    
    for (int i = 0; i < 100; i++) {
      GridCell temp = board[i/10][i%10];
      
      if (temp.hasEntity(Zombie.TAG))
        zombieTracker.add((Zombie) temp.getEntity());
      else if (temp.hasEntity(SmartZombie.TAG))
        smartZombieTracker.add((SmartZombie) temp.getEntity());
      else if (temp.hasEntity(Player.TAG))
        playerTracker.add((Player) temp.getEntity());

      if (temp.hasRoomType(Target.TAG))
        targetTracker.add((Target) temp.getRoomType());
      else if (temp.hasRoomType(Star.TAG))
        starTracker.add((Star) temp.getRoomType());
      else if (temp.hasRoomType(Elevator.TAG))
        elevatorTracker.add((Elevator) temp.getRoomType());

      for (byte wallNum = 0; wallNum < 2; wallNum++) {
        if (temp.hasWall(wallNum, LockedDoor.TAG))
          lockedDoorTracker.add((LockedDoor) temp.getWall(wallNum));
      }
      
      board[i/10][i%10] = temp;
    }

    // if (!playerTracker.isEmpty())
      players = playerTracker.toArray(players);
    // if (!zombieTracker.isEmpty())
      zombies = zombieTracker.toArray(zombies);
    // if (!smartZombieTracker.isEmpty())
      smartZombies = smartZombieTracker.toArray(smartZombies);
    // if (!targetTracker.isEmpty())
      targets = targetTracker.toArray(targets);
    // if (!lockedDoorTracker.isEmpty())
      lockedDoors = lockedDoorTracker.toArray(lockedDoors);
    // if (!starTracker.isEmpty())
      stars = starTracker.toArray(stars);
    // if (!elevatorTracker.isEmpty())
      elevators = elevatorTracker.toArray(elevators);

    if (!playerTracker.isEmpty())
      player = playerTracker.get(0);
    
  }

  
  public void move(byte direction) { // direction represents the direction of the arrow key that was pressed

    if (Animation.isOngoing)
      return;

    ArrayList<Animation> animations = new ArrayList<>();

    int delay = 0;

    player.turn(direction);

    if (player.canMove(direction)) {

      animations.addAll(player.move(direction));
      delay = player.getTimeOfMovement();
  
      if (!hasWon) {
  
        Arrays.sort(zombies);
        for (Zombie z : zombies)
          animations.addAll(z.move(getPlayerLocation(), delay, true));
    
        Arrays.sort(smartZombies);
        for (SmartZombie s : smartZombies)
          animations.addAll(s.move(delay));
    
        Arrays.sort(zombies);
        for (Zombie z : zombies)
          animations.addAll(z.move(getPlayerLocation(), delay, false));
      }
    }

    playAnimations(new PriorityQueue(animations));
  }


  private void evaluatePlayer() {
    if (hasWon) {
      Level.goToNextLevel = true;
      if (!level.isCustom()) {
        level.getUser().setLevels(Integer.parseInt(level.getLevelFile().getName().substring("level".length(), level.getLevelFile().getName().indexOf('.'))));
        if (stars.length == 0 && !level.isCustom())
          level.getUser().addPerfectLevel(level.getNum());
      }
    }

    if (hasLost) {
      player.infect();
      level.playerDeath();
    }
  }


  private PriorityQueue queue;
  private Animation animation;
  private long animationStartTime;
  private long timeUntilAnimationStart;
  private long endOfAnimation;
  
  public void playAnimations(PriorityQueue<Animation> queue) {
    this.queue = queue;
    final long baseTime = System.currentTimeMillis();

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      
      @Override
      protected Void doInBackground() throws Exception {
        Animation.isOngoing = true;

        while (!queue.isEmpty()) {
          animation = queue.poll();
          animationStartTime = baseTime + animation.getStartTime();

          if (animation.getClass() == EntityAnimation.class && animationStartTime + ((EntityAnimation) animation).getEntity().getAnimationDuration() > endOfAnimation)
            endOfAnimation = animationStartTime + ((EntityAnimation) animation).getEntity().getAnimationDuration();
    
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

        try {
          Thread.sleep(endOfAnimation - System.currentTimeMillis()); // Sleep until animation is done
        } catch (InterruptedException e) {
          // nothing
        }
        
        return null;
      }

      @Override
      protected void done() {
        Animation.isOngoing = false;
        evaluatePlayer();
      }
    };

    worker.execute();
  }



  public int[] getPlayerLocation() {
    return player.getGridCell().getCoordinates();
  }


  ////////////////////////////////////////////////////////////////////////////////////////// Setters and Getters
  

  public GridCell getGridCell(int[] firstSecond) throws IndexOutOfBoundsException {
    return board[firstSecond[0]][firstSecond[1]];
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
  

  private JPanel targetPanel;

  public void toggleTarget() {
    if (Target.isOngoing) {
      Target.isOngoing = false;
      return;
    }
    
    if (!player.getGridCell().hasRoomType(Target.TAG) || ((Target) player.getGridCell().getRoomType()).isGood())
      return;

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      
      @Override
      protected Void doInBackground() throws Exception {
        targetPanel = ((Target) player.getGridCell().getRoomType()).fixPanel();
        level.add(targetPanel);
        level.setComponentZOrder(targetPanel, 0);
        level.repaint();
        targetPanel.requestFocusInWindow();

        while (Target.isOngoing) {
          try {
              Thread.sleep(50);
          } catch (InterruptedException f) {
            Target.isOngoing = false;
          }
        }
        return null;
      }

      @Override
      protected void done() {
        level.remove(targetPanel);
        level.requestFocusInWindow();
        level.setGenNum(getNumOfGoodTargets());
        level.repaint();

        boolean targetsGood = true;
        for (int i = 0; i < targets.length && targetsGood; i++)
          targetsGood = targets[i].isGood();
        if (targetsGood)
          playAnimations(new PriorityQueue<Animation>(setPower(true, 0)));
      }
    };

    worker.execute();
  }
  

  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      animations.addAll(board[i/10][i%10].getWall((byte) 0).setPower(isPowered, delay));
      animations.addAll(board[i/10][i%10].getWall((byte) 1).setPower(isPowered, delay));
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
    super.remove(star.getLabel());
    ArrayList<Star> otherStars = new ArrayList<>(Arrays.asList(stars));
    otherStars.remove(star);
    star.getGridCell().setRoomType(null);
    stars = otherStars.toArray(stars);
    super.repaint();
  }


  public boolean isValidLayout() {
    scanGridCells();
    boolean isEdgeWalls = true;
    for (int i = 0; i < 10 && isEdgeWalls; i++)
      isEdgeWalls = board[i][9].hasWall((byte) 0, Wall.TAG);
    for (int i = 0; i < 10 && isEdgeWalls; i++)
      isEdgeWalls = board[9][i].hasWall((byte) 1, Wall.TAG);
    return isEdgeWalls && players.length == 1 && elevators.length == 1 && targets.length >= 1 && elevators[0].getGridCell().getItem() == null && elevators[0].getGridCell() == players[0].getGridCell();
  } /////////////////////////// Make a separate function that relocates things in all of the updated gridCells, similar to 'reset' but without the fileContents


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

  private AbstractRoomType roomType;
  private AbstractItem item;
  private AbstractEntity entity;
  private AbstractWall[] walls;
  private int[] firstSecond;
  private GameBoard board;
  

  public GridCell(String[] fileContents, int coordinates, GameBoard board) {
    firstSecond = new int[] {coordinates / 10, coordinates % 10};
    this.board = board;
    walls = new AbstractWall[2];
    if (firstSecond[1] == 9)
      walls[0] = AbstractWall.getWallByTag(Wall.TAG + ":" + Wall.DEFAULT, this, (byte) 0);
    else
      walls[0] = AbstractWall.getWallByTag(fileContents[0], this, (byte) 0);
    if (firstSecond[0] == 9)
      walls[1] = AbstractWall.getWallByTag(Wall.TAG + ":" + Wall.DEFAULT, this, (byte) 1);
    else
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
    return new int[] {GameBoard.WALL_THICKNESS + firstSecond[1] * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS + firstSecond[0] * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS)};
  }

  public double getDistanceFromPlayer() {
    int[] playerCoords = board.getPlayerLocation();
    return Math.sqrt(Math.pow(firstSecond[0] - playerCoords[0], 2) + Math.pow(firstSecond[1] - playerCoords[1], 2));
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
      } catch (IndexOutOfBoundsException e) {
        // nothing
      }
  }

  public GridCell getNeighbor(byte direction) throws IndexOutOfBoundsException {
    int secondShift = 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
    int firstShift = 2 - direction - 2 * ((5 - direction) / 5); // 0:0; 1:1; 2:0; 3:-1
    return board.getGridCell(new int[] {firstSecond[0] + firstShift, firstSecond[1] + secondShift});
  }

  public int[] getCoordinates() {
    return firstSecond;
  }

  public GameBoard getGameBoard() {
    return board;
  }

  public void draw() {
    JLabel room = new JLabel();
    room.setBackground(Data.Colors.roomBackground);
    int[] offset = getPixelOffset();
    room.setBounds(offset[0], offset[1], AbstractRoomType.DIMENSION, AbstractRoomType.DIMENSION);
    room.setOpaque(true);
    board.add(room);
    board.setLayer(room, GameBoard.BASE_LAYER);
    
    if (roomType != null) {
      board.add(roomType.getLabel());
      board.setLayer(roomType.getLabel(), GameBoard.ROOMTYPE_LAYER);
    }
    if (this.item != null) {
      board.add(item.getLabel());
      board.setLayer(item.getLabel(), GameBoard.ITEM_LAYER);
    }
    if (entity != null) {
      board.add(entity.getLabel());
      board.setLayer(entity.getLabel(), GameBoard.ENTITY_LAYER);
    }
    if (firstSecond[1] < 9) {
      board.add(walls[0].getLabel());
      board.setLayer(walls[0].getLabel(), (hasWall((byte) 0, Hallway.TAG)? GameBoard.BASE_LAYER : GameBoard.WALL_LAYER));
    }
    if (firstSecond[0] < 9) {
      board.add(walls[1].getLabel());
      board.setLayer(walls[1].getLabel(), (hasWall((byte) 1, Hallway.TAG)? GameBoard.BASE_LAYER : GameBoard.WALL_LAYER));
    }
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
  public static boolean isOngoing = false;
  
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

  public static final int timeBetweenTicksInMillis = 100;

  private AbstractEntity entity;
  private byte direction;
  private Point startPosition;
  private Point endPosition;
  private double[] currentPosition;
  private double[] distancePerTick;

  private Timer timer;
  private int timeElapsedInMillis;

  public EntityAnimation(int startTimeInMillis, AbstractEntity entity, byte direction) {
    super(startTimeInMillis);
    this.entity = entity;
    this.direction = direction;
  }

  public AbstractEntity getEntity() {
    return entity;
  }

  public void run() {
    startPosition = new Point(entity.getGameBoardPosition());
    int secondShift = 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
    int firstShift = 2 - direction - 2 * ((5 - direction) / 5); // 0:0; 1:1; 2:0; 3:-1
    entity.getGameBoardPosition().translate(secondShift * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), firstShift * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS));
    endPosition = new Point(entity.getGameBoardPosition());
    
    currentPosition = new double[] {startPosition.getX(), startPosition.getY()};
    distancePerTick = new double[] {(endPosition.getX() - startPosition.getX()) / (entity.getAnimationDuration() / timeBetweenTicksInMillis), (endPosition.getY() - startPosition.getY()) / (entity.getAnimationDuration() / timeBetweenTicksInMillis)};
    timeElapsedInMillis = 0;
    
    timer = new Timer(timeBetweenTicksInMillis, event -> {
      EntityAnimation.this.currentPosition[0] += EntityAnimation.this.distancePerTick[0];
      EntityAnimation.this.currentPosition[1] += EntityAnimation.this.distancePerTick[1];
      EntityAnimation.this.entity.getLabel().setLocation((int) Math.round(currentPosition[0]), (int) Math.round(currentPosition[1]));

      EntityAnimation.this.timeElapsedInMillis += EntityAnimation.timeBetweenTicksInMillis;
      if (EntityAnimation.this.timeElapsedInMillis >= EntityAnimation.this.entity.getAnimationDuration())
        EntityAnimation.this.timer.stop();

      EntityAnimation.this.entity.getGridCell().getGameBoard().repaint();
    });

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      
      @Override
      protected Void doInBackground() throws Exception {
        
        timer.start();

        while (timer.isRunning()) {
          try {
              Thread.sleep(timeBetweenTicksInMillis);
          } catch (InterruptedException f) {
              // nothing
          }
        }
        return null;
      }

      @Override
      protected void done() {
        entity.getLabel().setLocation(endPosition);
        entity.getGridCell().getGameBoard().repaint();
      }
    };

    worker.execute();
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