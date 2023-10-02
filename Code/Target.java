import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;


public class Target extends AbstractRoomType {

  public static final String TAG = "Target";
  public static final byte DEFAULT = 0;
  public static final byte BAD = 0;
  public static final byte GOOD = 1;
  public static boolean isOngoing = false;

  private boolean isGood;

  
  public Target() {
    super(Target.TAG + ":" + Target.DEFAULT, Data.Images.RoomType.target(Target.DEFAULT));
  }
  
  public Target(GridCell gridCell, byte startCondition) {
    super(Target.TAG + ":" + startCondition, gridCell, Data.Images.RoomType.target(startCondition));
    isGood = startCondition == Target.GOOD;
  }

  public boolean isGood() {
    return isGood;
  }

  public void setGood() {
    isGood = true;
    super.setImage(new ImageIcon(Data.Images.RoomType.target(Target.GOOD)).getImage());
  }

  public void setBad() {
    isGood = false;
    super.setImage(new ImageIcon(Data.Images.RoomType.target(Target.BAD)).getImage());
  }

  public void cycleOptions() {
    isGood = !isGood;
    super.setImage(new ImageIcon(Data.Images.RoomType.target((isGood)? Target.GOOD : Target.BAD)).getImage());
    super.identifier = Target.TAG + ":" + ((isGood)? Target.GOOD : Target.BAD);
  }

  
  public JPanel fixPanel() {
    Target.isOngoing = true;
    JPanel puzzle = new PipePuzzle(this);
    puzzle.setBounds(super.gridCell.getGameBoard().getBounds());

    // JButton b = new JButton("Press me to return");
    // b.setBounds(200, 200, 250, 100);
    // b.addActionListener(event -> {
    //   Target.this.isGood = true;
    //   Target.super.setImage(new ImageIcon(Data.Images.RoomType.target(Target.GOOD)).getImage());
    //   Target.isOngoing = false;
    // });

    // root.add(b);

    puzzle.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_NUMPAD0)
            Target.this.gridCell.getGameBoard().toggleTarget();
      }
    });
    puzzle.repaint();

    return puzzle;
  }

  public String getInfo() {
    return "The player will need to activate all of these before they can return to the elevator. Smart Zombies try to turn these off when they can.";
  }
}












class ArithmeticPuzzle extends JPanel {
  private JTextField jtxf_numDisplay;
  private JLabel jlbl_light;
  private JButton[] numpad = new JButton[12];
  private byte numOfDigits;
  private final char[] possibleOp = new char[] {'+', '-', '×'};
  private int randOp;
  private int rand1;
  private int rand2;
  private int correctAns;
  private Target host;
  
  public ArithmeticPuzzle(Target host) {
    this.host = host;
    randOp = (int) Math.round(Math.random() * Integer.MAX_VALUE % 3);
    rand1 = (int) Math.round(Math.random() * Integer.MAX_VALUE % (20 - 10 * (randOp / 2)));
    rand2 = (int) Math.round(Math.random() * Integer.MAX_VALUE % (20 - 10 * (randOp / 2)));

    switch (randOp) {
      case 0:
        correctAns = rand1 + rand2;
        break;
      case 1:
        rand2 %= rand1;
        correctAns = rand1 - rand2;
        break;
      case 2:
        correctAns = rand1 * rand2;
        break;
      default:
        correctAns = 0;
    }

    String equation = rand1 + " " + possibleOp[randOp] + " " + rand2 + " = ";
    numOfDigits = 0;

    jtxf_numDisplay = new JTextField(equation);
    jtxf_numDisplay.setEditable(false);
    jtxf_numDisplay.setBackground(Data.Colors.puzzleScreenBackground);
    jtxf_numDisplay.setForeground(Data.Colors.puzzleScreenForeground);
    jtxf_numDisplay.setFont(Data.Fonts.header2);
    jtxf_numDisplay.setBounds(60, 30, 480, 50);


    for (int i = 0, pos = 0; i < 10; i++, pos = i / 9) {
      JButton jbtn_numpadButton = new JButton(Integer.toString(i));
      // jbtn_numpadButton.setDisabledText(Integer.toString(i));
      jbtn_numpadButton.setFont(Data.Fonts.header1);
      jbtn_numpadButton.setBackground(Data.Colors.puzzleNumpadBackground);
      jbtn_numpadButton.setForeground(Data.Colors.puzzleNumpadForeground);
      jbtn_numpadButton.setBorder(BorderFactory.createLineBorder(Data.Colors.puzzleNumpadBorder, 3));
      jbtn_numpadButton.setBounds(75 + (i % 3 + pos) * (130 + 30), 110 + (i / 3) * (100 + 25), 130, 100);
      jbtn_numpadButton.addActionListener(e -> {
        if (ArithmeticPuzzle.this.numOfDigits < 2 && !ArithmeticPuzzle.this.host.isGood()) {
          ArithmeticPuzzle.this.jtxf_numDisplay.setText(ArithmeticPuzzle.this.jtxf_numDisplay.getText() + ((JButton) e.getSource()).getText());
          ArithmeticPuzzle.this.numOfDigits++;
        }
      });
      ArithmeticPuzzle.this.numpad[i] = jbtn_numpadButton;
    }

    JButton jbtn_numpadBackspace = new JButton(new ImageIcon(new ImageIcon(Data.Images.Puzzle.numpadBackspace).getImage().getScaledInstance(100, 40, Image.SCALE_FAST)));
    jbtn_numpadBackspace.setDisabledIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.numpadBackspace).getImage().getScaledInstance(100, 40, Image.SCALE_FAST)));
    jbtn_numpadBackspace.setBackground(Data.Colors.puzzleNumpadBackground);
    jbtn_numpadBackspace.setBorder(BorderFactory.createLineBorder(Data.Colors.puzzleNumpadBorder, 3));
    jbtn_numpadBackspace.setBounds(75, 485, 130, 100);
    jbtn_numpadBackspace.addActionListener(e -> {
      if (ArithmeticPuzzle.this.numOfDigits > 0 && !ArithmeticPuzzle.this.host.isGood()) {
        String text = ArithmeticPuzzle.this.jtxf_numDisplay.getText();
        ArithmeticPuzzle.this.jtxf_numDisplay.setText(text.substring(0, text.length() - 1));
        ArithmeticPuzzle.this.numOfDigits--;
      }
    });
    numpad[10] = jbtn_numpadBackspace;

    JButton jbtn_numpadEnter = new JButton(new ImageIcon(new ImageIcon(Data.Images.Puzzle.numpadEnter).getImage().getScaledInstance(100, 40, Image.SCALE_FAST)));
    jbtn_numpadEnter.setDisabledIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.numpadEnter).getImage().getScaledInstance(100, 40, Image.SCALE_FAST)));
    jbtn_numpadEnter.setBackground(Data.Colors.puzzleNumpadBackground);
    jbtn_numpadEnter.setBorder(BorderFactory.createLineBorder(Data.Colors.puzzleNumpadBorder, 3));
    jbtn_numpadEnter.setBounds(395, 485, 130, 100);
    jbtn_numpadEnter.addActionListener(e -> {
      if (!ArithmeticPuzzle.this.host.isGood()) {
        String text = ArithmeticPuzzle.this.jtxf_numDisplay.getText();
        String answer = text.substring(text.length() - numOfDigits).trim();
        if (!answer.isEmpty() && Integer.parseInt(answer) == ArithmeticPuzzle.this.correctAns) {
          ArithmeticPuzzle.this.jlbl_light.setIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.lightOn).getImage().getScaledInstance(30, 30, Image.SCALE_FAST)));
          ArithmeticPuzzle.this.host.setGood();
        } else
          ArithmeticPuzzle.this.jlbl_light.setIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.lightOff).getImage().getScaledInstance(30, 30, Image.SCALE_FAST)));
      }
    });
    numpad[11] = jbtn_numpadEnter;

    jlbl_light = new JLabel(new ImageIcon(new ImageIcon(Data.Images.Puzzle.lightNeutral).getImage().getScaledInstance(30, 30, Image.SCALE_FAST)));
    jlbl_light.setBounds(550, 550, 30, 30);

    super.setLayout(null);
    super.setBackground(Data.Colors.puzzleBackground);

    for (JButton j : numpad) {
      j.setFocusable(false);
      super.add(j);
    }
    
    super.add(jtxf_numDisplay);
    super.add(jlbl_light);
    
  }
}









class PipePuzzle extends JPanel {

  private static ConnectionType[] types = new ConnectionType[] {ConnectionType.COUPLING, ConnectionType.ELBOW, ConnectionType.TEE, ConnectionType.CROSS};

  public enum ConnectionType {
    COUPLING,
    ELBOW,
    TEE,
    CROSS
  }

  private class PipeBlock extends JButton {
    private ArrayList<Byte> connectionPoints;
    private int[] firstSecond;
    private ConnectionType type;
    private ImageIcon image;
    private boolean isPowered;
    private byte orientation;
    private PipePuzzle puzzleHost;

    public PipeBlock(byte orientation, ConnectionType type, int[] firstSecond, PipePuzzle puzzleHost) {
      this.puzzleHost = puzzleHost;
      this.firstSecond = firstSecond;
      this.type = type;
      this.orientation = orientation;
      super.setBounds(firstSecond[1] * 500 / PipePuzzle.BLOCK_NUM_WIDTH, firstSecond[0] * 400 / PipePuzzle.BLOCK_NUM_HEIGHT, 500 / PipePuzzle.BLOCK_NUM_WIDTH, 400 / PipePuzzle.BLOCK_NUM_HEIGHT);
      super.setBackground(Color.GRAY);

      this.isPowered = false;
      setProperties();
      
      super.addActionListener(e -> {
        if (!PipeBlock.this.puzzleHost.host.isGood()) {
          PipeBlock.this.orientation = (byte) ((PipeBlock.this.orientation + 1) % 4);
          setProperties();
          PipeBlock.this.puzzleHost.updatePower();
          PipeBlock.this.puzzleHost.checkIsGood();
        }
      });
    }
    
    private void setProperties() {
      
      connectionPoints = new ArrayList<Byte>();
      connectionPoints.add(Byte.valueOf((byte) 3));
      switch (type) {
        case CROSS:
          connectionPoints.add(Byte.valueOf((byte) 2));
        case TEE:
          connectionPoints.add(Byte.valueOf((byte) 0));
        case COUPLING:
          connectionPoints.add(Byte.valueOf((byte) (1)));
          break;
        case ELBOW:
          connectionPoints.add(Byte.valueOf((byte) 0));
      }

      connectionPoints.sort(null);
      for (int i = 0; i < connectionPoints.size(); i++)
        connectionPoints.set(i, Byte.valueOf((byte) ((connectionPoints.get(i).byteValue() + orientation) % 4)));
      this.setIcon(new ImageIcon(Data.Images.Puzzle.pipeBlock(type, isPowered)));
    }

    @Override
    public void setIcon(Icon newImage) {
      this.image = (ImageIcon) newImage;
      for (byte b = 0; b < this.orientation; b++) {
        this.image = Data.Images.rotateIcon(this.image);
      }
      this.image = new ImageIcon(this.image.getImage().getScaledInstance(500/PipePuzzle.BLOCK_NUM_WIDTH, 400/PipePuzzle.BLOCK_NUM_HEIGHT, Image.SCALE_FAST));
      super.setIcon(this.image);
    }

    public PipeBlock getNeighbor(byte direction) throws IndexOutOfBoundsException {
      int secondShift = 1 - direction + 2 * (direction / 3); // 0:1; 1:0; 2:-1; 3:0
      int firstShift = 2 - direction - 2 * ((5 - direction) / 5); // 0:0; 1:1; 2:0; 3:-1
      PipeBlock other = puzzleHost.getBlock(new int[] {firstSecond[0] + firstShift, firstSecond[1] + secondShift});
      if (other == null)
        throw new IndexOutOfBoundsException();
      return other;
    }

    public ArrayList<PipeBlock> getAllConnectedNeighbors() {
      ArrayList<PipeBlock> neighbors = new ArrayList<>();
      for (byte b : connectionPoints) {
        try {
          PipeBlock neighbor = getNeighbor(b);
          if (neighbor.connectionPoints.contains((byte) ((b + 2) % 4)))
            neighbors.add(getNeighbor(b));
        } catch (IndexOutOfBoundsException e) {
          // nothing
        }
      }
      return neighbors;
    }

    public boolean isPowered() {
      return isPowered;
    }

    public void setPower(boolean power) {
      this.isPowered = power;
    }

    public void setType(ConnectionType newType) {
      this.type = newType;
      setProperties();
    }

    public ConnectionType getType() {
      return type;
    }

    public int[] getCoordinates() {
      return firstSecond;
    }

    public ArrayList<Byte> getConnections() {
      return connectionPoints;
    }
  }


  public static final int BLOCK_NUM_HEIGHT = 4;
  public static final int BLOCK_NUM_WIDTH = 5;

  private PipeBlock[][] pipes;  
  private Target host;
  private JLabel jlbl_energyEnd;
  private JLabel jlbl_light;
  private JPanel jpnl_pipes;
  
  
  public PipePuzzle(Target host) {
    this.host = host;
    this.pipes = new PipeBlock[PipePuzzle.BLOCK_NUM_HEIGHT][PipePuzzle.BLOCK_NUM_WIDTH];
    int[] bendOnEachColumn = new int[PipePuzzle.BLOCK_NUM_WIDTH];

    for (int i = 0; i < bendOnEachColumn.length - 1; i++)
      bendOnEachColumn[i] = (int) Math.floor(Math.random() * Integer.MAX_VALUE % PipePuzzle.BLOCK_NUM_HEIGHT);
    bendOnEachColumn[PipePuzzle.BLOCK_NUM_WIDTH - 1] = PipePuzzle.BLOCK_NUM_HEIGHT;

    for (int i = 0; i < PipePuzzle.BLOCK_NUM_HEIGHT * PipePuzzle.BLOCK_NUM_WIDTH; i++) {
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH] = new PipeBlock((byte) Math.floor(Math.random() * Integer.MAX_VALUE % 4), ConnectionType.COUPLING, new int[] {i/PipePuzzle.BLOCK_NUM_WIDTH, i%PipePuzzle.BLOCK_NUM_WIDTH}, this);
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH].setFocusable(false);
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH].repaint();
    }
    
    for (int i = 0; i < PipePuzzle.BLOCK_NUM_WIDTH - 1; i++) {
      pipes[bendOnEachColumn[i]][i + 1].setType(ConnectionType.ELBOW);
      if (i == 0 || bendOnEachColumn[i] != bendOnEachColumn[i - 1])
        pipes[bendOnEachColumn[i]][i].setType(ConnectionType.ELBOW);
      else 
        pipes[bendOnEachColumn[i]][i].setType(ConnectionType.COUPLING);
    }

    int row = 0;
    ArrayList<int[]> importantCoords = new ArrayList<>();
    for (int i = 0; i < PipePuzzle.BLOCK_NUM_WIDTH; i++) {
      for ( ; ; row += Math.signum(bendOnEachColumn[i] - row)) {
        importantCoords.add(new int[] {row, i});
        if (row == bendOnEachColumn[i])
          break;
      }
    }

    for (int i = 0; i < PipePuzzle.BLOCK_NUM_HEIGHT * PipePuzzle.BLOCK_NUM_WIDTH / 2; i++) {
      int first = (int) Math.floor(Math.random() * Integer.MAX_VALUE % PipePuzzle.BLOCK_NUM_HEIGHT);
      int second = (int) Math.floor(Math.random() * Integer.MAX_VALUE % PipePuzzle.BLOCK_NUM_WIDTH);

      boolean contains = false;
      int[] coords = new int[] {first, second};
      for (int j = 0; j < importantCoords.size() && !contains; j++)
        contains = Arrays.equals(coords, importantCoords.get(j));

      if (contains)
        pipes[first][second].setType(ConnectionType.TEE);
      else
        pipes[first][second].setType(ConnectionType.ELBOW);
      pipes[first][second].repaint();
    }

    JLabel jlbl_energyStart = new JLabel(new ImageIcon(new ImageIcon(Data.Images.Puzzle.energyStart).getImage().getScaledInstance(40, 40, Image.SCALE_FAST)));
    jlbl_energyStart.setBounds(50, 60, 40, 40);

    jpnl_pipes = new JPanel();
    jpnl_pipes.setLayout(null);
    jpnl_pipes.setBounds(50, 100, 500, 400);
    for (int i = 0; i < PipePuzzle.BLOCK_NUM_HEIGHT * PipePuzzle.BLOCK_NUM_WIDTH; i++)
      jpnl_pipes.add(pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH]);

    this.updatePower();

    jlbl_energyEnd = new JLabel(new ImageIcon(new ImageIcon(Data.Images.Puzzle.energyEndOff).getImage().getScaledInstance(40, 40, Image.SCALE_FAST)));
    jlbl_energyEnd.setBounds(510, 500, 40, 40);
    
    jlbl_light = new JLabel(new ImageIcon(new ImageIcon(Data.Images.Puzzle.lightNeutral).getImage().getScaledInstance(30, 30, Image.SCALE_FAST)));
    jlbl_light.setBounds(550, 550, 30, 30);

    super.setLayout(null);
    super.setBackground(Data.Colors.puzzleBackground);
    super.add(jlbl_energyStart);
    super.add(jpnl_pipes);
    super.add(jlbl_energyEnd);
    super.add(jlbl_light);
    jpnl_pipes.repaint();
    super.repaint();
  }


  public PipeBlock getBlock(int[] firstSecond) throws IndexOutOfBoundsException {
    return pipes[firstSecond[0]][firstSecond[1]];
  }

  public void updatePower() {
    for (int i = 0; i < PipePuzzle.BLOCK_NUM_HEIGHT * PipePuzzle.BLOCK_NUM_WIDTH; i++)
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH].setPower(false);
    
    ArrayList<PipeBlock> checkedBlocks = new ArrayList<>();
    ArrayList<PipeBlock> temp = new ArrayList<>();
    temp.add(pipes[0][0]);
    int blocksToCheck = 1;
    int firstTime = 1;

    if (temp.get(0).getConnections().contains(Byte.valueOf((byte) 3))) {

      while (blocksToCheck > 0) {
        for (int i = temp.size() - 1; i >= temp.size() - blocksToCheck; i--) {
          temp.get(i).setPower(true);
          ArrayList<PipeBlock> neighbors = temp.get(i).getAllConnectedNeighbors();
          for (PipeBlock pb : neighbors) {
            if (!checkedBlocks.contains(pb))
              checkedBlocks.add(pb);
          }
          // System.out.println(temp.get(i).getAllConnectedNeighbors().size());
          // System.out.println(checkedBlocks.size() + " " + temp.size());
        }
        blocksToCheck = checkedBlocks.size() - temp.size() + firstTime;
        firstTime = 0;
        temp = new ArrayList<>(checkedBlocks);
      }
    }

    for (int i = 0; i < PipePuzzle.BLOCK_NUM_HEIGHT * PipePuzzle.BLOCK_NUM_WIDTH; i++) {
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH].setProperties();
      pipes[i/PipePuzzle.BLOCK_NUM_WIDTH][i%PipePuzzle.BLOCK_NUM_WIDTH].repaint();
    }
    this.jpnl_pipes.repaint();
  }


  public void checkIsGood() {
    if (pipes[PipePuzzle.BLOCK_NUM_HEIGHT - 1][PipePuzzle.BLOCK_NUM_WIDTH - 1].getConnections().contains(Byte.valueOf((byte) 1)) && pipes[PipePuzzle.BLOCK_NUM_HEIGHT - 1][PipePuzzle.BLOCK_NUM_WIDTH - 1].isPowered()) {
      jlbl_energyEnd.setIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.energyEndOff).getImage().getScaledInstance(40, 40, Image.SCALE_FAST)));
      jlbl_light.setIcon(new ImageIcon(new ImageIcon(Data.Images.Puzzle.lightOn).getImage().getScaledInstance(30, 30, Image.SCALE_FAST)));
      host.setGood();
      super.repaint();
    }
  }
}









class WirePuzzle extends JPanel {
  private static final int NUM_OF_JUNCTIONS = 3;
  private static final int NUM_OF_WIRES = 3;
  private JLabel jlbl_light;
  private Target host;
  
  public WirePuzzle(Target host) {
    this.host = host;

    
    
  }
}