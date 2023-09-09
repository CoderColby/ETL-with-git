import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;


public abstract class AbstractRoomType extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT;

  protected AbstractRoomType(String tag, GridCell gridCell, JLabel label) {
    super(tag, gridCell, AbstractRoomType.TYPE, label);
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setRoomType(AbstractRoomType.getRoomTypeByTag(this.getIdentifier(), gridCell));
  }

  protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
    JLabel label = new JLabel(new ImageIcon(image.getImage().getScaledInstance(AbstractRoomType.DIMENSION, AbstractRoomType.DIMENSION, Image.SCALE_FAST)));
    int[] rowColumnOff = gridCell.getPixelOffset();
    int placement = (GameBoard.ROOM_HEIGHT - AbstractRoomType.DIMENSION) / 2;
    label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractRoomType.DIMENSION, AbstractRoomType.DIMENSION);
    gridCell.getGameBoard().add(label, GameBoard.ROOMTYPE_LAYER);
    return label;
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