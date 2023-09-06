import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Comparable;

public class SmartZombie extends AbstractEntity implements Comparable {

  public static final String TAG = "Smart_Zombie";
  public static final byte DEFAULT = 1;

  private ArrayList<Sentinel> sentinels;
  private byte startCondition;

  private class Sentinel extends ArrayList<Byte> {
    private GridCell gridCell;
    private SmartZombie host;
    private ArrayList<Byte> illegalDirections;

    public Sentinel(GridCell gridCell, ArrayList<Byte> path, SmartZombie host, byte direction) {
      super(path);
      this.gridCell = gridCell;
      this.host = host;
      this.illegalDirections = new ArrayList<Byte>();
      super.add(direction);
      this.illegalDirections.add((direction + 2) % 4);
    }

    public void setIllegalDirection(byte illegalDirection) {
      this.illegalDirections.add(illegalDirection);
    }
    

    @Override
    public boolean equals(Object other) {
      return ((Sentinel) other).gridCell == this.gridCell;
    }


    public boolean hasFoundPlayer(int[] playerLocation) {
      return Arrays.equals(gridCell.getCoordinates(), playerLocation);
    }

    
    public ArrayList<Byte> scan() {
      ArrayList<Byte> viableDirections = new ArrayList<>();

      for (byte i = 0; i < 4; i++)
        if (gridCell.getWall(i).canPass(SmartZombie.TAG) && !illegalDirection.contains(i) &&
           !gridCell.getNeighbor(i).hasEntity(SmartZombie.TAG) && !gridCell.getNeighbor(i).hasEntity(Zombie.TAG))
          viableDirections.add(i);

      return viableDirections;
    }

    
    public ArrayList<Sentinel> seek() {
      ArrayList<Byte> viableDirections = scan();
      ArrayList<Sentinels> sentinels = new ArrayList<>();
      
      if (viableDirections.isEmpty())
        return sentinels;

      for (byte d : viableDirections) {
        Sentinel newSentinel = new Sentinel(gridCell.getNeighbor(d), this, host, d);

        int index = host.sentinels.indexOf(newSentinel);
        if (index >= 0)
          host.sentinels.get(index).setIllegalDirection((d + 2) % 4);
        else
          sentinels.add(newSentinel);
      }
      return sentinels;
    }
  }


  public SmartZombie(GridCell gridCell, byte startCondition) {
    super(SmartZombie.TAG + ":" + startCondition, gridCell, Data.Images.Entity.smartZombie(startCondition));
    this.startCondition = startCondition;
  }


  public int compareTo(SmartZombie other) {
    return Integer.compare(this.getMovesToTarget(), other.getMovesToTarget());
  }


  public int getMovesToTarget() {
    ArrayList<Byte> path = calculatePath();
    if (path.isEmpty())
      return Integer.MAX_VALUE;
    return path.size();
  }


  public ArrayList<Byte> calculatePath() {
    sentinels = new ArrayList<Sentinel>();
    sentinels.add(new Sentinel(super.gridCell, new ArrayList<Byte>(), this, new ArrayList<Byte>()));

    while (!sentinels.isEmpty()) {
      ArrayList<Sentinel> previousSentinels = (ArrayList<Sentinel>) sentinels.clone();
      sentinels = new ArrayList<Sentinel>();
      
      for (Sentinel s : previousSentinels)
        sentinels.addAll(s.seek());

      for (Sentinel s : sentinels)
        if (s.gridCell.hasEntity(Player.TAG) || (s.gridCell.hasRoomType(Target.TAG) && ((Target) s.gridCell.getRoomType()).isGood()))
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

    if (direction < 0)
      return animations;
    
    turn(direction);
      
    Wall passWall = super.gridCell.getWall(direction);
    GameBoard thisGameBoard = super.gridCell.getGameBoard();
    GridCell neighborCell = thisGameBoard.getGridCell(super.gridCell.getCoordinates(), direction);

    if (neighborCell.hasEntity(Player.TAG)) {
      thisGameBoard.infectPlayer();
      return animations;
    }

    neighborCell.setEntity(this);
    super.gridCell.setEntity(null);

    movementDelay += Data.Animation.humanTravelTime;
    animations.addAll(passWall.getAnimations(movementDelay, this));
    movementDelay += passWall.addDelayInMillis();

    if (neighborCell.hasRoomType(Target.TAG) && ((Target) neighborCell.getRoomType()).isGood()) {
      ((Target) neighborCell.getRoomType()).setBad();
      animations.addAll(thisGameBoard.setPower(false, movementDelay));
    }
    
    animations.add(new EntityAnimation(movementDelay, this, super.gridCell.getEntityXY(), neighborCell.getEntityXY(), Data.Animation.smartZombieTravelTime));
    super.gridCell = neighborCell;

    return animations;
  }

  public void cycleOptions() {
    startCondition = (byte) (++startCondition % 4);
    super.setImage(Data.Images.smartZombie(startCondition).getImage());
    super.identifier = SmartZombie.TAG + ":" + startCondition;
  }
}