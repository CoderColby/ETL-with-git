import javax.swing.ImageIcon;

public class AbstractGameObject extends ImageIcon {

  private final String TAG;

  protected AbstractGameObject(String tag, ImageIcon image) {
    super(image.getImage());
    this.TAG = tag;
  }

  private abstract String getType();

  private String getTag() {
    return TAG;
  }
}