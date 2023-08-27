import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public abstract class AbstractEntity extends AbstractGameObject {

  protected GridCell gridCell;

  protected AbstractEntity(String tag, GridCell gridCell, ImageIcon image) {
    super(tag, image);
    this.gridCell = gridCell;
  }
  
  public GridCell getGridCell() {
    return gridCell;
  }

  public String getType() {
    return Data.Utilities.forRoom;
  }
}