


public class Filled extends AbstractRoomType {

  public static final String TAG = "Filled";
  public static final byte DEFAULT = 0;

  public Filled(GridCell gridCell, byte startCondition) {
    super(Filled.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.filled);
  }

  public void cycleOptions() {
    // nothing
  }
}