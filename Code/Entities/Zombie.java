import javax.swing.ImageIcon;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Zombie extends AbstractEntity {

  public static final String TAG = "Zombie";

  private int repeatDelay;

  public Zombie(GridCell gridCell) {
    super(TAG, gridCell, Data.Images.Entity.zombie(2));
    repeatDelay = -1;
  }

  public void turn(byte direction) {
    super.super.setIcon(Data.Images.Entity.zombie(direction));
    super.gridCell.getGameBoard().repaint();
  }

  public boolean canMove(byte direction) {
    Wall passWall = super.gridCell.getWall(direction);

    if (!passWall.canPass(TAG))
      return false;
    
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell;
    try
      neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);
    catch (IndexOutOfBoundsException e)
      return false;
    
    if (neighborCell.hasEntity(Zombie.TAG) || neighborCell.hasEntity(SmartZombie.TAG))
      return false;
    return true;
  }

  
  public ArrayList<AnimationEvent> move(int[] playerLocation, int movementDelay) {
    ArrayList<AnimationEvent> animations = new ArrayList<>();
    
    byte direction;
    boolean viableDirection = false;
    for (int i = 0; i < 2 && !viableDirection; i++) { // Unnecessarily complicated line here; compares difference in row distance vs column distance from player
      direction = (byte) (Math.abs(playerLocation[0] - super.gridCell.getCoordinates()[0]) > Math.abs(playerLocation[1] - super.gridCell.getCoordinates()[1]) ^ i == 1) ? Math.signum(super.gridCell.getCoordinates()[1] - playerLocation[1]) + 1 : Math.signum(super.gridCell.getCoordinates()[0] - playerLocation[0]) + 2;
      viableDirection = canMove(direction);
    }
    if (!viableDirection)
      return animations;
    
    turn(direction);
      
    Wall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);

    if (neighborCell.hasEntity(Player.TAG)) {
      thisGameBoard.hasLost();
      return animations;
    }

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    movementDelay += (repeatDelay > 0)? repeatDelay : 0;

    animations.addAll(passWall.getAnimations(movementDelay, this));
    movementDelay += passWall.addDelayInMillis();

    if (repeatDelay < 0)
      repeatDelay = movementDelay + Data.Animation.zombieTravelTime;
    else
      repeatDelay = -1;

    animations.add(new EntityAnimation(movementDelay, this, super.gridCell.getEntityXY(), neighborCell.getEntityXY(), Data.Animation.zombieTravelTime));
    super.gridCell = neighborCell;

    return animations;
  }
}