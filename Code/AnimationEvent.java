
public abstract class AnimationEvent implements Runnable {
  protected int startTime;

  protected AnimationEvent(int startTime) {
    this.startTime = startTime;
  }

  @Override
  public void run();

  public int getStartTime() {
    return startTime;
  }
}
