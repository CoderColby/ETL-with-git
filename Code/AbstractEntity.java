import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public abstract class AbstractEntity extends AbstractGameObject {

  public static final String TYPE = Data.Utilities.forRoom;

  protected AbstractEntity(String tag, GridCell gridCell, ImageIcon image) {
    super(tag, gridCell, AbstractEntity.TYPE, image);
  }
  
  public GridCell getGridCell() {
    return gridCell;
  }

  protected double getDistanceFromPlayer() {
    int[] hereCoords = gridCell.getCoordinates();
    int[] playerCoords = gridCell.getGameBoard().getPlayerLocation();
    return Math.sqrt(Math.pow(hereCoords[0] - playerCoords[0], 2) + Math.pow(hereCoords[1] - playerCoords[1], 2));
  }

  public void addSelf(GridCell gridCell, byte modifier) {
    super.gridCell = gridCell;
    gridCell.setEntity(this);
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
    }
  }
}