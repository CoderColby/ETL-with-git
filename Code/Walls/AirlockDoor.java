import java.util.ArrayList;


public class AirlockDoor extends AbstracWall {

  public static final String TAG = "Airlock_Door";
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;

  public AirlockDoor(GridCell gridCell, byte startCondition) {
    super(AirlockDoor.TAG + ":" + startCondition, gridCell, Data.Images.airlockDoor(startCondition));
    isOpen = startCondition == AirlockDoor.OPEN;
  }

  public boolean canPass(String entityTag) {
    return isOpen || entityTag.equals(Player.TAG);
  }


  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    ArrayList<Animation> animations = new ArrayLsit<>();
    animations.add(new WallAnimation(delay + ((isOpen)? 0 : Data.Animation.humanTravelTime), this, (isOpen)? AirlockDoor.CLOSED : AirlockDoor.OPEN));
    isOpen = !isOpen;
    return animations;
  }


  public int addDelayInMillis() {
    return (isOpen)? 0 : 100;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    super.isPowered = isPowered;
    return new ArrayList<Animation>();
  }


  public boolean requiresPower() {
    return !isOpen;
  }


  public void transform(byte transformationType) {
    super.image = Data.Images.airlockDoor(transformationType).getImage();
    super.gridCell.repaint();
  }
}