import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Door extends AbstractWall {

  public static final String TAG = "Door";
  public static final byte DEFAULT = 0;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;


  public Door() {
    super(Door.TAG + ":" + Door.DEFAULT, Data.Images.Wall.door(Door.DEFAULT));
  }

  public Door(GridCell gridCell, byte startCondition, byte orientation) {
    super(Door.TAG + ":" + startCondition, gridCell, Data.Images.Wall.door(Door.CLOSED), orientation);
    isOpen = true;
  }

  public boolean canPass(String entityTag) {
    return entityTag.equals(Player.TAG) || super.isPowered;
  }


  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    animations.add(new WallAnimation(delay, this, Door.OPEN));
    animations.add(new WallAnimation(delay + Data.Animation.playerTravelTime, this, Door.CLOSED));
    return animations;
  }


  public int addDelayInMillis() {
    return (isOpen)? 0 : 100;
  }


  public ArrayList<Animation> setPower(boolean isPowered, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    if (super.isPowered == isPowered)
      return animations;
    
    super.isPowered = isPowered;
    isOpen = isPowered;
    animations.add(new WallAnimation(delay, this, (isOpen)? Door.OPEN : Door.CLOSED));
    return animations;
  }


  public boolean requiresEnergy() {
    return true;
  }


  public void transform(byte transformationType) {
    super.setImage(new ImageIcon(Data.Images.Wall.door(transformationType)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    // nothing
  }
}