import javax.swing.JLabel;
import javax.swing.Timer;

public class EntityAnimation extends Animation {

  private static final int timeBetweenTicksInMillis = 20;

  private JLabel entity;
  private int[] startPosition;
  private int[] endPosition;
  private int durationInMillis;

  private Timer timer;
  private int timeElapsedInMillis;

  public EntityAnimation(int startTimeInMillis, JLabel entity, int[] startPosition, int[] endPosition, int durationInMillis) {
    super(startTimeInMillis);
    this.entity = entity;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.durationInMillis = durationInMillis;
  }

  public void run() {
    float[] currentPosition = {startPosition[0], startPosition[1]};
    float[] distancePerTick = {(endPosition[0] - currentPosition[0]) / (durationInMillis / timeBetweenTicksInMillis), (endPosition[1] - currentPosition[1]) / (durationInMillis / timeBetweenTicksInMillis)};
    timeElapsedInMillis = 0;
    
    timer = new Timer(timeBetweenTicksInMillis, event -> {
      currentPosition[0] += distancePerTick[0];
      currentPosition[1] += distancePerTick[1];
      entity.setLocation(Math.round(currentPosition[0]), Math.round(currentPosition[1]));

      timeElapsedInMillis += timeBetweenTicksInMillis;
      if (timeElapsedInMillis >= durationInMillis)
        timer.stop();

      entity.getGridCell().getGameBoard().repaint();
    });
    timer.start();

    Thread.sleep(durationInMillis);

    entity.setLocation(endPosition[0], startPosition[1]);
    
    entity.getGridCell().getGameBoard().repaint();
  }
}