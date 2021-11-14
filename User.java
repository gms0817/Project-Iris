//This is where we will create and process user data
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.prefs.*;

public class User {
	//Private fields
	private String name;
	private Properties userInfo = new Properties();
	private Properties aiInfo = new Properties();
	private Preferences prefs;
	private String gender;
	private int age;
	
	//default constructor
	public User() {
		name = "Unknown";;
		gender = "Unknown";
		age = 0;
	}
	
	//Post-Condition: calls setUser() to assign values to object
	public User(String n,String g, int a) {
		setUser(n,g,a);
		setData(n,g,a);
	}
		
	//Post-Condition: Overloaded constructor to store user information and instantiate user object
	public void setUser(String n,String g, int a) {
		//assigns data to current object's variables
		this.name = n;
		this.gender = g;
		this.age = a;
	}
	
	//Post-Condition: Stores data fields into file for later use
	public void setData(String name,String gender, int age) {
		//set output location
		
		//create string variable using age values to save data
		String strAge = "" + age;
		
		//tries to save data / throws exception if failed
		try (OutputStream output = new FileOutputStream("user.properties")){
			//sets property values
			userInfo.setProperty("name", name);
			userInfo.setProperty("gender", gender);
			userInfo.setProperty("age", strAge);
			
			//saves properties to root folder
			userInfo.store(output,null);
			
			/*prints properties
			System.out.println(userInfo);*/
			
		} catch (IOException io) {
			io.printStackTrace();
		}
		
		//stores ai data
		try (OutputStream aiOutput = new FileOutputStream("ai.properties")){
			//set property values
			aiInfo.setProperty("aiName", Central.getAiName());
			
			//save properties file to root folder
			aiInfo.store(aiOutput, null);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
		
	}
	
	//Post-Condition: reads data files and builds object using their values
	public void getData() {
		try (InputStream input = new FileInputStream("user.properties")) {
			//load user properties file
			userInfo.load(input);
			
			//assigns values from properties file to current object
			this.name = userInfo.getProperty("name");
			this.gender = userInfo.getProperty("gender");
			String strAge = userInfo.getProperty("age");
			this.age = Integer.parseInt(strAge);	
			Central.getAiName();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		//load ai properties file
		try (InputStream input = new FileInputStream("ai.properties")) {
			//load properties file
			aiInfo.load(input);
			
			//assigns values from properties file to current object
			this.name = userInfo.getProperty("name");
			Central.setAiName(aiInfo.getProperty("aiName"));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

/*	//Post-Conditions: assigns default values of settings
	public void setPreferences() {
		prefs = Preferences.userRoot();
		//sets the value
		prefs.put();
		prefs.put("Gender",gender);
		prefs.putInt("Age", age);
	}
*/
	//Post-Condition: Return boolean value true or false if file exists
	public static boolean fileExists() {
		File temp = new File("user.properties");
		File temp2 = new File("ai.propeties");
		boolean exists = (temp.exists() || temp2.exists());
		
		return exists;
	}
	//Post-Condition: returns the name of the user
	public String getName() {
		if(fileExists())
			getData();
	
		return name;
	}
	
	//Post-Condition: returns the gender of the user
	public String getGender() {
		if(fileExists())
			getData();
		return gender;
	}
	
	//Post-Condition: returns the age of the user
	public int getAge() {
		if(fileExists())
			getData();
		return age;
	}
	
	//Post-Condition: toString method
	public String toString() {
		String str = "";
		
		return str;
	}
}
