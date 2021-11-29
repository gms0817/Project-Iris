import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//Post-Condition: Configures GUI
	public class Iris implements ActionListener{

		//Date Management Variables
		private String[] month = new String[] {"January","February","March","April","May","June","July","August","September","October","November","December"};
		private int monthCount = 0;
		private int day = 0;
		private int year = 2000;
		
		//Boolean Control Variables (Used to dictate ActionListeners depending on current process)
		private static boolean questionOne,questionTwo,questionThree,storingUser,communicateUser,changingAiName,changingUserName,changingGender,changingBirthday = false;
		
		//Temporary Class Objects
		private User user = new User(); //Instantiates User object for later use
		private Central nav = new Central();
		
		//Frames
		private JFrame frame = new JFrame("Iris: Your Personal Assistant"); 

	
		//Panels-----
		//Panel One - Home/Main Panel
		private JPanel paneOne = new JPanel();
		private JPanel footer = new JPanel();
		private JPanel body = new JPanel();
		private JPanel bPane = new JPanel();	
			
		//Border Styles
		private EmptyBorder borderless = new EmptyBorder(0, 0, 0, 0);
		private EmptyBorder smallHorBorder = new EmptyBorder(0, 10, 0, 10);
		private EmptyBorder smallVerBorder = new EmptyBorder(10, 0, 0, 0);
		
		//Icons
		private ImageIcon plus_icon = new ImageIcon(((new ImageIcon("_images/plus_sign.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
		private ImageIcon minus_icon = new ImageIcon(((new ImageIcon("_images/minus_sign.png")).getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
		private Color background = new Color(24,25,26);
		
		//Colors
		private Color background_two = new Color(36,37,38);
		
		//Fonts
		private Font mainFont = new Font("Sans Serif",Font.PLAIN,30);

		//GridBagLayout Declarations
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraints mid = new GridBagConstraints();
		
		//Main GUI JComponents
		private JTextField inputField = new JTextField(20);
		private JButton button = new JButton("Send");
		static JLabel outputText = new JLabel();
		private JLabel outputTextSub= new JLabel();
		
		//Menu Bar JComponents
		private JMenuBar menuBar = new JMenuBar();
		private JMenu config = new JMenu("Configure");
		/* Not for v2.0.0-----------------------------------
		 * private JMenu about = new JMenu("About");
		 * private JMenu home = new JMenu("Home");
		 * private JMenu settings = new JMenu("Settings");
		 * -------------------------------------------------
		*/
		private JMenuItem changeAiName = new JMenuItem("Change AI Name");
		private JMenuItem changeUserName = new JMenuItem("Change User Name");
		private JMenuItem changeGender = new JMenuItem("Change User Gender");
		private JMenuItem changeBirthday = new JMenuItem("Change Birthday");
		private JMenuItem spacer = new JMenuItem("");
		private JLabel liveClock = new JLabel();
		
		//Birthday Panel (bPane) JComponents
		JButton incrementMonth = new JButton(plus_icon);
		JButton incrementDay = new JButton(plus_icon);
		JButton incrementYear = new JButton(plus_icon);
		JButton decrementMonth = new JButton(minus_icon);
		JButton decrementDay = new JButton(minus_icon);
		JButton decrementYear = new JButton(minus_icon);
		JLabel monthLabel = new JLabel(month[monthCount],SwingConstants.CENTER);
		JLabel dayLabel = new JLabel(Integer.toString(day),SwingConstants.CENTER);
		JLabel yearLabel = new JLabel(Integer.toString(year),SwingConstants.CENTER);

		public static void main(String[] args) {
			new Iris(); //Launch Iris
		}
		
		//GUI Creation / Management
		public Iris() {
			//Check if User Exists and Navigate Accordingly
			checkForUser();
			
			//Builds GUI
			guiBuilder();
			
			//Starts Clock Thread
			runClockThread();

		}

		//Builds GUI 
		private void guiBuilder() {
			
			//Frame Properties
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//Menu Bar
			config.add(changeAiName);
			changeAiName.addActionListener(this);
			config.add(changeUserName);
			changeUserName.addActionListener(this);
			config.add(changeGender);
			changeGender.addActionListener(this);
			config.add(changeBirthday);
			changeBirthday.addActionListener(this);
			config.setForeground(Color.white);
			
			//Live Clock
			liveClock.setForeground(Color.white);
			spacer.setBackground(background);
			spacer.setEnabled(false);
			
			//Menu Bar Components
			menuBar.add(config);
			menuBar.add(spacer);
			menuBar.add(liveClock);
			menuBar.setBackground(background);
			menuBar.setBorder(smallHorBorder);
			/* Not for v2.0.0
			 * settings.setForeground(Color.white);
			 * about.setBackground(background);
			 * about.setForeground(Color.white);
			 * home.setForeground(Color.white);
			 
			home.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					body.setVisible(true);
				}
			});
			*/
			/*Not for v2.0.0
			 * menuBar.add(home);
			 * menuBar.add(about);
			 * menuBar.add(settings);
			 */
		
			//Text Output
			outputText.setFont(mainFont);
			outputText.setForeground(Color.white);
			outputTextSub.setFont(mainFont);
			outputTextSub.setForeground(Color.white);
			outputText.setMinimumSize(new Dimension(1000,300));
			outputText.setPreferredSize(new Dimension(1000,300));
			outputText.setHorizontalAlignment(SwingConstants.CENTER);
			outputTextSub.setHorizontalAlignment(SwingConstants.CENTER);
			outputTextSub.setMinimumSize(new Dimension(1000,100));
			outputTextSub.setPreferredSize(new Dimension(1000,100));
			
			//Text Input
			inputField.addActionListener(this);
			inputField.setBorder(borderless);
			inputField.setHorizontalAlignment(SwingConstants.CENTER);
			inputField.setMinimumSize(new Dimension(100,20));
			inputField.setPreferredSize(new Dimension(100,20));
			
			//Button
			button.addActionListener(this);
			button.setForeground(Color.white);
			button.setBorder(borderless);
			button.setBackground(background_two);
			button.setMinimumSize(new Dimension(40,20));
			button.setPreferredSize(new Dimension(40,20));
			
			//Panel Setup
			paneOne.setLayout(new GridBagLayout());
			paneOne.setBackground(background_two);
			body.setLayout(new GridBagLayout());
			body.setBackground(background_two);
			footer.add(inputField);
			footer.add(button);
			footer.setBackground(background);
			footer.setBorder(smallVerBorder);
			
			
			//GridBagConstraints Setup - c: main constraint | mid: exclusive body constraint
			//Header Panel
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.weightx = 0.5;
			c.weighty = 0.5;
			c.ipady = 15;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTH;
			paneOne.add(menuBar,c);
			
			//Body Panel
			mid.gridx = 0;
			mid.gridy = 0;
			mid.weightx = 0.5;
			mid.weighty = 0.5;
			mid.ipady = 0; 
			mid.fill = GridBagConstraints.NONE;
			mid.anchor = GridBagConstraints.CENTER;
			body.add(outputText,mid);
			
			mid.gridx = 0;
			mid.gridy = 1;
			mid.weightx = 0.5;
			mid.weighty = 0.5;
			mid.ipady = 0; 
			body.add(outputTextSub,mid);
			paneOne.add(body,mid);
			
			//Footer Panel
			c.gridx = 0;
			c.gridy = 3;
			c.weighty = 0.5;
			c.weighty = 0.5;
			c.ipady = 10;
			c.ipadx = 0;
			c.anchor = GridBagConstraints.SOUTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			paneOne.add(footer,c);

			//Finalize Frame and Make Visible
			frame.add(paneOne);
			frame.setSize(1280,720);
			frame.setVisible(true);			
		}
		
		private void buildBPane() {
			GridBagConstraints c = new GridBagConstraints();
			
			//Panel
			bPane.setLayout(new GridBagLayout());

			//Components-----
			//Increment Month
			incrementMonth.setBorder(borderless);
			incrementMonth.setBackground(background_two);
			incrementMonth.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(monthCount < 11)
						monthCount++;

					monthLabel.setText(month[monthCount]);
				}
			});
			//Increment Day
			incrementDay.setBorder(borderless);
			incrementDay.setBackground(background_two);
			incrementDay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(year % 4 == 0 && monthCount == 1 && day < 29)
						day++;
						
					else if(year % 4 != 0 && monthCount == 3 || monthCount == 5 || monthCount == 8 || monthCount == 10 && day < 30) 
							day++;
	
					else
						if(day < 31)
							day++;
					dayLabel.setText(Integer.toString(day));
				}
			});
			//Increment Year
			incrementYear.setBorder(borderless);
			incrementYear.setBackground(background_two);
			incrementYear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(year < 2010)
						year++;
					yearLabel.setText(Integer.toString(year));
				}
			});
			//Decrement Month
			decrementMonth.setBorder(borderless);
			decrementMonth.setBackground(background_two);
			decrementMonth.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(monthCount > 0)
						monthCount--;
					monthLabel.setText(month[monthCount]);
				}
			});
			//Decrement Day
			decrementDay.setBorder(borderless);
			decrementDay.setBackground(background_two);
			decrementDay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(day > 0)
						day--;
					dayLabel.setText(Integer.toString(day));
				}
			});
			
			//Decrement Year
			decrementYear.setBorder(borderless);
			decrementYear.setBackground(background_two);
			decrementYear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(year > 1950)
						year--;
					yearLabel.setText(Integer.toString(year));
				}
			});		

			monthLabel.setForeground(Color.white);
			monthLabel.setPreferredSize(new Dimension(50,30));
			
			dayLabel.setForeground(Color.white);
			dayLabel.setPreferredSize(new Dimension(50,30));

			yearLabel.setForeground(Color.white);
			yearLabel.setPreferredSize(new Dimension(50,30));

			//GridBagConstraints Setup
			c.gridx = 0;
			c.gridy = 0;
			c.ipadx = 20;
			c.ipady = 40;
			c.fill = GridBagConstraints.BOTH;
			bPane.add(incrementMonth,c);
			
			c.gridx = 1;
			c.gridy = 0;
			bPane.add(incrementDay,c);
			
			c.gridx = 2;
			c.gridy = 0;
			bPane.add(incrementYear,c);
			
			c.gridx = 0;
			c.gridy = 1;
			bPane.add(monthLabel,c);
			
			c.gridx = 1;
			c.gridy = 1;
			bPane.add(dayLabel,c);
			
			c.gridx = 2;
			c.gridy = 1;
			bPane.add(yearLabel,c);
			
			c.gridx = 0;
			c.gridy = 2;
			c.ipady = 30;
			bPane.add(decrementMonth,c);
			
			c.gridx = 1;
			c.gridy = 2;
			bPane.add(decrementDay,c);
			
			c.gridx = 2;
			c.gridy = 2;
			bPane.add(decrementYear,c);
			
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 3;
			c.ipady = 20;
			bPane.setBackground(background_two);
			
			//Switch to Birthday Pane from Main Pane
			
			paneOne.remove(body);
			body.remove(outputText);
			body.remove(outputTextSub);
			
			mid.gridx = 0;
			mid.gridy = 0;
			body.add(outputText,mid);
			
			mid.gridx = 0;
			mid.gridy = 1;
			body.add(outputTextSub,mid);
			
			mid.gridx = 0;
			mid.gridy = 2;
			body.add(bPane,mid);
			mid.ipady = 10;
			paneOne.add(body,mid);
		}

		//Processes ActionEvents
		public void actionPerformed(ActionEvent e) {
			//Button actions for creation of new user
			if(user.userExists() == false) {
				if(e.getSource()==button || e.getSource()==inputField) {
					//New User Question 1: Name
					if(questionOne) {
						user.setName(inputField.getText());
						inputField.setText("");
						outputText.setText("Nice to meet you!");
						outputTextSub.setText("What gender do you identify with?");
						questionOne = false;
						questionTwo = true;

					}
					//New User Question 2: Gender
					else if(questionTwo) {
						//button.setText("Send");
						user.setGender(inputField.getText());
						outputText.setText("Awesome! One more question...");
						outputTextSub.setText("When is your birthday?");
						buildBPane();
						inputField.setVisible(false);
						button.setText("Next");
						//System.out.println("GENDER COLLECTED."); Use for debug
						questionTwo = false;
						questionThree = true;
					}
		
					//New User Question 3: Birthday
					else if(questionThree) {
						body.remove(bPane);
						user.setBirthday(day,monthCount,year);
						//System.out.println("Birthday Collected"); Use for debug
						outputText.setText("");
						outputTextSub.setText("Click 'Finish' to complete registration.");
						button.setText("Finish");
						questionThree = false;
						storingUser = true;
					}
					
					//Stores New User Data to file(s)
					else if(storingUser) {
						inputField.setVisible(true);
						user.setData(user.getName(),user.getGender(),user.getBirthday());
						outputText.setText("Data Saved.");
						outputTextSub.setText("Please enter a command or question.");
						inputField.setText("");
						button.setText("Next");
					}
					
					//redirects user to main communication
					else if(communicateUser) {
						outputText.setText("Awesome! Let's get started!");
						inputField.setText("");
						communicate();
					}
				}
			}
			
			//Main Button actions for existing user
			else {
				//Menu Bar Action
				if(e.getSource()==changeAiName) {
					outputTextSub.setText("");
					outputText.setText("What would you like to call me?");
					changingAiName = true;
				}
				
				else if(changingAiName) {
					Central.setAiName(inputField.getText());
					outputText.setText(String.format("You can now call me, %s",Central.getAiName()));
					outputTextSub.setText("Please enter a command or question.");
					inputField.setText("");
					changingAiName = false;
				}
				
				else if(e.getSource()==changeUserName) {
					outputTextSub.setText("");
					outputText.setText("What would you like to be called?");
					changingUserName = true;
				}
				
				else if(changingUserName) {
					user.setName(inputField.getText());
					outputText.setText(String.format("You will now be called, %s",user.getName()));
					outputTextSub.setText("Please enter a command or question.");
					inputField.setText("");
					changingUserName = false;
				}
				
				else if(e.getSource()==changeGender) {
					outputTextSub.setText("");
					outputText.setText("What gender would you like to identify with?");
					changingGender = true;
				}
				
				else if(changingGender) {
					user.setGender(inputField.getText());
					outputText.setText(String.format("I have changed your gender to '%s'.",user.getGender()));
					outputTextSub.setText("Please enter a command or question.");
					inputField.setText("");
					changingGender = false;
				}
				
				else if(e.getSource()==changeBirthday) {
					outputTextSub.setText("");
					outputText.setText("Please enter your new birthday.");
					buildBPane();
					changingBirthday = true;
				}
				
				else if(changingBirthday) {
					body.remove(bPane);
					user.setBirthday(day,monthCount,year);
					outputText.setText(String.format("I have changed your birthday to '%s'.",user.getBirthday()));
					outputTextSub.setText("Please enter a command or question.");
					inputField.setText("");
					changingBirthday = false;
				}
				//Default Communication Actions
				else if(e.getSource()==button || e.getSource()==inputField) {
					getOutput();
				}
			
			}	
		}
		
		//
		private void runClockThread() {
			Runnable r = new Runnable() {
				public void run() {
					while(true) {
						liveClock.setText(nav.getTime());
					}
				}
			};
			new Thread(r).start();
		}
		//Post-Condition: checks to see if user exists and navigates accordingly
		private void checkForUser() {
			//New User
			if(user.userExists() == false) {
				questionOne = true;
				outputText.setText("Welcome to Project Iris.");
				outputTextSub.setText("What is your name?");
			}
			
			//Existing User
			else {
				communicate();
			}
		}
		
		//Post-Condition: Initiates conversation between ai and user
		private void communicate() {
			outputText.setText("How may I help you today?");
			button.setText("Send");
		}

		//Post-Condition: Returns response based on user input
		private void getOutput() {
			String inputText = inputField.getText();
			if(inputText.contains("bye") || inputText.contains("no")) {
				outputText.setText("It was nice speaking with you. See you next time!");
				System.exit(0);
			}
			
			else {
				new Central(inputText);
				
				String str = inputText;
				Central output = new Central(str);
				String conv = output.toString();
				outputTextSub.setText("");
				if(conv.isBlank())
					outputText.setText("I'm sorry. I do not understand.");
				else {
					outputText.setText("<html>" + conv + "</html>");
					outputTextSub.setText("Is there anything else I can help you with today?");
				}
				inputField.setText("");
			}

		}
			
}
