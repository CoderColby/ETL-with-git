import javax.swing.ImageIcon;
import java.lang.Comparable;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


public class Zombie extends AbstractEntity implements Comparable {

  public static final String TAG = "Zombie";
  public static final byte DEFAULT = 1;

  private byte startCondition;

  
  public Zombie() {
    super(Zombie.TAG + ":" + Zombie.DEFAULT, Data.Images.Entity.zombie(Zombie.DEFAULT));
  }
  
  public Zombie(GridCell gridCell, byte startCondition) {
    super(Zombie.TAG + ":" + startCondition, gridCell, Data.Images.Entity.zombie(startCondition), Data.Animation.zombieTravelTime);
    this.startCondition = startCondition;
  }

  public int compareTo(Object other) {
    return Double.compare(super.gridCell.getDistanceFromPlayer(), ((Zombie) other).gridCell.getDistanceFromPlayer());
  }

  public void turn(byte direction) {
    super.setImage(new ImageIcon(Data.Images.Entity.zombie(direction)).getImage());
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
    return true;
  }

  
  public ArrayList<Animation> move(int[] playerLocation, int movementDelay, boolean isFirstMove) {
    ArrayList<Animation> animations = new ArrayList<>();
    
    byte direction = (byte) -1;
    boolean viableDirection = false;
    int firstDifference = playerLocation[0] - super.gridCell.getCoordinates()[0];
    int secondDifference = playerLocation[1] - super.gridCell.getCoordinates()[1];
    for (int i = 0; i < 2 && !viableDirection && (i == 0 || firstDifference * secondDifference != 0); i++) {
      direction = (byte) ((Math.abs(secondDifference) > Math.abs(firstDifference) ^ i != 0) ? 1 - Math.signum(secondDifference) : 2 - Math.signum(firstDifference));
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

    if (!isFirstMove)
      movementDelay += movementDelay + Data.Animation.zombieTravelTime;

    animations.addAll(passWall.getAnimations(Zombie.TAG, movementDelay));
    movementDelay += passWall.addDelayInMillis();

    animations.add(new EntityAnimation(movementDelay, this, direction));
    super.gridCell = neighborCell;

    return animations;
  }

  public void cycleOptions() {
    startCondition = (byte) (++startCondition % 4);
    turn(startCondition);
    super.identifier = Zombie.TAG + ":" + startCondition;
  }

  public String getInfo() {
    return "This zombie will always get as close to the player as possible and never move farther away, even if doing so would allow a direct path to the player. Moves two cells for every player move. If moving horizontally and vertically will both get it equally close to the player, it will always prefer vertical movement first.";
  }
}