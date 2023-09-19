import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.ImageIcon;


public class AirlockDoor extends AbstractWall {

  public static final String TAG = "Airlock_Door";
  public static final byte DEFAULT = 0;
  public static final byte CLOSED = 0;
  public static final byte OPEN = 1;

  private boolean isOpen;


  public AirlockDoor() {
    super(AirlockDoor.TAG + ":" + AirlockDoor.DEFAULT, Data.Images.Wall.airlockDoor(AirlockDoor.DEFAULT));
  }

  public AirlockDoor(GridCell gridCell, byte startCondition, byte orientation) {
    super(AirlockDoor.TAG + ":" + startCondition, gridCell, Data.Images.Wall.airlockDoor(startCondition), orientation);
    isOpen = startCondition == AirlockDoor.OPEN;
  }

  public boolean canPass(String entityTag) {
    return isOpen || entityTag.equals(Player.TAG);
  }


  public ArrayList<Animation> getAnimations(String entityTag, int delay) {
    ArrayList<Animation> animations = new ArrayList<>();
    animations.add(new WallAnimation(delay + ((isOpen)? 0 : Data.Animation.playerTravelTime), this, (isOpen)? AirlockDoor.CLOSED : AirlockDoor.OPEN));
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


  public boolean requiresEnergy() {
    return !isOpen;
  }


  public void transform(byte transformationType) {
    super.setImage(new ImageIcon(Data.Images.Wall.airlockDoor(transformationType)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public void cycleOptions() {
    isOpen = !isOpen;
    super.setImage(new ImageIcon(Data.Images.Wall.airlockDoor((isOpen)? AirlockDoor.OPEN : AirlockDoor.CLOSED)).getImage());
    super.identifier = Target.TAG + ":" + ((isOpen)? AirlockDoor.OPEN : AirlockDoor.CLOSED);
  }

  public String getInfo() {
    return "This is a door with a toggleable state that requires energy to open but remains open until something passes through";
  }
}