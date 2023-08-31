
public abstract class Animation implements Runnable, Comparable {
  protected int startTimeInMillis;

  protected Animation(int startTimeInMillis) {
    this.startTimeInMillis = startTimeInMillis;
  }

  public int compareTo(Animation other) {
    return Integer.compare(this.startTimeInMillis, other.startTimeInMillis);
  }

  public abstract void run();

  public int getStartTime() {
    return startTimeInMillis;
  }
}
