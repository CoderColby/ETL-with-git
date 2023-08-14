import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public abstract class AbstractEntity extends JLabel {

  private final String TAG;
  protected GridCell gridCell;

  protected AbstractEntity(String TAG, GridCell gridCell, ImageIcon image) {
    super(image);
    this.TAG = TAG;
    this.gridCell = gridCell;
  }
  
  public GridCell getGridCell() {
    return gridCell;
  }
  
  public String getTag() {
    return TAG;
  }
}