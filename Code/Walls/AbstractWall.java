
public abstract class AbstractWall extends AbstractGameObject {

  protected AbstractWall(String tag, ImageIcon image) {
    super(tag, image);
  }

  public abstract boolean canPass(String entityTag);
}