import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.ImageIcon;


public class Battery extends AbstractItem {

  public static final String TAG = "Battery";
  public static final byte DEFAULT = 5;

  private byte energy;

  
  public Battery() {
    super(Battery.TAG + ":" + Battery.DEFAULT, Data.Images.Item.battery);
  }
  
  public Battery(GridCell gridCell, byte startCondition) {
    super(Battery.TAG + ":" + startCondition, gridCell, Data.Images.Item.battery);
    this.energy = startCondition;
  }

  public int getEnergy() {
    return energy;
  }

  public void cycleOptions() {
    SpinnerModel model = new SpinnerNumberModel(energy, 0, 100, 1);
    JSpinner spinner = new JSpinner(model);
    int option = JOptionPane.showConfirmDialog(null, spinner, "Energy Level", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      energy = ((Integer) spinner.getValue()).byteValue();
      super.identifier = Battery.TAG + ":" + energy;
    }
  }

  public String getInfo() {
    return "Obtaining this will increase the player's energy level by some amount (configured beforehand, not random).";
  }
}