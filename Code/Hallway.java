import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Hallway extends AbstractWall {

  public static final String TAG = "Hallway";
  public static final byte DEFAULT = 0;


  public Hallway() {
    super(Hallway.TAG + ":" + Hallway.DEFAULT, Data.Images.Wall.hallway);
  }

  public Hallway(GridCell gridCell, byte startCondition, byte orientation) {
    super(Hallway.TAG + ":" + startCondition, gridCell, Data.Images.Wall.hallway, orientation);
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

  public boolean canPass(String entityTag) {
    return true;
  }

  public boolean requiresEnergy() {
    return false;
  }


  public void transform(byte transformationType) {
    // empty; should never be called
  }

  public void cycleOptions() {
    // nothing
  }
}