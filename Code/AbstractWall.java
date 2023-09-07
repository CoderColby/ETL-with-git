import java.util.ArrayList;
import javax.swing.ImageIcon;


public abstract class AbstractWall extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forWall;

  protected boolean isPowered;

  protected AbstractWall(String tag, GridCell gridCell, ImageIcon image) {
    super(tag, gridCell, AbstractWall.TYPE, image);
  }

  public abstract ArrayList<Animation> getAnimations(String entityTag, int delay);

  public abstract ArrayList<Animation> setPower(boolean isPowered, int delay);

  public abstract boolean canPass(String entityTag);

  public abstract int addDelayInMillis();

  public abstract boolean requiresEnergy();

  public abstract void transform(byte transformationType);

  public void addSelf(GridCell gridCell, byte modifier) {
    super.gridCell = gridCell;
    gridCell.setWall(this, modifier);
  }

  public static AbstractWall getWallByTag(String wallTag, GridCell gridCell) {
    String wallType = wallTag.split(":")[0];
    byte startCondition = Byte.parseByte(wallTag.split(":")[1]);
    
    switch (wallType) {
      case AirlockDoor.TAG:
        return new AirlockDoor(gridCell, startCondition);
      case DetectionDoor.TAG:
        return new DetectionDoor(gridCell, startCondition);
      case Door.TAG:
        return new Door(gridCell, startCondition);
      case Hallway.TAG:
        return new Hallway(gridCell, startCondition);
      case LockedDoor.TAG:
        return new LockedDoor(gridCell, startCondition);
      case OnceDoor.TAG:
        return new OnceDoor(gridCell, startCondition);
      case PowerDoor.TAG:
        return new PowerDoor(gridCell, startCondition);
      case Wall.TAG:
        return new Wall(gridCell, startCondition);
      default:
        return null;
    }
  }
}