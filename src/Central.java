//Version 2.0 - Let's Try Graphics
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class Central {
	private String input,output;
	private String[][] dialogue = new String [30][2];
	private static String aiName = "Iris";
	private static User user = new User(); //Instantiates User object for later use
	
	//Post-Condition: Default constructor
	public Central() {
		input = "";
		output = "";
	}
	
	//Post-Condition: Overloaded constructor
	public Central(String i) {
		this.input = i;
		findResponse();
	}
	
	//Post-Condition: Returns dialogue values in array
	public String[][] dialogueArray () {
		//initialize all elements to 0
		int row,col;
		for (row = 0; row < dialogue.length; row++)
			for(col = 0; col < dialogue[row].length; col++)
				dialogue[row][col] = "";
		//v.1.0.0 -----
		//#1 - Time of day
		dialogue[0][0] = "time is";
		dialogue[0][1] = String.format("The time is currently: " + getTime());
		dialogue[1][0] = "the time";
		dialogue[1][1] = String.format("The time is currently: " + getTime());
		
		//#2 - the date
		dialogue[2][0] = "day is";
		dialogue[2][1] = String.format("Today is: " + getDate());
		dialogue[3][0] = "today's date";
		dialogue[3][1] = String.format("Today is: " + getDate());
		dialogue[4][0] = "the date";
		dialogue[4][1] = String.format("Today is: " + getDate());
		
		//#3 - what is ai name
		dialogue[5][0] = "your name";
		dialogue[5][1] = String.format("My name is " + getAiName() + ", of course!");
		dialogue[6][0] = "who are you";
		dialogue[6][1] = String.format("My name is " + getAiName() + ", of course!");
		
		//#4 - what is the user's name
		dialogue[7][0] = "my name";
		dialogue[7][1] = String.format("Your name is " + user.getName() + ".");
		dialogue[8][0] = "who am i";
		dialogue[8][1] = String.format("Your name is " + user.getName() + ".");
		
		//#5 - what is the user's age
		dialogue[9][0] = "old am i";
		//dialogue[9][1] = String.format("You are " + user.getAge() + " years old.");
		dialogue[10][0] = "old i am";
		//dialogue[10][1] = String.format("You are " + user.getAge() + "years old.");
		
		//v2.0.0 -----
		//#6 - who made the ai
		dialogue[11][0] = "who created you";
		dialogue[11][1] = "My creator is Gabriel Serrano.";
		dialogue[12][0] = "who made you";
		dialogue[12][1] = "My creator is Gabriel Serrano.";
		dialogue[13][0] = "who is your";
		dialogue[13][1] = "My creator is Gabriel Serrano.";
		
		//#7 - what is the users gender
		dialogue[14][0] = "my gender";
		dialogue[14][1] = String.format("You identify as a(n) '%s'.",user.getGender());
		dialogue[15][0] = "gender am i";
		dialogue[15][1] = String.format("You identify as a(n) '%s'.",user.getGender());
		dialogue[16][0] = "gender do i";
		dialogue[16][1] = String.format("You identify as a(n) '%s'.",user.getGender());
		
		//#8 - what is the user's birthday
		dialogue[17][0] = "my birthday";
		dialogue[17][1] = "Your birthday is " + user.getBirthday() + ".";
		dialogue[18][0] = "was i born";
		dialogue[18][1] = "Your birthday is " + user.getBirthday() + ".";
		dialogue[19][0] = "date of my conception"; //Lilly's personal phrase
		dialogue[19][1] = "Your birthday is " + user.getBirthday() + ".";
		
		//#9 - tell me a fun fact
		dialogue[20][0] = "fun fact";
		dialogue[20][1] = getFunFact();
		
		//#10 - when is the ai's birthday?
		dialogue[21][0] = "your birthday";
		dialogue[21][1] = "I was born on October 26th, 2021";
		dialogue[22][0] = "when were you born";

		return dialogue;
	}
	
	//Post-Condition: returns a random string type fun fact from a list of [50] fun facts
	private String getFunFact() {
		//Collects fun facts from txt file and builds funFactArr
		String funFact = "";
		List<String> funFactArrList;
		Random random = new Random();
		int r = random.nextInt(50);
		
		try {
			funFactArrList = Files.readAllLines(new File("_resources/fun_facts.txt").toPath(), Charset.forName("UTF-8"));
			String[] funFactArr = new String[funFactArrList.size()];
			funFactArr = funFactArrList.toArray(funFactArr);
			
			funFact = String.format("Did you know, %s?", funFactArr[r]);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return funFact;
	}

	//Post-Condition: Filters through dialogue elements and returns output
	public void findResponse() {
		//obtains dialogue array
		dialogueArray();
		int col = 0;
		for(int row = 0; row < dialogue.length; row++) {
			if(input.contains(dialogue[row][col])) {
				output = dialogue[row][col + 1];
				break;
			}
		}
	
	}
		
	
	//Post-Condition: Returns current time
	public String getTime() {
		//Uses date class to return date and time
		Date date = new Date();
		
		//formats time
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
		
		//displays time
		return formatter.format(date);
	}
	
	//Post-Condition: Returns current time
	public String getDate() {
		//Uses date class to return date and time
		Date date = new Date();
		
		//formats time
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy.");
		
		//displays time
		return formatter.format(date);
	}
	
	//Post-Condition: returns ai name 
	public static String getAiName() {
		try {
			File file = new File("ai/ai_name.txt");
			Scanner myReader = new Scanner(file);
			while(myReader.hasNextLine()) {
				aiName = myReader.nextLine();
			}
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}

		return aiName;
	}
	
	//Post-Condition: Re-assigns a new name to ai
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
	
	//Post-Condition: Converts input object to String
	public String toString() {
		String outputText = "";
		outputText = outputText + output;
		return outputText;
	}

}
