/*Version 1.0 - Welcome to Project Hope
 * Current Features:---------------------------------------------------
 * 5 Console-Based Interactions with Hope/iris
 * Single user per device
 * Save user input to computer
 */

import java.util.*; //import java utilities package

public class Main {
	//creates scanner
	static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) {
		String userInput = ""; //used for later user input
		User user = new User(); //temp user
		if(User.fileExists() == false) {
			newUser();
			communicate();
		}
		
		else {
			System.out.printf("%s, %s.\n","Welcome back",user.getName());
			communicate();
		
		}
	}
	
	//Post-Condition: Prompts user communication and only stops when user says 'bye'
	public static void communicate() {
		String userInput;
		boolean continueConvo = true;
		System.out.println("How may I help you today? ");
		do {
			userInput = input.nextLine();	
			if(userInput.contains("bye") || userInput.contains("no")) {
				System.out.println("It was nice speaking with you. See you next time!");
				continueConvo = false;
			}
			else {
				Central input =  new Central(userInput);
				//ADD FEATURE: RANDOMIZE DIFFERENT CONTINUE CONVO PHRASES TO REMOVE 'STALE' FEELING
				System.out.println("Is there anything I can help you with today?\n");
			}
		}while(continueConvo); 
	}
	
	//Post-Condition: Creates new user
	public static User newUser() {
		//initialize variables
		int a;
		String n,g;
		
		//Collect basic information from user 
		System.out.print("Welcome to Project Hope.\nMy Name is Hope, what's your preferred name? "); 
		n = input.nextLine();
		
		//CHANGE: currently only supporting THREE genders. will add more in future to accommodate.
		System.out.print("\nNice to meet you, " + n + ".\nIf you don't mind me asking, what gender do you identify with? ");
		g = input.nextLine();
	
		//CHANGE: ADD LIMITER TO 110 MAX
		System.out.print("\nNice! Last Question! How old are you? ");
		a = input.nextInt();
		
		//create user object
		User user = new User(n,g,a);
		return user;
	}
}
