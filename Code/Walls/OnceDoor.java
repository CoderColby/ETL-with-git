import java.util.ArrayList;


public class OnceDoor extends AbstractWall {

  public static final String TAG = "Once_Door";
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;

  public OnceDoor(GridCell gridCell, byte startCondition) {
    super(OnceDoor.TAG + ":" + startCondition, gridCell, Data.Images.onceDoor(startCondition));
    isOpen = startCondition == OnceDoor.OPEN;
  }

  public ArrayList<Animation> getAnimations(int delay, AbstractEntity entity) {
    ArrayList<Animation> animations = new ArrayList<>();
    animations.add(new WallAnimation(delay + Data.Animation.humanTravelTime, this, OnceDoor.CLOSED));
    isOpen = false;
    return animations;
  }


  public int addDelayInMillis() {
    return 0;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    super.isPowered = isPowered;
    return new ArrayList<Animation>();
  }

  public boolean canPass(String entityTag) {
    return isOpen;
  }

  public void requiresPower() {
    return false;
  }


  public void transform(byte transformationType) {
    super.image = Data.Images.onceDoor(transformationType).getImage();
    super.gridCell.repaint();
  }

  public void cycleOptions() {
    // nothing
  }
}