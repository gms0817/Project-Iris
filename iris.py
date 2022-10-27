# Import Statements
import tkinter as tk
from tkinter import *
import pyttsx3

# -----------------------------------------------------------------------
# Setup Variables and Tweaks
engine = pyttsx3.init()

# -----------------------------------------------------------------------


def get_response(input_text):
    return input_text
    print(input_text)


def set_output_text():
    output = output_label.config(text=get_response(input_field.get()))
    input_field.delete(0, 'end')  # Empty the input field


def build_gui():
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

    body_frame = Frame(root, bg="gray", width=window_width, height=window_height - 400)
    body_frame.pack(anchor=CENTER, fill=X, expand=TRUE)

    footer_frame = Frame(root, bg="black", width=window_width, height=window_height - 200)
    footer_frame.pack(anchor=S, expand=TRUE, fill=X, side=BOTTOM)

    input_frame = Frame(footer_frame, bg="black")
    input_frame.pack(anchor=CENTER)

    # Add menu bar to header
    home_button = Menubutton(header_frame, text="Home")
    home_button.grid(row=0, column=0, padx=10, pady=10)

    # Add label to display output
    global output
    output = "Hi, my name is Iris. How may I help you?"
    global output_label
    output_label = tk.Label(body_frame, text=output, font=("Arial", 20), padx=10, pady=10)
    output_label.pack()

    # Add text box to collect user input
    global input_field
    input_field= tk.Entry(input_frame)
    input_field.focus()  # Window will autofocus on Entry widget
    input_field.pack(side=LEFT,padx=10, pady=10)

    root.bind('<Return>', set_output_text)

    # Add button to submit user input for processing
    submit_button = tk.Button(input_frame, text="Submit", command=set_output_text)
    submit_button.pack(side=RIGHT, padx=10, pady=10)

    # Keeps window visible on the screen until program is closed
    root.mainloop()



def setup_tts():
    # Customize TTS Engine
    engine.setProperty("rate", 185)
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[1].id) # voices[0] == male, voices[1] == female


def main():
    setup_tts()
    build_gui()
    engine.say(output)
    engine.runAndWait()  # Speak all queued text


# Call the main function
if __name__ == "__main__":
    main()
