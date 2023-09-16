import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Wall extends AbstractWall {

  public static final String TAG = "Wall";
  public static final byte DEFAULT = 0;


  public Wall() {
    super(Wall.TAG + ":" + Wall.DEFAULT, Data.Images.Wall.wall);
  }

  public Wall(GridCell gridCell, byte startCondition, byte orientation) {
    super(Wall.TAG + ":" + startCondition, gridCell, Data.Images.Wall.wall, orientation);
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
    return false;
  }

  public boolean requiresEnergy() {
    return false;
  }


  public void transform(byte transformationType) {
    // should never get here
  }

  public void cycleOptions() {
    // nothing
  }

  public String getInfo() {
    return "This is a just a wall, nothing can pass.";
  }
}