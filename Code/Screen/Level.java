import java.io.File;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.util.Scanner;


public class Level extends JPanel {

  public static boolean returnToMenu;
  public static boolean goToNextLevel;

  private JLabel jlbl_energyAmt;
  private JLabel jlbl_genNum;
  
  private String startEnergy;
  private GameBoard levelBoard;

  
  public Level(File levelFile, boolean isCustom) {
    Level.returnToMenu = false;
    Level.goToNextLevel = false;
    this.levelFile = levelFile;
    super.setFocusable(true);
    super.requestFocus();

    Scanner fileIn = new Scanner(levelFile);
    String levelTitle = fileIn.nextLine().trim();
    startEnergy = fileIn.nextLine().trim();
    int perfectEnergy = fileIn.nextInt(); fileIn.nextLine();

    String[][] boardData = new String[100][5];

    for (int i = 0; i < 100; i++)
      String[i] = fileIn.nextLine().split();
    
    levelBoard = new GameBoard(boardData, Integer.parseInt(startEnergy), );
    fileIn.close();

    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    // Label for level title
    JLabel jlbl_levelTitle = new JLabel(levelTitle, SwingConstants.CENTER);
    jlbl_levelTitle.setBounds(0, 0, 1000, 60);
    jlbl_levelTitle.setFont(Data.Fonts.header2);

    // Label for level name
    String levelDesc = (isCustom) ? "made by " + levelFile.getParent().getName() : levelFile.getName().substring("level".length(), levelFile.getName().indexOf('.'));
    JLabel jlbl_levelName = new JLabel("Level " + levelDesc, SwingConstants.CENTER);
    jlbl_levelName.setBounds(0, 130, 350, 50);
    jlbl_levelName.setFont(Data.Fonts.dataLabel);

    // Label for energy level
    JLabel jlbl_energyLabel = new JLabel("Remaining Energy:", SwingConstants.CENTER);
    jlbl_energyLabel.setBounds(0, 200, 350, 30);
    jlbl_energyLabel.setFont(Data.Fonts.dataLabel);

    // Label for perfect energy level
    JLabel jlbl_perfectEnergy = new JLabel("Perfect Energy: " + perfectEnergy);
    jlbl_perfectEnergy.setBounds(230, 235, 100, 20);
    jlbl_perfectEnergy.setForeground(Data.Colors.perfectLevel);
    jlbl_perfectEnergy.setFont(new Font("Monospace", Font.PLAIN, 12));

    // Value for energy level
    jlbl_energyAmt = new JLabel(startEnergy, SwingConstants.CENTER);
    jlbl_energyAmt.setBounds(0, 230, 350, 30);
    jlbl_energyAmt.setFont(Data.Fonts.dataLabel);

    // Label for num of generators
    JLabel jlbl_genLabel = new JLabel("Generators Active:", SwingConstants.CENTER);
    jlbl_genLabel.setBounds(0, 300, 350, 30);
    jlbl_genLabel.setFont(Data.Fonts.dataLabel);

    // Value for num of generators
    jlbl_genNum = new JLabel("0", SwingConstants.CENTER);
    jlbl_genNum.setBounds(0, 330, 350, 30); // FIX ME
    jlbl_genNum.setFont(Data.Fonts.dataLabel);

    // Label for total num of generators
    JLabel jlbl_genTotal = new JLabel("/" + levelBoard.getNumOfGen(), SwingConstants.CENTER);
    jlbl_genTotal.setBounds(0, 330, 350, 30); // FIX ME
    jlbl_genTotal.setFont(Data.Fonts.dataLabel);

    // Button to restart
    JButton jbtn_restart = new JButton("Restart");
    jbtn_restart.setBounds(20, 430, 310, 50);
    jbtn_restart.setBackground(Data.Colors.buttonBackground);
    jbtn_restart.setFont(Data.Fonts.menuButton);
    jbtn_restart.addActionListener(e -> {
      resetLevel();
    });

    // Button to menu
    JButton jbtn_menu = new JButton("Quit");
    jbtn_menu.setBounds(20, 500, 310, 50);
    jbtn_menu.setBackground(Data.Colors.buttonBackground);
    jbtn_menu.setFont(Data.Fonts.menuButton);
    jbtn_menu.addActionListener(e -> {
      returnToMenu = true;
      notifyAll();
    });

    // JLabel note = new JLabel("<html>" + fileContents[0] + "</html>", SwingConstants.CENTER);
    // note.setBounds(10, 580, 330, 150);
    // note.setFont(new Font("Monospace", Font.ITALIC, 20));

    root.add(jlbl_levelTitle);
    root.add(jlbl_levelName);
    root.add(jlbl_energyLabel);
    root.add(jlbl_perfectEnergy);
    root.add(jlbl_energyAmt);
    root.add(jlbl_genLabel);
    root.add(jlbl_genNum);
    root.add(jbtn_restart);
    root.add(jbtn_menu);
    // root.add(note);

    root.add(levelBoard);

    /////////////////////////////////////////// i fukin dunno wutz aftur dis

    // Human human = new Human(floorGrid);
    KeyListener kl = new KeyAdapter() {
      public void keyPressed(KeyEvent event) {
        System.out.println(event.getKeyChar());
      }
    };

    root.addKeyListener(kl);

    class keyAction extends AbstractAction {

      private int directionMinus2;
      
      public keyAction(int keyPressed) {
        this.direction = keyPressed - 35;
      }

      public void actionPerformed(ActionEvent e) {
        levelBoard.move(floorGrid, direction - 4 * (direction / 2));
        if ()
      }
    }

    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("SPACE"), ACTIVATE_GEN);
    root.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD0"), ACTIVATE_GEN);

    root.getActionMap().put(MOVE_UP, new keyAction(3));
    root.getActionMap().put(MOVE_DOWN, new keyAction(1));
    root.getActionMap().put(MOVE_LEFT, new keyAction(2));
    root.getActionMap().put(MOVE_RIGHT, new keyAction(0));
    root.getActionMap().put(ACTIVATE_GEN, new keyAction(4));

    super.add(root);
    
  }

  
  public void setEnergy(int currentEnergy) {
    jlbl_energyAmt.setText(Integer.toString(currentEnergy));
  }

  public void setGenNum(int genNum) {
    jlbl_genNum.setText(Integer.toString(genNum));
  }

  public void playerDeath() {
    // Disable key press events and display message "YOU DIED" over floor with floor still visible
  }



  private void reset() {
    Level.returnToMenu = false;
    Level.goToNextLevel = false;
    this.levelFile = levelFile;
    
    levelBoard.reset();

    jlbl_energyAmt.setText(startEnergy);

    jlbl_genNum.setText("0");
  }
}