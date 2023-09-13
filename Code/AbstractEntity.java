import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;


public abstract class AbstractEntity extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT - 5;
  public static final int POSITION = (GameBoard.ROOM_HEIGHT - AbstractEntity.DIMENSION) / 2;

  private final int AnimationDuration;

  protected AbstractEntity(String tag, String imagePath) {
    super(tag, imagePath, AbstractEntity.TYPE, new Dimension(AbstractEntity.DIMENSION, AbstractEntity.DIMENSION));
    AnimationDuration = 0;
  }

  protected AbstractEntity(String tag, GridCell gridCell, String imagePath, int AnimationDuration) {
    super(tag, gridCell, AbstractEntity.TYPE, imagePath, new Dimension(AbstractEntity.DIMENSION, AbstractEntity.DIMENSION), new Point(AbstractEntity.POSITION, AbstractEntity.POSITION));
    this.AnimationDuration = AnimationDuration;
  }
  
  public GridCell getGridCell() {
    return gridCell;
  }

  public int getAnimationDuration() {
    return AnimationDuration;
  }

  protected double getDistanceFromPlayer() {
    int[] hereCoords = gridCell.getCoordinates();
    int[] playerCoords = gridCell.getGameBoard().getPlayerLocation();
    return Math.sqrt(Math.pow(hereCoords[0] - playerCoords[0], 2) + Math.pow(hereCoords[1] - playerCoords[1], 2));
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    gridCell.setEntity(AbstractEntity.getEntityByTag(this.getIdentifier(), gridCell));
  }

  // protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
  //   JLabel label = new JLabel(AbstractGameObject.setScale(image, AbstractEntity.DIMENSION));
  //   int[] rowColumnOff = gridCell.getPixelOffset();
  //   int placement = (GameBoard.ROOM_HEIGHT - AbstractEntity.DIMENSION) / 2;
  //   label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractEntity.DIMENSION, AbstractEntity.DIMENSION);
  //   gridCell.getGameBoard().add(label, GameBoard.ENTITY_LAYER);
  //   return label;
  // }
  

  public static AbstractEntity getEntityByTag(String entityTag, GridCell gridCell) {
    String entityType = entityTag.split(":")[0];
    
    switch (entityType) {
      case Player.TAG:
        return new Player(gridCell, Byte.parseByte(entityTag.split(":")[1]));
      case SmartZombie.TAG:
        return new SmartZombie(gridCell, Byte.parseByte(entityTag.split(":")[1]));
      case Zombie.TAG:
        return new Zombie(gridCell, Byte.parseByte(entityTag.split(":")[1]));
      default:
        return null;
    }
  }
}