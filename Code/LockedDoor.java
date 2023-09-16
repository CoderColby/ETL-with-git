import java.util.ArrayList;
import javax.swing.ImageIcon;


public class LockedDoor extends AbstractWall {

  public static final String TAG = "Locked_Door";
  public static final byte DEFAULT = 0;
  public static final byte OPEN = 10;

  private boolean isOpen;
  private byte ID;


  public LockedDoor() {
    super(LockedDoor.TAG + ":" + LockedDoor.DEFAULT, Data.Images.Wall.lockedDoor(LockedDoor.DEFAULT));
  }

  public LockedDoor(GridCell gridCell, byte startCondition, byte orientation) {
    super(LockedDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.lockedDoor(startCondition), orientation);
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
    if (this.ID == key.getID())
      animations.add(new WallAnimation(delay, this, LockedDoor.OPEN));
    return animations;
  }


  public void transform(byte transformationType) {
    super.setImage(new ImageIcon(Data.Images.Wall.lockedDoor(transformationType)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    ID = (byte) (++ID % 10);
    super.initializeLabel(new ImageIcon(Data.Images.Wall.lockedDoor(ID)));
    super.identifier = LockedDoor.TAG + ":" + ID;
  }

  public String getInfo() {
    return "This door can only be opened after obtaining the corresponding key, which then effectively turns it into a hallway.";
  }
}