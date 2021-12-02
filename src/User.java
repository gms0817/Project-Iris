//Version 2.0 - Let's Try Graphics
//This is where we will create and process user data
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class User {
	//Private fields
	private String name;
	private String gender;
	private String[] month = new String[] {"January","February","March","April","May","June","July","August","September","October","November","December"};


	private String birthMonth;
	private String birthday;
	
	//default constructor
	public User() {
		name = "Unknown";;
		gender = "Unknown";
		birthMonth = "";
	}
	
	//Post-Condition: calls setUser() to assign values to object
	public User(String n,String g, int bd, String bm, int by) {
		setUser(n,g,bd,bm,by);
		setData(n,g,birthday);
	}
		
	//Post-Condition: Overloaded constructor to store user information and instantiate user object
	public void setUser(String n,String g, int bd, String bm, int by) {
		//assigns data to current object's variables
		name = n;
		gender = g;
		birthMonth = bm;
	}
	
	//Post-Condition: Stores data fields into file for later use
	public void setData(String name, String gender, String birthday) {		
		//Write data to file
		try {

			//Writing to User File
			FileWriter myWriter = new FileWriter("user/user_name.txt");
			myWriter.write(getName());
			myWriter.close();
			myWriter = new FileWriter("user/user_gender.txt");
			myWriter.write(getGender());
			myWriter.close();
			myWriter = new FileWriter("user/user_birthday.txt");
			myWriter.write(getBirthday());
			myWriter.close();
			myWriter = new FileWriter("user/user_exists.txt");
			myWriter.write("User Exists");
			myWriter.close();
			
			//Writing to AI File
			myWriter = new FileWriter("ai/ai_name.txt");
			myWriter.write(Central.getAiName());
			myWriter.close();
		
			//Success Statement
			Iris.outputText.setText("Data saved.");
		} catch (IOException e) {
			Iris.outputText.setText("An error occured while saving your data.");
			e.printStackTrace();
		}		
	}
	
	//Post-Condition: reads data files and builds object using their values
	public void getData() {
		try {
			//Gets user info from file(s)
			//Name
			File userName = new File("user/user_name.txt");
			Scanner myReader = new Scanner(userName);
			while(myReader.hasNextLine()) {
				name = myReader.nextLine();
			}
			myReader.close();
			//Gender
			File userGender = new File("user/user_gender.txt");
			myReader = new Scanner(userGender);
			while(myReader.hasNextLine()) {
				gender = myReader.nextLine();
			}
			myReader.close();
			
			//Age
			File userAge = new File("user/user_birthday.txt");
			myReader = new Scanner(userAge);
			while(myReader.hasNextLine()) {
				birthday = myReader.nextLine();
			}
			myReader.close();
			
			//Gets ai info from files
			File aiName = new File("ai/ai_name.txt");
			myReader = new Scanner(aiName);
			while(myReader.hasNextLine()) {
				Central.setAiName(myReader.nextLine());
			}
			myReader.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	//Post-Condition: Creates folders and files for user and ai
	public void createFiles() {
		//Create the file or utilize what exists
		try {
			//Create User Folder
			File userDir = new File("user");
			if(!userDir.exists())
				userDir.mkdir();
			
			//Create AI Folder
			File aiDir = new File("ai");
			if(!aiDir.exists())
				aiDir.mkdir();
			
			//Creates Files for user and ai
			File userName = new File("user/user_name.txt");
			File userGender = new File("user/user_gender.txt");
			File userBirthday = new File("user/user_birthday.txt");
			File aiName = new File("ai/ai_name.txt");
			File userExists = new File("user/user_exists.txt");

			if(userName.createNewFile() && userGender.createNewFile() && userBirthday.createNewFile() && aiName.createNewFile() && userExists.createNewFile()) {
				System.out.println("File(s) created."); //Use for debugging			
			} else
				System.out.print("");
				//System.out.println("File(s) already exists. Overwriting prior file(s)."); //Use for debugging
		} catch (IOException e) {
			Iris.outputText.setText(("An error occured while saving your data."));
			e.printStackTrace();
		}
	}
	
	//Post-Condition: Return boolean value true or false if file exists
	public boolean userExists() {
		boolean exists = false;
		File userExists = new File("user/user_exists.txt");
		
		if(userExists.exists()) {
			try {
				String temp = "";
				Scanner myReader = new Scanner(userExists);
				while(myReader.hasNextLine())  { 
					temp = myReader.nextLine();
				}
				if(userExists.exists() && temp.isBlank() == false)
					exists = true;
				else {
					createFiles();
					exists = false;
				}
				myReader.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else
			createFiles();
		
		return exists;
	}
	
	//Post-Condition: returns the name of the user
	public String getName() {
		if(userExists())
			getData();
		return name;
	}
	
	//Post-Condition: sets the name of the user
	public void setName(String n) {
		name = n;
		try {
			FileWriter myWriter = new FileWriter("user/user_name.txt");
			myWriter.write(getName());
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//Post-Condition: returns the gender of the user
	public String getGender() {
		if(userExists())
			getData();
		return gender;
	}
	
	//Post-Condition: sets the gender of the user
	public void setGender(String g) {
		gender = g;
		try {
			FileWriter myWriter = new FileWriter("user/user_gender.txt");
			myWriter.write(getGender());
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/*//Post-Condition: returns the age of the user
	public int getAge() {
		int age = 0;
		if(userExists())
			getData();
		return age;
	}
	
	//Post-Condition: sets the age of the user
	public void setAge(String age) {
		age = Integer.parseInt(age);	

	}*/
	
	//Post-Condition: returns string value of the user's birthday
	public String getBirthday() {
		if(userExists())
			getData();
		return birthday;
		
	}
	//Post-Condition: returns value of birthdayMonth as String type format for writing to file exclusively
	public String getBirthMonth(int monthCount) {
		if(userExists())
			getData();
		birthMonth = month[monthCount];
		return birthMonth;
	}
	
	//Post-Condition: assigns value of birthday created by birthDay, birthMonth, and birthYear
	public void setBirthday(int day,int monthCount, int year) {
		//System.out.println(String.format("%s/%d/%d", month,day,year)); Use for debug
		birthday = String.format("%s %d,%d", getBirthMonth(monthCount),day,year);
		try {
			FileWriter myWriter = new FileWriter("user/user_birthday.txt");
			myWriter.write(birthday);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
