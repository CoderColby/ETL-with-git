import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;


public class Target extends AbstractRoomType {

  public static final String TAG = "Target";
  public static final byte BAD = 0;
  public static final byte GOOD = 1;
  public static boolean isOngoing = false;

  private boolean isGood;

  public Target(GridCell gridCell, byte startCondition) {
    super(Target.TAG + ":" + startCondition, gridCell, Data.Images.target);
    isGood = startCondition == Target.GOOD;
  }

  public void cycleOptions() {
    isGood = !isGood;
    super.image = Data.Images.target((isGood)? Target.GOOD : Target.BAD);
    super.identifier = Target.TAG + ":" + (isGood)? Target.GOOD : Target.BAD;
  }

  
  public JPanel fixPanel() {
    Target.isOngoing = true;
    JPanel root = new JPanel();
    root.setBackground(Color.BLACK);

    JButton b = new JButton("Press me to return");
    b.addActionListener(e -> {
      b.isGood = true;
      super.image = Data.Images.target(Target.GOOD);
      Target.isOngoing = false;
      notifyAll();
    });

    return root;
  }
}