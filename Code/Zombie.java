import javax.swing.ImageIcon;
import java.lang.Comparable;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Zombie extends AbstractEntity implements Comparable {

  public static final String TAG = "Zombie";
  public static final byte DEFAULT = 1;

  private int repeatDelay;
  private byte startCondition;

  
  public Zombie() {
    super(Zombie.TAG + ":" + Zombie.DEFAULT, Data.Images.Entity.zombie(Zombie.DEFAULT));
  }
  
  public Zombie(GridCell gridCell, byte startCondition) {
    super(Zombie.TAG + ":" + startCondition, gridCell, Data.Images.Entity.zombie(startCondition), Data.Animation.zombieTravelTime);
    this.startCondition = startCondition;
    repeatDelay = -1;
  }

  public int compareTo(Object other) {
    return Double.compare(super.getDistanceFromPlayer(), ((Zombie) other).getDistanceFromPlayer());
  }

  public void turn(byte direction) {
    super.setImage(new ImageIcon(Data.Images.Entity.zombie(direction)).getImage());
    super.gridCell.getGameBoard().repaint();
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
    return true;
  }

  
  public ArrayList<Animation> move(int[] playerLocation, int movementDelay) {
    ArrayList<Animation> animations = new ArrayList<>();
    
    byte direction = Zombie.DEFAULT;
    boolean viableDirection = false;
    for (int i = 0; i < 2 && !viableDirection; i++) { // Unnecessarily complicated line here; compares difference in row distance vs column distance from player
      direction = (byte) ((Math.abs(playerLocation[0] - super.gridCell.getCoordinates()[0]) > Math.abs(playerLocation[1] - super.gridCell.getCoordinates()[1]) ^ i == 1) ? Math.signum(super.gridCell.getCoordinates()[1] - playerLocation[1]) + 1 : Math.signum(super.gridCell.getCoordinates()[0] - playerLocation[0]) + 2);
      viableDirection = canMove(direction);
    }
    if (!viableDirection)
      return animations;
    
    turn(direction);
      
    AbstractWall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = super.gridCell.getNeighbor(direction);

    if (neighborCell.hasEntity(Player.TAG)) {
      thisGameBoard.infectPlayer();
      return animations;
    }

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    movementDelay += (repeatDelay > 0)? repeatDelay : 0;

    animations.addAll(passWall.getAnimations(Zombie.TAG, movementDelay));
    movementDelay += passWall.addDelayInMillis();

    if (repeatDelay < 0)
      repeatDelay = movementDelay + Data.Animation.zombieTravelTime;
    else
      repeatDelay = -1;

    animations.add(new EntityAnimation(movementDelay, this, direction));
    super.gridCell = neighborCell;

    return animations;
  }

  public void cycleOptions() {
    startCondition = (byte) (++startCondition % 4);
    super.setImage(new ImageIcon(Data.Images.Entity.zombie(startCondition)).getImage());
    super.identifier = Zombie.TAG + ":" + startCondition;
  }
}