import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;


public class EntityAnimation extends Animation {

  private static final int timeBetweenTicksInMillis = 20;

  private AbstractEntity entity;
  private JLabel entityLabel;
  private int[] startPosition;
  private int[] endPosition;
  private float[] currentPosition;
  private float[] distancePerTick;
  private int durationInMillis;

  private Timer timer;
  private int timeElapsedInMillis;

  public EntityAnimation(int startTimeInMillis, AbstractEntity entity, int[] startPosition, int[] endPosition, int durationInMillis) {
    super(startTimeInMillis);
    this.entity = entity;
    this.entityLabel = new JLabel(entity);
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.durationInMillis = durationInMillis;
  }

  public void run() {
    currentPosition = new float[] {startPosition[0], startPosition[1]};
    distancePerTick = new float[] {(endPosition[0] - currentPosition[0]) / (durationInMillis / timeBetweenTicksInMillis), (endPosition[1] - currentPosition[1]) / (durationInMillis / timeBetweenTicksInMillis)};
    timeElapsedInMillis = 0;
    
    timer = new Timer(timeBetweenTicksInMillis, event -> {
      EntityAnimation.this.currentPosition[0] += EntityAnimation.this.distancePerTick[0];
      EntityAnimation.this.currentPosition[1] += EntityAnimation.this.distancePerTick[1];
      EntityAnimation.this.entityLabel.setLocation(Math.round(EntityAnimation.this.currentPosition[0]), Math.round(EntityAnimation.this.currentPosition[1]));

      EntityAnimation.this.timeElapsedInMillis += EntityAnimation.timeBetweenTicksInMillis;
      if (EntityAnimation.this.timeElapsedInMillis >= EntityAnimation.this.durationInMillis)
        EntityAnimation.this.timer.stop();

      EntityAnimation.this.entity.getGridCell().getGameBoard().repaint();
    });
    timer.start();

    Thread.sleep(durationInMillis);

    entityLabel.setLocation(endPosition[0], startPosition[1]);
    
    entity.getGridCell().getGameBoard().repaint();
  }
}