import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;


public class Eraser extends AbstractItem {

  public static final String TAG = "Eraser";

  public Eraser() {
    super(Eraser.TAG, new GridCell(), new JLabel(new ImageIcon(Data.Images.Item.eraser.getImage().getScaledInstance(AbstractItem.DIMENSION, AbstractItem.DIMENSION, Image.SCALE_FAST))));
  }

  public void cycleOptions() {
    // nothing
  }
}