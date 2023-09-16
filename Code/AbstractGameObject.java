import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane; // remove
import java.awt.Image;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Point;


public abstract class AbstractGameObject extends ImageIcon {

  protected String identifier;
  private final String TYPE;
  protected GridCell gridCell;
  protected JLabel label;
  protected Dimension dimensions;
  protected Point gridCellPosition;
  protected Point gameBoardPosition;

  protected AbstractGameObject(String identifier, String imagePath, String type, Dimension dimensions) {
    super((new ImageIcon(imagePath)).getImage().getScaledInstance((int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()), Image.SCALE_FAST));
    this.identifier = identifier;
    this.dimensions = dimensions;
    // this.position = position;
    // this.label = initializeLabel();
    TYPE = type;
  }

  protected AbstractGameObject(String identifier, GridCell gridCell, String type, String imagePath, Dimension dimensions, Point position) {
    super();
    this.identifier = identifier;
    this.gridCell = gridCell;
    this.dimensions = dimensions;
    this.gridCellPosition = position;
    this.TYPE = type;
    if (type.equals(Data.Utilities.forRoom))
      initializeLabel(new ImageIcon(imagePath));
  }

  public String getType() {
    return TYPE;
  }

  public JLabel getLabel() {
    return label;
  }

  public Point getGridCellPosition() {
    return gridCellPosition;
  }

  public String getIdentifier() {
    return identifier;
  }

  public GridCell getGridCell() {
    return gridCell;
  }

  protected void initializeLabel(ImageIcon thisImage) {
    super.setImage(thisImage.getImage().getScaledInstance((int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()), Image.SCALE_FAST));
    this.label = new JLabel(this);
    int[] offset = gridCell.getPixelOffset();
    this.gameBoardPosition = new Point(this.gridCellPosition);
    this.gameBoardPosition.translate(offset[0], offset[1]);
    label.setLocation(this.gameBoardPosition);
    label.setSize(this.dimensions);

    // if (TYPE == Data.Utilities.forWall)
    //   JOptionPane.showMessageDialog(null, "first", "Icon", JOptionPane.INFORMATION_MESSAGE, thisImage);
    //   JOptionPane.showMessageDialog(null, "second", "Icon", JOptionPane.INFORMATION_MESSAGE, this);

  }

  // protected static ImageIcon setScale(ImageIcon image, int dimension) {
  //   image.setImage(image.getImage().getScaledInstance(dimension, dimension, Image.SCALE_FAST));
  //   return image;
  // }

  public abstract void addSelf(GridCell gridCell, byte modifier);

  public abstract void cycleOptions();

  public abstract String getInfo();
}