


public class Filled extends AbstractRoomType {

  public static final String TAG = "Filled";
  public static final byte DEFAULT = 0;

  
  public Filled() {
    super(Filled.TAG + ":" + Filled.DEFAULT, new GridCell(), AbstractItem.initializeLabel(new GridCell(), Data.Images.RoomType.filled));
  }
  
  public Filled(GridCell gridCell, byte startCondition) {
    super(Filled.TAG + ":" + startCondition, gridCell, AbstractRoomType.initializeLabel(gridCell, Data.Images.RoomType.filled));
  }

  public void cycleOptions() {
    // nothing
  }
}