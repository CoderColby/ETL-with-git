

public class Key extends AbstractItem {

  public static final String TAG = "Key";
  public static final byte DEFAULT = 0;

  private byte ID;

  public Key(GridCell gridCell, byte startCondition) {
    super(Key.TAG + ":" + startCondition, gridCell, Data.Images.Item.key(startCondition));
    this.ID = startCondition;
  }


  public byte getID() {
    return ID;
  }

  public void cycleOptions() {
    ID = (byte) (++ID % 10);
    super.setImage(Data.Images.Item.key(ID).getImage());
    super.identifier = Key.TAG + ":" + ID;
  }
}