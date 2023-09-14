import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Player extends AbstractEntity {

  public static final String TAG = "Player";
  public static final byte DEFAULT = 0;
  public static final byte HEALTHY = 0;
  public static final byte INFECTED = 5;

  private int movementDelayInMillis;
  private byte startCondition;

  
  public Player() {
    super(Player.TAG + ":" + Player.DEFAULT, Data.Images.Entity.player(Player.DEFAULT));
  }
  
  public Player(GridCell gridCell, byte startCondition) {
    super(Player.TAG + ":" + startCondition, gridCell, Data.Images.Entity.player(Player.HEALTHY), Data.Animation.playerTravelTime);
    this.startCondition = startCondition;
  }

  public void turn(byte direction) {
    super.initializeLabel(new ImageIcon(Data.Images.Entity.player(direction)));
    // super.gridCell.getGameBoard().repaint();
  }

  public void infect() {
    super.setImage(new ImageIcon(Data.Images.Entity.player(Player.INFECTED)).getImage());
    // super.gridCell.getGameBoard().repaint();
  }

  public boolean canMove(byte direction) {
    AbstractWall passWall = super.gridCell.getWall(direction);

    if (!passWall.canPass(TAG))
      return false;
    
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell;
    try {
      neighborCell = super.gridCell.getNeighbor(direction);
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
    AbstractWall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = super.gridCell.getNeighbor(direction);

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    if (passWall.requiresEnergy())
      thisGameBoard.decrementEnergy();

    ArrayList<Animation> animations = new ArrayList<>();

    animations.addAll(passWall.getAnimations(Player.TAG, (byte) 0));
    movementDelayInMillis = passWall.addDelayInMillis();

    animations.add(new EntityAnimation(movementDelayInMillis, this, direction));
    super.gridCell = neighborCell;

    if (super.gridCell.hasItem(Key.TAG)) {
      animations.addAll(thisGameBoard.unlockDoorsWithKey((Key) super.gridCell.getItem(), movementDelayInMillis + Data.Animation.playerTravelTime));
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
    startCondition = (byte) (++startCondition % 4);
    turn(startCondition);
    super.identifier = Player.TAG + ":" + startCondition;
  }
}