import javax.swing.ImageIcon;

public abstract class AbstractItem extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;

  protected AbstractItem(String tag, GridCell gridCell, ImageIcon image) {
    super(tag, gridCell, AbstractItem.TYPE, image);
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    super.gridCell = gridCell;
    gridCell.setItem(this);
  }

  public static AbstractItem getItemByTag(String itemTag, GridCell gridCell) {
    String itemType = itemTag.split(":")[0];
    byte startCondition = Byte.parseByte(itemTag.split(":")[1]);
    
    switch (itemType) {
      case Battery.TAG:
        return new Battery(gridCell, startCondition);
      case Key.TAG:
        return new Key(gridCell, startCondition);
    }
  }
}