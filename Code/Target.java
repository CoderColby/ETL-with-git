import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;


public class Target extends AbstractRoomType {

  public static final String TAG = "Target";
  public static final byte DEFAULT = 0;
  public static final byte BAD = 0;
  public static final byte GOOD = 1;
  public static boolean isOngoing = false;

  private boolean isGood;

  
  public Target() {
    super(Target.TAG + ":" + Target.DEFAULT, new GridCell(), AbstractItem.initializeLabel(new GridCell(), Data.Images.RoomType.target(Target.DEFAULT)));
  }
  
  public Target(GridCell gridCell, byte startCondition) {
    super(Target.TAG + ":" + startCondition, gridCell, AbstractRoomType.initializeLabel(gridCell, Data.Images.RoomType.target(startCondition)));
    isGood = startCondition == Target.GOOD;
  }

  public boolean isGood() {
    return isGood;
  }

  public void setBad() {
    isGood = false;
    super.setImage(Data.Images.RoomType.target(Target.BAD).getImage());
  }

  public void cycleOptions() {
    isGood = !isGood;
    super.setImage(Data.Images.RoomType.target((isGood)? Target.GOOD : Target.BAD).getImage());
    super.identifier = Target.TAG + ":" + ((isGood)? Target.GOOD : Target.BAD);
  }

  
  public JPanel fixPanel() {
    Target.isOngoing = true;
    JPanel root = new JPanel();
    root.setBackground(Color.BLACK);

    JButton b = new JButton("Press me to return");
    b.addActionListener(event -> {
      Target.this.isGood = true;
      Target.super.setImage(Data.Images.RoomType.target(Target.GOOD).getImage());
      Target.isOngoing = false;
      notifyAll();
    });

    return root;
  }
}