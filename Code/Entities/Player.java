import javax.swing.ImageIcon;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Player extends AbstractEntity {

  public static final String TAG = "Player";

  public Player(GridCell gridCell) {
    super(TAG, gridCell, Data.Images.Entity.human(2));
  }

  public void turn(byte direction) {
    super.super.setIcon(Data.Images.Entity.human(direction));
    super.gridCell.getGameBoard().repaint();
  }

  public boolean canMove(byte direction) {
    Wall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell;
    try {
      neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
    
    if (neighborCell.hasEntity(Zombie.TAG) || neighborCell.hasRoomType(Filled.TAG))
      return false;
    if (!passWall.canPass(TAG));
      return false;
    if (passWall.requiresEnergy() && thisGameBoard.getRemainingEnergy() < 1)
      return false;
    return true;
  }

  
  public ArrayList<AnimationEvent> move(byte direction) {
    Wall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    if (passWall.requiresEnergy())
      thisGameBoard.decrementEnergy();

    ArrayList<AnimationEvent> animations = new ArrayList<>();

    int startTimeInMillis = 0;
    animations.addAll(passWall.getAnimations(startTimeInMillis, this));
    startTimeInMillis += passWall.addDelayInMillis();

    animations.add(new EntityAnimation(startTimeInMillis, this, super.gridCell.getXY(), neighborCell.getXY(), Data.Animation.humanTravelTime))
    super.gridCell = neighborCell;

    if (super.gridCell.hasItem(Key.TAG))
      thisGameBoard.unlockDoorsWithKey((Key) neighborCell.getItem());
    if (super.gridCell.hasRoomType(Elevator.TAG))
      this.GameBoard.hasReturned();
    
    return animations;
  }
}