import java.util.ArrayList;
import javax.swing.ImageIcon;


public class DetectionDoor extends AbstractWall {

  public static final String TAG = "Detection_Door";
  public static final byte DEFAULT = 1;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;


  public DetectionDoor() {
    super(DetectionDoor.TAG + ":" + DetectionDoor.DEFAULT, Data.Images.Wall.detectionDoor(DetectionDoor.DEFAULT));
  }

  public DetectionDoor(GridCell gridCell, byte startCondition, byte orientation) {
    super(DetectionDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.detectionDoor(startCondition), orientation);
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
    super.setImage(new ImageIcon(Data.Images.Wall.detectionDoor(transformationType)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    // nothing
  }

  public String getInfo() {
    return "This is a door acts similar to a once-door but only torwards zombies. The player has no effect on the door, but it will permanently close once a zombie passes through.";
  }
}