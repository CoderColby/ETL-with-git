import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;


public abstract class AbstractItem extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT - 20;
  public static final int POSITION = (GameBoard.ROOM_HEIGHT - AbstractItem.DIMENSION) / 2;

  protected AbstractItem(String tag, String imagePath) {
    super(tag, imagePath, AbstractItem.TYPE, new Dimension(AbstractItem.DIMENSION, AbstractItem.DIMENSION));
  }

  protected AbstractItem(String tag, GridCell gridCell, String imagePath) {
    super(tag, gridCell, AbstractItem.TYPE, imagePath, new Dimension(AbstractItem.DIMENSION, AbstractItem.DIMENSION), new Point(AbstractItem.POSITION, AbstractItem.POSITION));
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setItem(AbstractItem.getItemByTag(super.identifier, gridCell));
  }

  // protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
  //   JLabel label = new JLabel(AbstractGameObject.setScale(image, AbstractItem.DIMENSION));
  //   int[] rowColumnOff = gridCell.getPixelOffset();
  //   int placement = (GameBoard.ROOM_HEIGHT - AbstractItem.DIMENSION) / 2;
  //   label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractItem.DIMENSION, AbstractItem.DIMENSION);
  //   gridCell.getGameBoard().add(label, GameBoard.ITEM_LAYER);
  //   return label;
  // }

  public static AbstractItem getItemByTag(String itemTag, GridCell gridCell) {
    String itemType = itemTag.split(":")[0];
    
    switch (itemType) {
      case Battery.TAG:
        return new Battery(gridCell, Byte.parseByte(itemTag.split(":")[1]));
      case Key.TAG:
        return new Key(gridCell, Byte.parseByte(itemTag.split(":")[1]));
      default:
        return null;
    }
  }
}