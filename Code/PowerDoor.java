import java.util.ArrayList;


public class PowerDoor extends AbstractWall {

  public static final String TAG = "Power_Door";
  public static final byte DEFAULT = 0;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;


  public PowerDoor() {
    super(PowerDoor.TAG + ":" + PowerDoor.DEFAULT, new GridCell(), AbstractWall.initializeLabel(new GridCell(), Data.Images.Wall.powerDoor(PowerDoor.DEFAULT), (byte) 0), (byte) 0);
  }

  public PowerDoor(GridCell gridCell, byte startCondition, byte orientation) {
    super(PowerDoor.TAG + ":" + startCondition, gridCell, AbstractWall.initializeLabel(gridCell, Data.Images.Wall.powerDoor(PowerDoor.CLOSED), orientation), orientation);
    isOpen = false;
  }

  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    return new ArrayList<Animation>();
  }


  public int addDelayInMillis() {
    return 0;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    super.isPowered = isPowered;
    isOpen = isPowered;
    ArrayList<Animation> animations = new ArrayList<>();
    animations.add(new WallAnimation(delay, this, (isPowered)? PowerDoor.OPEN : PowerDoor.CLOSED));
    return animations;
  }

  public boolean canPass(String entityTag) {
    return isOpen;
  }

  public boolean requiresEnergy() {
    return false;
  }


  public void transform(byte transformationType) {
    super.setImage(Data.Images.Wall.powerDoor(transformationType).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    // nothing
  }
}