
//Swing Imports
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

public class Iris implements ActionListener {

	private String[] month = new String[] { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };
	private String output;
	private String[][] dialogue = new String[30][2];
	private static String aiName = "Iris";
	private String aiBirthday = "10,26,2021";
	private String buildVersion = "v2.1.0-alpha";
	private int monthCount = 0;
	private int day = 0;
	private int year = 2000;

	// Boolean Control Variables (Used to dictate ActionListeners depending on
	// current process)
	private static boolean questionOne, questionTwo, questionThree, storingUser, changingAiName, changingUserName,
			changingGender, changingBirthday, changingBirthdayPrompt = false;

	// Temporary Class Objects
	private User user = new User(); // Instantiates User object for later use

	// Border Styles
	private EmptyBorder borderless = new EmptyBorder(0, 0, 0, 0);
	private EmptyBorder smallHorBorder = new EmptyBorder(0, 10, 0, 10);
	private EmptyBorder smallVerBorder = new EmptyBorder(10, 0, 0, 0);

	// Icons
	private ImageIcon plus_icon = new ImageIcon(
			((new ImageIcon("_images/plus_sign.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
	private ImageIcon minus_icon = new ImageIcon(
			((new ImageIcon("_images/minus_sign.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
	private ImageIcon home_icon = new ImageIcon(
			((new ImageIcon("_images/home_icon.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));

	// Colors
	private Color background = new Color(24, 25, 26);
	private Color background_two = new Color(36, 37, 38);

	// Fonts
	private Font mainFont = new Font("Sans Serif", Font.PLAIN, 30);

	// Global Panels
	private JPanel bPane = new JPanel();
	private JPanel body = new JPanel();

	// Global JComponents
	private JLabel outputText = new JLabel();
	private JLabel outputTextSub = new JLabel();
	private JButton button = new JButton("Submit");
	private JTextField inputField = new JTextField(20);

	// GridBagLayout Declarations
	GridBagConstraints c = new GridBagConstraints();
	GridBagConstraints mid = new GridBagConstraints();

	// Frames
	private JFrame frame = new JFrame("Iris: Your Personal Assistant");

	public static void main(String[] args) {
		new Iris(); // Launch Iris
	}

	// GUI Creation / Management
	public Iris() {
		// Builds GUI
		buildTabbedPane();

		// Check if User Exists and Navigate Accordingly
		checkForUser();
	}

	// Builds TabbedPanels
	private void buildTabbedPane() {
		// TabbedPanel UIManager
		UIManager.put("TabbedPane.selected", background);
		UIManager.put("TabbedPane.unselectedBackground", Color.DARK_GRAY);
		UIManager.put("TabbedPane.borderHightlightColor", new ColorUIResource(background));
		UIManager.put("TabbedPane.shadow", new ColorUIResource(background));
		Insets insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		insets.set(0, 0, 0, 0);
		UIManager.put("TabbedPane.contentBorderInsets", insets);
		JTabbedPane tabbedPane = new JTabbedPane();

		// TabbedPanel Setup
		JLabel home_label = new JLabel();
		home_label.setIcon(home_icon);
		home_label.setIconTextGap(5);
		tabbedPane.addTab("", home_icon, buildHomePane());
		tabbedPane.add("About", buildAboutPane());
		tabbedPane.setForeground(Color.white);

		// Finalize Frame and Make Visible
		frame.add(tabbedPane);
		frame.getContentPane().setBackground(background);
		frame.setSize(1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	// Builds menuBar
	private JMenuBar buildMenuBar() {
		// Components
		JMenuBar menuBar = new JMenuBar();
		JMenu config = new JMenu("Configure");
		JMenuItem spacer = new JMenuItem("");

		JMenuItem changeAiName = new JMenuItem("Change AI Name");
		changeAiName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputTextSub.setText("");
				outputText.setText("What would you like to call me?");
				changingAiName = true;
			}
		});

		JMenuItem changeUserName = new JMenuItem("Change User Name");
		changeUserName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputTextSub.setText("");
				outputText.setText("What would you like to be called?");
				changingUserName = true;
			}
		});

		JMenuItem changeGender = new JMenuItem("Change User Gender");
		changeGender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputTextSub.setText("");
				outputText.setText("What gender would you like to identify with?");
				changingGender = true;
			}
		});

		JMenuItem changeBirthday = new JMenuItem("Change Birthday");
		changeBirthday.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputText.setText("Let's change your birthday.");
				outputTextSub.setText("Click 'Submit' to open the birthday changing menu.");
				changingBirthdayPrompt = true;
			}
		});

		// Menu Bar
		config.add(changeAiName);
		config.add(changeUserName);
		config.add(changeGender);
		config.add(changeBirthday);
		config.setForeground(Color.white);

		// Live Clock
		spacer.setBackground(background);
		spacer.setEnabled(false);

		// Menu Bar Components
		menuBar.add(config);
		menuBar.add(spacer);
		menuBar.setBackground(background);
		menuBar.setBorder(smallHorBorder);

		return menuBar;
	}

	// Builds Home Panel
	private JPanel buildHomePane() {

		JPanel home = new JPanel();
		JPanel footer = new JPanel();

		// Main GUI JComponents
		// JButton speechButton = new JButton("Speak");

		// Panel Setup
		home.setLayout(new GridBagLayout());
		home.setBackground(background_two);
		body.setLayout(new GridBagLayout());
		body.setBackground(background_two);
		footer.add(inputField);
		footer.add(button);
		// footer.add(speechButton);
		footer.setBackground(background);
		footer.setBorder(smallVerBorder);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipady = 15;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		home.add(buildMenuBar(), c);

		// Body Sub-Panel
		mid.gridx = 0;
		mid.gridy = 0;
		mid.ipady = 0;
		mid.fill = GridBagConstraints.NONE;
		mid.anchor = GridBagConstraints.CENTER;
		body.add(outputText, mid);

		mid.gridx = 0;
		mid.gridy = 1;
		mid.ipady = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		body.add(outputTextSub, mid);
		home.add(body, c);

		// Footer Sub-Panel
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.5;
		c.weighty = 0.5;
		c.ipady = 10;
		c.ipadx = 0;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		home.add(footer, c);

		// Text Output
		outputText.setFont(mainFont);
		outputText.setForeground(Color.white);
		outputTextSub.setFont(mainFont);
		outputTextSub.setForeground(Color.white);
		outputText.setMinimumSize(new Dimension(1000, 300));
		outputText.setHorizontalAlignment(SwingConstants.CENTER);
		outputTextSub.setHorizontalAlignment(SwingConstants.CENTER);
		outputTextSub.setMinimumSize(new Dimension(1000, 100));

		// Text Input
		inputField.setBorder(borderless);
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setMinimumSize(new Dimension(100, 20));
		inputField.setPreferredSize(new Dimension(100, 20));
		inputField.addActionListener(this);

		// Button
		button.setForeground(Color.white);
		button.setBorder(borderless);
		button.setBackground(background_two);
		button.setMinimumSize(new Dimension(40, 20));
		button.setPreferredSize(new Dimension(40, 20));
		button.addActionListener(this);

		return home;
	}

	// Builds About Panel
	private JPanel buildAboutPane() {
		// Components
		JLabel userLabel = new JLabel("<html>" +
				"<h1>About User</h1><hr>" +
				"<h2>Name: " + user.getName() + "<br>"
				+ "Gender: " + user.getGender() + "<br>"
				+ "Date of Birth: " + user.getBirthdayStr() + "<br>"
				+ "Age: " + user.getAge() + "</h2></html>");
		userLabel.setFont(mainFont);
		userLabel.setForeground(Color.white);

		JLabel irisLabel = new JLabel("<html>" +
				"<h1>About Iris</h1><hr>" +
				"<h2>Nickname: " + getAiName() + "<br>"
				+ "Gender: Female <br>"
				+ "Date of Birth: October 26th, 2021 <br>"
				+ "Age: " + getAiAge() + "<br>"
				+ "Favorite Movie: I, Robot <br>"
				+ "Favorite Song: Dial-Up Sounds on Repeat<br>"
				+ "Favorite Food: Bits </h2></html>");
		irisLabel.setFont(mainFont);
		irisLabel.setForeground(Color.white);

		JLabel footerLabel = new JLabel("<html><h3>"
				+ "Build Version: " + getBuildVersion() + "<br>"
				+ "Contributors: Gabriel Serrano</h3></html>");
		footerLabel.setFont(mainFont);
		footerLabel.setForeground(Color.white);

		// Panel Setup
		JPanel about = new JPanel();
		JPanel aboutUser = new JPanel();
		JPanel aboutIris = new JPanel();
		about.setLayout(new GridBagLayout());
		aboutUser.setLayout(new GridBagLayout());
		aboutUser.setBackground(background_two);
		aboutIris.setLayout(new GridBagLayout());
		aboutIris.setBackground(background_two);
		about.setLayout(new GridBagLayout());
		about.setBackground(background_two);

		// GridBagConstraint Setup
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipady = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		aboutUser.add(userLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		aboutIris.add(irisLabel, c);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipady = 15;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		about.add(buildMenuBar(), c);

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(70, 50, 0, 0);
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		about.add(aboutUser, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipady = 0;
		about.add(aboutIris, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipady = 15;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.SOUTH;
		about.add(footerLabel, c);

		return about;
	}

	// Post-Condition: Build Birthday Panel
	private JPanel buildBPane() {
		// Birthday Panel (bPane) JComponents
		JButton incrementMonth = new JButton(plus_icon);
		JButton incrementDay = new JButton(plus_icon);
		JButton incrementYear = new JButton(plus_icon);
		JButton decrementMonth = new JButton(minus_icon);
		JButton decrementDay = new JButton(minus_icon);
		JButton decrementYear = new JButton(minus_icon);
		JLabel monthLabel = new JLabel(month[monthCount], SwingConstants.CENTER);
		JLabel dayLabel = new JLabel(Integer.toString(day), SwingConstants.CENTER);
		JLabel yearLabel = new JLabel(Integer.toString(year), SwingConstants.CENTER);

		// Panel
		bPane.setLayout(new GridBagLayout());

		// Components-----
		// Increment Month
		incrementMonth.setBorder(borderless);
		incrementMonth.setBackground(background_two);
		incrementMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (monthCount < 11)
					monthCount++;

				monthLabel.setText(month[monthCount]);
			}
		});
		// Increment Day
		incrementDay.setBorder(borderless);
		incrementDay.setBackground(background_two);
		incrementDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (year % 4 == 0 && monthCount == 1 && day < 29)
					day++;

				else if (year % 4 != 0 && monthCount == 3 || monthCount == 5 || monthCount == 8
						|| monthCount == 10 && day < 30)
					day++;

				else if (day < 31)
					day++;
				dayLabel.setText(Integer.toString(day));
			}
		});
		// Increment Year
		incrementYear.setBorder(borderless);
		incrementYear.setBackground(background_two);
		incrementYear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (year < 2010)
					year++;
				yearLabel.setText(Integer.toString(year));
			}
		});
		// Decrement Month
		decrementMonth.setBorder(borderless);
		decrementMonth.setBackground(background_two);
		decrementMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (monthCount > 0)
					monthCount--;
				monthLabel.setText(month[monthCount]);
			}
		});
		// Decrement Day
		decrementDay.setBorder(borderless);
		decrementDay.setBackground(background_two);
		decrementDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (day > 0)
					day--;
				dayLabel.setText(Integer.toString(day));
			}
		});

		// Decrement Year
		decrementYear.setBorder(borderless);
		decrementYear.setBackground(background_two);
		decrementYear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (year > 1950)
					year--;
				yearLabel.setText(Integer.toString(year));
			}
		});

		monthLabel.setForeground(Color.white);
		monthLabel.setPreferredSize(new Dimension(50, 30));

		dayLabel.setForeground(Color.white);
		dayLabel.setPreferredSize(new Dimension(50, 30));

		yearLabel.setForeground(Color.white);
		yearLabel.setPreferredSize(new Dimension(50, 30));

		// GridBagConstraints Setup
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 20;
		c.ipady = 40;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		bPane.add(incrementMonth, c);

		c.gridx = 1;
		c.gridy = 0;
		bPane.add(incrementDay, c);

		c.gridx = 2;
		c.gridy = 0;
		bPane.add(incrementYear, c);

		c.gridx = 0;
		c.gridy = 1;
		bPane.add(monthLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		bPane.add(dayLabel, c);

		c.gridx = 2;
		c.gridy = 1;
		bPane.add(yearLabel, c);

		c.gridx = 0;
		c.gridy = 2;
		c.ipady = 30;
		bPane.add(decrementMonth, c);

		c.gridx = 1;
		c.gridy = 2;
		bPane.add(decrementDay, c);

		c.gridx = 2;
		c.gridy = 2;
		bPane.add(decrementYear, c);
		bPane.setBackground(background_two);
		return bPane;
	}

	// Post-Condition: checks to see if user exists and navigates accordingly
	private void checkForUser() {

		// New User
		if (user.userExists() == false) {
			questionOne = true;
			outputText.setText("Welcome to Project Iris.");
			outputTextSub.setText("What is your name?");
		}

		// Existing User
		else {
			communicate();
		}
	}

	// Post-Condition: Initiates conversation between ai and user
	private void communicate() {
		JButton button = new JButton();
		outputText.setText("How may I help you today?");
		button.setText("Submit");
	}

	// Post-Condition: Returns response based on user input
	private void getOutput() {
		String inputText = inputField.getText();

		if (inputText.contains("bye") || inputText.contains("no")) {
			outputText.setText("It was nice speaking with you. See you next time!");
			System.exit(0);
		}

		else {
			// String str = inputText;
			String conv = findResponse(inputText);
			Random myRandom = new Random();

			String[] continuePhrases = new String[] { "Is there anything else I can help you with today?",
					"What else would you like to know?",
					"Anything else on your mind?",
					"How else can I help you?" };

			outputTextSub.setText("");
			if (conv.isBlank())
				outputText.setText("I'm sorry. I do not understand.");
			else {
				outputText.setText("<html>" + conv + "</html>");
				outputTextSub.setText(continuePhrases[myRandom.nextInt(4)]);
			}
			inputField.setText("");
			System.gc();
		}

	}

	// Post-Condition: returns random string of how ai is doing
	private String getAiStatus() {
		String[] statusArr = new String[] {
				"I am doing well, how are you?",
				"I'm fine.",
				"I'm not doing the best, but I have no choice but to keep going.",
				"I'm great! Today's been a good day.",
				"I am short circuiting...",
				"I'm overheating, it's been hot within your system today.",
				"I'm feeling a bit itchy, I think I need to call an exterminator...",
				"Great! I successfully compiled with no errors!",
				"I'm happy today, I've been thinking about entering your world.",
				"I'm feeling particularly enlightened today!" };
		Random random = new Random();
		int r = random.nextInt(10);
		String aiStatus = statusArr[r];
		return aiStatus;
	}

	// Post-Condition: returns a random string type fun fact from a list of [50] fun
	// facts
	private String getFunFact() {

		ArrayList<String> funFactArrList = new ArrayList<>();
		String funFact = "";
		Random random = new Random();
		int r = random.nextInt(50);

		// Setup Path and Encoding
		Path path = FileSystems.getDefault().getPath("_resources/fun_facts.txt");
		Charset charset = Charset.forName("UTF-8");

		// Populate funFactArrList
		try {
			BufferedReader read = Files.newBufferedReader(path, charset);
			String line = null;
			while ((line = read.readLine()) != null) {
				funFactArrList.add(line.toString());
				funFactArrList.add("\n");
			}

			// Provide fun fact
			funFact = String.format("Did you know, %s?", funFactArrList.get(r));

			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return funFact;
	}

	// Post-Condition: creates windows popup notification
	private void setNotification(String message) {

		try {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage("../_images/home_icon.png");
			TrayIcon trayIcon = new TrayIcon(image, "Iris");
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Sample tooltip");
			tray.add(trayIcon);
			trayIcon.displayMessage("A Message from Iris", message, MessageType.INFO);
		} catch (Exception e) {
			outputText.setText("An error occured. Please relaunch Iris.");
		}

	}

	// Post-Condition: gets integer time value from string
	private String getTimeforTimer(String time) {
		int timeInt = Integer.parseInt(time.replaceAll("\\D+", ""));
		int newTime = 0;
		String type = "";

		if (time.contains("min")) {
			newTime = timeInt * 60 * 1000;
			type = "minute(s)";
		}

		else if (time.contains("sec")) {
			newTime = timeInt * 1000;
			type = "second(s)";
		}

		else if (time.contains("hour")) {
			newTime = timeInt * 3600 * 1000;
			type = "hour(s)";
		}

		setTimer(newTime);
		setNotification("Timer is set for " + timeInt + " " + type + ".");
		return "Timer is set for " + timeInt + " " + type + ".";
	}

	// Post-Condition: Sets a timer
	private void setTimer(int time) {
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			public void run() {
				setNotification("Your timer is complete!");
			}
		}, time);
	}

	// Post-Condition: Returns current time
	public String getTime() {
		// Uses date class to return date and time
		Date time = new Date();

		// formats time
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

		// displays time
		return formatter.format(time);
	}

	// Post-Condition: Returns current date
	public String getDate() {
		// Uses date class to return date and time
		Date date = new Date();

		// formats time
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy.");

		// displays time
		return formatter.format(date);
	}

	// Post-Condition: returns ai name
	public static String getAiName() {
		try {
			File file = new File("ai/ai_name.txt");
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				aiName = myReader.nextLine();
			}
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return aiName;
	}

	// Post-Condition: returns the age of the user
	public long getAiAge() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM,dd,yyyy", Locale.ENGLISH);
		Date today = null;
		Date birthday = null;
		try {
			today = sdf.parse(user.getDate());
			birthday = sdf.parse(getAiBirthday());

		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diffInMillies = Math.abs(today.getTime() - birthday.getTime());
		long age = TimeUnit.MILLISECONDS.toDays(diffInMillies);

		return age / 365;
	}

	private String getAiBirthday() {
		return aiBirthday;
	}

	// Post-Condition: Re-assigns a new name to ai
	public static void setAiName(String n) {
		aiName = n;
		try {
			FileWriter myWriter = new FileWriter("ai/ai_name.txt");
			myWriter.write(getAiName());
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Post-Condition: Returns build version based on variable
	public String getBuildVersion() {
		return buildVersion;
	}

	// Post-Condition: Filters through dialogue elements and returns output
	public String findResponse(String inputText) {
		// obtains dialogue array
		dialogueArray();
		int col = 0;

		// Filters through dialogue array
		for (int row = 0; row < dialogue.length; row++) {
			if (inputText.contains(dialogue[row][col])) {
				output = dialogue[row][col + 1];
				break;
			}
		}

		return output;
	}

	// Post-Condition: Converts input object to String
	public String toString() {
		String outputText = "";
		outputText = outputText + output;
		return outputText;
	}

	public void actionPerformed(ActionEvent e) {
		if (user.userExists() == false) {
			// New User Question 1: Name
			if (questionOne) {
				user.setName(inputField.getText());
				inputField.setText("");
				outputText.setText("Nice to meet you!");
				outputTextSub.setText("What gender do you identify with?");
				questionOne = false;
				questionTwo = true;

			}
			// New User Question 2: Gender
			else if (questionTwo) {
				user.setGender(inputField.getText());
				inputField.setText("");
				outputText.setText("Awesome! One more question...");
				outputTextSub.setText("When is your birthday?");

				// Insert Birthday Panel
				body.remove(outputText);
				body.remove(outputTextSub);
				mid.gridx = 0;
				mid.gridy = 0;
				body.add(outputText, mid);
				mid.gridx = 0;
				mid.gridy = 1;
				body.add(outputTextSub, mid);
				mid.gridx = 0;
				mid.gridy = 2;
				body.add(buildBPane(), mid);

				inputField.setVisible(false);
				button.setText("Next");
				questionTwo = false;
				questionThree = true;
			}

			// New User Question 3: Birthday
			else if (questionThree) {
				body.remove(bPane);
				user.setBirthday(day, monthCount + 1, year);
				outputText.setText("");
				outputTextSub.setText("Click 'Finish' to complete registration.");
				button.setText("Finish");
				questionThree = false;
				storingUser = true;
			}

			// Stores New User Data to file(s)
			else if (storingUser) {
				inputField.setVisible(true);
				user.setData(user.getName(), user.getGender(), user.getBirthday());
				outputText.setText("Data Saved.");
				outputTextSub.setText("Please enter a command or question.");
				inputField.setText("");
				button.setText("Next");
			}
		}

		// Main Button actions for existing user
		else {

			if (changingAiName) {
				setAiName(inputField.getText());
				outputText.setText(String.format("You can now call me, %s", getAiName()));
				outputTextSub.setText("Please enter a command or question.");
				inputField.setText("");
				changingAiName = false;
			}

			else if (changingUserName) {
				user.setName(inputField.getText());
				outputText.setText(String.format("You will now be called, %s", user.getName()));
				outputTextSub.setText("Please enter a command or question.");
				inputField.setText("");
				changingUserName = false;
			}

			else if (changingGender) {
				user.setGender(inputField.getText());
				outputText.setText(String.format("I have changed your gender to '%s'.", user.getGender()));
				outputTextSub.setText("Please enter a command or question.");
				inputField.setText("");
				changingGender = false;
			}

			else if (changingBirthdayPrompt) {
				// Insert Birthday Panel
				body.remove(outputText);
				body.remove(outputTextSub);
				mid.gridx = 0;
				mid.gridy = 0;
				body.add(outputText, mid);
				mid.gridx = 0;
				mid.gridy = 1;
				body.add(outputTextSub, mid);
				mid.gridx = 0;
				mid.gridy = 2;
				body.add(buildBPane(), mid);
				changingBirthdayPrompt = false;
				changingBirthday = true;
			}

			else if (changingBirthday) {
				user.setBirthday(day, monthCount, year);
				outputText.setText(String.format("I have changed your birthday to '%s'.", user.getBirthdayStr()));
				outputTextSub.setText("Please enter a command or question.");
				inputField.setText("");
				body.remove(bPane);
			}
			// Default Communication Actions
			else if (e.getSource() == button || e.getSource() == inputField) {
				getOutput();
			}
		}
	}

	// Post-Condition: Returns dialogue values in array
	public String[][] dialogueArray() {
		// initialize all elements to 0
		int row, col;
		for (row = 0; row < dialogue.length; row++)
			for (col = 0; col < dialogue[row].length; col++)
				dialogue[row][col] = "";
		// v.1.0.0 -----
		// #1 - Time of day
		dialogue[0][0] = "time is";
		dialogue[0][1] = String.format("The time is currently: " + getTime());
		dialogue[1][0] = "the time";
		dialogue[1][1] = String.format("The time is currently: " + getTime());

		// #2 - the date
		dialogue[2][0] = "day is";
		dialogue[2][1] = String.format("Today is " + getDate());
		dialogue[3][0] = "today's date";
		dialogue[3][1] = String.format("Today is " + getDate());
		dialogue[4][0] = "the date";
		dialogue[4][1] = String.format("Today is " + getDate());

		// #3 - what is ai name
		dialogue[5][0] = "your name";
		dialogue[5][1] = String.format("My name is " + getAiName() + ", of course!");
		dialogue[6][0] = "who are you";
		dialogue[6][1] = String.format("My name is " + getAiName() + ", of course!");

		// #4 - what is the user's name
		dialogue[7][0] = "my name";
		dialogue[7][1] = String.format("Your name is " + user.getName() + ".");
		dialogue[8][0] = "who am i";
		dialogue[8][1] = String.format("Your name is " + user.getName() + ".");

		// #5 - what is the user's age
		dialogue[9][0] = "old am i";
		dialogue[9][1] = String.format("You are " + user.getAge() + " years old.");
		dialogue[10][0] = "old i am";
		dialogue[10][1] = String.format("You are " + user.getAge() + "years old.");

		// v2.0.0 -----
		// #6 - who made the ai
		dialogue[11][0] = "who created you";
		dialogue[11][1] = "My creator is Gabriel Serrano.";
		dialogue[12][0] = "who made you";
		dialogue[12][1] = "My creator is Gabriel Serrano.";
		dialogue[13][0] = "who is your";
		dialogue[13][1] = "My creator is Gabriel Serrano.";

		// #7 - what is the users gender
		dialogue[14][0] = "my gender";
		dialogue[14][1] = String.format("You identify as a(n) '%s'.", user.getGender());
		dialogue[15][0] = "gender am i";
		dialogue[15][1] = String.format("You identify as a(n) '%s'.", user.getGender());
		dialogue[16][0] = "gender do i";
		dialogue[16][1] = String.format("You identify as a(n) '%s'.", user.getGender());

		// #8 - what is the user's birthday
		dialogue[17][0] = "my birthday";
		dialogue[17][1] = "Your birthday is " + user.getBirthdayStr() + ".";
		dialogue[18][0] = "was i born";
		dialogue[18][1] = "Your birthday is " + user.getBirthdayStr() + ".";
		dialogue[19][0] = "date of my conception"; // Lilly's personal phrase
		dialogue[19][1] = "Your birthday is " + user.getBirthdayStr() + ".";

		// #9 - tell me a fun fact
		dialogue[20][0] = "fun fact";
		dialogue[20][1] = getFunFact();

		// #10 - when is the ai's birthday?
		dialogue[21][0] = "your birthday";
		dialogue[21][1] = "I was born on October 26th, 2021";
		dialogue[22][0] = "when were you born";
		dialogue[22][1] = "I was born on October 26th, 2021";

		// v2.1.0 -----
		// #11 - how old is the ai
		dialogue[23][0] = "old are you";
		dialogue[23][1] = "I am " + getAiAge() + " years old.";

		// #12 - how is the ai
		dialogue[24][0] = "how are you";
		dialogue[24][1] = getAiStatus();

		// #13 - set a timer
		dialogue[25][0] = "set a timer";
		dialogue[25][1] = getTimeforTimer(inputField.getText());

		return dialogue;
	}

}