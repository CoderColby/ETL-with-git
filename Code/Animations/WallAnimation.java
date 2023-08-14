
public class WallAnimation extends AnimationEvent {

  private Wall wall;
  byte transformationType;

  public WallAnimation(int startTimeInMillis, Wall wall, byte transformationType) {
    super(startTimeInMillis);
    this.wall = wall;
    this.transformationType = transformationType;
  }

  @Override
  public void run() {
    wall.transform(transformationType);
    wall.getGridCell().getGameBoard().repaint();
  }
}