
public class EntityAnimation extends AnimationEvent {

  private Wall wall;
  byte transformationType;

  public WallAnimation(int startTime, Wall wall, byte transformationType) {
    super(startTime);
    this.wall = wall;
    this.transformationType = transformationType;
  }

  @Override
  public void run() {
    wall.transform(transformationType);
    wall.getGridCell().getGameBoard().repaint();
  }
}