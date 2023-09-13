import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.InputMismatchException;

public class User {

  private File userFile;
  private String username;
  private String realName;
  private String password;
  private int unlockedLevels;
  private ArrayList<Integer> perfectLevels;

  // Constructors
  
  public User(File userFile) throws FileNotFoundException { // Reads data for an existing user
    
    try { // Read data from file
      Scanner fileIn = new Scanner(userFile);

      /*
      password
      realName
      unlockedLevels
      perfectLevels
      ---------------------
      P@55W0RD
      Colby
      23
      1 2 4 8 14 20 22
      */
      
      this.userFile = userFile;
      this.username = userFile.getName();
      this.username = this.username.substring(0, this.username.length() - ".txt".length());
      this.password = fileIn.nextLine().trim();
      this.realName = fileIn.nextLine().trim();
      this.unlockedLevels = fileIn.nextInt(); fileIn.nextLine();
      this.perfectLevels = new ArrayList<Integer>(Data.Utilities.numOfLevels);
      while (fileIn.hasNext())
        this.perfectLevels.add(Integer.parseInt(fileIn.next()));
      fileIn.close();
      
    } catch (InputMismatchException e) {
      System.out.println("Error: userFile is incorrectly formatted");
      return;
    }
  }

  public User(String username, String password, String realName) { // New user
    this.userFile = new File(Data.Utilities.getUserFilePath(username));
    (new File(Data.Utilities.customLevelDirectory + username)).mkdir();
    this.username = username;
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
    } catch (FileNotFoundException e) {
      System.out.println("Error: userFile does not exist");
      try {
        this.userFile.createNewFile();
        fileOut = new PrintWriter(this.userFile);
      } catch (Exception f) {
        // System.out.println("Error: userFile could not be created");
        System.out.println(f);
      }
      return;
    }
    fileOut.println(this.password);
    fileOut.println(this.realName);
    fileOut.println(this.unlockedLevels);

    for (int level : this.perfectLevels)
      fileOut.print(" " + level);
    
    fileOut.close();
  }

  // Setters
  
  public void incrementLevels() {
    this.unlockedLevels++;
    updateFile();
  }

  public void setUsername(String username) {
    this.userFile.delete();
    this.userFile = new File(Data.Utilities.getUserFilePath(username));
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
    updateFile();
  }

  public void setRealName(String realName) {
    this.realName = realName;
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

  public String getUsername() {
    return this.username;
  }

  public String passwordDots() { // Not Necessary?
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

  // Utilities

  public static String isValidUsername(String username) {
    if (username.isEmpty())
      return "Please provide a username\n";
    else if (username.contains(" "))
      return "Please ensure username does not contain spaces\n";
    return "";
  }

  public static String isValidPassword(String password) {
    if (password.isEmpty())
      return "Please provide a password\n";
    else if (password.contains(" "))
      return "Please ensure password does not contain spaces\n";
    return "";
  }

  public static boolean isUserExist(String username) {
    return (new File(Data.Utilities.getUserFilePath(username))).exists();
  }

  public static boolean isCorrectPassword(String username, String password) {
    File userFile = new File(Data.Utilities.getUserFilePath(username));
    Scanner fileIn;
    try {
      fileIn = new Scanner(userFile);
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      return false;
    }
    String filePassword = fileIn.nextLine().trim();
    fileIn.close();
    return filePassword.equals(password);
  }
  
}