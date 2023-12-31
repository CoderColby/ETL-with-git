import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.AbstractAction;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Level extends JPanel {

  public static boolean returnToMenu;
  public static boolean goToNextLevel;

  private JLabel jlbl_energyAmt;
  private JLabel jlbl_genNum;
  private JButton jbtn_next;
  
  private String startEnergy;
  private GameWindow window;
  private GameBoard levelBoard;
  private String levelTitle;
  private File levelFile;
  private int levelID;
  private boolean isCustom;
  private boolean enableInputs;

  class InvalidLevelException extends RuntimeException {};

  
  public Level(File levelFile, boolean isCustom, GameWindow window, int levelID) {
    Level.returnToMenu = false;
    Level.goToNextLevel = false;
    this.levelFile = levelFile;
    this.window = window;
    this.isCustom = isCustom;
    this.levelID = levelID;
    this.enableInputs = true;
    super.setFocusable(true);
    super.requestFocus();

    Scanner fileIn = null;
    try {
      fileIn = new Scanner(levelFile);
    } catch (FileNotFoundException e) {
      System.out.println("Level file not found");
      System.exit(1);
    }
    levelTitle = fileIn.nextLine().trim();
    startEnergy = fileIn.nextLine().trim();

    String[][] boardData = new String[100][5];

    for (int i = 0; i < 100; i++)
      boardData[i] = fileIn.nextLine().split(" ");
    fileIn.close();
    
    levelBoard = new GameBoard(boardData, Integer.parseInt(startEnergy), this, false);
    if (!levelBoard.isValidLayout())
      throw new InvalidLevelException();
    levelBoard.setLocation(350, 100);
    levelBoard.setVisible(true);

    // Base panel
    super.setLayout(null);
    super.setBackground(Color.WHITE);
    super.setBounds(window.getContentPane().getBounds());

    // Label for level title
    JLabel jlbl_levelTitle = new JLabel(levelTitle, SwingConstants.CENTER);
    jlbl_levelTitle.setBounds(0, 0, 1000, 60);
    jlbl_levelTitle.setFont(Data.Fonts.header2);

    // Label for level name
    String levelDesc = (isCustom) ? "made by " + levelFile.getParentFile().getName() : Integer.toString(levelID);
    JLabel jlbl_levelName = new JLabel("Level " + levelDesc, SwingConstants.CENTER);
    jlbl_levelName.setBounds(0, 130, 350, 50);
    jlbl_levelName.setFont(Data.Fonts.dataLabel);

    // Label for energy level
    JLabel jlbl_energyLabel = new JLabel("Remaining Energy:", SwingConstants.CENTER);
    jlbl_energyLabel.setBounds(0, 200, 350, 30);
    jlbl_energyLabel.setFont(Data.Fonts.dataLabel);

    // // Label for perfect energy level
    // JLabel jlbl_perfectEnergy = new JLabel("Perfect Energy: " + perfectEnergy);
    // jlbl_perfectEnergy.setBounds(230, 235, 100, 20);
    // jlbl_perfectEnergy.setForeground(Data.Colors.perfectLevel);
    // jlbl_perfectEnergy.setFont(new Font("Monospace", Font.PLAIN, 12));

    // Value for energy level
    jlbl_energyAmt = new JLabel(startEnergy, SwingConstants.CENTER);
    jlbl_energyAmt.setBounds(0, 230, 350, 30);
    jlbl_energyAmt.setFont(Data.Fonts.dataLabel);

    // Label for num of generators
    JLabel jlbl_genLabel = new JLabel("Generators Active:", SwingConstants.CENTER);
    jlbl_genLabel.setFont(Data.Fonts.dataLabel);

    // Value for num of generators
    jlbl_genNum = new JLabel(Integer.toString(levelBoard.getNumOfGoodTargets()), SwingConstants.CENTER);
    jlbl_genNum.setFont(Data.Fonts.dataLabel);

    // Label for total num of generators
    JLabel jlbl_genTotal = new JLabel("/ " + levelBoard.getNumOfTargets(), SwingConstants.CENTER);
    jlbl_genTotal.setFont(Data.Fonts.dataLabel);

    JPanel jpnl_genLabels = new JPanel();
    jpnl_genLabels.setBounds(30, 330, 290, 30);
    jpnl_genLabels.setOpaque(false);
    jpnl_genLabels.add(jlbl_genLabel);
    jpnl_genLabels.add(jlbl_genNum);
    jpnl_genLabels.add(jlbl_genTotal);

    // Button to restart
    JButton jbtn_restart = new JButton("Restart");
    jbtn_restart.setBounds(20, 430, 310, 50);
    jbtn_restart.setBackground(Data.Colors.buttonBackground);
    jbtn_restart.setFont(Data.Fonts.menuButton);
    jbtn_restart.addActionListener(e -> {
      reset();
    });

    // Button to menu
    JButton jbtn_menu = new JButton("Quit");
    jbtn_menu.setBounds(20, 500, 310, 50);
    jbtn_menu.setBackground(Data.Colors.buttonBackground);
    jbtn_menu.setFont(Data.Fonts.menuButton);
    jbtn_menu.addActionListener(e -> {
      Level.returnToMenu = true;
    });

    // Button to next level
    if (!isCustom) {
      jbtn_next = new JButton();
      jbtn_next.setHorizontalAlignment(SwingConstants.CENTER);
      if (window.getUser().getLevels() > levelID)
        jbtn_next.setText("Next Level");
      else {
        jbtn_next.setIcon(new ImageIcon(new ImageIcon(Data.Images.Other.lock).getImage().getScaledInstance(40, 40, Image.SCALE_FAST)));
        jbtn_next.setDisabledIcon(new ImageIcon(new ImageIcon(Data.Images.Other.lock).getImage().getScaledInstance(40, 40, Image.SCALE_FAST)));
        jbtn_next.setEnabled(false);
      }
      jbtn_next.setBounds(20, 570, 310, 50);
      jbtn_next.setBackground(Data.Colors.buttonBackground);
      jbtn_next.setFont(Data.Fonts.menuButton);
      jbtn_next.addActionListener(e -> {
        Level.goToNextLevel = true;
      });
      super.add(jbtn_next);
    }
      
    // JLabel note = new JLabel("<html>" + fileContents[0] + "</html>", SwingConstants.CENTER);
    // note.setBounds(10, 580, 330, 150);
    // note.setFont(new Font("Monospace", Font.ITALIC, 20));

    super.add(jlbl_levelTitle);
    super.add(jlbl_levelName);
    super.add(jlbl_energyLabel);
    // root.add(jlbl_perfectEnergy);
    super.add(jlbl_energyAmt);
    // root.add(jlbl_genLabel);
    // root.add(jlbl_genNum);
    super.add(jpnl_genLabels);
    super.add(jbtn_restart);
    super.add(jbtn_menu);
    // root.add(note);

    super.add(levelBoard);
    jpnl_genLabels.revalidate();
    super.repaint();

    /////////////////////////////////////////// i fukin dunno wutz aftur dis

    super.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {

        if (Level.this.enableInputs) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
              Level.this.levelBoard.move((byte) 0);
              break;
            case KeyEvent.VK_DOWN:
              Level.this.levelBoard.move((byte) 1);
              break;
            case KeyEvent.VK_LEFT:
              Level.this.levelBoard.move((byte) 2);
              break;
            case KeyEvent.VK_UP:
              Level.this.levelBoard.move((byte) 3);
              break;
            case KeyEvent.VK_NUMPAD0:
              Level.this.levelBoard.toggleTarget();
              break;
          }
        }
      }
    });
    
  }

  
  public void setEnergy(int currentEnergy) {
    jlbl_energyAmt.setText(Integer.toString(currentEnergy));
  }

  public void setGenNum(int genNum) {
    jlbl_genNum.setText(Integer.toString(genNum));
  }

  private JLabel cover;
  
  public void playerDeath() {
    cover = new JLabel("You were caught!", SwingConstants.CENTER);
    cover.setFont(Data.Fonts.header1);
    cover.setForeground(Color.WHITE);
    cover.setBackground(new Color(60, 60, 180, 60));
    cover.setBounds(levelBoard.getBounds());
    cover.setOpaque(true);
    super.add(cover);
    super.setComponentZOrder(cover, 0);
    this.enableInputs = false;
    levelBoard.repaint();
    super.repaint();
  }

  public void playerWon(boolean wasPerfect) {
    cover = new JLabel("You escaped" + ((wasPerfect) ? " perfectly" : "") + "!", SwingConstants.CENTER);
    cover.setFont(Data.Fonts.header1);
    cover.setForeground(Color.WHITE);
    cover.setBackground(new Color(60, 60, 180, 60));
    cover.setBounds(levelBoard.getBounds());
    cover.setOpaque(true);
    super.add(cover);
    super.setComponentZOrder(cover, 0);
    this.enableInputs = false;
    
    if (!isCustom) {
      jbtn_next.setIcon(null);
      jbtn_next.setText("Next Level");
      jbtn_next.setEnabled(true);
      window.getUser().setLevels(levelID);
      if (wasPerfect)
        window.getUser().addPerfectLevel(levelID);
    }
    
    levelBoard.repaint();
    super.repaint();
  }

  public boolean isCustom() {
    return isCustom;
  }

  public int getNum() {
    return Integer.parseInt(levelFile.getName().substring("level".length(), levelFile.getName().length() - ".txt".length()));
  }

  // public User getUser() {
  //   return window.getUser();
  // }

  public File getLevelFile() {
    return levelFile;
  }

  public String getTitle() {
    return levelTitle;
  }

  private void reset() {
    Level.returnToMenu = false;
    Level.goToNextLevel = false;
    Animation.isOngoing = false;
    this.enableInputs = true;
    
    levelBoard.reset();
    if (cover != null)
      super.remove(cover);
    jlbl_energyAmt.setText(startEnergy);
    jlbl_genNum.setText(Integer.toString(levelBoard.getNumOfGoodTargets()));
    super.revalidate();
    super.repaint();

    super.requestFocusInWindow();
  }

  public GameBoard getGameBoard() {
    return levelBoard;
  }
}