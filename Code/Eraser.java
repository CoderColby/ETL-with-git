

public class Eraser extends AbstractItem {

  public static final String TAG = "Eraser";

  public Eraser() {
    super(Eraser.TAG, new GridCell(), Data.Images.Item.eraser);
  }

  public void cycleOptions() {
    // nothing
  }
}