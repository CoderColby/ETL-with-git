


public class Filled extends AbstractRoomType {

  public static final String TAG = "Filled";

  public Filled(GridCell gridCell, byte startCondition) {
    super(Filled.TAG + ":" + startCondition, gridCell, Data.Images.filled);
  }

  public void cycleOptions() {
    // nothing
  }
}