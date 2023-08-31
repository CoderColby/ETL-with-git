

public class Battery extends AbstractItem {

  public static final String TAG = "Battery";

  private byte energy;

  public Battery(GridCell gridCell, byte startCondition) {
    super(Battery.TAG + ":" + startCondition, gridCell, Data.Images.battery);
    this.energy = startCondition;
  }

  public int getEnergy() {
    return energy;
  }
}