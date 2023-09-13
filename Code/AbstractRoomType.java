import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;


public abstract class AbstractRoomType extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT;
  public static final int POSITION = (GameBoard.ROOM_HEIGHT - AbstractRoomType.DIMENSION) / 2;

  protected AbstractRoomType(String tag, String imagePath) {
    super(tag, imagePath, AbstractRoomType.TYPE, new Dimension(AbstractRoomType.DIMENSION, AbstractRoomType.DIMENSION));
  }

  protected AbstractRoomType(String tag, GridCell gridCell, String imagePath) {
    super(tag, gridCell, AbstractRoomType.TYPE, imagePath, new Dimension(AbstractItem.DIMENSION, AbstractItem.DIMENSION), new Point(AbstractRoomType.POSITION, AbstractRoomType.POSITION));
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setRoomType(AbstractRoomType.getRoomTypeByTag(this.getIdentifier(), gridCell));
  }

  // protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
  //   JLabel label = new JLabel(AbstractGameObject.setScale(image, AbstractRoomType.DIMENSION));
  //   int[] rowColumnOff = gridCell.getPixelOffset();
  //   int placement = (GameBoard.ROOM_HEIGHT - AbstractRoomType.DIMENSION) / 2;
  //   label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractRoomType.DIMENSION, AbstractRoomType.DIMENSION);
  //   gridCell.getGameBoard().add(label, GameBoard.ROOMTYPE_LAYER);
  //   return label;
  // }

  public static AbstractRoomType getRoomTypeByTag(String roomTypeTag, GridCell gridCell) {
    String roomTypeType = roomTypeTag.split(":")[0];
    
    switch (roomTypeType) {
      case Elevator.TAG:
        return new Elevator(gridCell, Byte.parseByte(roomTypeTag.split(":")[1]));
      case Filled.TAG:
        return new Filled(gridCell, Byte.parseByte(roomTypeTag.split(":")[1]));
      case Star.TAG:
        return new Star(gridCell, Byte.parseByte(roomTypeTag.split(":")[1]));
      case Target.TAG:
        return new Target(gridCell, Byte.parseByte(roomTypeTag.split(":")[1]));
      default:
        return null;
    }
  }
}