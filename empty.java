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
      replace(createLevelDesigner());
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
    JButton jbtn_settings = new JButton("Settings");
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
