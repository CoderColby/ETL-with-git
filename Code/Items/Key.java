

public class Key extends AbstractItem {

  public static final String TAG = "Key";

  private byte ID;

  public Key(GridCell gridCell, byte startCondition) {
    super(Key.TAG + ":" + startCondition, gridCell, Data.Images.key(startConditions));
    this.ID = startCondition
  }


  public byte getID() {
    return ID;
  }

  public void cycleOptions() {
    ID = ++ID % 10;
    super.image = Data.Images.key(ID);
    super.identifier = Key.TAG + ":" + ID;
  }
}