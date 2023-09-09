


public class Star extends AbstractRoomType {

  public static final String TAG = "Star";
  public static final byte DEFAULT = 0;

  
  public Star() {
    super(Star.TAG + ":" + Star.DEFAULT, new GridCell(), AbstractItem.initializeLabel(new GridCell(), Data.Images.RoomType.star));
  }
  
  public Star(GridCell gridCell, byte startCondition) {
    super(Star.TAG + ":" + startCondition, gridCell, AbstractRoomType.initializeLabel(gridCell, Data.Images.RoomType.star));
  }

  public void cycleOptions() {
    // nothing
  }
}