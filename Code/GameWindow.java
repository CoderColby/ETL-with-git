import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.lang.Character;
import java.lang.Object;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Action;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Image;
import java.awt.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class GameWindow extends JFrame {

  private User currentUser;

  private class LevelPanel extends JPanel {
    private int groupNum;
    private final int MAX_GROUP_NUM = (Data.Utilities.numOfLevels - 1) / 10;
    private Rectangle bounds;

    public LevelPanel(int xPos, int yPos) {
      this.bounds = new Rectangle(xPos, yPos, 150 * 4, 150 * 3);
      
      super.setLayout(null);
      super.setBounds(this.bounds);
      super.setBackground(Color.WHITE);
    }
    
    public int getGroupNum() {
      return this.groupNum;
    }

    public void incrememntGroupNum() {
      groupNum++;
      if (this.groupNum > this.MAX_GROUP_NUM)
        this.groupNum = 0;
      this.updateLevelGrid();
    }

    public void decrementGroupNum() {
      groupNum--;
      if (this.groupNum < 0)
        this.groupNum = this.MAX_GROUP_NUM;
      this.updateLevelGrid();
    }

    public void updateLevelGrid() {
      super.removeAll();
      for (int row = 0; row < 3; row++) {
        int selector = row / 2;
        for (int column = selector, int levelID = levelGroup * 10 + 4 * row; column < 4 - selector && levelID <= Data.Utilities.numOfLevels; column++, levelID++)
          super.add(new JDataButton(levelID, currentUser.getLevels() >= levelID, currentUser.getPerfectLevels().contains(levelID), column, row));
      }
      super.revalidate();
    }
  }
  
  private LevelPanel jpnl_menuLevels;
  private int customPage;
  private GameBoard gameBoard;
  

  public GameWindow() {
    super("ESCAPE THE LAB 2");
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.setSize(1000, 820);
    super.setPreferredSize(new Dimension(1000, 820));
    super.getContentPane().setLayout(null);
    super.getContentPane().add(createLogin());
    super.setVisible(true);
  }

  
  public JPanel createLogin() { // Creates login page for new and old users

    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    /////////////////////////////////////////////////////////// LOG IN PORTION

    // Title "Log In"
    JLabel jlbl_loginTitle = new JLabel("Log In", SwingConstants.CENTER);
    jlbl_loginTitle.setBounds(180, 80, 140, 40);
    jlbl_loginTitle.setFont(Data.Fonts.header2);

    // Instruction "Username:"
    JLabel jlbl_loginUsername = new JLabel("Username:");
    jlbl_loginUsername.setBounds(100, 150, 100, 25);
    jlbl_loginUsername.setFont(Data.Fonts.textLabel);

    // Instruction "Password:"
    JLabel jlbl_loginPassword = new JLabel("Password:");
    jlbl_loginPassword.setBounds(100, 230, 100, 25);
    jlbl_loginPassword.setFont(Data.Fonts.textLabel);

    // Field for username
    JTextField jtxf_loginUsername = new JTextField();
    jtxf_loginUsername.setBounds(100, 175, 300, 25);
    jtxf_loginUsername.setFont(Data.Fonts.textField);

    // Field for password
    JTextField jtxf_loginPassword = new JTextField();
    jtxf_loginPassword.setBounds(100, 255, 300, 25);
    jtxf_loginPassword.setFont(Data.Fonts.textField);

    // Button to log in
    JButton jbtn_loginButton = new JButton("Log in");
    jbtn_loginButton.setBounds(170, 650, 160, 40);
    jbtn_loginButton.setBackground(Data.Colors.buttonBackground);
    jbtn_loginButton.setFont(Data.Fonts.menuButton);
    jbtn_loginButton.addActionListener(l -> {
      String username = jtxf_loginUsername.getText().trim();
      String password = jtxf_loginPassword.getText();
      String errorMessages = "";

      // Validate input
      errorMessages += User.isValidUsername(username);
      errorMessages += User.isValidPassword(password);
      if (errorMessages.isEmpty()) {
        if (!User.isUserExist(username))
          errorMessages = "The username you entered does not exist! Try signing up if you don't have an account yet.";
        else if (!User.isCorrectPassword(username, password))
          errorMessage = "This user has a password different than the one provided, please try again.";
      }
      if (errorMessages.isEmpty()) {
        currentUser = newUser(new File(Data.getUserPath(username)));
        replace(createMenu(0));
      } else
        JOptionPane.showMessageDialog(this, errorMessages.trim());
    });

    // Add all elements to base panel
    root.add(jlbl_loginTitle);
    root.add(jlbl_loginUsername);
    root.add(jlbl_loginPassword);
    root.add(jtxf_loginUsername);
    root.add(jtxf_loginPassword);
    root.add(jbtn_loginButton);

    /////////////////////////////////////////////////////////// SIGN UP PORTION

    // Title "Sign Up"
    JLabel jlbl_signupTitle = new JLabel("Sign Up", SwingConstants.CENTER);
    jlbl_signupTitle.setBounds(650, 80, 200, 40);
    jlbl_signupTitle.setFont(Data.Fonts.header2);

    // Instruction "Username:"
    JLabel jlbl_signupUsername = new JLabel("Username:");
    jlbl_signupUsername.setBounds(600, 150, 100, 25);
    jlbl_signupUsername.setFont(Data.Fonts.textLabel);

    // Instruction "Password:"
    JLabel jlbl_signupPassword = new JLabel("Password:");
    jlbl_signupPassword.setBounds(600, 230, 100, 25);
    jlbl_signupPassword.setFont(Data.Fonts.textLabel);

    // Instruction "Confirm Password:"
    JLabel jlbl_signupConfirmPassword = new JLabel("Confirm Password:");
    jlbl_signupConfirmPassword.setBounds(600, 310, 200, 25);
    jlbl_signupConfirmPassword.setFont(Data.Fonts.textLabel);

    // Instruction "Name:"
    JLabel jlbl_signupName = new JLabel("Name:");
    jlbl_signupName.setBounds(600, 390, 100, 25);
    jlbl_signupName.setFont(Data.Fonts.textLabel);

    // Field for username
    JTextField jtxf_signupUsername = new JTextField();
    jtxf_signupUsername.setBounds(600, 175, 300, 25);
    jtxf_signupUsername.setFont(Data.Fonts.textField);

    // Field for password
    JTextField jtxf_signupPassword = new JTextField();
    jtxf_signupPassword.setBounds(600, 255, 300, 25);
    jtxf_signupPassword.setFont(Data.Fonts.textField);

    // Field for password confirmation
    JTextField jtxf_signupConfirmPassword = new JTextField();
    jtxf_signupConfirmPassword.setBounds(600, 335, 300, 25);
    jtxf_signupConfirmPassword.setFont(Data.Fonts.textField);

    // Field for name
    JTextField jtxf_signupName = new JTextField();
    jtxf_signupName.setBounds(600, 415, 300, 25);
    jtxf_signupName.setFont(Data.Fonts.textField);

    // Button to sign up
    JButton jbtn_signupButton = new JButton("Sign up");
    jbtn_signupButton.setBounds(670, 650, 160, 40);
    jbtn_signupButton.setBackground(Data.Colors.buttonBackground);
    jbtn_signupButton.setFont(Data.Fonts.menuButton);
    jbtn_signupButton.addActionListener(l -> {
      String username = jtxf_signupUsername.getText().trim();
      String password = jtxf_signupPassword.getText();
      String realName = jtxf_signupName.getText();
      String errorMessages = "";

      // Validate input
      errorMessages += User.isValidUsername(username);
      if (errorMessages.isEmpty() && User.isUserExist(username))
          errorMessages = "This username already exists! Please try a different one or log in.\n";
      else {
        errorMessages += User.isValidPassword(password);
        if (realName.isEmpty())
          errorMessages += "Please provide your name.\n";
        if (!password.equals(jtxf_signupConfirmPassword.getText()))
          errorMessages += "Please ensure that both passwords are identical.\n";
      }
      if (errorMessages.isEmpty()) {
        currentUser = new User(username, password, realName, Data.getUserFilePath(username));
        replace(createMenu(0));
      } else
        JOptionPane.showMessageDialog(this, errorMessages);
    });

    // Add all elements to base panel
    root.add(jlbl_signupTitle);
    root.add(jlbl_signupUsername);
    root.add(jlbl_signupPassword);
    root.add(jlbl_signupConfirmPassword);
    root.add(jlbl_signupName);
    root.add(jtxf_signupUsername);
    root.add(jtxf_signupPassword);
    root.add(jtxf_signupConfirmPassword);
    root.add(jtxf_signupName);
    root.add(jbtn_signupButton);

    return root;
    
  } // Login page for new and existing users, creates new user object


  public JPanel createMenu(int levelGroup) { // Creates menu page showing all unlocked levels, with custom level options and account settings
    
    // Base Panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);
    
    // JLabel escapeTitle = new JLabel("ESCAPE");
    // escapeTitle.setBounds(20, 20, 200, 55);
    // escapeTitle.setFont(new Font("Monospaced", Font.PLAIN, 55));
    // escapeTitle.setForeground(new Color(100, 100, 100));

    // JLabel theTitle = new JLabel("the");
    // theTitle.setBounds(20, 75, 45, 25);
    // theTitle.setFont(new Font("Monospaced", Font.BOLD, 24));

    // JLabel labTitle = new JLabel("LAB");
    // labTitle.setBounds(65, 75, 155, 80);
    // labTitle.setFont(new Font("Monospaced", Font.PLAIN, 85));
    // labTitle.setForeground(new Color(200, 60, 60));

    // root.add(escapeTitle);
    // root.add(theTitle);
    // root.add(labTitle);

    {
    // ETL logo
    JLabel jlbl_logo = new JLabel(Data.Images.Other.logo);
    jlbl_logo.setBounds(20, 20, 65 + 155, 75 + 80);

    // Label for name
    JLabel jlbl_nameLabel = new JLabel("Name:");
    jlbl_nameLabel.setBounds(640, 20, 60, 45);
    jlbl_nameLabel.setFont(Data.Fonts.dataLabel);

    // Label for completed levels
    JLabel jlbl_completedLabel = new JLabel("Levels Completed:");
    jlbl_completedLabel.setBounds(640, 65, 205, 45);
    jlbl_completedLabel.setFont(Data.Fonts.dataLabel);

    // Label for perfect levels
    JLabel jlbl_perfectLabel = new JLabel("Perfect Levels:");
    jlbl_perfectLabel.setBounds(640, 110, 185, 45);
    jlbl_perfectLabel.setFont(Data.Fonts.dataLabel);

    // Value for name
    JLabel jlbl_nameField = new JLabel(currentUser.getRealName());
    jlbl_nameField.setBounds(860, 20, 120, 45);
    jlbl_nameField.setFont(Data.Fonts.dataLabel);

    // Value for completed levels
    JLabel jlbl_completedField = new JLabel((currentUser.getLevels() - 1) + "/" + Data.Utilites.numOfLevels);
    jlbl_completedField.setBounds(860, 65, 100, 45);
    jlbl_completedField.setFont(Data.Fonts.dataLabel);

    // Value for perfect levels
    JLabel jlbl_perfectField = new JLabel(currentUser.getPerfectLevels().size() + "/" + Data.Utilites.numOfLevels);
    jlbl_perfectField.setBounds(860, 110, 100, 45);
    jlbl_perfectField.setFont(Data.Fonts.dataLabel);

    // Add all elements to base panel
    root.add(jlbl_logo);
    root.add(jlbl_nameLabel);
    root.add(jlbl_completedLabel);
    root.add(jlbl_perfectLabel);
    root.add(jlbl_nameField);
    root.add(jlbl_completedField);
    root.add(jlbl_perfectField);

    JButton jbtn_create = new JButton("Create New");
    jbtn_create.setBounds(40, 670, 200, 80);
    jbtn_create.setBackground(Data.Colors.buttonBackground);
    jbtn_create.setFont(Data.Fonts.menuButton);
    jbtn_create.addActionListener(l -> {
      replace(createLevelDesigner());
    });

    JButton jbtn_load = new JButton("Load Custom");
    jbtn_load.setBounds(760, 670, 200, 80);
    jbtn_load.setBackground(Data.Colors.buttonBackground);
    jbtn_load.setFont(Data.Fonts.menuButton);
    jbtn_load.addActionListener(l -> {
      replace(createLevelBrowser());
    });

    JButton jbtn_settings = new JButton("Settings");
    jbtn_settings.setBounds(420, 690, 160, 40);
    jbtn_settings.setBackground(Data.Colors.buttonBackground);
    jbtn_settings.setFont(Data.Fonts.menuButton);
    jbtn_settings.addActionListener(l -> {
      replace(createSettings());
    });

    root.add(jbtn_create);
    root.add(jbtn_load);
    root.add(jbtn_settings);
    } // Menu stuff

    {
    // Level panel
    jpnl_menuLevels = new LevelPanel(levelGroup);
    root.add(jpnl_menuLevels);

    // Button for level navigation 
    JButton jbtn_leftArrow = new JButton(new ImageIcon(Data.Images.Other.leftNavArrow.getImage().getScaledInstance(100, 180, Image.SCALE_FAST)));
    jbtn_leftArrow.setBounds(50, 335, 100, 180);
    jbtn_leftArrow.setBorderPainted(false);
    jbtn_leftArrow.addActionListener(e -> {
      jpnl_menuLevels.decrementGroupNum();
    });

    // Button for level navigation
    JButton jbtn_rightArrow = new JButton(new ImageIcon(Data.Images.Other.rightNavArrow.getImage().getScaledInstance(100, 180, Image.SCALE_FAST)));
    jbtn_rightArrow.setBounds(840, 335, 100, 180);
    jbtn_rightArrow.setBorderPainted(false);
    jbtn_rightArrow.addActionListener(e -> {
      jpnl_menuLevels.incrementGroupNum();
    });

    root.add(jbtn_leftArrow);
    root.add(jbtn_rightArrow);
    } // Level stuff

    // currentScreen = "menu";
    return root;
  } // Creates menu with level navigation and user info with custom levels


  public void startLevelSequence(int startingLevel) {
    int level;
    boolean statusGood = true;
    for (level = startingLevel; level <= Data.numOfLevels && statusGood; level++)
      statusGood = displayLevel(new File(Data.Utilities.getLevelFilePath(level)));

    createMenu((level - 1) / 10)
  }


  public boolean displayLevel (File levelFile) {
    Scanner fileIn = new Scanner(levelFile);
    String levelTitle = fileIn.nextLine().trim();
    int startEnergy = fileIn.nextInt(); fileIn.nextLine();
    int perfectMoves = fileIn.nextInt(); fileIn.nextLine();
    GameBoard levelBoard = new GameBoard(fileIn);

    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    /////////////////////////////////////////// i fukin dunno wutz aftur dis
    String[] fileContents = null;
    try {
      fileContents = fileReader(levelFile);
      root.repaint(gameGrid(Arrays.copyOfRange(fileContents, fileStart, 100 + fileStart), 350, 130));
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: file not found");
    }

    JLabel title = new JLabel(fileContents[1], SwingConstants.CENTER);
    title.setBounds(0, 0, 1000, 60);
    title.setFont(new Font("Monospace", Font.PLAIN, 30));

    JLabel name = new JLabel(fileContents[2], SwingConstants.CENTER);
    name.setBounds(0, 130, 350, 50);
    name.setFont(Data.Fonts.dataLabel);

    JLabel energyLabel = new JLabel("Remaining Energy:", SwingConstants.CENTER);
    energyLabel.setBounds(0, 200, 350, 30);
    energyLabel.setFont(Data.Fonts.dataLabel);

    JLabel perfectEnergy = new JLabel(fileContents[4]);
    perfectEnergy.setBounds(230, 235, 100, 20);
    perfectEnergy.setForeground(Data.Colors.perfectLevel);
    perfectEnergy.setFont(new Font("Monospace", Font.PLAIN, 12));

    JLabel energyAmt = new JLabel(fileContents[3], SwingConstants.CENTER);
    energyAmt.setBounds(0, 230, 350, 30);
    energyAmt.setFont(Data.Fonts.dataLabel);

    JLabel genLabel = new JLabel("Generators Active:", SwingConstants.CENTER);
    genLabel.setBounds(0, 300, 350, 30);
    genLabel.setFont(Data.Fonts.dataLabel);

    int activeGen = 0;
    JLabel genAmt = new JLabel(activeGen + "/" + numOfGen, SwingConstants.CENTER);
    genAmt.setBounds(0, 330, 350, 30);
    genAmt.setFont(Data.Fonts.dataLabel);

    JButton restart = new JButton("Restart");
    restart.setBounds(20, 430, 310, 50);
    restart.setBackground(Data.Colors.buttonBackground);
    restart.setFont(Data.Fonts.menuButton);
    restart.addActionListener(e -> {
      replace(createLevel(levelFile));
    });

    JButton menu = new JButton("Menu");
    menu.setBounds(20, 500, 310, 50);
    menu.setBackground(Data.Colors.buttonBackground);
    menu.setFont(Data.Fonts.menuButton);
    menu.addActionListener(e -> {
      replace(createMenu());
    });

    JLabel note = new JLabel("<html>" + fileContents[0] + "</html>", SwingConstants.CENTER);
    note.setBounds(10, 580, 330, 150);
    note.setFont(new Font("Monospace", Font.ITALIC, 20));

    root.add(title);
    root.add(name);
    root.add(energyLabel);
    root.add(perfectEnergy);
    root.add(energyAmt);
    root.add(genLabel);
    root.add(genAmt);
    root.add(restart);
    root.add(menu);
    root.add(note);

    Human human = new Human(floorGrid);
    // KeyListener kl = new KeyAdapter() {
    //   public void keyPressed(KeyEvent event) {
    //     System.out.println(event.getKeyChar());
    //   }
    // };

    // root.addKeyListener(kl);

    class keyAction extends AbstractAction {

      private int keyPressed;
      
      public keyAction(int keyPressed) {
        this.keyPressed = keyPressed;
      }

      public void actionPerformed(ActionEvent e) {
        human.move(floorGrid, keyPressed);
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
    

    currentScreen = "Level";
    return root;
  }

  


  public JPanel createSettings() { // Ability to log out or delete account along with change user characteristics
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    JLabel jlbl_title = new JLabel("SETTINGS", SwingConstants.CENTER);
    jlbl_title.setBounds(350, 30, 300, 50);
    jlbl_title.setFont(Data.Fonts.header1);

    root.add(jlbl_title);

    JLabel jlbl_usernameLabel = new JLabel("Userame:");
    jlbl_usernameLabel.setBounds(350, 120, 120, 25);
    jlbl_usernameLabel.setFont(Data.Fonts.textLabel);

    JLabel jlbl_passwordLabel = new JLabel("Password:");
    jlbl_passwordLabel.setBounds(350, 200, 120, 25);
    jlbl_passwordLabel.setFont(Data.Fonts.textLabel);

    JLabel jlbl_nameLabel = new JLabel("Name:");
    jlbl_nameLabel.setBounds(350, 280, 100, 25);
    jlbl_nameLabel.setFont(Data.Fonts.textLabel);

    JTextField jtxf_usernameField = new JTextField(currentUser.getUserName());
    jtxf_usernameField.setBounds(350, 145, 300, 25);
    jtxf_usernameField.setFont(Data.Fonts.textField);

    JTextField jtxf_passwordField = new JTextField(currentUser.passwordDots()); // Don't show password, check password when applying settings instead
    jtxf_passwordField.setBounds(350, 225, 300, 25);
    jtxf_passwordField.setFont(Data.Fonts.textField);

    JTextField jtxf_nameField = new JTextField(currentUser.getRealName());
    jtxf_nameField.setBounds(350, 305, 300, 25);
    jtxf_nameField.setFont(Data.Fonts.textField);

    root.add(jlbl_usernameLabel);
    root.add(jlbl_passwordLabel);
    root.add(jlbl_nameLabel);
    root.add(jtxf_usernameField);
    root.add(jtxf_passwordField);
    root.add(jtxf_nameField);

    JButton jbtn_apply = new JButton("Apply Settings");
    jbtn_apply.setBounds(380, 385, 240, 55);
    jbtn_apply.setBackground(Data.Colors.buttonBackground);
    jbtn_apply.setFont(Data.Fonts.menuButton);
    jbtn_apply.addActionListener(e -> {
      if (jtxf_usernameField.getText().contains(" ") || jtxf_passwordField.getText().contains(" ") || jtxf_usernameField.getText().isEmpty() || jtxf_passwordField.getText().isEmpty() || jtxf_nameField.getText().isEmpty())
        JOptionPane.showMessageDialog(this, "Please make sure the username and password fields are filled WITHOUT spaces.");
      else {
        File userFile = new File("Users/" + currentUser.getUserName() + ".txt");
        userFile.delete();
        String oldUsername = currentUser.getUserName();
        currentUser = new User(jtxf_usernameField.getText(), jtxf_passwordField.getText(), jtxf_nameField.getText(), currentUser.getLevels(), currentUser.getPerfectLevels());
        try {
          replaceUsername(oldUsername, currentUser.getUserName());
        } catch (Exception exc) {
          System.out.println("Unkown error");
        }
        replace(createMenu());
      }
    });
    
    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(380, 475, 240, 55);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu());
    });
    
    JButton jbtn_logout = new JButton("Log Out");
    jbtn_logout.setBounds(380, 565, 240, 55);
    jbtn_logout.setBackground(Data.Colors.buttonBackground);
    jbtn_logout.setFont(Data.Fonts.menuButton);
    jbtn_logout.addActionListener(e -> {
      currentUser.updateFile(); // Just in case something was missed
      currentUser = null;
      replace(createLogin());
    });
    
    JButton jbtn_delete = new JButton("DELETE ACCOUNT");
    jbtn_delete.setBounds(380, 655, 240, 55);
    jbtn_delete.setBackground(Data.Colors.buttonBackground);
    jbtn_delete.setForeground(new Color(180, 0, 0));
    jbtn_delete.setFont(Data.Fonts.menuButton);
    jbtn_delete.addActionListener(e -> {
      int option = JOptionPane.showConfirmDialog(this, "All player data will be irretrievably lost and ownership over public custom levels will be forfeited.\nPress yes to permanently delete your account, press no to cancel.", "WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (option == JOptionPane.YES_OPTION) {
        File userFile = new File("../Users/" + currentUser.getUserName() + ".txt");
        userFile.delete();
        String oldUsername = currentUser.getUserName();
        currentUser = null;
        try {
          replaceUsername(oldUsername, "[deleted]");
        } catch (Exception exc) {
          System.out.println("Unkown error");
        }
        replace(createLogin());
      }
    });

    root.add(jbtn_apply);
    root.add(jbtn_cancel);
    root.add(jbtn_logout);
    root.add(jbtn_delete);

    // currentScreen = "settings";
    return root;
  } // Sets up settings page for editing and reviewing limited player data


  public void replaceUsername(String original, String current) throws FileNotFoundException {
    File directory = new File("../GameAssets/Levels/CustomLevels/");
    File[] files = directory.listFiles();
    for (File f : files) {
      Scanner fileIn = new Scanner(f);
      if (fileIn.nextLine().equals(original)) {
        ArrayList<String> fileContents = new ArrayList<String>();
        fileContents.add(current);
        while (fileIn.hasNextLine())
          fileContents.add(fileIn.nextLine());
        String last = fileContents.remove(fileContents.size() - 1);
        fileIn.close();
        PrintWriter fileOut = new PrintWriter(f);
        for (String s : fileContents) {
          fileOut.println(s);
        }
        fileOut.print(last);
        fileOut.close();
      }
    }
  } // Replaces username in custom levels so data is up to date and accurate


  public JPanel createLevelBrowser() {
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    JLabel jlbl_title = new JLabel("Select a Level:", SwingConstants.CENTER);
    jlbl_title.setBounds(300, 20, 400, 50);
    jlbl_title.setFont(Data.Fonts.header1);

    root.add(jlbl_title);
    customPage = 0;
    levelList = levelList(customPage, false);
    root.add(levelList);

    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(40, 700, 150, 60);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu());
    });

    JLabel jlbl_page = new JLabel(Integer.toString(customPage + 1), SwingConstants.CENTER);
    jlbl_page.setBounds(460, 710, 80, 40);
    jlbl_page.setBackground(Data.Colors.buttonBackground);
    jlbl_page.setOpaque(true);
    jlbl_page.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    jlbl_page.setFont(Data.Fonts.header2);

    JCheckBox jchk_privateOnly = new JCheckBox("My levels only", false);
    jchk_privateOnly.setBounds(770, 720, 150, 25);
    jchk_privateOnly.setOpaque(false);
    jchk_privateOnly.setFont(Data.Fonts.checkboxLabel);
    jchk_privateOnly.addActionListener(e -> {
      root.remove(levelList);
      levelList = levelList(0, privateOnly.isSelected());
      jlbl_page.setText("1");
      root.add(levelList);
      root.repaint();
      root.revalidate();
    });

    JButton jbtn_leftArrow = new JButton(new ImageIcon((new ImageIcon("Images/ArrowLeft.png")).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_leftArrow.setBounds(400, 700, 30, 60);
    jbtn_leftArrow.setBorderPainted(false);
    jbtn_leftArrow.addActionListener(e -> {
      if (customPage > 0) {
        root.remove(levelList);
        levelList = levelList(customPage - 1, jchk_privateOnly.isSelected());
        jlbl_page.setText(Integer.toString(Integer.parseInt(page.getText()) - 1));
        root.add(levelList);
        root.repaint();
        root.revalidate();
      }
    });

    JButton jbtn_rightArrow = new JButton(new ImageIcon((new ImageIcon("Images/ArrowRight.png")).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_rightArrow.setBounds(570, 700, 30, 60);
    jbtn_rightArrow.setBorderPainted(false);
    jbtn_rightArrow.addActionListener(e -> {
      File directory = new File("Custom/");
      File[] fileList = directory.listFiles();
      int levelCount = 0;
      for (int i = 0; i < fileList.length; i++) {
        try {
          Scanner fileIn = new Scanner(fileList[i]);
          if (!jchk_privateOnly.isSelected() || currentUser.getUserName().equals(fileIn.nextLine()))
            levelCount++;
          fileIn.close();
        } catch (FileNotFoundException f) {
          System.out.println("Unkown error");
        }
      }
      if (customPage < (levelCount - 1) / 8) {
        root.remove(levelList);
        levelList = levelList(customPage + 1, jchk_privateOnly.isSelected());
        jlbl_page.setText(Integer.toString(Integer.parseInt(page.getText()) + 1));
        root.add(levelList);
        root.repaint();
        root.revalidate();
      }
    });

    root.add(jbtn_cancel);
    root.add(jlbl_page);
    root.add(jbtn_leftArrow);
    root.add(jbtn_rightArrow);
    root.add(jchk_privateOnly);

    // currentScreen = "browser";
    return root;
  }


  public JPanel levelList(int customPage, boolean privateOnly) {
    this.customPage = customPage;
    JPanel list = new JPanel();
    list.setLayout(null);
    list.setBounds(80, 80, 840, 600);
    list.setBackground(Color.WHITE);

    File directory = new File("Custom/");
    File[] levelNames = directory.listFiles();
    int skips = 0;
    for (int i = 0; i + customPage * 8 + skips < levelNames.length && i - skips < 8; i++) {
      String username = "";
      try {
        Scanner fileInput = new Scanner(levelNames[i + customPage * 8 + skips]);
        username = fileInput.nextLine();
        fileInput.close();
      } catch (FileNotFoundException e) {
        System.out.println("Unkown Error");
        return null;
      }
      if (!privateOnly || username.equals(currentUser.getUserName())) {
        JDataButton b = new JDataButton(levelNames[i + customPage * 8 + skips]);
        b.setBounds(200, 0 + i * 75, 640, 75);
        b.setBackground(Data.Colors.buttonBackground);
        b.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        b.setFont(Data.Fonts.customButton);
        b.addActionListener(e -> {
          replace(createLevel(b.getFile()));
        });

        JLabel l = new JLabel(username, SwingConstants.RIGHT);
        l.setBounds(0, 0 + i * 75, 185, 75);
        l.setFont(Data.Fonts.dataLabel);

        list.add(b);
        list.add(l);
      } else {
        skips++;
        i--;
      }
    }

    return list;
  }


  public JPanel createLevelDesigner() {
    JPaintTool root = new JPaintTool();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    JLabel title = new JLabel("Level Creator");
    title.setBounds(20, 20, 300, 50);
    title.setFont(Data.Fonts.header1);

    root.add(title);

    ArrayList<Block> blocks = new ArrayList<Block>();

    blocks.add(new Block(770, 20, 110, 110, new Color(120, 120, 120), ""));
    blocks.add(new Block(780, 30, 90, 90, new Color(150, 150, 150), ""));
    blocks.add(new Block(700, 150, 250, 100, new Color(120, 120, 120), ""));
    blocks.add(new Block(710, 160, 230, 80, new Color(150, 150, 150), ""));
    blocks.add(new Block(700, 270, 250, 165, new Color(120, 120, 120), ""));
    blocks.add(new Block(710, 280, 230, 145, new Color(150, 150, 150), ""));
    // int fileStart = 5;
    try {
      blocks.addAll(gameGrid(Arrays.copyOfRange(fileReader(new File("Levels/default.txt")), fileStart, 100 + fileStart), 50, 150));
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: file not found");
    }

    char[] wallType = new char[] {'w', 'h', 'd', 'p', 'a', 'o', '0'};
    for (int i = 0; i < 7; i++)
      blocks.addAll(drawObject('w', 0, wallType[i], 730 + i * 30, 175, Integer.toString(i)));

    for (int i = 1; i < 4; i++)
      blocks.add(new Block(798 + (i / 2) * 75, 293 + (i / 3) * 65, 54, 54, Color.BLACK, ""));
    String[] roomType = new String[] {"e0", "g0", "n0", "f0", "z2", "b1"};
    for (int i = 0; i < 6; i++)
      blocks.addAll(drawObject('r', Character.getNumericValue(roomType[i].charAt(1)), roomType[i].charAt(0), 725 + (i / 2) * 75, 295 + (i % 2) * 65, ""));
    
    root.repaint(blocks);
    root.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        // ArrayList<Block> oldBlocks = (ArrayList<Block>) blocks.clone();
        int mx = e.getX();
        int my = e.getY();
        if (mx > 55 && mx < 645 && my > 155 && my < 745 && currentItem != null) { ////////////////////////////////////////////// Clicked on board
          // mx = mx - 50;
          // my = my - 150;
          // System.out.println("Click on board");
          int coordinate;
          char clickLocation = ' ';
          for (coordinate = 0; coordinate < 100 && clickLocation == ' '; coordinate++) // {
            clickLocation = floorGrid[coordinate % 10][coordinate / 10].wasClicked(new int[] {mx, my});
            // System.out.println(clickLocation);
          // }
          coordinate--;
          int first = coordinate % 10;
          int second = coordinate / 10;
          
          if (currentItem.charAt(0) == 'w' && Character.isDigit(clickLocation)) { // Click on wall
            if (!Character.isDigit(currentItem.charAt(1)) || doorsPlaced < 10) {
              while (blocks.remove(new Block(0, 0, 0, 0, null, "w" + Character.getNumericValue(clickLocation) + "" + first + "" + second))) {}
              if (Character.isDigit(floorGrid[first][second].getWall(Character.getNumericValue(clickLocation))))
                doorsPlaced--;
              floorGrid[first][second].setWall(currentItem.charAt(1), Character.getNumericValue(clickLocation));
              // System.out.println("add blocks");
              blocks.addAll(floorGrid[first][second].getWallBlocks(Character.getNumericValue(clickLocation)));
              if (Character.isDigit(currentItem.charAt(1)))
                doorsPlaced++;
            } else
              JOptionPane.showMessageDialog(root, "You can only have up to 10 locked doors!");
          } else if (currentItem.charAt(0) == 'r' && clickLocation == 'r') { // Click on room
            if (currentItem.charAt(1) == 'n' || currentItem.charAt(1) == 'f') {
              if (floorGrid[first][second].getInterior() == 'e')
                elevatorPlaced = false;
              floorGrid[first][second].setInterior(currentItem.charAt(1));
              floorGrid[first][second].setItem(' ');
              floorGrid[first][second].setOccupant(' ');
            } else if (currentItem.charAt(1) == 'e' || currentItem.charAt(1) == 'g') {
              if (currentItem.charAt(1) == 'e' && elevatorPlaced)
                JOptionPane.showMessageDialog(root, "You can only have one elevator!");
              else {
                if (floorGrid[first][second].getInterior() == 'e')
                  elevatorPlaced = false;
                floorGrid[first][second].setInterior(currentItem.charAt(1));
                if (currentItem.charAt(1) == 'e')
                  elevatorPlaced = true;
              }
            } else if (currentItem.charAt(1) == 'z') {
              floorGrid[first][second].setOccupant(currentItem.charAt(1));
            } else if (Character.isDigit(currentItem.charAt(1)) || currentItem.charAt(1) == 'b') {
              floorGrid[first][second].setItem(currentItem.charAt(1));
            }
            for (int i = 0; i < 3; i++)
              while (blocks.remove(new Block(0, 0, 0, 0, null, "r" + i + "" + first + "" + second))) {}
            // System.out.println("add blocks");
            blocks.addAll(floorGrid[first][second].getContentBlocks(false));
          }
        } else if (currentItem == null || currentItem.charAt(0) == 'w' || !Character.isDigit(currentItem.charAt(1))) { /////////////////////////////////////////////////////////////////////////////////////////////////////////////// Clicked on items
          for (int i = 0; i < 7; i++) {
            if (mx > 730 + i * 30 && mx < 740 + i * 30 && my > 175 && my < 225) {
              // System.out.println("Click wall");
              currentItem = "w" + ((i == 6) ? Integer.toString(doorsPlaced) : Character.toString(wallType[i]));
              while (blocks.remove(new Block(0, 0, 0, 0, null, "currentItem"))) {}
              blocks.addAll(drawObject('w', 0, currentItem.charAt(1), 820, 50, "currentItem"));
            }
          }
          for (int i = 0; i < 6; i++) {
            if (mx > 725 + (i / 2) * 75 && mx < 775 + (i / 2) * 75 && my > 295 + (i % 2) * 65 && my < 345 + (i % 2) * 65) {
              // System.out.println("Click object");
              currentItem = "r" + roomType[i].charAt(0);
              while (blocks.remove(new Block(0, 0, 0, 0, null, "currentItem"))) {}
              blocks.addAll(drawObject('r', Character.getNumericValue(roomType[i].charAt(1)), roomType[i].charAt(0), 800, 50, "currentItem"));
            }
          }
        } else
          JOptionPane.showMessageDialog(root, "You must place the key first!");
        // root.invalidate();
        root.repaint(blocks);
        // System.out.println("repaint");
        // int i;
        // for (i = 0; i < oldBlocks.size(); i++) {
        //   if (oldBlocks.get(i) != blocks.get(i))
        //     i = 1000000;
        // }
        // if (i > oldBlocks.size())
        //   System.out.println("different");
      } 
    });

    JLabel nameLabel = new JLabel("Level name:", SwingConstants.CENTER);
    nameLabel.setBounds(700, 465, 250, 25);
    nameLabel.setFont(Data.Fonts.textLabel);

    JTextField nameField = new JTextField();
    nameField.setBounds(700, 490, 250, 30);
    nameField.setFont(Data.Fonts.textField);

    JLabel energyLabel = new JLabel("Energy:");
    energyLabel.setBounds(765, 540, 80, 25);
    energyLabel.setFont(Data.Fonts.textLabel);

    JTextField energyField = new JTextField();
    energyField.setBounds(845, 538, 40, 29);
    energyField.setFont(Data.Fonts.textField);

    JButton demo = new JButton("Demo");
    demo.setBounds(725, 590, 200, 40);
    demo.setBackground(Data.Colors.buttonBackground);
    demo.setFont(Data.Fonts.menuButton);
    demo.addActionListener(e -> {
      System.out.println("demo");
    });

    JButton save = new JButton("Save As...");
    save.setBounds(725, 650, 200, 40);
    save.setBackground(Data.Colors.buttonBackground);
    save.setFont(Data.Fonts.menuButton);
    save.addActionListener(e -> {
      boolean done = false;
      while (!done) {
        String filename = JOptionPane.showInputDialog(this, "Filename:");
        File file = new File(filename);
        if (file.exists()) {
          Scanner fileIn = null;
          try {
            fileIn = new Scanner(file);
          } catch (FileNotFoundException f) {}
          if (fileIn.nextLine().equals(currentUser.getUsername())) {
            int option = JOptionPane.showConfirmDialog(this, "One of your custom levels already has this file name, would you like to overwrite it?");
            switch (option) {
              case JOptionPane.YES_OPTION:
                // overwrite w/ no "break;"
              case JOptionPane.CANCEL_OPTION:
                done = true;
                break;
            }
          }
          fileIn.close();
        } else {
          
        }
      }
    });

    JButton exit = new JButton("Exit");
    exit.setBounds(725, 710, 200, 40);
    exit.setBackground(Data.Colors.buttonBackground);
    exit.setFont(Data.Fonts.menuButton);
    exit.addActionListener(e -> {
      replace(createMenu());
    });

    root.add(nameLabel);
    root.add(nameField);
    root.add(energyLabel);
    root.add(energyField);
    root.add(demo);
    root.add(save);
    root.add(exit);
    
    
    
    currentScreen = "create";
    return root;
  } // FIX


  public ArrayList<Block> gameGrid(String[] fileContents, int startx, int starty) {
    floorGrid = new GridCell[10][10];
    // String[] fileContents = null;
    ArrayList<Block> blocks = new ArrayList<Block>();
    elevatorPlaced = false;
    numOfGen = 0;
    doorsPlaced = 0;

    // try {
    //   fileContents = Arrays.copyOfRange(fileReader(file), fileStart, fileStart + 100);
    // } catch (FileNotFoundException e) {
    //   System.out.println("ERROR: File not found");
    // }
    // System.out.println(fileContents.length); /////

    blocks.add(new Block(startx, starty, 600, 600, new Color(80, 80, 80), ""));

    for (int i = 0; i < 100; i++) {
      String[] line = fileContents[i].split("-");
      floorGrid[i % 10][i / 10] = new GridCell(line[0].toCharArray(), line[1].toCharArray(), new int[] {i % 10, i / 10}, new int[] {startx + 5 + (i % 10) * 60, starty + 5 + (i / 10) * 60});
      if (line[0].charAt(0) == 'e')
        elevatorPlaced = true;
      if (line[0].charAt(0) == 'g')
        numOfGen++;
      if (Character.isDigit(line[1].charAt(0)))
        doorsPlaced++;
      if (Character.isDigit(line[1].charAt(1)))
        doorsPlaced++;
      blocks.addAll(floorGrid[i % 10][i / 10].getAllBlocks());
    }

    return blocks;
  } // DELETE


  public ArrayList<Block> drawObject(char type, int position, char object, int startx, int starty, String id) {
    ArrayList<Block> blocks = new ArrayList<Block>();
    if (object == ' ') {
      return blocks;
    }
    if (type == 'r') {
      if (position == 0) {
        switch (object) {
          case 'e':
            blocks.add(new Block(startx, starty, 50, 50, new Color(50, 50, 50), id));
            blocks.add(new Block(startx + 10, starty + 10, 30, 30, Color.BLACK, id));
            blocks.add(new Block(startx + 15, starty + 15, 20, 20, new Color(50, 50, 50), id));
            break;
          case 'g':
            blocks.add(new Block(startx, starty, 50, 50, new Color(150, 0, 0), id));
            blocks.add(new Block(startx + 10, starty + 10, 30, 30, Color.BLACK, id));
            blocks.add(new Block(startx + 15, starty + 15, 20, 20, new Color(150, 0, 0), id));
            break;
          case 'x':
            blocks.add(new Block(startx, starty, 50, 50, new Color(0, 190, 0), id));
            blocks.add(new Block(startx + 10, starty + 10, 30, 30, Color.BLACK, id));
            blocks.add(new Block(startx + 15, starty + 15, 20, 20, new Color(0, 190, 0), id));
            break;
          case 'f':
            blocks.add(new Block(startx, starty, 50, 50, new Color(80, 80, 80), id));
            break;
          case 'n':
            blocks.add(new Block(startx, starty, 50, 50, new Color(220, 220, 220), id));
            break;
        }
      } else if (position == 1) {
        switch (object) {
          case 'b':
            blocks.add(new Block(startx, starty, 50, 50, new Color(220, 220, 220), id));
            blocks.add(new Block(startx + 3, starty + 10, 41, 30, Color.BLACK, id));
            blocks.add(new Block(startx + 6, starty + 13, 35, 24, new Color(220, 220, 220), id));
            blocks.add(new Block(startx + 44, starty + 20, 3, 10, Color.BLACK, id));
            break;
          default:
            blocks.add(new Block(startx + 10, starty + 20, 30, 10, new Color((50 + Character.getNumericValue(object) * 80) % 255, Math.abs(250 - Character.getNumericValue(object) * 170) % 255, (100 + Character.getNumericValue(object) * 230) % 255), id));
            // Draw key
        }
      } else if (position == 2) {
        switch (object) {
          case 'z':
            blocks.add(new Block(startx, starty, 50, 50, new Color(220, 220, 220), id));
            blocks.add(new Block(startx + 8, starty + 8, 34, 34, new Color(0, 150, 30), id));
            blocks.add(new Block(startx + 14, starty + 16, 6, 6, Color.BLACK, id));
            blocks.add(new Block(startx + 30, starty + 16, 6, 6, Color.BLACK, id));
            // blocks.add(new)
            break;
          case 'c':
            blocks.add(new Block(startx, starty, 50, 50, new Color(220, 220, 220), id));
            blocks.add(new Block(startx + 8, starty + 8, 34, 34, new Color(0, 150, 30), id));
            blocks.add(new Block(startx + 14, starty + 16, 6, 6, Color.BLACK, id));
            blocks.add(new Block(startx + 30, starty + 16, 6, 6, Color.BLACK, id));
            break;
        }
      }
    } else if (type == 'w') {
      Color temp = new Color(180, 180, 180);
      switch (object) {
        case 'w':
          temp = new Color(80, 80, 80);
          break;
        case 'h':
          temp = new Color(220, 220, 220);
          break;
        case 'd':
          blocks.add(new Block(startx + position * 15, starty + (1 - position) * 15, 10 + position * 10, 20 - position * 10, new Color(190, 0, 0), id));
          break;
        case 'p':
          blocks.add(new Block(startx + position * 15, starty + (1 - position) * 15, 10 + position * 10, 20 - position * 10, Color.BLACK, id));
          break;
        case 'a':
          for (int i = 0; i < 2; i++)
            blocks.add(new Block(startx + position * 10 + i * 20 * position, starty + 10 - position * 10 + i * 20 * (1 - position), 10, 10, new Color(190, 0, 0), id));
          break;
        case 'o':
          for (int i = 0; i < 2; i++)
            blocks.add(new Block(startx + position * 10 + i * 20 * position, starty + 10 - position * 10 + i * 20 * (1 - position), 10, 10, new Color(130, 130, 130), id));
          break;
        case 'u':
          blocks.add(new Block(startx + position * 15, starty + (1 - position) * 15, 10 + position * 10, 20 - position * 10, new Color(0, 190, 0), id));
          break;
        case 's':
          for (int i = 0; i < 2; i++)
            blocks.add(new Block(startx + position * 10 + i * 20 * position, starty + 10 - position * 10 + i * 20 * (1 - position), 10, 10, new Color(0, 190, 0), id));
          break;
        default:
          blocks.add(new Block(startx + position * 8, starty + (1 - position) * 8, 10 + position * 24, 34 - position * 24, new Color((50 + Character.getNumericValue(object) * 80) % 255, Math.abs(250 - Character.getNumericValue(object) * 170) % 255, (100 + Character.getNumericValue(object) * 230) % 255), id));
          // Draw key door
      }
      blocks.add(0, new Block(startx, starty, 10 + position * 40, 50 - position * 40, temp, id));
    }

    return blocks;
  } // DELETE


  // private final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
  // private final Object MOVE_UP = new String("move up");
  // private final Object MOVE_DOWN = new String("move down");
  // private final Object MOVE_LEFT = new String("move left");
  // private final Object MOVE_RIGHT = new String("move right");
  // private final Object ACTIVATE_GEN = new String("activate gen");
  // private final Object NUMPAD0 = new String("num 0");
  

  public JPanel createLevel(File levelFile) {
    JPaintTool root = new JPaintTool();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    String[] fileContents = null;
    try {
      fileContents = fileReader(levelFile);
      root.repaint(gameGrid(Arrays.copyOfRange(fileContents, fileStart, 100 + fileStart), 350, 130));
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: file not found");
    }

    JLabel title = new JLabel(fileContents[1], SwingConstants.CENTER);
    title.setBounds(0, 0, 1000, 60);
    title.setFont(new Font("Monospace", Font.PLAIN, 30));

    JLabel name = new JLabel(fileContents[2], SwingConstants.CENTER);
    name.setBounds(0, 130, 350, 50);
    name.setFont(Data.Fonts.dataLabel);

    JLabel energyLabel = new JLabel("Remaining Energy:", SwingConstants.CENTER);
    energyLabel.setBounds(0, 200, 350, 30);
    energyLabel.setFont(Data.Fonts.dataLabel);

    JLabel perfectEnergy = new JLabel(fileContents[4]);
    perfectEnergy.setBounds(230, 235, 100, 20);
    perfectEnergy.setForeground(Data.Colors.perfectLevel);
    perfectEnergy.setFont(new Font("Monospace", Font.PLAIN, 12));

    JLabel energyAmt = new JLabel(fileContents[3], SwingConstants.CENTER);
    energyAmt.setBounds(0, 230, 350, 30);
    energyAmt.setFont(Data.Fonts.dataLabel);

    JLabel genLabel = new JLabel("Generators Active:", SwingConstants.CENTER);
    genLabel.setBounds(0, 300, 350, 30);
    genLabel.setFont(Data.Fonts.dataLabel);

    int activeGen = 0;
    JLabel genAmt = new JLabel(activeGen + "/" + numOfGen, SwingConstants.CENTER);
    genAmt.setBounds(0, 330, 350, 30);
    genAmt.setFont(Data.Fonts.dataLabel);

    JButton restart = new JButton("Restart");
    restart.setBounds(20, 430, 310, 50);
    restart.setBackground(Data.Colors.buttonBackground);
    restart.setFont(Data.Fonts.menuButton);
    restart.addActionListener(e -> {
      replace(createLevel(levelFile));
    });

    JButton menu = new JButton("Menu");
    menu.setBounds(20, 500, 310, 50);
    menu.setBackground(Data.Colors.buttonBackground);
    menu.setFont(Data.Fonts.menuButton);
    menu.addActionListener(e -> {
      replace(createMenu());
    });

    JLabel note = new JLabel("<html>" + fileContents[0] + "</html>", SwingConstants.CENTER);
    note.setBounds(10, 580, 330, 150);
    note.setFont(new Font("Monospace", Font.ITALIC, 20));

    root.add(title);
    root.add(name);
    root.add(energyLabel);
    root.add(perfectEnergy);
    root.add(energyAmt);
    root.add(genLabel);
    root.add(genAmt);
    root.add(restart);
    root.add(menu);
    root.add(note);

    Human human = new Human(floorGrid);
    // KeyListener kl = new KeyAdapter() {
    //   public void keyPressed(KeyEvent event) {
    //     System.out.println(event.getKeyChar());
    //   }
    // };

    // root.addKeyListener(kl);

    class keyAction extends AbstractAction {

      private int keyPressed;
      
      public keyAction(int keyPressed) {
        this.keyPressed = keyPressed;
      }

      public void actionPerformed(ActionEvent e) {
        human.move(floorGrid, keyPressed);
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
    

    currentScreen = "Level";
    return root;
  } // DELETE


  public void animate(JPaintTool panel, JLabel energy, GridCell[][] currentState, int i, int direction) {
    char Occupant = GridCell[i % 10][i / 10].getOccupant().getChar();
    char wallType = GridCell[(i % 10) - keyPressed / 2 + keyPressed / 3][(i / 10) - keyPressed / 3].getWall(keyPressed % 2);
    try {
      boolean moved = false;
      boolean flashEnergy = false;
      
      switch (GridCell[(i % 10) - keyPressed / 2 + keyPressed / 3][(i / 10) - keyPressed / 3].getWall(keyPressed % 2)) {
        case 'w':
          // Animate wall flashing
        case 'd':
          if (energy > 0) {
            // Move character, animate door open, decrease energy
            moved = true;
          } else
            flashEnergy = true;
        case 'p':
          // Animate door flashing
        case 'a':
          if (energy > 0) {
            // Move character, animate door open, decrease energy
            moved = true;
          } else
            flashEnergy = true;
          moved = true
        case 'o':
          // Move character, create wall
        case 'h':
          // Move character
      }

      if (flashEnergy) {
        // Make energy flash
      }
    } catch (IndexOutOfBoundsException e) {
      return currentState;
    }
    
    System.out.println(keyPressed);
    return currentState;
    
  } // DELETE


  public void replace(JPanel newPanel) {
    super.getContentPane().removeAll();
    super.getContentPane().add(newPanel);
    super.revalidate();
  }


  public String[] fileReader(File file) throws FileNotFoundException {
    ArrayList<String> fileContents = new ArrayList<String>();

    // file = new File("Levels/default.txt");
    Scanner fileIn = new Scanner(file);
    while (fileIn.hasNextLine())
      fileContents.add(fileIn.nextLine());
    fileIn.close();
    
    return fileContents.toArray(new String[fileContents.size()]);
  } // DELETE ??


  public void pressKey(int keyCode) {
    switch(keyCode) {
      case 32:
        gameBoard.deactivateSecurity();
        return;
      case 39:
        gameBoard.movePlayer(GameBoard.MOVE_RIGHT);
        return;
      case 40:
        gameBoard.movePlayer(GameBoard.MOVE_DOWN);
        return;
      case 37:
        gameBoard.movePlayer(GameBoard.MOVE_LEFT);
        return;
      case 38:
        gameBoard.movePlayer(GameBoard.MOVE_UP);
    }
  }
  
}




class KeyEvents implements KeyListener {

  public void KeyEvents() {}
  
  @Override
  public void keyPressed(KeyEvent e) {
    if ((e.getKeyCode() => 37 && e.getKeyCode() =< 40) || e.getKeyCode() == 32)
      Main.pressKey(e.getKeyCode);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // bleh
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // bleh
  }
}