import javax.swing.JLabel;
import java.lang.Comparable;
import java.lang.Thread;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.JLayeredPane;
import java.util.PriorityQueue;
import java.util.ArrayList;

// Each wall animation goes before an enitity animation so I am pairing them up

public class AnimationList extends PriorityQueue<AnimationEvent> {

  public final byte PLAYER_MOVE_PHASE = 0;
  public final byte PLAYER_AFTER_PHASE = 1;
  public final byte ZOMBIE_FIRST_MOVE_PHASE = 2;
  public final byte ZOMBIE_FIRST_AFTER_PHASE = 3;
  public final byte ZOMBIE_SECOND_MOVE_PHASE = 4;
  public final byte ZOMBIE_SECOND_AFTER_PHASE = 5;

  private GameBoard board;
  private int[] phaseTiming;

  public AnimationList(GameBoard board) {
    this.board = board;
    this.phaseTiming = int[] {0, 1200, 1400, 2400, 2600, 3600};
  }

  public void addAnimation(GridCell cell, Entity entity, byte direction, Wall wall, byte phase) {
    super.add(new WallAnimation(wall, phaseTiming[phase]));
    Dimension distance = new Dimension(GameBoard.getRowShift(direction) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.getColumnShift(direction) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS));
    super.add(new EntityAnimation(entity, direction, distance, phaseTiming[phase] + wall.getDelay(), Data.Animation.humanTravelTime, board));
  }

  // public void addWallAnimation(Wall wall, byte phase) {
  //   this.push(new WallAnimation(wall))
  // }

  // public void addEntityAnimation(Entity entity, byte direction, AnimationEvent executeAfterMe, phase) {
    
  // }
  
}



abstract class AnimationEvent implements Comparable<AnimationEvent> {

  private int startTime;
  private GameBoard board;

  public AnimationEvent(int startTime, GameBoard board) {
    this.startTime = startTime;
    this.board = board;
  }

  public int startTime() {
    return startTime;
  }

  public GameBoard board() {
    return board;
  }

  public abstract void run();

  @Override
  public int compareTo(AnimationEvent other) {
    return Integer.compare(other.startTime(), this.startTime());
  }
}


class WallAnimation extends AnimationEvent {

  private JLabel removal;
  private JLabel replacement;

  public WallAnimation(Wall wall, int startTime, GameBoard board) {
    super(startTime, board);
    this.removal = wall.getCurrentJLabel();
    this.replacement = wall.getNextJLabel();
  }

  public int getDelay(Entity entity) {
    
  }

  @Override
  public void run() {
    int layer = JLayeredPane.getLayer(this.removal);
    super.board().remove(this.removal);
    super.board().add(this.replacement, layer);
    Main.mainWindow.repaint();
  }
  
}


class EntityAnimation extends AnimationEvent implements Runnable {
  
  private JLabel movingImage;
  private int durationInMilliseconds;
  private Dimension travelDistance;
  private Point endPosition;
  private Timer timer;
  private int numOfRepeats;
  private double updatedVerticalPosition;
  private double updatedHorizontalPosition;


  public EntityAnimation(Entity entity, byte direction, Dimension travelDistance, int startTime, int durationInMilliseconds, GameBoard board) {
    super(startTime, board);
    this.movingImage = entity.getJLabel(direction);
    this.travelDistance = travelDistance;
    this.endPosition = movingImage.getBounds().getLocation();
    this.durationInMilliseconds = durationInMilliseconds;
    this.numOfRepeats = 0;
    this.updatedVerticalPosition = movingImage.getBounds().getY();
    this.updatedHorizontalPosition = movingImage.getBounds().getX();
  }

  @Override
  public void run() {
    double verticalMovementPerMillisecond = travelDistance.getHeight() / durationInMilliseconds;
    double horizontalMovementPerMillisecond = travelDistance.getWidth() / durationInMilliseconds;

    timer = new Timer(Data.Animation.animationSpeed, event -> {
      
      this.updatedVerticalPosition += verticalMovementPerMillisecond % 1;
      this.updatedHorizontalPosition += horizontalMovementPerMillisecond % 1;
      
      movingImage.setLocation((int) updatedHorizontalPosition, (int) updatedVerticalPosition);
      
      if (++numOfRepeats > durationInMilliseconds / Data.Animation.animationSpeed)
        timer.stop();
    });

    this.movingImage.setLocation((int) endPosition.getX(), (int) endPosition.getY());
  }
}