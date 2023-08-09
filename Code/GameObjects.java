import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.util.ArrayList;

//////////////////////////////////////////////////////////////////////////////////////////////////// Wall

abstract class Wall {

  private ImageIcon currentImage;
  private boolean isVertical;
  private Rectangle coords;
  private boolean isOpen;
  public static final byte EAST_WALL = 0;
  public static final byte SOUTH_WALL = 1;
  public final String wallID;
  public abstract byte layer;
  private String status;

  // public JLabel getSizedImage(Rectangle dimensions) {
  //   JLabel label = new JLabel(this.image);
  //   label.setBounds(dimensions);
  //   return label;
  // }

  public static Wall makeNewWall(String wallType) {
    switch(wallType) {
      case "regularWall":
        return new RegularWall();
        break;
    }
  }

  public abstract boolean canPass(Entity entity, int energy);

  public abstract int takeEnergy(int remainingEnergy);

  public abstract boolean hasOpenSequence(Entity entity);

  public abstract ArrayList<AnimationEvent> getOpenSequence(Entity entity);

  // Make a list of events that each wall listens for like when a key is picked up or when the power is turned on
  // Then make an array of walls for each listener an update them when the event happens
  
}

class RegularWall extends Wall {
  
}

class RegularDoor extends Wall {
  
}

class AirlockDoor extends Wall {
  
}

class SecurityDoor extends Wall {

}

class OneTimeDoor extends Wall {
  
}

class DetectionDoor extends Wall {
  
}

class LockedDoor extends Wall {
  
}

class Hallway extends Wall {
  
}

//////////////////////////////////////////////////////////////////////////////////////////////////// Entity

abstract class Entity {

  private ImageIcon image;

  public abstract void turn(byte direction);

  public Dimension getTravelDistance(byte direction) {
    int X = (Data.ROOM_HEIGHT + Data.WALL_THICKNESS) * (1 - direction + 2 * (direction / 3)); // 0:1; 1:0; 2:-1; 3:0
    int Y = (Data.ROOM_HEIGHT + Data.WALL_THICKNESS) * (direction - 2 + 2 * ((3 - direction) / 3)); // 0:0; 1:-1; 2:0; 3:1
    return new Dimension(X, Y);
  }
  
}

class Human extends Entity {

}

class Zombie extends Entity implements Comparable<Zombie> {

  public int compareTo(Zombie other) {
    // organize by distance from player
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////// Item

abstract class Item {
  
}

class key extends Item {
  
}

//////////////////////////////////////////////////////////////////////////////////////////////////// RoomObject

abstract class RoomObject {
  
}

class Elevator extends RoomObject {
  
}

class SecuritySystem extends RoomObject {
  
}