import javax.swing.JButton;
import java.io.File;


public class JDataButton extends JButton {

  private int ID;
  private File file;
  
  public JDataButton(int ID) { // This constructor takes in a level number for in the menu
    super(Integer.toString(ID));
    this.ID = ID;
  }

  public JDataButton(File file) { // This constructor takes in a file for choosing custom levels
    super(file.getName());
    this.file = file;
  }

  public int getID() {
    return ID;
  }

  public File getFile() {
    return file;
  }
}