import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;


public class Target extends AbstractRoomType {

  public static final String TAG = "Target";
  public static final byte DEFAULT = 0;
  public static final byte BAD = 0;
  public static final byte GOOD = 1;
  public static boolean isOngoing = false;

  private boolean isGood;

  
  public Target() {
    super(Target.TAG + ":" + Target.DEFAULT, Data.Images.RoomType.target(Target.DEFAULT));
  }
  
  public Target(GridCell gridCell, byte startCondition) {
    super(Target.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.target(startCondition));
    isGood = startCondition == Target.GOOD;
  }

  public boolean isGood() {
    return isGood;
  }

  public void setBad() {
    isGood = false;
    super.setImage(new ImageIcon(Data.Images.RoomType.target(Target.BAD)).getImage());
  }

  public void cycleOptions() {
    isGood = !isGood;
    super.initializeLabel(new ImageIcon(Data.Images.RoomType.target((isGood)? Target.GOOD : Target.BAD)));
    super.identifier = Target.TAG + ":" + ((isGood)? Target.GOOD : Target.BAD);
  }

  
  public JPanel fixPanel() {
    Target.isOngoing = true;
    JPanel root = new JPanel();
    root.setBackground(Color.BLACK);

    JButton b = new JButton("Press me to return");
    b.addActionListener(event -> {
      Target.this.isGood = true;
      Target.super.setImage(new ImageIcon(Data.Images.RoomType.target(Target.GOOD)).getImage());
      Target.isOngoing = false;
      notifyAll();
    });

    return root;
  }
}