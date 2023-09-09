


public class Elevator extends AbstractRoomType {

  public static final String TAG = "Elevator";
  public static final byte DEFAULT = 0;

  
  public Elevator() {
    super(Elevator.TAG + ":" + Elevator.DEFAULT, new GridCell(), AbstractItem.initializeLabel(new GridCell(), Data.Images.RoomType.elevator));
  }
  
  public Elevator(GridCell gridCell, byte startCondition) {
    super(Elevator.TAG + ":" + startCondition, gridCell, AbstractRoomType.initializeLabel(gridCell, Data.Images.RoomType.elevator));
  }

  public void cycleOptions() {
    // nothing
  }
}