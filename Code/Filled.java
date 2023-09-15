import javax.swing.ImageIcon;


public class Filled extends AbstractRoomType {

  public static final String TAG = "Filled";
  public static final byte DEFAULT = 0;

  
  public Filled() {
    super(Filled.TAG + ":" + Filled.DEFAULT, Data.Images.RoomType.filled);
  }
  
  public Filled(GridCell gridCell, byte startCondition) {
    super(Filled.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.filled);
  }

  public void cycleOptions() {
    // nothing
  }

  @Override
  public void addSelf(GridCell gridCell, byte modifier) {
    super.addSelf(gridCell, modifier);
    for (byte i = 0; i < 4; i++)
      gridCell.setWall(AbstractWall.getWallByTag(Wall.TAG + ":" + Wall.DEFAULT, gridCell, (byte) (i % 2)), i);
  }
}