import java.util.ArrayList;


public class LockedDoor extends AbstractWall {

  public static final String TAG = "Locked_Door";
  public static final byte DEFAULT = 0;
  public static final byte OPEN = 10;

  private boolean isOpen;
  private byte ID;

  public LockedDoor(GridCell gridCell, byte startCondition) {
    super(LockedDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.lockedDoor(startCondition));
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


  public boolean requiresEnergy() {
    return false;
  }


  public ArrayList<Animation> openWithKey(Key key, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    if (this.ID = key.getID())
      animations.add(new WallAnimation(delay, this, LockedDoor.OPEN));
    return animations;
  }


  public void transform(byte transformationType) {
    super.setImage(Data.Images.Wall.lockedDoor(transformationType).getImage());
    super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    ID = ++ID % 10;
    super.setImage(Data.Images.Wall.lockedDoor(ID).getImage());
    super.identifier = LockedDoor.TAG + ":" + ID;
  }
}