import javax.swing.ImageIcon;


public class Star extends AbstractRoomType {

  public static final String TAG = "Star";
  public static final byte DEFAULT = 0;

  
  public Star() {
    super(Star.TAG + ":" + Star.DEFAULT, Data.Images.RoomType.star);
  }
  
  public Star(GridCell gridCell, byte startCondition) {
    super(Star.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.star);
  }

  public void cycleOptions() {
    // nothing
  }
}