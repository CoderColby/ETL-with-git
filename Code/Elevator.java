


public class Elevator extends AbstractRoomType {

  public static final String TAG = "Elevator";
  public static final byte DEFAULT = 0;

  public Elevator(GridCell gridCell, byte startCondition) {
    super(Elevator.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.elevator);
  }

  public void cycleOptions() {
    // nothing
  }
}