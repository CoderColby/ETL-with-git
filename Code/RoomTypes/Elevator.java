


public class Elevator extends AbstractRoomType {

  public static final String TAG = "Elevator";

  public Elevator(GridCell gridCell, byte startCondition) {
    super(Elevator.TAG + ":" + startCondition, gridCell, Data.Images.elevator);
  }

  public void cycleOptions() {
    // nothing
  }
}