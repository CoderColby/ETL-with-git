import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Collections;
import java.util.Comparator;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.lang.Character;
import java.lang.Object;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Component;
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
        super.setText(Integer.toString(ID));
        super.setFont(Data.Fonts.menuLevelButton);
      } else {
        super.setText(null);
        super.setIcon(new ImageIcon(new ImageIcon(Data.Images.Other.lock).getImage().getScaledInstance(100, 100, Image.SCALE_FAST)));
        super.setDisabledIcon(new ImageIcon(new ImageIcon(Data.Images.Other.lock).getImage().getScaledInstance(100, 100, Image.SCALE_FAST)));
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
      super(object);
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

    public LevelPanel() {
      
      super.setLayout(null);
      super.setSize(150 * 4, 150 * 3);
      super.setOpaque(false);
      updateLevelGrid();
    }
    
    public int getGroupNum() {
      return this.groupNum;
    }

    public void incrementGroupNum() {
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
        for (int column = selector, levelID = this.groupNum * 10 + 4 * row + 1; column < 4 - selector && levelID <= Data.Utilities.numOfLevels; column++, levelID++) {
          JDataButton button = new JDataButton(levelID, currentUser.getLevels() >= levelID, currentUser.getPerfectLevels().contains(levelID), column, row);
          button.addActionListener(e -> {
            startLevelSequence(((JDataButton) e.getSource()).getID());
          });
          super.add(button);
        }
      }
      super.revalidate();
    }
  }
  
  private LevelPanel jpnl_menuLevels;

  private JPanel jpnl_customLevelList;
  private JLabel jlbl_pageNum;
  private int customPageNum;

  // Level creator

  private class LevelDesigner extends JPanel {

    private GameWindow window;
    private GameBoard levelBoard;
    private JPanel levelDisplay;
    private AbstractGameObject currentObject;

    private JTextField jtxf_levelName;
    private JTextField jtxf_levelEnergy;


    private abstract class DesignerButton extends JButton {
      protected GridCell gridCell;

      protected DesignerButton(GridCell gridCell) {
        this.gridCell = gridCell;
      }

      public GridCell getGridCell() {
        return gridCell;
      }

      public abstract void setObject(AbstractGameObject object);

      public abstract void cycleOptions();
    }

    private class RoomButton extends DesignerButton {

      public RoomButton(GridCell gridCell) {
        super(gridCell);
        super.repaint();
      }

      public void setObject(AbstractGameObject object) {
        if (object.getIdentifier().split(":")[0].equals(Eraser.TAG)) {
          super.gridCell.setRoomType(null);
          super.gridCell.setEntity(null);
          super.gridCell.setItem(null);
        } else if (object.getType().equals(Data.Utilities.forRoom))
          object.addSelf(super.gridCell, (byte) 0);
        else
          return;
        super.repaint();
      }

      public void cycleOptions() {
        if (super.gridCell.getItem() != null)
          super.gridCell.getItem().cycleOptions();
        else if (super.gridCell.getEntity() != null)
          super.gridCell.getEntity().cycleOptions();
        else if (super.gridCell.getRoomType() != null)
          super.gridCell.getRoomType().cycleOptions();
        super.repaint();
      }

      @Override
      protected void paintComponent(Graphics g) {
        AbstractGameObject[] images = new AbstractGameObject[] {gridCell.getRoomType(), gridCell.getEntity(), gridCell.getItem()};
        super.paintComponent(g);
        for (AbstractGameObject image : images) {
          if (image != null) {
            Point position = image.getGridCellPosition();
            g.drawImage(image.getImage(), (int) Math.round(position.getX()), (int) Math.round(position.getY()), null);
          }
        }
      }
    }

    private class WallButton extends DesignerButton {
      private byte orientation;

      public WallButton(GridCell gridCell, byte orientation) {
        super(gridCell);
        this.orientation = orientation;
        repaint();
      }

      public void setObject(AbstractGameObject object) {
        if (object.getIdentifier().split(":")[0].equals(Eraser.TAG))
          super.gridCell.setWall(AbstractWall.getWallByTag(Hallway.TAG + ":" + Hallway.DEFAULT, super.gridCell, orientation), orientation);
        else if (object.getType().equals(Data.Utilities.forWall))
          object.addSelf(super.gridCell, orientation);
        else
          return;
        repaint();
      }

      public void cycleOptions() {
        super.gridCell.getWall(orientation).cycleOptions();
        super.repaint();
      }

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
          g.drawImage(super.gridCell.getWall(orientation).getImage(), 0, 0, null);
      }
    }

    // Read load-in file
    private String levelName;
    private String startEnergy;
    private File levelFile;
    private JLabel jlbl_selectedItem;
    
    public LevelDesigner(File levelFile, GameWindow window) {
      this.window = window;
      this.levelFile = levelFile;

      Scanner fileIn = null;
      try {
        fileIn = new Scanner(levelFile);
      } catch (FileNotFoundException ef) {
        // nothing
        System.exit(1);
      }
      levelName = fileIn.nextLine().trim();
      startEnergy = fileIn.nextLine().trim();
      levelDisplay = buildLevel(fileIn);
      super.add(levelDisplay);
      super.repaint();
      
      // Base panel
      super.setLayout(null);
      super.setBackground(Color.WHITE);
      super.setBounds(window.getContentPane().getBounds());
  
      // Title "Level Creator"
      JLabel jlbl_title = new JLabel("Level Creator");
      jlbl_title.setBounds(20, 20, 300, 50);
      jlbl_title.setFont(Data.Fonts.header1);
  
      // Label for currently selected item
      jlbl_selectedItem = new JLabel();
      jlbl_selectedItem.setBounds(780, 30, 90, 90);
      jlbl_selectedItem.setBackground(new Color(150, 150, 150));
      jlbl_selectedItem.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      jlbl_selectedItem.setHorizontalAlignment(SwingConstants.CENTER);
  
      // Panel for selecting items
      JPanel jpnl_gameItems = new JPanel();
      jpnl_gameItems.setBounds(700, 160, 250, 270);
      jpnl_gameItems.setBackground(new Color(150, 150, 150));
      jpnl_gameItems.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      jpnl_gameItems.setLayout(null);
  
      // For displaying game objects to select
      AbstractWall[] walls = new AbstractWall[] {new Wall(), new Door(), new PowerDoor(), new OnceDoor(), new AirlockDoor(), new DetectionDoor(), new LockedDoor()};
      AbstractRoomType[] roomTypes = new AbstractRoomType[] {new Elevator(), new Target(), new Filled(), new Star()};
      AbstractItem[] items = new AbstractItem[] {new Key(), new Battery(), new Eraser()};
      AbstractEntity[] entities = new AbstractEntity[] {new Player(), new Zombie(), new SmartZombie()};
      int objectSeparation;
      int startLocation;
  
      // Draw wall types
      ArrayList<JDataButton> buttons = new ArrayList<>(300);
      objectSeparation = (jpnl_gameItems.getWidth() - walls.length * GameBoard.WALL_THICKNESS) / (walls.length + 1);
      startLocation = (jpnl_gameItems.getWidth() - (walls.length * (objectSeparation + GameBoard.WALL_THICKNESS) - objectSeparation)) / 2;
      for (int i = 0; i < walls.length; i++) {
        JDataButton newWall = new JDataButton(walls[i]);
        newWall.setBounds(startLocation + i * (objectSeparation + GameBoard.WALL_THICKNESS), 15, GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT);
        buttons.add(newWall);
      }
  
      // Draw room types
      objectSeparation = (jpnl_gameItems.getWidth() - roomTypes.length * GameBoard.ROOM_HEIGHT) / (roomTypes.length + 1);
      startLocation = (jpnl_gameItems.getWidth() - (roomTypes.length * (objectSeparation + GameBoard.ROOM_HEIGHT) - objectSeparation)) / 2;
      for (int i = 0; i < roomTypes.length; i++) {
        JDataButton newRoomType = new JDataButton(roomTypes[i]);
        newRoomType.setBounds(startLocation + i * (objectSeparation + GameBoard.ROOM_HEIGHT), 80, GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        buttons.add(newRoomType);
      }

      // Draw item types
      objectSeparation = (jpnl_gameItems.getWidth() - items.length * GameBoard.ROOM_HEIGHT) / (items.length + 1);
      startLocation = (jpnl_gameItems.getWidth() - (items.length * (objectSeparation + GameBoard.ROOM_HEIGHT) - objectSeparation)) / 2;
      for (int i = 0; i < items.length; i++) {
        JDataButton newItem = new JDataButton(items[i]);
        newItem.setBounds(startLocation + i * (objectSeparation + GameBoard.ROOM_HEIGHT), 140, GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        buttons.add(newItem);
      }
  
      // Draw entity types
      objectSeparation = (jpnl_gameItems.getWidth() - entities.length * GameBoard.ROOM_HEIGHT) / (entities.length + 1);
      startLocation = (jpnl_gameItems.getWidth() - (entities.length * (objectSeparation + GameBoard.ROOM_HEIGHT) - objectSeparation)) / 2;
      for (int i = 0; i < entities.length; i++) {
        JDataButton newEntity = new JDataButton(entities[i]);
        newEntity.setBounds(startLocation + i * (objectSeparation + GameBoard.ROOM_HEIGHT), 200, GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        buttons.add(newEntity);
      }

      for (JDataButton j : buttons) {
        j.setBackground(Data.Colors.roomBackground);
        j.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              // if (currentObject != null)
              // ((DesignerButton) e.getSource()).setObject(currentObject);
              currentObject = ((JDataButton) e.getSource()).getObject();
              jlbl_selectedItem.setIcon(currentObject);
              // super.repaint();
              LevelDesigner.this.levelDisplay.repaint();
            } else if (SwingUtilities.isRightMouseButton(e)) {
              JTextArea ta = new JTextArea(((JDataButton) e.getSource()).getObject().getInfo());
              ta.setWrapStyleWord(true);
              ta.setLineWrap(true);
              ta.setOpaque(false);
              ta.setEditable(false);
              JScrollPane sp = new JScrollPane(ta);
              sp.setPreferredSize(new Dimension(300, 80));
             JOptionPane.showMessageDialog(LevelDesigner.this, sp, ((JDataButton) e.getSource()).getObject().getIdentifier().split(":")[0], JOptionPane.INFORMATION_MESSAGE); 
            }
          }
        });
        jpnl_gameItems.add(j);
      }

      // Label level name
      JLabel jlbl_levelName = new JLabel("Level name:", SwingConstants.CENTER);
      jlbl_levelName.setBounds(700, 565, 250, 25);
      jlbl_levelName.setFont(Data.Fonts.textLabel);

      // Field level name
      jtxf_levelName = new JTextField(levelName);
      jtxf_levelName.setBounds(700, 590, 250, 30);
      jtxf_levelName.setFont(Data.Fonts.textField);

      // Label energy
      JLabel jlbl_levelEnergy = new JLabel("Energy:");
      jlbl_levelEnergy.setBounds(765, 640, 80, 25);
      jlbl_levelEnergy.setFont(Data.Fonts.textLabel);

      // Field energy
      jtxf_levelEnergy = new JTextField(startEnergy);
      jtxf_levelEnergy.setBounds(845, 638, 40, 29);
      jtxf_levelEnergy.setFont(Data.Fonts.textField);

      // Button to load a different level
      JButton jbtn_load = new JButton("Choose Level");
      jbtn_load.setBounds(25, 740, 200, 40);
      jbtn_load.setBackground(Data.Colors.buttonBackground);
      jbtn_load.setFont(Data.Fonts.menuButton);
      jbtn_load.addActionListener(e -> {
        File[] allCustomLevelFiles = Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory));
        // Arrays.sort(allCustomLevelFiles, Comparator.comparingLong(File::lastModified).reversed());
        String filename = JOptionPane.showInputDialog(this, "Filename:");
        if (filename != null && !filename.isEmpty()) {
          if (!filename.matches(".*\\.txt$"))
            filename = filename.concat(".txt");
          for (File f : allCustomLevelFiles)
            if (filename.equals(f.getName())) {
              super.remove(levelDisplay);
              // Read load-in file
              Scanner fileIn2 = null;
              try {
                fileIn2 = new Scanner(f);
              } catch (FileNotFoundException ef) {
                return;
                // nothing
              }
              levelName = fileIn2.nextLine().trim();
              jtxf_levelName.setText(levelName);
              startEnergy = fileIn2.nextLine().trim();
              jtxf_levelEnergy.setText(startEnergy);
              levelDisplay = buildLevel(fileIn2);
              super.add(levelDisplay);
              super.repaint();
              fileIn2.close();
              return;
            }
          JOptionPane.showMessageDialog(this, "A level of that name does not exist!");
        }
      });

      // Button to demo level
      JButton jbtn_demo = new JButton("Demo");
      jbtn_demo.setBounds(275, 740, 200, 40);
      jbtn_demo.setBackground(Data.Colors.buttonBackground);
      jbtn_demo.setFont(Data.Fonts.menuButton);
      jbtn_demo.addActionListener(e -> {
        if (levelBoard.isValidLayout()) {
          LevelDesigner.this.levelFile = new File(Data.Utilities.customLevelDirectory + GameWindow.this.currentUser.getUsername() + "/.temp");
          LevelDesigner.this.writeToFile(LevelDesigner.this.levelFile);

          SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            
            @Override
            protected Void doInBackground() throws Exception {
              Level level = new Level(LevelDesigner.this.levelFile, true, GameWindow.this, 0);
              GameWindow.this.replace(level);
              level.setFocusable(true);
              level.requestFocusInWindow();
  
              while (!(Level.goToNextLevel || Level.returnToMenu)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException f) {
                    // nothing
                }
              }
  
              return null;
            }
    
            @Override
            protected void done() {
              GameWindow.this.replace(new LevelDesigner(LevelDesigner.this.levelFile, LevelDesigner.this.window));
              LevelDesigner.this.levelFile.delete();
            }
          };
      
          worker.execute();
        } else
          JOptionPane.showMessageDialog(GameWindow.this, "This level is missing some essential parts that make it unplayable.");

      });

      // Button to save construction
      JButton jbtn_save = new JButton("Save As...");
      jbtn_save.setBounds(525, 740, 200, 40);
      jbtn_save.setBackground(Data.Colors.buttonBackground);
      jbtn_save.setFont(Data.Fonts.menuButton);
      jbtn_save.addActionListener(e -> {
        if (LevelDesigner.this.levelBoard.isValidLayout()) {
          String filename = JOptionPane.showInputDialog(this, "Filename:");
          if (filename == null || filename.trim().isEmpty())
            return;
          filename = filename.trim();
          if (!filename.matches(".*\\.txt$"))
            filename = filename.concat(".txt");
  
          File tempLevelFile = null;
          File[] levelFiles = Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory));
          for (int i = 0; i < levelFiles.length; i++) {
            if (levelFiles[i].getName().equals(filename)) {
              tempLevelFile = levelFiles[i];
              break;
            }
          }
          if (tempLevelFile == null || (tempLevelFile.getParentFile().getName().equals(GameWindow.this.currentUser.getUsername()) && JOptionPane.showConfirmDialog(this, "One of your custom levels already has this file name, would you like to overwrite it?") == JOptionPane.YES_OPTION)) {
            LevelDesigner.this.writeToFile(new File(Data.Utilities.customLevelDirectory + GameWindow.this.currentUser.getUsername() + "/" + filename));
          } else if (!tempLevelFile.getParentFile().getName().equals(GameWindow.this.currentUser.getUsername())) {
            JOptionPane.showMessageDialog(GameWindow.this, "This filename is already being used by someone else. Please choose a different name.");
          }
        } else
          JOptionPane.showMessageDialog(GameWindow.this, "This level is missing some essential parts that make it unplayable.");
          
      });

      // Button to return home
      JButton jbtn_exit = new JButton("Exit");
      jbtn_exit.setBounds(775, 740, 200, 40);
      jbtn_exit.setBackground(Data.Colors.buttonBackground);
      jbtn_exit.setFont(Data.Fonts.menuButton);
      jbtn_exit.addActionListener(e -> {
        GameWindow.this.replace(GameWindow.this.createMenu(0));
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
    }


    private JPanel buildLevel(Scanner fileIn) {
  
      String[][] boardData = new String[100][5];
  
      for (int i = 0; i < 100; i++)
        boardData[i] = fileIn.nextLine().split(" ");
      fileIn.close();
      
      levelBoard = new GameBoard(boardData, 0, null, true);
      levelDisplay = new JPanel();
      levelDisplay.setLayout(null);
      levelDisplay.setBounds(70, 110, GameBoard.BOARD_DIMENSION, GameBoard.BOARD_DIMENSION);
      levelDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK, GameBoard.WALL_THICKNESS));
      levelDisplay.setBackground(Data.Colors.levelBackground);

      ArrayList<DesignerButton> buttons = new ArrayList<>(280);

      for (int i = 0; i < 100; i++) {
        GridCell gridCell = levelBoard.getGridCell(new int[] {i / 10, i % 10});
        RoomButton rb = new RoomButton(gridCell);
        rb.setBounds(GameBoard.WALL_THICKNESS + (i % 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS + (i / 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.ROOM_HEIGHT, GameBoard.ROOM_HEIGHT);
        buttons.add(rb);
      }

      for (int i = 0; i < 90; i++) {
        GridCell gridCell = levelBoard.getGridCell(new int[] {i / 9, i % 9});
        WallButton wb = new WallButton(gridCell, (byte) 0);
        wb.setBounds(GameBoard.WALL_THICKNESS + GameBoard.ROOM_HEIGHT + (i % 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS + (i / 9) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS, GameBoard.ROOM_HEIGHT);
        buttons.add(wb);
      }

      for (int i = 0; i < 90; i++) {
        GridCell gridCell = levelBoard.getGridCell(new int[] {i / 10, i % 10});
        WallButton wb = new WallButton(gridCell, (byte) 1);
        wb.setBounds(GameBoard.WALL_THICKNESS + (i % 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.WALL_THICKNESS + GameBoard.ROOM_HEIGHT + (i / 10) * (GameBoard.ROOM_HEIGHT + GameBoard.WALL_THICKNESS), GameBoard.ROOM_HEIGHT, GameBoard.WALL_THICKNESS);
        buttons.add(wb);
      }

      for (DesignerButton db : buttons) {
        db.setBackground(Data.Colors.roomBackground);
        db.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              if (currentObject != null)
              ((DesignerButton) e.getSource()).setObject(currentObject);
              LevelDesigner.this.levelDisplay.repaint();
            } else if (SwingUtilities.isRightMouseButton(e))
              ((DesignerButton) e.getSource()).cycleOptions();
          }
        });
        levelDisplay.add(db);
      }

      return levelDisplay;
    }


    private void writeToFile(File file) {
      PrintWriter fileOut = null;
      try {
        fileOut = new PrintWriter(file);
      } catch (FileNotFoundException f) {
        System.out.println("NO FILE!!");
        System.out.println(file.getPath());
      }
      fileOut.println(jtxf_levelName.getText().trim());
      fileOut.println(jtxf_levelEnergy.getText().trim());
      // fileOut.println();
  
      for (String s : this.levelBoard.stringify())
        fileOut.println(s);
      
      fileOut.close();
    }
  }

  private LevelDesigner levelDesigner;
  
  
  /* √ */
  public GameWindow() {
    super("ESCAPE THE LAB 2");
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.getContentPane().setSize(1000, 800);
    super.setSize(1000, 820);
    super.setMinimumSize(new Dimension(1000, 820));
    super.setMaximumSize(new Dimension(1000, 820));
    super.getContentPane().setLayout(null);
    super.getContentPane().add(createLogin());
    super.setVisible(true);
  }

  
  private JTextField jtxf_loginUsername;
  private JTextField jtxf_loginPassword;
  private JTextField jtxf_signupUsername;
  private JTextField jtxf_signupPassword;
  private JTextField jtxf_signupConfirmPassword;
  private JTextField jtxf_signupName;

  /* √ */
  public JPanel createLogin() { // Creates login page for new and old users

    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);
    root.setBounds(super.getContentPane().getBounds());
    

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
    jtxf_loginUsername = new JTextField();
    jtxf_loginUsername.setBounds(100, 175, 300, 25);
    jtxf_loginUsername.setFont(Data.Fonts.textField);

    // Field for password
    jtxf_loginPassword = new JTextField();
    jtxf_loginPassword.setBounds(100, 255, 300, 25);
    jtxf_loginPassword.setFont(Data.Fonts.textField);

    // Button to log in
    JButton jbtn_loginButton = new JButton("Log in");
    jbtn_loginButton.setBounds(170, 650, 160, 40);
    jbtn_loginButton.setBackground(Data.Colors.buttonBackground);
    jbtn_loginButton.setFont(Data.Fonts.menuButton);
    jbtn_loginButton.addActionListener(l -> {
      String username = GameWindow.this.jtxf_loginUsername.getText().trim();
      String password = GameWindow.this.jtxf_loginPassword.getText();
      String errorMessages = "";

      // Validate input
      errorMessages += User.isValidUsername(username);
      errorMessages += User.isValidPassword(password);
      if (errorMessages.isEmpty()) { // If no invalid input yet
        if (!User.isUserExist(username)) // If user does not exist
          errorMessages = "The username you entered does not exist! Try signing up if you don't have an account yet.";
        else if (!User.isCorrectPassword(username, password)) // If password is incorrect
          errorMessages = "This user has a password different than the one provided, please try again.";
      }
      if (errorMessages.isEmpty()) { // If no invalid input yet
        try {
          currentUser = new User(new File(Data.Utilities.getUserFilePath(username))); // Make new user
        } catch (FileNotFoundException f) {
          // nothing
        }
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
    jtxf_signupUsername = new JTextField();
    jtxf_signupUsername.setBounds(600, 175, 300, 25);
    jtxf_signupUsername.setFont(Data.Fonts.textField);

    // Field for password
    jtxf_signupPassword = new JTextField();
    jtxf_signupPassword.setBounds(600, 255, 300, 25);
    jtxf_signupPassword.setFont(Data.Fonts.textField);

    // Field for password confirmation
    jtxf_signupConfirmPassword = new JTextField();
    jtxf_signupConfirmPassword.setBounds(600, 335, 300, 25);
    jtxf_signupConfirmPassword.setFont(Data.Fonts.textField);

    // Field for name
    jtxf_signupName = new JTextField();
    jtxf_signupName.setBounds(600, 415, 300, 25);
    jtxf_signupName.setFont(Data.Fonts.textField);

    // Button to sign up
    JButton jbtn_signupButton = new JButton("Sign up");
    jbtn_signupButton.setBounds(670, 650, 160, 40);
    jbtn_signupButton.setBackground(Data.Colors.buttonBackground);
    jbtn_signupButton.setFont(Data.Fonts.menuButton);
    jbtn_signupButton.addActionListener(l -> {
      String username = GameWindow.this.jtxf_signupUsername.getText().trim();
      String password = GameWindow.this.jtxf_signupPassword.getText();
      String realName = GameWindow.this.jtxf_signupName.getText();
      String errorMessages = "";

      // Validate input
      errorMessages += User.isValidUsername(username);
      if (errorMessages.isEmpty() && User.isUserExist(username)) // If no invalid input but user exists
          errorMessages = "This username already exists! Please try a different one or log in.\n";
      else {
        errorMessages += User.isValidPassword(password);
        if (realName.isEmpty()) // If no name is provided
          errorMessages += "Please provide your name.\n";
        if (!password.equals(GameWindow.this.jtxf_signupConfirmPassword.getText())) // If confirmation password is different
          errorMessages += "Please ensure that both passwords are identical.\n";
      }
      if (errorMessages.isEmpty()) { // If no invalid input
        currentUser = new User(username, password, realName); // Make brand new user
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

    jtxf_loginUsername.setNextFocusableComponent(jtxf_loginPassword);
    jtxf_loginPassword.setNextFocusableComponent(jbtn_loginButton);
    jbtn_loginButton.setNextFocusableComponent(jtxf_signupUsername);
    jtxf_signupUsername.setNextFocusableComponent(jtxf_signupPassword);
    jtxf_signupPassword.setNextFocusableComponent(jtxf_signupConfirmPassword);
    jtxf_signupConfirmPassword.setNextFocusableComponent(jtxf_signupName);
    jtxf_signupName.setNextFocusableComponent(jbtn_signupButton);

    /////////////////////////
    JButton jbtn_shortcut = new JButton("Guest");
    jbtn_shortcut.setBounds(50, 50, 80, 30);
    jbtn_shortcut.addActionListener(e -> {
      try {
        currentUser = new User(new File(Data.Utilities.getUserFilePath("CoderColby"))); // Make new user
      } catch (FileNotFoundException f) {
        // nothing
      }
      replace(createMenu(0));
    });
    root.add(jbtn_shortcut);
    /////////////////////////
    

    return root;
    
  } // Login page for new and existing users, creates new user object

  
  /* √ */
  public JPanel createMenu(int levelGroup) { // Creates menu page showing all unlocked levels, with custom level options and account settings
    
    // Base Panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);
    root.setBounds(super.getContentPane().getBounds());

    // ETL logo
    JLabel jlbl_logo = new JLabel(new ImageIcon(Data.Images.Other.logo));
    jlbl_logo.setBounds(20, 20, 65 + 155, 75 + 80);

    // Label for name
    JLabel jlbl_nameLabel = new JLabel("Name:");
    jlbl_nameLabel.setBounds(440, 20, 85, 45);
    jlbl_nameLabel.setFont(Data.Fonts.dataLabel);

    // Label for completed levels
    JLabel jlbl_completedLabel = new JLabel("Levels Completed:");
    jlbl_completedLabel.setBounds(440, 65, 205, 45);
    jlbl_completedLabel.setFont(Data.Fonts.dataLabel);

    // Label for perfect levels
    JLabel jlbl_perfectLabel = new JLabel("Perfect Levels:");
    jlbl_perfectLabel.setBounds(440, 110, 185, 45);
    jlbl_perfectLabel.setFont(Data.Fonts.dataLabel);

    // Value for name
    JLabel jlbl_nameField = new JLabel(currentUser.getRealName());
    jlbl_nameField.setBounds(660, 20, 300, 45);
    jlbl_nameField.setFont(Data.Fonts.dataLabel);

    // Value for completed levels
    JLabel jlbl_completedField = new JLabel((currentUser.getLevels() - 1) + "/" + Data.Utilities.numOfLevels);
    jlbl_completedField.setBounds(660, 65, 100, 45);
    jlbl_completedField.setFont(Data.Fonts.dataLabel);

    // Value for perfect levels
    JLabel jlbl_perfectField = new JLabel(currentUser.getPerfectLevels().size() + "/" + Data.Utilities.numOfLevels);
    jlbl_perfectField.setBounds(660, 110, 100, 45);
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
      replace(new LevelDesigner(new File(Data.Utilities.defaultLevelFile), GameWindow.this));
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
    jpnl_menuLevels = new LevelPanel();
    jpnl_menuLevels.setLocation(195, 285);
    root.add(jpnl_menuLevels);

    // Button for level navigation 
    JButton jbtn_leftArrow = new JButton(new ImageIcon(new ImageIcon(Data.Images.Other.leftNavArrow).getImage().getScaledInstance(100, 180, Image.SCALE_FAST)));
    jbtn_leftArrow.setBounds(50, 335, 100, 180);
    jbtn_leftArrow.setBorderPainted(false);
    jbtn_leftArrow.setBackground(Color.WHITE);
    jbtn_leftArrow.addActionListener(e -> {
      jpnl_menuLevels.decrementGroupNum();
    });

    // Button for level navigation
    JButton jbtn_rightArrow = new JButton(new ImageIcon(new ImageIcon(Data.Images.Other.rightNavArrow).getImage().getScaledInstance(100, 180, Image.SCALE_FAST)));
    jbtn_rightArrow.setBounds(840, 335, 100, 180);
    jbtn_rightArrow.setBorderPainted(false);
    jbtn_rightArrow.setBackground(Color.WHITE);
    jbtn_rightArrow.addActionListener(e -> {
      jpnl_menuLevels.incrementGroupNum();
    });

    // Add navigation arrows
    root.add(jbtn_leftArrow);
    root.add(jbtn_rightArrow);

    return root;
  } // Creates menu with level navigation and user info with custom levels

  
  private int startingLevel;
  /* √ */
  public void startLevelSequence(int startingLevel) {
    this.startingLevel = startingLevel;
    
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      private int levelNum;
      
      @Override
      protected Void doInBackground() throws Exception {
        Level.goToNextLevel = false;
        Level.returnToMenu = false;
        for (levelNum = GameWindow.this.startingLevel; levelNum <= Data.Utilities.numOfLevels && !Level.returnToMenu; levelNum++) {
          Level level = new Level(new File(Data.Utilities.getLevelFilePath(levelNum)), false, GameWindow.this, levelNum);
          GameWindow.this.replace(level);
          level.setFocusable(true);
          level.requestFocusInWindow();

          while (!(Level.goToNextLevel || Level.returnToMenu)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException f) {
                // nothing
            }
          }
        }
        return null;
      }

      @Override
      protected void done() {
        GameWindow.this.replace(GameWindow.this.createMenu((levelNum - 1) / 10));
      }
    };

    worker.execute();
  }


  public JPanel createSettings() { // Ability to log out or delete account along with change user characteristics
    
    // Base panel
    JPanel root = new JPanel();
    root.setLayout(null);
    root.setBackground(Color.WHITE);
    root.setBounds(super.getContentPane().getBounds());

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
    JTextField jtxf_usernameField = new JTextField(currentUser.getUsername());
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
    // jbtn_apply.addActionListener(e -> {
    //   String username = jtxf_usernameField.getText();
    //   String password = jtxf_passwordField.getText();
    //   String realName = jtxf_nameField.getText();
    //   String errorMessages = "";
      
    //   // Validate input
    //   errorMessages += User.isValidUsername(username);
    //   errorMessages += User.isValidPassword(password);
    //   if (realName.isEmpty()) // If no name is provided
    //     errorMessages += "Please provide your name.\n";
      
    //   if (errorMessages.isEmpty()) { // If no invalid input
    //     if (!currentUser.getUsername().equals(username))
    //       publicizeCustomLevels();
    //     currentUser.setUsername(username);
    //     currentUser.setPassword(password);
    //     currentUser.setRealName(realName);
    //   } else
    //     JOptionPane.showMessageDialog(this, errorMessages);
    // });

    // Button to cancel and return to menu
    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(380, 475, 240, 55);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu(0));
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
        publicizeCustomLevels();
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


  private JCheckBox jchk_privateOnly;
  private JPanel CLBroot;

  /* √ */
  public JPanel createLevelBrowser() {
    
    // Base panel
    CLBroot = new JPanel();
    CLBroot.setLayout(null);
    CLBroot.setBackground(Color.WHITE);
    CLBroot.setBounds(super.getContentPane().getBounds());

    // Title
    JLabel jlbl_title = new JLabel("Select a Level:", SwingConstants.CENTER);
    jlbl_title.setBounds(300, 20, 400, 50);
    jlbl_title.setFont(Data.Fonts.header1);

    // Check box for private level visibility setting
    jchk_privateOnly = new JCheckBox("My levels only", false);
    jchk_privateOnly.setBounds(770, 720, 150, 25);
    jchk_privateOnly.setOpaque(false);
    jchk_privateOnly.setFont(Data.Fonts.checkboxLabel);
    jchk_privateOnly.addActionListener(e -> {
      GameWindow.this.CLBroot.remove(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.customPageNum = 0;
      GameWindow.this.jpnl_customLevelList = GameWindow.this.levelList();
      GameWindow.this.jlbl_pageNum.setText("1");
      GameWindow.this.CLBroot.add(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.CLBroot.repaint();
    });

    // Level selection page
    customPageNum = 0;
    jpnl_customLevelList = levelList();

    // Button to cancel and return to menu
    JButton jbtn_cancel = new JButton("Cancel");
    jbtn_cancel.setBounds(40, 700, 150, 60);
    jbtn_cancel.setBackground(Data.Colors.buttonBackground);
    jbtn_cancel.setFont(Data.Fonts.menuButton);
    jbtn_cancel.addActionListener(e -> {
      replace(createMenu(0));
    });

    // Value for page number
    jlbl_pageNum = new JLabel("1", SwingConstants.CENTER);
    jlbl_pageNum.setBounds(460, 710, 80, 40);
    jlbl_pageNum.setBackground(Data.Colors.buttonBackground);
    jlbl_pageNum.setOpaque(true);
    jlbl_pageNum.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    jlbl_pageNum.setFont(Data.Fonts.header2);

    // Button to decrement page number
    JButton jbtn_leftArrow = new JButton(new ImageIcon(new ImageIcon(Data.Images.Other.leftNavArrow).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_leftArrow.setBounds(400, 700, 30, 60);
    jbtn_leftArrow.setBorderPainted(false);
    jbtn_leftArrow.setBackground(Color.WHITE);
    jbtn_leftArrow.addActionListener(e -> {
      GameWindow.this.customPageNum--;
      if (GameWindow.this.customPageNum < 0)
        GameWindow.this.customPageNum = (Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory + ((GameWindow.this.jchk_privateOnly.isSelected())? GameWindow.this.currentUser.getUsername() : ""))).length - 1) / 8;
      
      GameWindow.this.CLBroot.remove(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.jpnl_customLevelList = GameWindow.this.levelList();
      GameWindow.this.jlbl_pageNum.setText(Integer.toString(GameWindow.this.customPageNum + 1));
      GameWindow.this.CLBroot.add(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.CLBroot.repaint();
    });

    // Button to increment page number
    JButton jbtn_rightArrow = new JButton(new ImageIcon((new ImageIcon(Data.Images.Other.rightNavArrow)).getImage().getScaledInstance(30, 60, Image.SCALE_FAST)));
    jbtn_rightArrow.setBounds(570, 700, 30, 60);
    jbtn_rightArrow.setBorderPainted(false);
    jbtn_rightArrow.setBackground(Color.WHITE);
    jbtn_rightArrow.addActionListener(e -> {
      GameWindow.this.customPageNum++;
      if (GameWindow.this.customPageNum > ((Data.Utilities.getAllRegFilesInDirectory(new File(Data.Utilities.customLevelDirectory + ((GameWindow.this.jchk_privateOnly.isSelected())? GameWindow.this.currentUser.getUsername() : ""))).length - 1) / 8))
        GameWindow.this.customPageNum = 0;
      
      GameWindow.this.CLBroot.remove(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.jpnl_customLevelList = GameWindow.this.levelList();
      GameWindow.this.jlbl_pageNum.setText(Integer.toString(GameWindow.this.customPageNum + 1));
      GameWindow.this.CLBroot.add(GameWindow.this.jpnl_customLevelList);
      GameWindow.this.CLBroot.repaint();
    });

    CLBroot.add(jlbl_title);
    CLBroot.add(jpnl_customLevelList);
    CLBroot.add(jbtn_cancel);
    CLBroot.add(jlbl_pageNum);
    CLBroot.add(jbtn_leftArrow);
    CLBroot.add(jbtn_rightArrow);
    CLBroot.add(jchk_privateOnly);

    // currentScreen = "browser";
    return CLBroot;
  }

  
  /* √ */
  public JPanel levelList() {
    JPanel jpnl_list = new JPanel();
    jpnl_list.setLayout(null);
    jpnl_list.setBounds(80, 80, 840, 600);
    jpnl_list.setBackground(Color.WHITE);

    String path = Data.Utilities.customLevelDirectory + ((jchk_privateOnly.isSelected())? currentUser.getUsername() : "");
    File[] levels = Data.Utilities.getAllRegFilesInDirectory(new File(path));
    Arrays.sort(levels, Comparator.comparingLong(File::lastModified).reversed());
    for (int i = customPageNum * 8, position = 0; i < levels.length && position < 8; i++, position++) {
      
      JDataButton button = new JDataButton(levels[i]);
      button.setBounds(200, 0 + position * 75, 640, 75);
      button.setBackground(Data.Colors.buttonBackground);
      button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      button.setFont(Data.Fonts.customButton);
      button.addActionListener(e -> {
        Level level = new Level(((JDataButton) e.getSource()).getFile(), true, GameWindow.this, 0);
        GameWindow.this.replace(level);
        level.setFocusable(true);
        level.requestFocusInWindow();
    
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {

            while (!(Level.goToNextLevel || Level.returnToMenu)) {
              try {
                  Thread.sleep(50);
              } catch (InterruptedException f) {
                  // nothing
              }
            }

            return null;
          }
  
          @Override
          protected void done() {
            GameWindow.this.replace(GameWindow.this.createLevelBrowser());
          }
        };
    
        worker.execute();
      });

      JLabel jlbl_creatorName = new JLabel(levels[i].getParentFile().getName(), SwingConstants.RIGHT);
      jlbl_creatorName.setBounds(0, 0 + position * 75, 185, 75);
      jlbl_creatorName.setFont(Data.Fonts.dataLabel);

      jpnl_list.add(button);
      jpnl_list.add(jlbl_creatorName);
    }

    return jpnl_list;
  }

  
  public User getUser() {
    return currentUser;
  }

  
  /* √ */
  public void replace(JPanel newPanel) {
    super.getContentPane().removeAll();
    super.getContentPane().add(newPanel);
    repaint();
  }
}