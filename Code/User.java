import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.InputMismatchException;

public class User {

  private File userFile;
  private String userName;
  private String realName;
  private String password;
  private int unlockedLevels;
  private ArrayList<Integer> perfectLevels;

  // Constructors
  
  public User(File userFile) throws FileNotFoundException { // Reads data for an existing user
    
    try { // Read data from file
      Scanner fileIn = new Scanner(userFile);

      /*
      userName
      password
      realName
      unlockedLevels
      perfectLevels
      ---------------------
      coderColby
      P@55W0RD
      Colby
      23
      1 2 4 8 14 20 22
      */
      
      this.userFile = userFile;
      this.userName = fileIn.nextLine().trim();
      this.password = fileIn.nextLine().trim();
      this.realName = fileIn.nextLine().trim();
      this.unlockedLevels = Integer.parseInt(fileIn.nextLine().trim());
      this.perfectLevels = new ArrayList<Integer>(Data.MAX_LEVELS);

      while (fileIn.hasNext())
        this.perfectLevels.add(Integer.parseInt(fileIn.next()));
      fileIn.close();
      
    } catch (InputMismatchException e) {
      System.out.println("Error: userFile is incorrectly formatted");
      return;
    }
  }

  public User(String userName, String password, String realName, String path) { // New user
    this.userFile = new File(path);
    this.userName = userName;
    this.password = password;
    this.realName = realName;
    this.unlockedLevels = 1;
    this.perfectLevels = new ArrayList<Integer>();
    updateFile();
  }

  // Methods

  public void updateFile() {
    PrintWriter fileOut;
    try {
      fileOut = new PrintWriter(this.userFile);
    } catch(FileNotFoundException e) {
      System.out.println("Error: userFile no longer exists");
      return;
    }
    fileOut.println(this.userName);
    fileOut.println(this.password);
    fileOut.println(this.realName);
    fileOut.println(this.unlockedLevels);

    for (int level : this.perfectLevels)
      fileOut.print(level + " ");
    
    fileOut.close();
  }

  // Setters
  
  public void incrementLevels() {
    this.unlockedLevels++;
    updateFile();
  }

  public void setUserName(String newName) {
    this.userName = newName;
    updateFile();
  }

  public void setPassword(String newPassword) {
    this.password = newPassword;
    updateFile();
  }

  public void setRealName(String newName) {
    this.realName = newName;
    updateFile();
  }

  public void addPerfectLevel(int newLevel) {
    this.perfectLevels.add(newLevel);
    Collections.sort(this.perfectLevels);
    updateFile();
  }

  // Getters

  public int getLevels() {
    return this.unlockedLevels;
  }

  public String getUserName() {
    return this.userName;
  }

  public boolean passwordIsValid(String passwordInQuestion) {
    return this.password.equals(passwordInQuestion);
  }

  public String passwordDots() {
    String dots = "";
    for (int i = 0; i < password.length(); i++)
      dots += "•";
    return dots;
  }

  public String getRealName() {
    return this.realName;
  }

  public ArrayList<Integer> getPerfectLevels() {
    return this.perfectLevels;
  }
  
}