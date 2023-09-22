import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Collections;
import java.lang.Comparable;
import javax.swing.ImageIcon;


public class SmartZombie extends AbstractEntity implements Comparable {

  public static final String TAG = "Smart_Zombie";
  public static final byte DEFAULT = 1;

  private ArrayList<Sentinel> sentinels;
  private byte startCondition;

  private class Sentinel extends ArrayList<Byte> implements Comparable {
    private GridCell gridCell;
    private SmartZombie host;
    private ArrayList<Byte> illegalDirections;

    public Sentinel(GridCell gridCell, ArrayList<Byte> path, SmartZombie host, byte direction) {
      super(path);
      this.gridCell = gridCell;
      this.host = host;
      this.illegalDirections = new ArrayList<Byte>();
      if (direction >= 0) {
        super.add(direction);
        this.illegalDirections.add((byte) ((direction + 2) % 4));
      }
    }

    public void setIllegalDirection(byte illegalDirection) {
      this.illegalDirections.add(illegalDirection);
    }
    

    @Override
    public boolean equals(Object other) {
      return ((Sentinel) other).gridCell == this.gridCell;
    }

    public int compareTo(Object other) {
      return Double.compare(gridCell.getDistanceFromPlayer(), ((Sentinel) other).gridCell.getDistanceFromPlayer());
    }

    public boolean hasFoundPlayer(int[] playerLocation) {
      return Arrays.equals(gridCell.getCoordinates(), playerLocation);
    }

    
    public ArrayList<Byte> scan() {
      ArrayList<Byte> viableDirections = new ArrayList<>();
      
      for (byte b = 0; b < 4; b++)
        if (gridCell.getWall(b).canPass(SmartZombie.TAG) && !illegalDirections.contains(b) &&
           !gridCell.getNeighbor(b).hasEntity(SmartZombie.TAG) && !gridCell.getNeighbor(b).hasEntity(Zombie.TAG))
          viableDirections.add(b);

      // for (byte i = (byte) (viableDirections.size() - 2); i >= 0; i--) {
      //   byte b = i;
      //   while (b + 1 < viableDirections.size() && gridCell.getNeighbor(viableDirections.get(b)).getDistanceFromPlayer() > gridCell.getNeighbor(viableDirections.get(b + 1)).getDistanceFromPlayer()) {
      //     Collections.swap(viableDirections, b, b + 1);
      //     b++;
      //   }
      // }


      return viableDirections;
    }

    
    public ArrayList<Sentinel> seek(ArrayList<Byte> viableDirections) {
      ArrayList<Sentinel> sentinels = new ArrayList<>();
      
      if (viableDirections.isEmpty())
        return sentinels;

      for (byte d : viableDirections) {
        Sentinel newSentinel = new Sentinel(gridCell.getNeighbor(d), this, host, d);

        int index = host.sentinels.indexOf(newSentinel);
        if (index >= 0)
          host.sentinels.get(index).setIllegalDirection((byte) ((d + 2) % 4));
        else
          sentinels.add(newSentinel);
      }
      return sentinels;
    }
  }

  
  public SmartZombie() {
    super(SmartZombie.TAG + ":" + SmartZombie.DEFAULT, Data.Images.Entity.smartZombie(SmartZombie.DEFAULT));
  }
  
  public SmartZombie(GridCell gridCell, byte startCondition) {
    super(SmartZombie.TAG + ":" + startCondition, gridCell, Data.Images.Entity.smartZombie(startCondition), Data.Animation.smartZombieTravelTime);
    this.startCondition = startCondition;
  }

  public void turn(byte direction) {
    super.setImage(new ImageIcon(Data.Images.Entity.smartZombie(direction)).getImage());
    super.gridCell.getGameBoard().repaint();
  }


  public int compareTo(Object other) {
    if (this.getMovesToTarget() != ((SmartZombie) other).getMovesToTarget())
      return Integer.compare(this.getMovesToTarget(), ((SmartZombie) other).getMovesToTarget());
    return Double.compare(super.gridCell.getDistanceFromPlayer(), ((SmartZombie) other).gridCell.getDistanceFromPlayer());
  }


  public int getMovesToTarget() {
    ArrayList<Byte> path = calculatePath();
    if (path.isEmpty())
      return Integer.MAX_VALUE;
    return path.size();
  }


  public ArrayList<Byte> calculatePath() {
    // ArrayList<Byte> viableDirections = start.scan();

    // for (byte i = (byte) (viableDirections.size() - 2); i >= 0; i--) {
    //   byte b = i;
    //   while (b + 1 < viableDirections.size() && gridCell.getNeighbor(viableDirections.get(b)).getDistanceFromPlayer() > gridCell.getNeighbor(viableDirections.get(b + 1)).getDistanceFromPlayer()) {
    //     Collections.swap(viableDirections, b, b + 1);
    //     b++;
    //   }
    // }
    
    sentinels = new ArrayList<Sentinel>();
    sentinels.add(new Sentinel(super.gridCell, new ArrayList<Byte>(), this, (byte) -1));

    while (!sentinels.isEmpty()) {
      ArrayList<Sentinel> previousSentinels = (ArrayList<Sentinel>) sentinels.clone();
      sentinels = new ArrayList<Sentinel>();
      
      for (Sentinel s : previousSentinels)
        sentinels.addAll(s.seek(s.scan()));

      sentinels.sort(null);

      for (Sentinel s : sentinels)
        if (s.gridCell.hasEntity(Player.TAG))
          return s;
        

      for (Sentinel s : sentinels)
        if (s.gridCell.hasRoomType(Target.TAG) && ((Target) s.gridCell.getRoomType()).isGood())
          return s;
    }
    return new ArrayList<Byte>();
  }


  public byte calculateDirection() {
    ArrayList<Byte> path = calculatePath();
    if (path.isEmpty())
      return -1;
    return path.get(0);
  }


  public ArrayList<Animation> move(int movementDelay) {
    byte direction = calculateDirection();
    ArrayList<Animation> animations = new ArrayList<>();

    if (direction < 0) {
      
      boolean viableDirection = false;
      int[] playerLocation = super.gridCell.getGameBoard().getPlayerLocation();
      int firstDifference = playerLocation[0] - super.gridCell.getCoordinates()[0];
      int secondDifference = playerLocation[1] - super.gridCell.getCoordinates()[1];
      for (int i = 0; i < 2 && !viableDirection && (i == 0 || firstDifference * secondDifference != 0); i++) {
        direction = (byte) ((Math.abs(secondDifference) > Math.abs(firstDifference) ^ i != 0) ? 1 - Math.signum(secondDifference) : 2 - Math.signum(firstDifference));
        viableDirection = super.gridCell.getWall(direction).canPass(SmartZombie.TAG) && !super.gridCell.getNeighbor(direction).hasEntity(SmartZombie.TAG) && !super.gridCell.getNeighbor(direction).hasEntity(Zombie.TAG);
      }
      if (!viableDirection)
        return animations;
    }
    
    // turn(direction);
      
    AbstractWall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = super.gridCell.getNeighbor(direction);

    if (neighborCell.hasEntity(Player.TAG)) {
      thisGameBoard.infectPlayer();
      return animations;
    }

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    movementDelay += Data.Animation.playerTravelTime;
    animations.addAll(passWall.getAnimations(SmartZombie.TAG, movementDelay));
    movementDelay += passWall.addDelayInMillis();

    // if (neighborCell.hasRoomType(Target.TAG) && ((Target) neighborCell.getRoomType()).isGood()) {
    //   ((Target) neighborCell.getRoomType()).setBad();
    //   animations.addAll(thisGameBoard.setPower(false, movementDelay));
    //   super.gridCell.getGameBoard().updateTargets();
    // }
    
    animations.add(new EntityAnimation(movementDelay, this, direction));
    super.gridCell = neighborCell;

    return animations;
  }

  public ArrayList<Animation> evaluatePosition(int startTime) {
    ArrayList<Animation> animations = new ArrayList<>();
    if (super.gridCell.hasRoomType(Target.TAG) && ((Target) super.gridCell.getRoomType()).isGood()) {
      ((Target) super.gridCell.getRoomType()).setBad();
      animations.addAll(super.gridCell.getGameBoard().setPower(false, startTime));
      super.gridCell.getGameBoard().updateTargets();
    }
    return animations;
  }

  public void cycleOptions() {
    startCondition = (byte) (++startCondition % 4);
    turn(startCondition);
    super.identifier = SmartZombie.TAG + ":" + startCondition;
  }

  public String getInfo() {
    return "This zombie will navigate to the player and targets. Moves one cell for every player move.";
  }
}