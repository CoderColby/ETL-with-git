
public abstract class AnimationEvent implements Runnable, Comparable {
  protected int startTimeInMillis;

  protected AnimationEvent(int startTimeInMillis) {
    this.startTimeInMillis = startTimeInMillis;
  }

  @Override
  public int compareTo(AnimationEvent other) {
    return Integer.compareTo(this.startTimeInMillis, other.startTimeInMillis);
  }

  @Override
  public void run();

  public int getStartTime() {
    return startTimeInMillis;
  }
}
