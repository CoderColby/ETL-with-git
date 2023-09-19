import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Data {

  public static class Animation {
    public static final int playerTravelTime = 500;
    public static final int zombieTravelTime = 400;
    public static final int smartZombieTravelTime = 500;
  }

  public static class Fonts {
    public static final Font header1 = new Font("Monospace", Font.PLAIN, 40);
    public static final Font header2 = new Font("Monospace", Font.PLAIN, 28);
    public static final Font textLabel = new Font("Monospace", Font.ITALIC, 18);
    public static final Font textField = new Font("Monospace", Font.PLAIN, 16);
    public static final Font dataLabel = new Font("Monospace", Font.PLAIN, 20);
    public static final Font menuButton = new Font("Monospace", Font.PLAIN, 20);
    public static final Font levelButton = new Font("Monospace", Font.PLAIN, 60);
    public static final Font customButton = new Font("Monospace", Font.PLAIN, 30);
    public static final Font checkboxLabel = new Font("Monospace", Font.ITALIC, 16);
    public static final Font menuLevelButton = new Font("Monospace", Font.PLAIN, 60);
  }

  public static class Colors {
    public static final Color buttonBackground = new Color(180, 180, 180);
    public static final Color perfectLevel = new Color(255, 192, 0);
    public static final Color perfectBorder = new Color(127, 96, 0);
    public static final Color standardLevel = new Color(200, 200, 200);

    public static final Color levelBackground = new Color(89, 89, 89);
    public static final Color roomBackground = new Color(191, 191, 191);
  }

  public static class Images {

    public static class Wall { // Change ImageIcons to strings so that object references don't go everywhere; ALSO make objects resize their image using AbstractGameObject.setScale() in empty constructor - figure out how to resize walls correctly
      
      public static final String wall = "./GameAssets/Images/Wall/Wall.png";
      public static final String hallway = "./GameAssets/Images/Wall/Hallway.png";
      public static String door(byte num) {
        return "./GameAssets/Images/Wall/Door" + num + ".png";
      }
      public static String airlockDoor(byte num) {
        return "./GameAssets/Images/Wall/AirlockDoor" + num + ".png";
      }
      public static String detectionDoor(byte num) {
        return "./GameAssets/Images/Wall/DetectionDoor" + num + ".png";
      }
      public static String onceDoor(byte num) {
        return "./GameAssets/Images/Wall/OnceDoor" + num + ".png";
      }
      public static String powerDoor(byte num) {
        return "./GameAssets/Images/Wall/PowerDoor" + num + ".png";
      }
      public static String lockedDoor(byte num) {
        return "./GameAssets/Images/Wall/LockedDoor" + num + ".png";
      }
    }

    public static class Entity {

      public static String player(byte direction) {
        return "./GameAssets/Images/Entity/Player" + direction + ".png";
      }
      public static String zombie(byte direction) {
        return "./GameAssets/Images/Entity/Zombie" + direction + ".png";
      }
      public static String smartZombie(byte direction) {
        return "./GameAssets/Images/Entity/SmartZombie" + direction + ".png";
      }
    }

    public static class Item {

      public static String key(byte num) {
        return "./GameAssets/Images/Item/Key" + num + ".png";
      }
      public static final String eraser = "./GameAssets/Images/Item/Eraser.png";
      public static String battery = "./GameAssets/Images/Item/Battery.png";
    }

    public static class RoomType {
      public static final String emptyRoom = ".GameAssets/Images/RoomType/Empty.png";
      public static final String star = "./GameAssets/Images/RoomType/Star.png";
      public static final String filled = "./GameAssets/Images/RoomType/Filled.png";
      public static final String elevator = "./GameAssets/Images/RoomType/Elevator.png";
      public static String target(byte num) {
        return "./GameAssets/Images/RoomType/Target" + num + ".png";
      }
    }

    public static class Other {
      public static final String settings = "./GameAssets/Images/Other/Settings.png";
      public static final String rightNavArrow = "./GameAssets/Images/Other/RightNavArrow.png";
      public static final String leftNavArrow = "./GameAssets/Images/Other/LeftNavArrow.png";
      public static final String logo = "./GameAssets/Images/Other/ETLogo.png";
      public static final String lock = "./GameAssets/Images/Other/Lock.png";
    }
    

    public static ImageIcon rotateIcon(ImageIcon icon) {
      int width = icon.getIconWidth();
      int height = icon.getIconHeight();
  
      Image image = icon.getImage();
      BufferedImage bufferedImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
  
      Graphics2D g2d = bufferedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  
      AffineTransform transform = new AffineTransform();
      transform.rotate(Math.toRadians(-90), width / 2.0, width / 2.0);

      g2d.drawImage(image, transform, null);
  
      g2d.dispose();
  
      return new ImageIcon(bufferedImage);
    }
  
  }

  public static class Utilities {

    public static final String userFileDirectory = "./Users/";
    public static final int numOfLevels = (new File(Utilities.standardLevelDirectory).list() == null)? 0 : new File(Utilities.standardLevelDirectory).list().length;

    public static File[] getAllRegFilesInDirectory(File directory) {
      ArrayList<File> allFiles = new ArrayList<>();
      if (directory.listFiles() == null)
        return new File[0];
      for (File f : directory.listFiles()) {
        if (f.isDirectory())
          allFiles.addAll(Arrays.asList(getAllRegFilesInDirectory(f)));
        else
          allFiles.add(f);
      }
      File[] allFilesArray = new File[allFiles.size()];
      allFiles.toArray(allFilesArray);
      return allFilesArray;
    }

    public static String getLevelFilePath(int num) {
      return Utilities.standardLevelDirectory + "level" + num + ".txt";
    }

    public static String getUserFilePath(String username) {
      return Utilities.userFileDirectory + username + ".txt";
    }

    public static final String standardLevelDirectory = "./GameAssets/Levels/StandardLevels/";
    public static final String customLevelDirectory = "./GameAssets/Levels/CustomLevels/";
    public static final String customUnownedDirectory = customLevelDirectory + "UnownedLevels/";
    public static final String defaultLevelFile = "./GameAssets/Levels/default.txt";
    public static final String temporaryDemoFile = "./GameAssets/Levels/temp.txt";

    public static final String forRoom = "Room";
    public static final String forWall = "Wall";
  }
  
}