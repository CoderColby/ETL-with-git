import javax.swing.ImageIcon;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Player extends AbstractEntity {

  public static final String TAG = "Player";
  public static final byte HEALTHY = 0;
  public static final byte INFECTED = 1;

  private int movementDelayInMillis;

  public Player(GridCell gridCell, byte startCondition) {
    super(Player.TAG + ":" + startCondition, gridCell, Data.Images.Entity.human(Player.HEALTHY));
  }

  public void turn(byte direction) {
    super.setImage(Data.Images.Entity.human(direction).getImage());
    super.gridCell.getGameBoard().repaint();
  }

  public void infect() {
    super.setImage(Data.Images.Entity.human(Player.INFECTED).getImage());
    super.gridCell.getGameBoard().repaint();
  }

  public boolean canMove(byte direction) {
    Wall passWall = super.gridCell.getWall(direction);

    if (!passWall.canPass(TAG))
      return false;
    
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell;
    try {
      neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);
    } catch (IndexOutOfBoundsException e) {
      return false;
    }
    
    if (neighborCell.hasEntity(Zombie.TAG) || neighborCell.hasEntity(SmartZombie.TAG))
      return false;
    if (passWall.requiresEnergy() && thisGameBoard.getRemainingEnergy() < 1)
      return false;
    return true;
  }

  
  public ArrayList<Animation> move(byte direction) {
    Wall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    if (passWall.requiresEnergy())
      thisGameBoard.decrementEnergy();

    ArrayList<Animation> animations = new ArrayList<>();

    animations.addAll(passWall.getAnimations(0, this));
    movementDelayInMillis = passWall.addDelayInMillis();

    animations.add(new EntityAnimation(movementDelayInMillis, new JLabel(this), super.gridCell.getXY(), neighborCell.getXY(), Data.Animation.humanTravelTime));
    super.gridCell = neighborCell;

    if (super.gridCell.hasItem(Key.TAG)) {
      animations.addAll(this.gameBoard.unlockDoorsWithKey((Key) super.gridCell.getItem(), movementDelayInMillis + Data.Animation.humanTravelTime));
      super.gridCell.setItem(null);
    } else if (super.gridCell.hasItem(Battery.TAG)) {
      super.gridCell.getGameBoard().addEnergy(((Battery) super.gridCell.getItem()).getEnergy());
      super.gridCell.setItem(null);
    }
    if (super.gridCell.hasRoomType(Elevator.TAG))
      super.gridCell.getGameBoard().hasReturned();
    else if (super.gridCell.hasRoomType(Star.TAG))
      super.gridCell.getGameBoard().removeStar((Star) super.gridCell.getRoomType());
    
    return animations;
  }


  public int getTimeOfMovement() {
    return movementDelayInMillis;
  }

  public void cycleOptions() {
    // Nothing
  }
}