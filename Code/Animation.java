
public abstract class Animation implements Runnable, Comparable {
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
