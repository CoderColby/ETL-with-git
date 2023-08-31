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
    public static final int humanTravelTime = 1000;
    public static final int zombieTravelTime = 800;
    public static final int animationSpeed = 5;
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

    public static final Color levelBackground = new Color(100, 100, 100);
  }

  public static class Images {

    public static class Wall {
      
      public static final ImageIcon wall = new ImageIcon("../GameAssets/Images/Walls/Wall.png");
      public static final ImageIcon regDoor = new ImageIcon("../GameAssets/Images/Walls/Door_Regular.png");
      public static final ImageIcon airDoor = new ImageIcon("../GameAssets/Images/Walls/Door_Airlock.png");
      public static final ImageIcon secDoor = new ImageIcon("../GameAssets/Images/Walls/Door_Security.png");
      public static final ImageIcon oneDoor = new ImageIcon("../GameAssets/Images/Walls/Door_Once.png");
      public static final ImageIcon detDoor = new ImageIcon("../GameAssets/Images/Walls/Door_Detection.png");
      public static final ImageIcon doorActive = new ImageIcon("../Images/Walls/Door_Active.png");
      public static final ImageIcon airDoorActive = new ImageIcon("../GameAssets/Images/Walls/Door_Airlock_Active.png");
    
      public static ImageIcon lockDoor (byte num) {
        return new ImageIcon("../GameAssets/Images/Walls/LockedDoor" + num + ".png");
      }
    }

    public static class Entity {

      public static ImageIcon human(byte direction) {
        return new ImageIcon("../GameAssets/Images/Entity/Human" + direction + ".png");
      }
      public static ImageIcon zombie(byte direction) {
        return new ImageIcon("../GameAssets/Images/Entity/Zombie" + direction + ".png");
      }
      public static ImageIcon smartZombie(byte direction) {
        return new ImageIcon("../GameAssets/Images/Entity/SmartZombie" + direction + ".png");
      }
      
    }

    public static class Item {

      public static ImageIcon key(byte num) {
        return new ImageIcon("../GameAssets/Images/Item/Key" + num + ".png");
      }
      
    }

    public static class RoomType {
      public static final ImageIcon generator = new ImageIcon("../GameAssets/Images/RoomObjects/Generator.png");
      public static final ImageIcon elevator = new ImageIcon("../GameAssets/Images/RoomObjects/Elevator.png");
      public static final ImageIcon generatorActive = new ImageIcon("../GameAssets/Images/RoomObjects/Generator_Active.png");
    }

    public static class Other {
      public static final ImageIcon settings = new ImageIcon("../GameAssets/Images/Other/Settings.png");
      public static final ImageIcon rightNavArrow = new ImageIcon("../GameAssets/Images/Other/RightNavArrow.png");
      public static final ImageIcon leftNavArrow = new ImageIcon("../GameAssets/Images/Other/LeftNavArrow.png");
      public static final ImageIcon logo = new ImageIcon("../GameAssets/Images/Other/ETLogo.png");
      public static final ImageIcon lock = new ImageIcon("../GameAssets/Images/Other/Lock.png");
    }

    public static ImageIcon rotateIcon(ImageIcon icon, double angle) { // Courtesy of ChatGPT
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        Image image = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(height, 0);
        transform.rotate(Math.toRadians(angle));
        g2d.drawImage(image, transform, null);

        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }
  
  }

  public static class Utilities {

    public static final String userFileDirectory = "../User/";
    public static final int numOfLevels = new File(Utilities.standardLevelDirectory).list().length;

    public static File[] getAllRegFilesInDirectory(File directory) {
      ArrayList<File> allFiles = new ArrayList<>();
      for (File f : directory.listFiles()) {
        if (f.isDirectory())
          allFiles.addAll(Arrays.asList(getAllRegFilesInDirectory(f)));
        else
          allFiles.add(f);
      }
      return (File[]) allFiles.toArray();
    }

    public static String getUserFilePath(String username) {
      return Utilities.userFileDirectory + username;
    }

    public static final String standardLevelDirectory = "../GameAssets/Levels/StandardLevels/";
    public static final String customLevelDirectory = "../GameAssets/Levels/CustomLevels/";
    public static final String customUnownedDirectory = customLevelDirectory + "Unowned Levels/";
    public static final String defaultLevelFile = "../GameAssets/Levels/StandardLevels/default.txt";

    public static final String forRoom = "Room";
    public static final String forWall = "Wall";
  }
  
}