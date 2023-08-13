import java.util.ArrayList;

public abstract class Entity {
  
  public abstract String getTag();

  public abstract boolean canMove(byte direction);

  public abstract ArrayList<Animation> move(byte direction);
}