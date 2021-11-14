
import java.text.SimpleDateFormat;
import java.util.Date;

public class Central {
	private String input,output;
	private String[][] dialogue = new String [20][2];
	private static String aiName = "Hope";
	private User user = new User();

	//Post-Condition: Default constructor
	public Central() {
		input = "";
		output = "";
	}
	
	//Post-Condition: Overloaded constuctor
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
		dialogue[4][0] = "today's date";
		dialogue[4][1] = String.format("Today is: " + getDate());
		
		//#3 - what is ai name
		dialogue[5][0] = "your name";
		dialogue[5][1] = String.format("My name is " + getAiName() + ", of course!");
		
		//#4 - what is the user's name
		dialogue[6][0] = "my name";
		dialogue[6][1] = String.format("Your name is " + user.getName() + ".");
		dialogue[7][0] = "who am i";
		dialogue[7][1] = String.format("Your name is " + user.getName() + ".");

		//#5 - whomade the ai
		dialogue[8][0] = "who created you";
		dialogue[8][1] = "My creator is Gabriel Serrano.";
		dialogue[9][0] = "who made you";
		dialogue[9][1] = "My creator is Gabriel Serrano.";
		dialogue[10][0] = "who is your";
		dialogue[10][1] = "My creator is Gabriel Serrano.";

		return dialogue;
	}
	
	//Post-Condition: Filters through dialogue elements and returns output
	public void findResponse() {
		//obtains dialogue array
		dialogueArray();
		
		int col = 0;
		for(int row = 0; row < dialogue.length; row++) {
			//System.out.printf("%d, %d\n",row,col);
			if(input.contains(dialogue[row][col])) {
				output = dialogue[row][col + 1];
				System.out.println(dialogue[row][col + 1]);
				break;
			}
		}
	}
		
	
	//Post-Condition: Returns current time
	public String getTime() {
		//Uses date class to return date and time
		Date date = new Date();
		
		//formats time
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		
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
		return aiName;
	}
	
	//Post-Condition: Re-assigns a new name to ai
	public static void setAiName(String n) {
		aiName = n;
	}
}
