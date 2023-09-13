import java.util.ArrayList;
import javax.swing.ImageIcon;


public class OnceDoor extends AbstractWall {

  public static final String TAG = "Once_Door";
  public static final byte DEFAULT = 1;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;


  public OnceDoor() {
    super(OnceDoor.TAG + ":" + OnceDoor.DEFAULT, Data.Images.Wall.onceDoor(OnceDoor.DEFAULT));
  }

  public OnceDoor(GridCell gridCell, byte startCondition, byte orientation) {
    super(OnceDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.onceDoor(startCondition), orientation);
    isOpen = startCondition == OnceDoor.OPEN;
  }

  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    animations.add(new WallAnimation(delay + Data.Animation.playerTravelTime, this, OnceDoor.CLOSED));
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

  public boolean requiresEnergy() {
    return false;
  }


  public void transform(byte transformationType) {
    super.setImage(new ImageIcon(Data.Images.Wall.onceDoor(transformationType)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    // nothing
  }
}