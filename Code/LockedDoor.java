import java.util.ArrayList;


public class LockedDoor extends AbstracWall {

  public static final String TAG = "Locked_Door";
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;
  private byte ID;

  public LockedDoor(GridCell gridCell, byte startCondition) {
    super(LockedDoor.TAG + ":" + startCondition, gridCell, Data.Images.lockedDoor(LockedDoor.CLOSED));
    this.ID = startCondition;
    isOpen = false;
  }

  public boolean canPass(String entityTag) {
    return isOpen;
  }


  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    return new ArrayList<Animation>();
  }


  public int addDelayInMillis() {
    return 0;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    super.isPowered = isPowered;
    return new ArrayList<Animation>();
  }


  public boolean requiresPower() {
    return false;
  }


  public ArrayList<Animation> openWithKey(Key key, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    if (this.ID = key.getID())
      animations.add(new WallAnimation(delay, this, LockedDoor.OPEN));
    return animations;
  }


  public void transform(byte transformationType) {
    super.image = Data.Images.lockedDoor(transformationType).getImage();
    super.gridCell.repaint();
  }

  public void cycleOptions() {
    ID = ++ID % 10;
    super.image = Data.Images.lockedDoor(ID);
    super.identifier = LockedDoor.TAG + ":" + ID;
  }
}