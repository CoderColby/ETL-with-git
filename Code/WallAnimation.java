

public class WallAnimation extends Animation {

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