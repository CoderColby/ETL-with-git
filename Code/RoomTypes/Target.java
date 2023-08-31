


public class Target extends AbstractRoomType {

  public static final String TAG = "Target";
  public static boolean isOngoing = false;

  private boolean isGood;

  public Target(GridCell gridCell, byte startCondition) {
    super(Target.TAG + ":" + startCondition, gridCell, Data.Images.target);
    isGood = startCondition == 1;
  }
}