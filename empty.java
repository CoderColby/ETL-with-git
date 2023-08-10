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
        replace(createMenu());
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