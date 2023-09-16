import javax.swing.ImageIcon;


public class Star extends AbstractRoomType {

  public static final String TAG = "Star";
  public static final byte DEFAULT = 0;

  
  public Star() {
    super(Star.TAG + ":" + Star.DEFAULT, Data.Images.RoomType.star);
  }
  
  public Star(GridCell gridCell, byte startCondition) {
    super(Star.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.star);
  }

  public void cycleOptions() {
    // nothing
  }

  public String getInfo() {
    return "Collecting all of the stars in a level means that the player has mastered that level; acts as a small bonus.";
  }
}