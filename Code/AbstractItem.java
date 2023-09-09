import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;


public abstract class AbstractItem extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT - 10;

  protected AbstractItem(String tag, GridCell gridCell, JLabel label) {
    super(tag, gridCell, AbstractItem.TYPE, label);
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setItem(AbstractItem.getItemByTag(this.getIdentifier(), gridCell));
  }

  protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
    JLabel label = new JLabel(new ImageIcon(image.getImage().getScaledInstance(AbstractItem.DIMENSION, AbstractItem.DIMENSION, Image.SCALE_FAST)));
    int[] rowColumnOff = gridCell.getPixelOffset();
    int placement = (GameBoard.ROOM_HEIGHT - AbstractItem.DIMENSION) / 2;
    label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractItem.DIMENSION, AbstractItem.DIMENSION);
    gridCell.getGameBoard().add(label, GameBoard.ITEM_LAYER);
    return label;
  }

  public static AbstractItem getItemByTag(String itemTag, GridCell gridCell) {
    String itemType = itemTag.split(":")[0];
    byte startCondition = Byte.parseByte(itemTag.split(":")[1]);
    
    switch (itemType) {
      case Battery.TAG:
        return new Battery(gridCell, startCondition);
      case Key.TAG:
        return new Key(gridCell, startCondition);
      default:
        return null;
    }
  }
}