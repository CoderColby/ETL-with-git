import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.util.ArrayList;


public abstract class AbstractWall extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forWall;

  protected boolean isPowered;
  protected byte orientation;

  protected AbstractWall(String tag, GridCell gridCell, JLabel label, byte orientation) {
    super(tag, gridCell, AbstractWall.TYPE, label);
    this.orientation = orientation;
  }

  public abstract ArrayList<Animation> getAnimations(String entityTag, int delay);

  public abstract ArrayList<Animation> setPower(boolean isPowered, int delay);

  public abstract boolean canPass(String entityTag);

  public abstract int addDelayInMillis();

  public abstract boolean requiresEnergy();

  public abstract void transform(byte transformationType);

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setWall(AbstractWall.getWallByTag(this.getIdentifier(), gridCell, modifier), modifier);
  }

  protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image, byte orientation) {
    if (orientation == 1)
      image = Data.Images.rotateIcon(image, 90);
    int[] rowColumnSize = new int[] {(orientation == 0)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT, (orientation == 1)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT};
    JLabel label = new JLabel(new ImageIcon(image.getImage().getScaledInstance(rowColumnSize[0], rowColumnSize[1], Image.SCALE_FAST)));
    int[] rowColumnOff = gridCell.getPixelOffset();
    label.setBounds(rowColumnOff[0] + (1 - orientation) * GameBoard.ROOM_HEIGHT, rowColumnOff[1] + orientation * GameBoard.ROOM_HEIGHT, rowColumnSize[0], rowColumnSize[1]);
    gridCell.getGameBoard().add(label, GameBoard.WALL_LAYER);
    return label;
  }

  public static AbstractWall getWallByTag(String wallTag, GridCell gridCell, byte orientation) {
    String wallType = wallTag.split(":")[0];
    byte startCondition = Byte.parseByte(wallTag.split(":")[1]);
    
    switch (wallType) {
      case AirlockDoor.TAG:
        return new AirlockDoor(gridCell, startCondition, orientation);
      case DetectionDoor.TAG:
        return new DetectionDoor(gridCell, startCondition, orientation);
      case Door.TAG:
        return new Door(gridCell, startCondition, orientation);
      case Hallway.TAG:
        return new Hallway(gridCell, startCondition, orientation);
      case LockedDoor.TAG:
        return new LockedDoor(gridCell, startCondition, orientation);
      case OnceDoor.TAG:
        return new OnceDoor(gridCell, startCondition, orientation);
      case PowerDoor.TAG:
        return new PowerDoor(gridCell, startCondition, orientation);
      case Wall.TAG:
        return new Wall(gridCell, startCondition, orientation);
      default:
        return null;
    }
  }
}