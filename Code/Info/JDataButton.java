import javax.swing.JButton;
import java.io.File;


public class JDataButton extends JButton {

  private int ID;
  private File file;
  
  public JDataButton(int ID, boolean levelUnlocked, boolean levelPerfect, int column, int row) { // This constructor is for buttons shown in the main menu

    this.ID = ID;
    super.setBounds(column * 150, row * 150, 150, 150);
    
    if (levelUnlocked) {
      super.setText(Integer.tostring(ID));
      super.setFont(Data.Fonts.menuLevelButton);
      super.addActionListener(e -> {
        Main.mainWindow.startLevelSequence(this.ID);
      });
    } else {
      super.setText(null);
      super.setIcon(Data.Images.Other.lock);
      super.setEnabled(false);
    }

    if (levelPerfect) {
      super.setBackground(Data.Colors.perfectLevel);
      super.setBorder(BorderFactory.createLineBorder(Data.Colors.perfectBorder, 7));
    } else {
      super.setBackground(Data.Colors.standardLevel);
      super.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
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