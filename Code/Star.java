


public class Star extends AbstractRoomType {

  public static final String TAG = "Star";

  public Star(GridCell gridCell, byte startCondition) {
    super(Star.TAG + ":" + startCondition, gridCell, Data.Images.star);
  }

  public void cycleOptions() {
    // nothing
  }
}