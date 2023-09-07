import java.util.ArrayList;


public class DetectionDoor extends AbstractWall {

  public static final String TAG = "Detection_Door";
  public static final byte DEFAULT = 1;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;

  public DetectionDoor(GridCell gridCell, byte startCondition) {
    super(DetectionDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.detectionDoor(startCondition));
    isOpen = startCondition == DetectionDoor.OPEN;
  }

  public boolean canPass(String entityTag) {
    return isOpen;
  }


  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    if (!entityTag.equals(Player.TAG)) {
      animations.add(new WallAnimation(delay + Data.Animation.zombieTravelTime, this, DetectionDoor.CLOSED));
      isOpen = !isOpen;
    }
    return animations;
  }


  public int addDelayInMillis() {
    return 0;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    super.isPowered = isPowered;
    return new ArrayList<Animation>();
  }


  public boolean requiresEnergy() {
    return false;
  }


  public void transform(byte transformationType) {
    super.setImage(Data.Images.Wall.detectionDoor(transformationType).getImage());
    super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    // nothing
  }
}