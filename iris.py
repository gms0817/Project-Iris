# Import Statements
import os
import tkinter as tk
from tkinter import *
import numpy as np
import speech_recognition as sr
import pyttsx3
import transformers
import tensorflow

# -----------------------------------------------------------------------
# Setup Global-scope Variables
global output
global output_label
global input_field
global iris
global engine
engine = pyttsx3.init()


# -----------------------------------------------------------------------

class ChatBotAI():
    # Setup / Customize TTS Engine)
    engine.setProperty("rate", 185)
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[1].id)  # voices[0] == male, voices[1] == female

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


def get_response(input_text):
    global iris
    print(input_text)
    iris.text_to_speech(input_text)
    return input_text


def set_output_text():
    # Global Variables
    global output_label
    global input_field
    output_label.config(text=get_response(input_field.get()))
    input_field.delete(0, 'end')  # Empty the input field


def set_output_text_voice(speech):
    # Global Variables
    global output_label
    global input_field
    output_label.config(text=get_response(speech))
    input_field.delete(0, 'end')  # Empty the input field


def set_output_text_key(self):
    # Global Variables
    global output_label
    global input_field

    output_label.config(text=get_response(input_field.get()))
    input_field.delete(0, 'end')  # Empty the input field


def toggle_stt():
    # Global Variables
    global iris

    iris.speech_to_text()


def build_gui():
    # Global Variables
    global output
    global output_label
    global input_field

    # Build the user interface
    # Create instance of tk.Tk class to create application window
    root = tk.Tk()  # Main Window
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
    header_frame = Frame(root, bg="black", width=window_width, height=window_height - 200)
    header_frame.pack(anchor=N, fill=X, expand=TRUE)

    body_frame = Frame(root, width=window_width, height=window_height - 400)
    body_frame.pack(anchor=CENTER, fill=X, expand=TRUE)

    footer_frame = Frame(root, bg="black", width=window_width, height=window_height - 200)
    footer_frame.pack(anchor=S, expand=TRUE, fill=X, side=BOTTOM)

    input_frame = Frame(footer_frame, bg="black")
    input_frame.pack(anchor=CENTER)

    # Add menu bar to header
    home_button = Menubutton(header_frame, text="Home")
    home_button.grid(row=0, column=0, padx=10, pady=10)

    # Add label to display output
    output = "Hi, my name is Iris. How may I help you?"

    output_label = tk.Label(body_frame, text=output, font=("Arial", 20), padx=10, pady=10)
    output_label.pack()

    # Add text box to collect user input
    input_field = tk.Entry(input_frame)
    input_field.focus()  # Window will autofocus on Entry widget
    input_field.pack(side=LEFT, padx=10, pady=10)

    input_field.bind('<Return>', set_output_text_key)

    # Add button to submit user input for processing
    submit_button = tk.Button(input_frame, text="Submit", command=set_output_text)
    submit_button.pack(side=RIGHT, padx=10, pady=10)

    # Add toggle to turn on voice recognition
    stt_is_on = True
    stt_button = tk.Button(input_frame, text="STT", command=toggle_stt)
    stt_button.pack(side=RIGHT, padx=10, pady=10)

    # Keeps window visible on the screen until program is closed
    root.mainloop()


def main():
    # Global Variables
    global iris
    # Instantiate Iris AI Chatbot
    iris = ChatBotAI(name="Iris")

    build_gui()


# Call the main function
if __name__ == "__main__":
    main()
