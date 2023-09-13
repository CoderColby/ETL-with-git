import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;


public class Eraser extends AbstractItem {

  public static final String TAG = "Eraser";

  public Eraser() {
    super(Eraser.TAG, Data.Images.Item.eraser);
  }

  public void cycleOptions() {
    // nothing
  }
}