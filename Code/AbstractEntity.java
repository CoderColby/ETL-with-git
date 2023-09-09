import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.ArrayList;


public abstract class AbstractEntity extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;
  public static final int DIMENSION = GameBoard.ROOM_HEIGHT - 5;

  private final int AnimationDuration;

  protected AbstractEntity(String tag, GridCell gridCell, JLabel label, int AnimationDuration) {
    super(tag, gridCell, AbstractEntity.TYPE, label);
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

  protected static JLabel initializeLabel(GridCell gridCell, ImageIcon image) {
    JLabel label = new JLabel(new ImageIcon(image.getImage().getScaledInstance(AbstractEntity.DIMENSION, AbstractEntity.DIMENSION, Image.SCALE_FAST)));
    int[] rowColumnOff = gridCell.getPixelOffset();
    int placement = (GameBoard.ROOM_HEIGHT - AbstractEntity.DIMENSION) / 2;
    label.setBounds(rowColumnOff[0] + placement, rowColumnOff[1] + placement, AbstractEntity.DIMENSION, AbstractEntity.DIMENSION);
    gridCell.getGameBoard().add(label, GameBoard.ENTITY_LAYER);
    return label;
  }
  

  public static AbstractEntity getEntityByTag(String entityTag, GridCell gridCell) {
    String entityType = entityTag.split(":")[0];
    byte startCondition = Byte.parseByte(entityTag.split(":")[1]);
    
    switch (entityType) {
      case Player.TAG:
        return new Player(gridCell, startCondition);
      case SmartZombie.TAG:
        return new SmartZombie(gridCell, startCondition);
      case Zombie.TAG:
        return new Zombie(gridCell, startCondition);
      default:
        return null;
    }
  }
}