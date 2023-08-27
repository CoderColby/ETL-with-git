import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Collections;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Paths;

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

  // Holds data about what to execute when this button is pressed
  private class JDataButton extends JButton {
  
    private int ID;
    private File file;
    private AbstractGameObject object;
    
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
      super(file.getName().substring(0, file.getName().length() - ".txt".length()));
      this.file = file;
    }
  
    public JDataButton(AbstractGameObject object) { // For buttons on level creator
      super(object.getImage());
      this.object = object;
    }
  
    public int getID() {
      return ID;
    }
  
    public File getFile() {
      return file;
    }
  
    public AbstractGameObject getObject() {
      return object;
    }
  }

  // Displays levels in menu
  private class LevelPanel extends JPanel {
    private int groupNum;
    private final int MAX_GROUP_NUM = (Data.Utilities.numOfLevels - 1) / 10;
    private Rectangle bounds;

    public LevelPanel(int xPos, int yPos) {
      this.bounds = new Rectangle(xPos, yPos, 150 * 4, 150 * 3);
      
      super.setLayout(null);
      super.setBounds(this.bounds);
      super.setOpaque(false);
      updateLevelGrid();
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
        for (int column = selector, int levelID = levelGroup * 10 + 4 * row; column < 4 - selector && levelID <= Data.Utilities.numOfLevels; column++, levelID++) {
          JDataButton button = new JDataButton(levelID, currentUser.getLevels() >= levelID, currentUser.getPerfectLevels().contains(levelID), column, row);
          button.addActionListener(e -> {
            startLevelSequence(button.getID());
          });
          super.add(button);
        }
      }
      super.revalidate();
    }
  }
  
  private LevelPanel jpnl_menuLevels;

  private JPanel customLevelList;
  private JPanel jlbl_pageNum;
  private int customPageNum;

  // Level creator

  private class LevelDesigner extends JPanel {

    private GameBoard levelBoard;
    private AbstractGameObject currentItem;

    private class DesignerButton extends JButton {
      ImageIcon[] images;
      String type;

      public DesignerButton(String type, ImageIcon[] images) {
        this.type = type;
        this.images = images;
      }

      public boolean isType(String other) {
        return type.equals(other);
      }

      public void setImage(ImageIcon newImage, int position) {
        if (position < 0)
          images = new ImageIcon[3];
        else
          images[position] = newImage;
        repaint();
      }

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (ImageIcon image : images) {
          if (image != null)
            g.drawImage(image.getImage(), 0, 0, null);
        }
      }
    }
    
    public LevelDesigner(File loadFile) {
      
      // Base panel
      super.setLayout(null);
      super.setBackground(Color.WHITE);
  
      // Title "Level Creator"
      JLabel jlbl_title = new JLabel("Level Creator");
      jlbl_title.setBounds(20, 20, 300, 50);
      jlbl_title.setFont(Data.Fonts.header1);
  
      // Label for currently selected item
      JLabel jlbl_selectedItem = new JLabel();
      jlbl_selectedItem.setBounds(780, 30, 90, 90);
      jlbl_selectedItem.setBackground(new Color(150, 150, 150));
      jlbl_selectedItem.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
  
      // Panel for selecting items
      JPanel jpnl_gameItems = new JPanel();
      jpnl_gameItems.setBounds(710, 160, 230, 200);
      jpnl_gameItems.setBackground(new Color(150, 150, 150));
      jpnl_gameItems.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
  
      // For displaying game objects to select
      AbstractWall[] walls = new AbstractWall[] {new Wall(), new Door(), new PowerDoor(), new OnceDoor(), new AirlockDoor(), new DetectionDoor(), new LockedDoor()};
      AbstractRoomType[] roomTypes = new AbstractRoomType[] {new Elevator(), new Target(), new Filled()};
      AbstractItem[] items = new AbstractItem[] {new Key(), new Battery(), new Eraser()};
      AbstractEntity[] entities = new AbstractEntity[] {new Zombie(), new SmartZombie()};
  
      // Draw wall types
      for (int i = 0; i < walls.length; i++) {
        JDataButton newWall = new JDataButton(walls[i]);
        newWall.setBounds(10 + (210 / walls.length) * i - GameBoard.WALL_THICKNESS / 2, 15, GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT);
        newWall.addActionListener(e -> {
          currentItem = newWall.getObject();
          super.repaint();
        });
        jpnl_gameItems.add(newWall);
      }
  
      // Draw room types
      for (int i = 0; i < roomTypes.length; i++) {
        JDataButton newRoomType = new JDataButton(roomTypes[i]);
        newRoomType.setBounds(25 + (180 / roomTypes.length) * i - GameBoard.ROOM_HEIGHT / 2, 80, GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        newRoomType.addActionListener(e -> {
          currentItem = newRoomType.getObject();
          super.repaint();
        });
        jpnl_gameItems.add(newRoomType);
      }

      // Draw item types
      //////////////////////// FINISH
      //////////////////////// Also add eraser
  
      // Draw entity types
      for (int i = 0; i < entities.length; i++) {
        JDataButton newEntity = new JDataButton(entities[i]);
        newEntity.setBounds(25 + (180 / entities.length) * i - GameBoard.ROOM_HEIGHT / 2, 140, GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        newEntity.addActionListener(e -> {
          currentItem = newEntity.getObject();
          super.repaint();
        });
        jpnl_gameItems.add(newEntity);
      }

      // Read load-in file
      Scanner fileIn = new Scanner(loadFile);
      String levelTitle = fileIn.nextLine().trim();
      String startEnergy = fileIn.nextLine().trim();
  
      String[][] boardData = new String[100][5];
  
      for (int i = 0; i < 100; i++)
        boardData[i] = fileIn.nextLine().split();
      fileIn.close();

      // Label level name
      JLabel jlbl_levelName = new JLabel("Level name:", SwingConstants.CENTER);
      jlbl_levelName.setBounds(700, 465, 250, 25);
      jlbl_levelName.setFont(Data.Fonts.textLabel);

      // Field level name
      JTextField jtxf_levelName = new JTextField(levelTitle);
      jtxf_levelName.setBounds(700, 490, 250, 30);
      jtxf_levelName.setFont(Data.Fonts.textField);

      // Label energy
      JLabel jlbl_levelEnergy = new JLabel("Energy:");
      jlbl_levelEnergy.setBounds(765, 540, 80, 25);
      jlbl_levelEnergy.setFont(Data.Fonts.textLabel);

      // Field energy
      JTextField jtxf_levelEnergy = new JTextField(startEnergy);
      jtxf_levelEnergy.setBounds(845, 538, 40, 29);
      jtxf_levelEnergy.setFont(Data.Fonts.textField);

      // Button to load a different level
      JButton jbtn_load = new JButton("Build Level");
      jbtn_load.setBounds(725, 590, 200, 40);
      jbtn_load.setBackground(Data.Colors.buttonBackground);
      jbtn_load.setFont(Data.Fonts.menuButton);
      jbtn_load.addActionListener(e -> {
        System.out.println("load");
      });

      // Button to demo level
      JButton jbtn_demo = new JButton("Demo");
      jbtn_demo.setBounds(725, 640, 200, 40);
      jbtn_demo.setBackground(Data.Colors.buttonBackground);
      jbtn_demo.setFont(Data.Fonts.menuButton);
      jbtn_demo.addActionListener(e -> {
        System.out.println("demo");
      });

      // Button to save construction
      JButton jbtn_save = new JButton("Save As...");
      jbtn_save.setBounds(725, 690, 200, 40);
      jbtn_save.setBackground(Data.Colors.buttonBackground);
      jbtn_save.setFont(Data.Fonts.menuButton);
      jbtn_save.addActionListener(e -> {
        /*
        // boolean done = false;
        // while (!done) {
        //   String filename = JOptionPane.showInputDialog(this, "Filename:");
        //   File file = new File(filename);
        //   if (file.exists()) {
        //     Scanner fileIn = null;
        //     try {
        //       fileIn = new Scanner(file);
        //     } catch (FileNotFoundException f) {}
        //     if (fileIn.nextLine().equals(currentUser.getUsername())) {
        //       int option = JOptionPane.showConfirmDialog(this, "One of your custom levels already has this file name, would you like to overwrite it?");
        //       switch (option) {
        //         case JOptionPane.YES_OPTION:
        //           // overwrite w/ no "break;"
        //         case JOptionPane.CANCEL_OPTION:
        //           done = true;
        //           break;
        //       }
        //     }
        //     fileIn.close();
        //   } else {
            
        //   }
        // }
        */
        System.out.println("save");
      });

      // Button to return home
      JButton jbtn_exit = new JButton("Exit");
      jbtn_exit.setBounds(725, 740, 200, 40);
      jbtn_exit.setBackground(Data.Colors.buttonBackground);
      jbtn_exit.setFont(Data.Fonts.menuButton);
      jbtn_exit.addActionListener(e -> {
        System.out.println("exit");
      });
  
      super.add(jlbl_title);
      super.add(jlbl_selectedItem);
      super.add(jpnl_gameItems);
      super.add(jlbl_levelName);
      super.add(jtxf_levelName);
      super.add(jlbl_levelEnergy);
      super.add(jtxf_levelEnergy);
      super.add(jbtn_load);
      super.add(jbtn_demo);
      super.add(jbtn_save);
      super.add(jbtn_exit);
      
      levelBoard = new GameBoard(boardData, 0);

      for (int i = 0; i < 100; i++) {
        GridCell cell = levelBoard.getGridCell(int [] {i % 10, i / 10});
        DesignerButton db = new DesignerButton(Data.Utilities.forRoom, new ImageIcon[] {cell.getRoomType(), cell.getItem(), cell.getEntity()});
        db.setBounds((i % 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), (i / 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        db.addActionListener(e -> {
          if (db.isType(currentItem.getType()) || currentItem.TAG.equals(Eraser.TAG))
            db.setImage(currentItem.getImage(), currentItem.getLayer());
        });
      }

      for (int i = 0; i < 90; i++) {
        Wall cellWall = levelBoard.getGridCell(int [] {i % 9, i / 9}).getWall(0);
        DesignerButton db = new DesignerButton(Data.Utilities.forWall, new ImageIcon[] {cellWall, null, null});
        db.setBounds(GameBoard.ROOM_HEIGHT + (i % 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), (i / 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT);
        db.addActionListener(e -> {
          if (db.isType(currentItem.getType()) || currentItem.TAG.equals(Eraser.TAG))
            db.setImage(currentItem.getImage(), currentItem.getLayer());
        });
      }

      for (int i = 0; i < 90; i++) {
        Wall cellWall = levelBoard.getGridCell(int [] {i / 9, i % 9}).getWall(1);
        DesignerButton db = new DesignerButton(Data.Utilities.forWall, new ImageIcon[] {cellWall, null, null});
        db.setBounds(GameBoard.ROOM_HEIGHT + (i / 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), (i % 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT);
        db.addActionListener(e -> {
          if (db.isType(currentItem.getType()) || currentItem.getTag().equals(Eraser.TAG))
            db.setImage(Data.Images.rotateIcon(currentItem.getImage(), 90), currentItem.getLayer());
        });
      }
    }
  }

  private LevelDesigner levelDesigner;
  
  
  /* √ */
  public GameWindow() {
    super("ESCAPE THE LAB 2");
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.setSize(1000, 820);
    super.setPreferredSize(new Dimension(1000, 820));
    super.getContentPane().setLayout(null);
    super.getContentPane().add(createLogin());
    super.setVisible(true);
  }

  /* √ */
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
      if (errorMessages.isEmpty()) { // If no invalid input yet
        if (!User.isUserExist(username)) // If user does not exist
          errorMessages = "The username you entered does not exist! Try signing up if you don't have an account yet.";
        else if (!User.isCorrectPassword(username, password)) // If password is incorrect
          errorMessage = "This user has a password different than the one provided, please try again.";
      }
      if (errorMessages.isEmpty()) { // If no invalid input yet
        currentUser = new User(new File(Data.getUserPath(username))); // Make new user
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
      if (errorMessages.isEmpty() && User.isUserExist(username)) // If no invalid input but user exists
          errorMessages = "This username already exists! Please try a different one or log in.\n";
      else {
        errorMessages += User.isValidPassword(password);
        if (realName.isEmpty()) // If no name is provided
          errorMessages += "Please provide your name.\n";
        if (!password.equals(jtxf_signupConfirmPassword.getText())) // If confirmation password is different
          errorMessages += "Please ensure that both passwords are identical.\n";
      }
      if (errorMessages.isEmpty()) { // If no invalid input
        currentUser = new User(username, password, realName, Data.getUserFilePath(username)); // Make brand new user
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

  /* √ */
  public JPanel createMenu(int levelGroup) { // Creates menu page showing all unlocked levels, with custom level options and account settings
    
    // Base Panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

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

    // Button to create new level
    JButton jbtn_create = new JButton("Create New");
    jbtn_create.setBounds(40, 670, 200, 80);
    jbtn_create.setBackground(Data.Colors.buttonBackground);
    jbtn_create.setFont(Data.Fonts.menuButton);
    jbtn_create.addActionListener(l -> {
      replace(new LevelDesigner(new File(Data.Utilities.defaultLevelFile)));
    });

    // Button to load a custom level
    JButton jbtn_load = new JButton("Load Custom");
    jbtn_load.setBounds(760, 670, 200, 80);
    jbtn_load.setBackground(Data.Colors.buttonBackground);
    jbtn_load.setFont(Data.Fonts.menuButton);
    jbtn_load.addActionListener(l -> {
      replace(createLevelBrowser());
    });

    // Button to access settings
    JButton jbtn_settings = new JButton("Options");
    jbtn_settings.setBounds(420, 690, 160, 40);
    jbtn_settings.setBackground(Data.Colors.buttonBackground);
    jbtn_settings.setFont(Data.Fonts.menuButton);
    jbtn_settings.addActionListener(l -> {
      replace(createSettings());
    });

    // Add buttons
    root.add(jbtn_create);
    root.add(jbtn_load);
    root.add(jbtn_settings);

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

    // Add navigation arrows
    root.add(jbtn_leftArrow);
    root.add(jbtn_rightArrow);

    return root;
  } // Creates menu with level navigation and user info with custom levels

  /* √ */
  public void startLevelSequence(int startingLevel) {
    int level;
    boolean statusGood = true;
    for (level = startingLevel; level <= Data.numOfLevels && !Level.returnToMenu; level++) {
      replace(new Level(new File(Data.Utilities.getLevelFilePath(level)), false));
      while (!(Level.goToNextLevel || Level.returnToMenu));
        wait();
    }

    createMenu((level - 1) / 10)
  }


  public JPanel createSettings() { // Ability to log out or delete account along with change user characteristics
    
    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    // Title
    JLabel jlbl_title = new JLabel("SETTINGS", SwingConstants.CENTER);
    jlbl_title.setBounds(350, 30, 300, 50);
    jlbl_title.setFont(Data.Fonts.header1);

    // Label for username
    JLabel jlbl_usernameLabel = new JLabel("Userame:");
    jlbl_usernameLabel.setBounds(350, 120, 120, 25);
    jlbl_usernameLabel.setFont(Data.Fonts.textLabel);

    // Label for password
    JLabel jlbl_passwordLabel = new JLabel("Password:");
    jlbl_passwordLabel.setBounds(350, 200, 120, 25);
    jlbl_passwordLabel.setFont(Data.Fonts.textLabel);

    // Label for name
    JLabel jlbl_nameLabel = new JLabel("Name:");
    jlbl_nameLabel.setBounds(350, 280, 100, 25);
    jlbl_nameLabel.setFont(Data.Fonts.textLabel);

    // Value for username
    JTextField jtxf_usernameField = new JTextField(currentUser.getUserName());
    jtxf_usernameField.setBounds(350, 145, 300, 25);
    jtxf_usernameField.setFont(Data.Fonts.textField);

    // Value for password
    JTextField jtxf_passwordField = new JTextField(currentUser.passwordDots()); // Don't show password, check password when applying settings instead
    jtxf_passwordField.setBounds(350, 225, 300, 25);
    jtxf_passwordField.setFont(Data.Fonts.textField);

    // Value for name
    JTextField jtxf_nameField = new JTextField(currentUser.getRealName());
    jtxf_nameField.setBounds(350, 305, 300, 25);
    jtxf_nameField.setFont(Data.Fonts.textField);

    // Add all elements to base panel
    root.add(jlbl_title);
    root.add(jlbl_usernameLabel);
    root.add(jlbl_passwordLabel);
    root.add(jlbl_nameLabel);
    root.add(jtxf_usernameField);
    root.add(jtxf_passwordField);
    root.add(jtxf_nameField);

    // Button to apply settings
    JButton jbtn_apply = new JButton("Apply Settings");
    jbtn_apply.setBounds(380, 385, 240, 55);
    jbtn_apply.setBackground(Data.Colors.buttonBackground);
    jbtn_apply.setFont(Data.Fonts.menuButton);
    jbtn_apply.addActionListener(e -> {
      String username = jtxf_usernameField.getText();
      String password = jtxf_passwordField.getText();
      String realName = jtxf_nameField.getText();
      String errorMessages = "";
      
      // Validate input
      errorMessages += User.isValidUsername(username);
      errorMessages += User.isValidPassword(password);
      if (realName.isEmpty()) // If no name is provided
        errorMessages += "Please provide your name.\n";
      
      if (errorMessages.isEmpty()) { // If no invalid input
        if (!currentUser.getUsername().equals(username))
          updateCustomLevels(username);
        currentUser.setUsername(username);
        currentUser.setPassword(password);
        currentUser.setRealName(realName);
      } else
        JOptionPane.showMessageDialog(this, errorMessages);
    });

    // Button to cancel and return to menu
    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(380, 475, 240, 55);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu());
    });

    // Button to log out
    JButton jbtn_logout = new JButton("Log Out");
    jbtn_logout.setBounds(380, 565, 240, 55);
    jbtn_logout.setBackground(Data.Colors.buttonBackground);
    jbtn_logout.setFont(Data.Fonts.menuButton);
    jbtn_logout.addActionListener(e -> {
      currentUser.updateFile(); // Just in case something was missed
      currentUser = null;
      replace(createLogin());
    });

    // Button to delete account
    JButton jbtn_delete = new JButton("DELETE ACCOUNT");
    jbtn_delete.setBounds(380, 655, 240, 55);
    jbtn_delete.setBackground(Data.Colors.buttonBackground);
    jbtn_delete.setForeground(new Color(180, 0, 0));
    jbtn_delete.setFont(Data.Fonts.menuButton);
    jbtn_delete.addActionListener(e -> {
      int option = JOptionPane.showConfirmDialog(this, "All player data will be irretrievably lost and ownership over public custom levels will be forfeited.\nPress yes to permanently delete your account, press no to cancel.", "WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (option == JOptionPane.YES_OPTION) {
        File userFile = new File(Data.Utilities.getUserFilePath(currentUser.getUsername()));
        userFile.delete();
        updateCustomLevels("[deleted]");
        currentUser = null;
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

  /* √ */
  public void publicizeCustomLevels() {
    File ownerDirectory = new File(Data.Utilities.customLevelDirectory + currentUser.getUsername());
    File destinationDirectory = new File(Data.Utilities.customUnownedDirectory);
    File[] levelFiles = ownerDirectory.listFiles();

    if (levelFiles != null) {
      for (File f : levelFiles) {

        try {
            Files.move(f.toPath(), destinationDirectory.toPath().resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
    }

    ownerDirectory.delete();
  } // Replaces username in custom levels so data is up to date and accurate

  /* √ */
  public boolean customLevelExists(String levelName) {
    ArrayList<String> filenames = new ArrayList<>();
    File[] userDirectories = (new File(Data.Utilities.customLevelDirectory)).listFiles();
    for (File dir : userDirectories)
      Collections.addAll(filenames, dir.list());
    return filenames.contains(levelName);
  }

  /* √ */
  public JPanel createLevelBrowser() {
    
    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);

    // Title
    JLabel jlbl_title = new JLabel("Select a Level:", SwingConstants.CENTER);
    jlbl_title.setBounds(300, 20, 400, 50);
    jlbl_title.setFont(Data.Fonts.header1);

    // Level selection page
    customPageNum = 0;
    customLevelList = levelList(customPageNum, false);

    // Button to cancel and return to menu
    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(40, 700, 150, 60);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu());
    });

    // Value for page number
    jlbl_pageNum = new JLabel("1", SwingConstants.CENTER);
    jlbl_pageNum.setBounds(460, 710, 80, 40);
    jlbl_pageNum.setBackground(Data.Colors.buttonBackground);
    jlbl_pageNum.setOpaque(true);
    jlbl_pageNum.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    jlbl_pageNum.setFont(Data.Fonts.header2);

    // Check box for private level visibility setting
    JCheckBox jchk_privateOnly = new JCheckBox("My levels only", false);
    jchk_privateOnly.setBounds(770, 720, 150, 25);
    jchk_privateOnly.setOpaque(false);
    jchk_privateOnly.setFont(Data.Fonts.checkboxLabel);
    jchk_privateOnly.addActionListener(e -> {
      root.remove(customLevelList);
      customLevelList = levelList(0, privateOnly.isSelected());
      jlbl_pageNum.setText("1");
      root.add(customLevelList);
      root.repaint();
    });

    // Button to decrement page number
    JButton jbtn_leftArrow = new JButton(new ImageIcon((new ImageIcon(Data.Images.Other.leftNavArrow)).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_leftArrow.setBounds(400, 700, 30, 60);
    jbtn_leftArrow.setBorderPainted(false);
    jbtn_leftArrow.addActionListener(e -> {
      customPageNum--;
      if (customPageNum < 0)
        customPageNum = (Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory + (privateOnly)? currentUser.getUsername() : "")).length - 1) / 8;
      
      root.remove(customLevelList);
      customLevelList = levelList(customPageNum, jchk_privateOnly.isSelected());
      jlbl_pageNum.setText(Integer.toString(customPageNum + 1));
      root.add(customLevelList);
      root.repaint();
    });

    // Button to increment page number
    JButton jbtn_rightArrow = new JButton(new ImageIcon((new ImageIcon("Images/ArrowRight.png")).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_rightArrow.setBounds(570, 700, 30, 60);
    jbtn_rightArrow.setBorderPainted(false);
    jbtn_rightArrow.addActionListener(e -> {
      customPageNum++;
      if (customPageNum > ((Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory + (privateOnly)? currentUser.getUsername() : "")).length - 1) / 8)
        customPageNum = 0;
      
      root.remove(customLevelList);
      customLevelList = levelList(customPageNum, jchk_privateOnly.isSelected());
      jlbl_pageNum.setText(Integer.toString(customPageNum + 1));
      root.add(customLevelList);
      root.repaint();
    });

    root.add(jlbl_title);
    root.add(customLevelList);
    root.add(jbtn_cancel);
    root.add(jlbl_page);
    root.add(jbtn_leftArrow);
    root.add(jbtn_rightArrow);
    root.add(jchk_privateOnly);

    // currentScreen = "browser";
    return root;
  }

  /* √ */
  public JPanel levelList(boolean privateOnly) {
    JPanel jpnl_list = new JPanel();
    jpnl_list.setLayout(null);
    jpnl_list.setBounds(80, 80, 840, 600);
    jpnl_list.setBackground(Color.WHITE);

    String path = Data.Utilities.customLevelDirectory + (privateOnly)? currentUser.getUsername() : "";
    File[] levels = Data.Utilities.getAllRegFilesInDirectory(new File(path));
    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
    for (int i = customPageNum * 8, position = 0; i < levels.length; i++, position++) {
      
      JDataButton button = new JDataButton(levels[i]);
      button.setBounds(200, 0 + position * 75, 640, 75);
      button.setBackground(Data.Colors.buttonBackground);
      button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      button.setFont(Data.Fonts.customButton);
      button.addActionListener(e -> {
        replace(new Level(button.getFile());
        while (!(Level.goToNextLevel || Level.returnToMenu));
          wait();
        replace(createLevelBrowser);
      });

      JLabel jlbl_creatorName = new JLabel(levels[i].getParentFile().getName(), SwingConstants.RIGHT);
      jlbl_creatorName.setBounds(0, 0 + i * 75, 185, 75);
      jlbl_creatorName.setFont(Data.Fonts.dataLabel);

      jpnl_list.add(button);
      jpnl_list.add(jlbl_creatorName);
    }

    return jpnl_list;
  }

  /* √ */
  public void replace(JPanel newPanel) {
    super.getContentPane().removeAll();
    super.getContentPane().add(newPanel);
    super.repaint();
  }