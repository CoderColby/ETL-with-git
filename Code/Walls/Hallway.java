import java.util.ArrayList;


public class Hallway extends AbstractWall {

  public static final String TAG = "Hallway";

  public Hallway(GridCell gridCell, byte startCondition) {
    super(Hallway.TAG + ":" + startCondition, gridCell, new ImageIcon());
  }

  public ArrayList<Animation> getAnimations(int delay, AbstractEntity entity) {
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

  public void requiresPower() {
    return false;
  }


  public void transform(byte transformationType) {
    // empty; should never be called
  }

  public void cycleOptions() {
    // nothing
  }
}