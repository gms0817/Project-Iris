# Import Statements
import os
import pickle
import random
from tkinter import ttk
from datetime import datetime
import tkinter as tk
import sv_ttk
import speech_recognition as sr
import pyttsx3
import numpy as np
import requests

# -----------------------------------------------------------------------
# Setup Global-scope Variables
global root
global output_label
global input_field
global iris
global engine
engine = pyttsx3.init()
global voice_option
global user_obj


# -----------------------------------------------------------------------
# ChatBotAI Setup / Functions && STT/TTS Functions
class ChatBotAI:
    global voice_option
    voice_option = 1
    # Setup / Customize TTS Engine)
    engine.setProperty("rate", 185)
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[voice_option].id)  # voices[0] == male, voices[1] == female

    def __init__(self, name):
        print("---Loading ", name, "---")
        self.name = name

    def wake_up(self, text):
        return True if self.name in text.lower() else False

    def speech_to_text(self):
        recognizer = sr.Recognizer()
        with sr.Microphone() as mic:
            print("listening...")
            audio = recognizer.listen(mic)
        try:
            self.text = recognizer.recognize_google(audio)
            print("me --> ", self.text)
            set_output_text_voice(self.text)
        except:
            print("me --> ERROR")

    @staticmethod
    def text_to_speech(text):
        global engine
        print("AI --> ", text)
        engine.say(text)
        engine.runAndWait()

    @staticmethod
    def switch_tts_voice(self, option):
        global voice_option
        voice_option = option
        print(voice_option)
        voices = engine.getProperty('voices')
        engine.setProperty('voice', voices[voice_option].id)  # voices[0] == male, voices[1] == female


def get_response(input_text):
    # -----------------------------------------------------------------------
    # Pre-Defined Responses
    if "time" in input_text:
        return get_time()
    elif "bye" in input_text or "see you" in input_text:
        return np.random.choice(["Have a great day!", "See you next time!", "Goodbye!", "Bye!!"])
    elif "thank" in input_text:
        return np.random.choice(["No problem!", "You're welcome!", "Anytime!", "I'm here if you need me!"])
    elif "your name" in input_text or "who are you" in input_text:
        return "My name is Iris, and I am your personal assistant!"
    else:
        return "I'm sorry. I do not understand."

    # -----------------------------------------------------------------------
    # AI Generated Responses - WIP


def toggle_stt():
    # Global Variables
    global iris
    iris.speech_to_text()


# -----------------------------------------------------------------------
# Output Control Functions
def set_output_text():
    # Global Variables
    global output_label
    global input_field
    output = get_response(input_field.get())
    output_label.config(text=(get_response(output)))
    iris.text_to_speech(output)
    input_field.delete(0, 'end')  # Empty the input field


def set_output_text_voice(speech):
    # Global Variables
    global output_label
    global input_field
    output = get_response(input_field.get())
    output_label.config(text=(get_response(output)))
    iris.text_to_speech(output)
    input_field.delete(0, 'end')  # Empty the input field


def set_output_text_key(self):
    # Global Variables
    global output_label
    global input_field
    output = get_response(input_field.get())
    output_label.config(text=(get_response(output)))
    iris.text_to_speech(output)
    input_field.delete(0, 'end')  # Empty the input field


# -----------------------------------------------------------------------
# Helper Functions
def get_time():
    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    return "The current time is " + current_time


# -----------------------------------------------------------------------
# User Functions
class User:
    def __init__(self, user_name, user_zipcode):
        print("User created. Hello " + user_name)
        self.user_name = user_name
        self.user_zipcode = user_zipcode


def load_user(filename):
    try:
        print(filename)
        with open(filename, "rb") as f:
            return pickle.load(f)
    except Exception as ex:
        # Create User
        print("Error: User not found. Calling create_user()")
        create_user()


def save_user(user_obj):
    try:
        with open("user.pickle", "wb") as f:
            pickle.dump(user_obj, f, protocol=pickle.HIGHEST_PROTOCOL)
    except Exception as ex:
        print("Success: User has been loaded")


def create_user():
    global user_obj
    # Launch new user popup
    new_user_popup()


def new_user_popup():
    # Global Variables
    global root
    global user_obj

    # Create popup window
    new_user_window = tk.Toplevel(root)
    new_user_window.geometry("400x400")
    new_user_window.focus()

    # Add entry widget(s) with labels to popup window
    new_user_label = ttk.Label(new_user_window, text="New User Form")
    new_user_label.pack()

    user_name_label = ttk.Label(new_user_window, text="Enter your name: ")
    user_name_label.pack(padx=10, pady=10)

    user_name_field = ttk.Entry(new_user_window, width=25)
    user_name_field.pack(padx=10, pady=10)
    user_name_field.focus()

    zipcode_label = ttk.Label(
        new_user_window,
        text="Enter your zip-code:\n(Required for location services.)")
    zipcode_label.pack(padx=10, pady=10)
    zipcode_field = ttk.Entry(new_user_window, width=25)
    zipcode_field.pack(padx=10, pady=10)

    # Add submit button to create new user with data provided
    submit_button = ttk.Button(
        new_user_window, text="Create User",
        command=lambda: create_user_obj(user_name_field.get(), zipcode_field.get()))
    submit_button.pack(padx=10, pady=10, side="bottom")

    def create_user_obj(user_name, zipcode):
        global user_obj
        user_obj = User(user_name, zipcode)
        save_user(user_obj)  # Save user as an object to a picke file
        print(user_obj.user_name)
        close_new_user_popup()

    def close_new_user_popup():
        new_user_window.destroy()


# -----------------------------------------------------------------------
# GUI Window(s) Setup and Functions
def build_gui():
    # Global Variables
    global output_label
    global input_field
    global root

    # Build the user interface
    # Create instance of tk.Tk class to create application window and style
    root = tk.Tk()  # Main window
    root.title('Iris: Your Personal Assistant')

    # Setup window dimensions
    window_width = 720
    window_height = 480

    # Get screen dimensions
    screen_width = root.winfo_screenwidth()
    screen_height = root.winfo_screenheight()

    # Find the center point
    center_x = int(screen_width / 2 - window_width / 2)
    center_y = int(screen_height / 2 - window_height / 2)

    # Set position of window to the center of the screen
    root.geometry(f'{window_width}x{window_height}+{center_x}+{center_y}')  # Width, Height, x, y

    # Setup header, body, footer, and input frames
    header_frame = ttk.Frame(root, width=window_width, height=window_height - 200)
    header_frame.pack(side="top", fill="x")

    body_frame = ttk.Frame(root, width=window_width, height=window_height - 400)
    body_frame.pack(side="top", fill="x", expand=True)

    footer_frame = ttk.Frame(root, width=window_width, height=window_height - 200)
    footer_frame.pack(side="bottom", fill="x")

    input_frame = ttk.Frame(footer_frame)
    input_frame.pack()

    # Add menu bar to header
    voice_menu_button = ttk.Menubutton(header_frame, text="Voices ")
    menu = tk.Menu(voice_menu_button, tearoff=0)
    menu.add_radiobutton(label="David", value="David", command=lambda: iris.switch_tts_voice(0))
    menu.add_radiobutton(label="Zika", value="Zika", command=lambda: iris.switch_tts_voice(1))
    voice_menu_button["menu"] = menu
    voice_menu_button.pack(side="left", padx=10, pady=10)

    # Add label to display output
    output_label = ttk.Label(body_frame, text="Hi, my name is Iris. How may I help you?", font=("Arial", 20))
    output_label.pack()

    # Add text box to collect user input
    input_field = ttk.Entry(input_frame)
    input_field.focus()  # Window will autofocus on Entry widget
    input_field.pack(side="left", padx=5, pady=10)

    input_field.bind('<Return>', set_output_text_key)

    # Add button to submit user input for processing
    submit_button = ttk.Button(input_frame, text="Submit", width=8, command=set_output_text)
    submit_button.pack(side="right", padx=5, pady=10)

    # Add toggle to turn on voice recognition
    stt_is_on = True
    stt_button = ttk.Button(input_frame, text="STT", width=4, command=toggle_stt)
    stt_button.pack(side="right", padx=5, pady=10)

    # Set Sun Valley TTK Theme
    # Sun Valley TTK Theme by rdbende
    sv_ttk.set_theme("dark")

    # Load user, create new one if needed
    load_user("user.pickle")

    # Keeps window visible on the screen until program is closed
    root.mainloop()

# ------------------------------------------------------------------------
# Main Function and Program Direction to Main
def main():
    # Global Variables
    global iris

    # Instantiate Iris AI Chatbot
    iris = ChatBotAI(name="Iris")

    # Build main window
    build_gui()


# Call the main function
if __name__ == "__main__":
    main()
