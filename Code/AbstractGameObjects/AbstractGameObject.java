import javax.swing.ImageIcon;

public abstract class AbstractGameObject extends ImageIcon {

  private String identifier;
  private final String TYPE;
  protected final GridCell gridCell

  protected AbstractGameObject(String identifier, GridCell gridCell, String type, ImageIcon image) {
    super(image.getImage());
    this.identifier = identifier;
    this.gridCell = gridCell;
    this.TYPE = type;
  }

  public String getType() {
    return TYPE;
  }

  public String getIdentifier() {
    return identifier;
  }

  public abstract void addSelf(GridCell gridCell, byte modifier);
}