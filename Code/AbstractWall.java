import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;


public abstract class AbstractWall extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forWall;

  protected boolean isPowered;
  protected byte orientation;

  protected AbstractWall(String tag, String imagePath) {
    super(tag, imagePath, AbstractWall.TYPE, new Dimension(GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT));
  }

  protected AbstractWall(String tag, GridCell gridCell, String imagePath, byte orientation) {
    super(tag, gridCell, AbstractWall.TYPE, imagePath, new Dimension((orientation == 0)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT, (orientation == 1)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT), new Point((1 - orientation) * GameBoard.ROOM_HEIGHT, orientation * GameBoard.ROOM_HEIGHT));
    this.orientation = orientation;
    initializeLabel(new ImageIcon(imagePath));
  }

  protected void initializeLabel(ImageIcon image) {
    if (orientation == (byte) 1)
      image = Data.Images.rotateIcon(image);
    super.initializeLabel(image);
  }

  
  @Override
  public void setImage(Image image) {
    if (orientation == (byte) 1)
      image = Data.Images.rotateIcon(new ImageIcon(image)).getImage();
    super.setImage(image);
  }

  public abstract ArrayList<Animation> getAnimations(String entityTag, int delay);

  public abstract ArrayList<Animation> setPower(boolean isPowered, int delay);

  public abstract boolean canPass(String entityTag);

  public abstract int addDelayInMillis();

  public abstract boolean requiresEnergy();

  public abstract void transform(byte transformationType);

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setWall(AbstractWall.getWallByTag(super.identifier, gridCell, modifier), modifier);
  }

  // protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image, byte orientation) {
  //   // if (orientation == 1)
  //   //   image = Data.Images.rotateIcon(image, 90);
  //   int[] rowColumnSize = new int[] {(orientation == 0)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT, (orientation == 1)? GameBoard.WALL_THICKNESS : GameBoard.ROOM_HEIGHT};
  //   JLabel label = new JLabel(new ImageIcon(image.getImage().getScaledInstance(rowColumnSize[0], rowColumnSize[1], Image.SCALE_FAST)));
  //   int[] rowColumnOff = gridCell.getPixelOffset();
  //   label.setBounds(rowColumnOff[0] + (1 - orientation) * GameBoard.ROOM_HEIGHT, rowColumnOff[1] + orientation * GameBoard.ROOM_HEIGHT, rowColumnSize[0], rowColumnSize[1]);
  //   gridCell.getGameBoard().add(label, GameBoard.WALL_LAYER);
  //   return label;
  // }

  public static AbstractWall getWallByTag(String wallTag, GridCell gridCell, byte orientation) {
    String wallType = wallTag.split(":")[0];
    
    switch (wallType) {
      case AirlockDoor.TAG:
        return new AirlockDoor(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case DetectionDoor.TAG:
        return new DetectionDoor(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case Door.TAG:
        return new Door(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case Hallway.TAG:
        return new Hallway(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case LockedDoor.TAG:
        return new LockedDoor(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case OnceDoor.TAG:
        return new OnceDoor(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case PowerDoor.TAG:
        return new PowerDoor(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      case Wall.TAG:
        return new Wall(gridCell, Byte.parseByte(wallTag.split(":")[1]), orientation);
      default:
        return new Hallway(gridCell, Hallway.DEFAULT, orientation);
    }
  }
}