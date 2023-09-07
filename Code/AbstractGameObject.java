import javax.swing.ImageIcon;

public abstract class AbstractGameObject extends ImageIcon {

  protected String identifier;
  private final String TYPE;
  protected GridCell gridCell;
  protected JLabel label;

  protected AbstractGameObject(String identifier, GridCell gridCell, String type, ImageIcon image, JLabel label) {
    super(image.getImage());
    this.identifier = identifier;
    this.gridCell = gridCell;
    this.TYPE = type;
    this.label = label;
  }

  public String getType() {
    return TYPE;
  }

  public JLabel getLabel() {
    return label;
  }

  public String getIdentifier() {
    return identifier;
  }

  public GridCell getGridCell() {
    return gridCell;
  }

  public abstract void addSelf(GridCell gridCell, byte modifier);

  public abstract void cycleOptions();
}