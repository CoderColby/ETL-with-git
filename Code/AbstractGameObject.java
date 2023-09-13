import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;


public abstract class AbstractGameObject extends ImageIcon {

  protected String identifier;
  private final String TYPE;
  protected GridCell gridCell;
  protected JLabel label;
  protected Dimension dimensions;
  protected Point position;

  protected AbstractGameObject(String identifier, String imagePath, String type, Dimension dimensions) {
    super((new ImageIcon(imagePath)).getImage().getScaledInstance((int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()), Image.SCALE_FAST));
    this.identifier = identifier;
    this.dimensions = dimensions;
    // this.position = position;
    // this.label = initializeLabel();
    TYPE = type;
  }

  protected AbstractGameObject(String identifier, GridCell gridCell, String type, String imagePath, Dimension dimensions, Point position) {
    super(imagePath);
    this.identifier = identifier;
    this.gridCell = gridCell;
    this.dimensions = dimensions;
    this.position = position;
    System.out.println(position);
    this.TYPE = type;
    initializeLabel();
  }

  public String getType() {
    return TYPE;
  }

  public JLabel getLabel() {
    return label;
  }

  public Point getPosition() {
    return position;
  }

  public String getIdentifier() {
    return identifier;
  }

  public GridCell getGridCell() {
    return gridCell;
  }

  protected void initializeLabel() {
    this.label = new JLabel(this);
    int[] offset = gridCell.getPixelOffset();
    this.position.translate(offset[0], offset[1]);
    label.setLocation(this.position);
    label.setSize(this.dimensions);
    // gridCell.getGameBoard().add(label, GameBoard.ENTITY_LAYER);
  }

  // protected static ImageIcon setScale(ImageIcon image, int dimension) {
  //   image.setImage(image.getImage().getScaledInstance(dimension, dimension, Image.SCALE_FAST));
  //   return image;
  // }

  public abstract void addSelf(GridCell gridCell, byte modifier);

  public abstract void cycleOptions();
}