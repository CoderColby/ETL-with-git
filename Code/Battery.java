import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;


public class Battery extends AbstractItem {

  public static final String TAG = "Battery";
  public static final byte DEFAULT = 5;

  private byte energy;

  public Battery(GridCell gridCell, byte startCondition) {
    super(Battery.TAG + ":" + startCondition, gridCell, Data.Images.Item.battery);
    this.energy = startCondition;
  }

  public int getEnergy() {
    return energy;
  }

  public void cycleOptions() {
    SpinnerModel model = new SpinnerNumberModel(5, 0, 100, 1);
    JSpinner spinner = new JSpinner(model);
    int option = JOptionPane.showConfirmDialog(super.gridCell.getGameBoard(), spinner, "Energy Level", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      energy = (byte) spinner.getValue();
      super.identifier = Battery.TAG + ":" + energy;
    }
  }
}