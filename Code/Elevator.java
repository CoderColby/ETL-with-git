import javax.swing.ImageIcon;


public class Elevator extends AbstractRoomType {

  public static final String TAG = "Elevator";
  public static final byte DEFAULT = 0;

  
  public Elevator() {
    super(Elevator.TAG + ":" + Elevator.DEFAULT, Data.Images.RoomType.elevator);
  }
  
  public Elevator(GridCell gridCell, byte startCondition) {
    super(Elevator.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.elevator);
  }

  public void cycleOptions() {
    // nothing
  }

  @Override
  public void addSelf(GridCell gridCell, byte modifier) {
    super.addSelf(gridCell, modifier);
    gridCell.setEntity(AbstractEntity.getEntityByTag(Player.TAG + ":" + 1, gridCell));
  }

  public String getInfo() {
    return "This is entry and exit point for the player to finish the level after activating all targets.";
  }
}