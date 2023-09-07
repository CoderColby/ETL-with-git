import javax.swing.ImageIcon;


public abstract class AbstractRoomType extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;

  protected AbstractRoomType(String tag, GridCell gridCell, ImageIcon image) {
    super(tag, gridCell, AbstractRoomType.TYPE, image);
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    super.gridCell = gridCell;
    gridCell.setRoomType(this);
  }

  public static AbstractRoomType getRoomTypeByTag(String roomTypeTag, GridCell gridCell) {
    String roomTypeType = roomTypeTag.split(":")[0];
    byte startCondition = Byte.parseByte(roomTypeTag.split(":")[1]);
    
    switch (roomTypeType) {
      case Elevator.TAG:
        return new Elevator(gridCell, startCondition);
      case Filled.TAG:
        return new Filled(gridCell, startCondition);
      case Star.TAG:
        return new Star(gridCell, startCondition);
      case Target.TAG:
        return new Target(gridCell, startCondition);
      default:
        return null;
    }
  }
}