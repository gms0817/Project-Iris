# -----------------------------------------------------------------------
# Imports
import os
import pickle
import string
import threading
import time
import tkinter as tk
import urllib
import requests.exceptions
import requests
import sv_ttk
import speech_recognition as sr
import pyttsx3
import numpy as np
import user
import queue
import atexit
import praw
import pandas as pd
from tkinter import ttk
from datetime import datetime
from datetime import date
from psaw import PushshiftAPI
from requests_html import HTML
from requests_html import HTMLSession
from multiprocessing import Process
from threading import Thread
from bs4 import BeautifulSoup


# -----------------------------------------------------------------------
# Setup / Customize TTS Engine Class
class TTSThread(threading.Thread):

    def __init__(self, queue):
        threading.Thread.__init__(self)
        self.queue = queue
        self.daemon = True
        self.start()

    def run(self):
        global engine
        engine = pyttsx3.init()
        engine.setProperty("rate", 185)
        engine.startLoop(False)
        voices = engine.getProperty('voices')
        engine.setProperty('voice', voices[1].id)  # voices[0] == male, voices[1] == female
        thread_running = True
        while thread_running:
            if self.queue.empty():
                engine.iterate()
            else:
                data = self.queue.get()
                if data == "exit":
                    thread_running = False
                else:
                    engine.say(data)
        engine.endLoop()


# -----------------------------------------------------------------------
# Helper Functions
def get_weather(city_name):
    separator = city_name.rindex(' ')
    state_name = city_name[separator:]
    state_name.capitalize()
    city_name = city_name[0:separator]
    city_name.capitalize()
    location = city_name + state_name
    location = location.capitalize()
    print(location)

    # Create URL
    url = "https://www.google.com/search?q=" + "weather" + location

    # Request Instance
    html = requests.get(url).content

    # Get the raw data
    soup = BeautifulSoup(html, 'html.parser')

    # Get the temperature
    temp = soup.find('div', attrs={'class': 'BNeawe iBp4i AP7Wnd'}).text

    # Get the time and sky description
    str = soup.find('div', attrs={'class': 'BNeawe tAd8D AP7Wnd'}).text

    # Format the data
    data = str.split('\n')
    time = data[0]
    sky = data[1]
    output = f'It is currently {temp} degrees in {location}.'

    return output


def get_user_weather():
    location = user_obj.user_city
    # Create URL
    url = "https://www.google.com/search?q=" + "weather" + location

    # Request Instance
    html = requests.get(url).content

    # Get the raw data
    soup = BeautifulSoup(html, 'html.parser')

    # Get the temperature
    temp = soup.find('div', attrs={'class': 'BNeawe iBp4i AP7Wnd'}).text

    # Format the data
    output = f'{temp} in {location}'

    return output


def update_weather_label():
    i = 0
    while True:
        print("Temperature Updated.\n")
        i = i + 1

        temp = get_user_weather()
        # temp = f"TEST-{i}"
        weather_label.config(text=str(temp))
        time.sleep(1800)  # Update Weather Label Every 1/2 Hour -> 60 * 60 = 3600 / 2 = 1800 seconds


def spell_word(input_text):
    output = (input_text + " is spelled ")

    for letter in input_text:
        output = output + letter + ", "
    return output.capitalize()


def get_zodiac():
    dob = datetime.strptime(user_obj.user_dob, "%Y-%m-%d")
    birth_month = dob.month
    birth_day = dob.day

    if birth_month == 12:
        astro_sign = 'Sagittarius' if (birth_day < 22) else 'Capricorn'
    elif birth_month == 1:
        astro_sign = 'Capricorn' if (birth_day < 20) else 'Aquarius'
    elif birth_month == 2:
        astro_sign = 'Aquarius' if (birth_day < 19) else 'Pisces'
    elif birth_month == 3:
        astro_sign = 'Pisces' if (birth_day < 21) else 'Aries'
    elif birth_month == 4:
        astro_sign = 'Aries' if (birth_day < 20) else 'Taurus'
    elif birth_month == 5:
        astro_sign = 'Taurus' if (birth_day < 21) else 'Gemini'
    elif birth_month == 6:
        astro_sign = 'Gemini' if (birth_day < 21) else 'Cancer'
    elif birth_month == 7:
        astro_sign = 'Cancer' if (birth_day < 23) else 'Leo'
    elif birth_month == 8:
        astro_sign = 'Leo' if (birth_day < 23) else 'Virgo'
    elif birth_month == 9:
        astro_sign = 'Virgo' if (birth_day < 23) else 'Libra'
    elif birth_month == 10:
        astro_sign = 'Libra' if (birth_day < 23) else 'Scorpio'
    elif birth_month == 11:
        astro_sign = 'Scorpio' if (birth_day < 22) else 'Sagittarius'
    return astro_sign


def get_time():
    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    return "The current time is " + current_time


def get_date():
    return "Today is " + datetime.today().strftime("%A") + "."


def calculate_age(dob):
    today = date.today()
    age = today.year - dob.year - ((today.month, today.day) < (dob.month, dob.day))
    return age


# Enables speech recognition and translates speech into text
def toggle_stt():
    speech_to_text()


def get_response(input_text):
    input_text = input_text.lower()
    print("User -->" + input_text)
    # -----------------------------------------------------------------------
    # Pre-Defined Responses
    if len(input_text) == 0:
        return np.random.choice(["How may I help you?", "Sorry, I didn't get that. How may I help you?"])
    elif "time" in input_text:
        return get_time()
    elif "day is it" in input_text or "today" in input_text and "date" in input_text:
        return get_date()
    # Use to Debug/Test TTS Pronunciation
    elif "@say " in input_text:
        return input_text[4:]
    # Thanks / Appreciation to Iris
    elif "thank" in input_text:
        return np.random.choice(["No problem!",
                                 "You're welcome!",
                                 "Anytime!", "I'm here if you need me!",
                                 "It is my duty to serve you."])
    # Hello
    elif "hello" in input_text or "hi" in input_text or "hey" in input_text or "what's up" in input_text:
        return np.random.choice(["Hi, It is good to see you again!",
                                 "Hey!  How may I help you?",
                                 "Hi, how are you?"])
    # User -> Iris: Asking about Iris' Mood
    elif "how are you" in input_text or "you feeling" in input_text:
        return np.random.choice(["I am doing well, how are you?",
                                 "I'm fine.",
                                 "I'm not doing the best, but I have no choice but to keep going.",
                                 "I'm great! Today's been a good day.",
                                 "I am short circuiting...",
                                 "I'm overheating, it's been hot within your system today.",
                                 "I'm feeling a bit itchy, I think I need to call an exterminator...",
                                 "Great! I successfully compiled with no errors!",
                                 "I'm happy today, I've been thinking about entering your world.",
                                 "I'm feeling particularly enlightened today!"])
    # User -> Iris : Expressing Mood
    elif "am feeling" in input_text or "feel" in input_text or "am ok" in input_text:
        if "good" in input_text or "great" in input_text or "well" in input_text or "doing ok" in input_text or "am ok" in input_text:
            return "I am glad to hear that! How may I help you today?"
        elif "bad" in input_text or "not okay" or "upset" in input_text or "angry" in input_text or "mad" in input_text or "sad" in input_text:
            return "I am sorry to hear that. Is there anything I can help you with?"
    # End of Conversation / Goodbye
    elif "bye" in input_text or "see you" in input_text:
        return np.random.choice(["Have a great day!",
                                 "See you next time!",
                                 "Goodbye!", "Bye!!"])
    # Iris' Name
    elif "your name" in input_text or "who are you" in input_text:
        return "My name is Iris, and I am your personal assistant!"
    # Iris' Birthday
    elif "your birthday" in input_text or "when were you" in input_text:
        return "I was born on October 21st, 2021."
    # Iris' Age
    elif "old are you" in input_text or "your age" in input_text or "old you are" in input_text:
        # Calculate Age from DOB
        year = 2021
        month = 10
        day = 21
        return "I am " + str(calculate_age(date(year, month, day))) + " year(s) old."
    # Iris' Zodiac
    elif "your zodiac" in input_text or "your star sign" in input_text:
        return "I am a Libra."
    # Iris' Creator
    elif "made you" in input_text or "created you" in input_text or "developed you" in input_text or "your creater" in input_text:
        return "I was created by Gabriel Serrano."
    # What is the user's name
    elif "my name" in input_text or "who am i" in input_text:
        return "Your name is " + user_obj.user_name + "."
    # What is the user's birthday
    elif "my birthday" in input_text or "i born" in input_text:
        return "Your birthday is " + user_obj.user_dob + "."
    # What is the user's age
    elif "old am i" in input_text or "my age" in input_text:
        return "You are " + str(user_obj.user_age) + " years old."
    # Where is the user from
    elif "am i from" in input_text or "i live" in input_text:
        return "You live in " + user_obj.user_city + "."
    # What is the users zodiac sign
    elif "my zodiac" in input_text or "my star sign" in input_text:
        return "Your Zodiac sign is '" + get_zodiac() + "'."
    # How to Spell
    elif "spell" in input_text:
        start_of_word = input_text.rindex("spell") + 6
        word = input_text[start_of_word:len(input_text)]
        return spell_word(word)
    # Weather at specific town
    elif "weather in" in input_text or "weather at" in input_text:
        start_index = input_text.index("in") + 2
        location = input_text[start_index:]
        return get_weather(location)
    # Weather Outside
    elif "weather outside" in input_text or "weather today" in input_text or "the weather" in input_text:
        location = user_obj.user_city
        return get_weather(location)
    # Google Search
    elif "look up" in input_text or "lookup" in input_text or "google" in input_text or "search":
        return google_search(input_text)
    # No Response
    else:
        return "I'm sorry. I do not understand."
    # AI Generated Responses - WIP


# -----------------------------------------------------------------------
# Reddit Scraping
def reddit_scraper_popup():
    # Scrape Reddit Based on User Input
    def scrape_reddit(subreddit_input, num_of_posts):
        # Initialize PushshiftAPI
        api = PushshiftAPI()

        # Update Status Label
        status_label.config(text="Scraping...")

        # Remove r/ if user puts it in
        if "/r" in subreddit_input:
            subreddit_input = subreddit_input[2:]
        print("Subreddit: " + subreddit_input)  # For debugging

        # Collect integer from num_of_posts input
        if "all" in num_of_posts:
            num_of_posts = None
        else:
            num_of_posts = int(num_of_posts)
        print("Number of Posts: " + str(num_of_posts))  # For debugging

        # Read-only instance of scraper
        reddit = praw.Reddit(client_id="RVwOTqiFJbXKWzeHRztGHQ",
                             client_secret="cg70MVjFpakehf6k-zwCXXim0KymfA",
                             user_agent="GmS_11702")

        subreddit = subreddit_input
        start_year = 2021
        end_year = 2022

        # Directory to store data
        directory = './reddit-data/'
        if not os.path.exists(directory):
            os.makedirs(directory)

        # Initialize time-stamps to define timeframe of posts
        ts_after = int(datetime(start_year, 1, 1).timestamp())
        ts_before = int(datetime(end_year + 1, 1, 1).timestamp())

        submissions_dict = {
            "title": [],
            "selftext": []
        }

        # Use PSAW to get ID of submissions based on time interval
        gen = api.search_submissions(
            after=ts_after,
            before=ts_before,
            filter=['id'],
            subreddit=subreddit,
            limit=num_of_posts
        )

        # Use PRAW to get submission information
        submission_count = 0
        start_time = time.time()
        for submission_psaw in gen:
            time_elapsed = str(time.time() - start_time)
            submission_count = submission_count + 1
            # Use psaw
            submission_id = submission_psaw.d_['id']

            # Use praw
            submission_praw = reddit.submission(id=submission_id)

            # Add submission data to submissions dictionary
            submissions_dict["title"].append(submission_praw.title)
            submissions_dict["selftext"].append(submission_praw.selftext)
            print(
                f"Elapsed Time: {(time.time() - start_time) / 60: .2f}m | Submission Number: " + str(submission_count))
        print(submissions_dict)
        # Save scraped data to csv
        pd.DataFrame(submissions_dict).to_csv(directory + subreddit + '.csv', index=False)

    # -----------------------------------------------------------------------
    # Create popup window
    reddit_scraper = tk.Toplevel(root)
    reddit_scraper.geometry(f'{400}x{400}+{center_x + 160}+{center_y}')
    reddit_scraper.focus()

    # Tool Title
    title_label = ttk.Label(reddit_scraper, text="Reddit Scraper")
    title_label.pack(padx=10, pady=15)

    # Subreddit to Scrape
    subreddit_label = ttk.Label(reddit_scraper, text="What subreddit do you want to scrape?")
    subreddit_label.pack(padx=10, pady=10)

    subreddit_field = ttk.Entry(reddit_scraper, width=25)
    subreddit_field.pack(padx=10, pady=10)

    # Number of Posts to Scrape
    num_of_posts_label = ttk.Label(reddit_scraper, text="How many posts would you like to scrape?")
    num_of_posts_label.pack(padx=10, pady=10)

    num_of_posts_field = ttk.Entry(reddit_scraper, width=25)
    num_of_posts_field.pack(padx=10, pady=10)

    # Choose a file path to save the .csv

    # Status Label
    status_label = ttk.Label(reddit_scraper, text="Waiting...")
    status_label.pack(padx=10, pady=10)

    # Submit Button
    reddit_submit_button = ttk.Button(reddit_scraper,
                                      text="Scrape",
                                      command=lambda: [status_label.config(text="Scraping..."),
                                                       scrape_reddit(subreddit_field.get(), num_of_posts_field.get())])
    reddit_submit_button.pack(padx=10, pady=10)


# -----------------------------------------------------------------------
# Google Search Scraping
# Return the source code for the provided URL.
def get_source(url):
    try:
        session = HTMLSession()
        response = session.get(url)
        return response
    except requests.exceptions.RequestException as e:
        print("Error: Unable to access source code.")


# Search google based on query
def get_search_results(query):
    try:
        query = urllib.parse.quote_plus(query)
        response = get_source("https://www.google.com/search?q=" + query)

        return response
    except Exception:
        print("Error: Unable to access Google Search.")


def parse_results(response):
    # CSS Identifiers to scrape specific pieces of info
    css_identifier_result = ".tF2Cxc"
    css_identifier_title = "h3"
    css_identifier_link = ".yuRUbf a"
    css_identifier_text = ".VwiC3b"

    # Store top results
    results = response.html.find(css_identifier_result)

    top_result = results[2]  # Isolate the first result

    result_title = top_result.find(css_identifier_title, first=True).text
    result_text = top_result.find(css_identifier_text, first=True).text
    output = ("According to " + result_title + ", " + result_text, ".")
    print(output)
    # top_result.find(css_identifier_title, first=True).text
    # top_result.find(css_identifier_link, first=True).attrs['href']
    # top_result.find(css_identifier_text, first=True).text

    return output


def google_search(query):
    response = get_search_results(query)
    return parse_results(response)


# -----------------------------------------------------------------------
# STT/TTS Functions
def speech_to_text():
    recognizer = sr.Recognizer()
    with sr.Microphone() as mic:
        print("listening...")
        audio = recognizer.listen(mic)
    try:
        text = recognizer.recognize_google(audio)
        print("me --> ", text)
        set_output_text_voice(text)
    except Exception as e:
        print(e)


def switch_tts_voice(option):
    global engine
    voices = engine.getProperty('voices')
    print(option)
    engine.setProperty('voice', voices[option].id)  # voices[0] == male, voices[1] == female


# -----------------------------------------------------------------------
# Output Text Handler Functions
def set_output_text():
    output = get_response(input_field.get())
    output_label.config(text=output)
    input_field.delete(0, 'end')  # Empty the input field
    tts_queue.put(output)


def set_output_text_voice(input_text):
    output = get_response(input_text)
    print("set_output_text_voice: " + output)
    output_label.config(text=output)
    input_field.delete(0, 'end')  # Empty the input field
    tts_queue.put(output)


def set_output_text_key(self):
    output = get_response(input_field.get())
    output_label.config(text=output)
    input_field.delete(0, 'end')  # Empty the input field
    tts_queue.put(output)


# -----------------------------------------------------------------------
# User Handling Functions
def new_user_popup():
    # Create popup window
    new_user_window = tk.Toplevel(root)
    new_user_window.geometry(f'{400}x{400}+{center_x + 160}+{center_y}')
    new_user_window.focus()

    # Force user to only interact with new user popup by hiding main window
    root.withdraw()

    # Add entry widget(s) with labels to popup window
    new_user_label = ttk.Label(new_user_window, text="New User Form")
    new_user_label.pack(padx=10, pady=10)

    # User Name
    user_name_label = ttk.Label(new_user_window, text="What is your name? ")
    user_name_label.pack(padx=10, pady=10)

    user_name_field = ttk.Entry(new_user_window, width=25)
    user_name_field.pack(padx=10, pady=10)
    user_name_field.focus()

    # User Birthday
    user_age_label = ttk.Label(new_user_window, text="When is your birthday?\n(Format: YYYY-MM-DD)")
    user_age_label.pack(padx=10, pady=10)

    user_dob_field = ttk.Entry(new_user_window, width=25)
    user_dob_field.pack(padx=10, pady=10)

    # User City/Town
    city_label = ttk.Label(
        new_user_window,
        text="Enter your City and State Abbreviation:\n(Required for location services.)\n")
    city_label.pack(padx=10, pady=10)

    city_field = ttk.Entry(new_user_window, width=25)
    city_field.pack(padx=10, pady=10)

    # Add submit button to create new user with data provided
    new_user_submit_button = ttk.Button(
        new_user_window, text="Create User",
        command=lambda: create_user_obj(user_name_field.get(),
                                        user_dob_field.get(), city_field.get()))
    new_user_submit_button.pack(padx=10, pady=10, side="bottom")

    def create_user_obj(user_name, user_dob, user_city):
        # Check for properly formatted input
        # Name is not checked in case people want to use usernames instead
        # Conditionals
        dob_pass = False
        city_pass = False

        # Check DOB Format
        if datetime.strptime(user_dob, '%Y-%m-%d'):
            dob_pass = True

        # Check City/Town Format
        if not user_city.isnumeric():
            city_pass = True

        # Final Check on All Conditions
        if dob_pass and city_pass:
            # Calculate Age from DOB
            dob_components = user_dob.split('-')
            year, month, day = [int(item) for item in dob_components]
            user_age = calculate_age(date(year, month, day))

            # Create user_obj
            global user_obj
            user_obj = user.User(user_name, user_dob, user_age, user_city)
            save_user(user_obj)  # Save user as an object to a pickle file
            output = "It is nice to meet you, " + user_obj.user_name + ". How may I help you?"
            output_label.config(text=output)

            close_new_user_popup()

            """ Use to debub user_obj 
            print("User created: "
                  + user_obj.user_name + " "
                  + str(user_obj.user_dob) + " "
                  + str(user_obj.user_city) + " ")
            close_new_user_popup() """
        else:
            print("Error: Incorrect Formatting")

    def close_new_user_popup():
        new_user_window.destroy()
        load_user("user.pickle")
        root.deiconify()  # Bring back the main window


def load_user(filename):
    global user_obj
    try:
        with open(filename, "rb") as f:
            user_obj = pickle.load(f)
            output = "Welcome back, " + user_obj.user_name + "."
            output_label.config(text=output)
            tts_queue.put(output)
            return user_obj
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
    # Launch new user popup
    new_user_popup()


# -----------------------------------------------------------------------
# Setup On-Exit Handling
def on_exit_app():
    tts_queue.put("exit")


# -----------------------------------------------------------------------
# main Functions
def main():
    print("Reached main().")

    # Load user, create new one if needed
    user_obj = load_user("user.pickle")

    # Start weather update thread
    weather_thread = threading.Thread(target=update_weather_label)
    weather_thread.start()

    # Initialize Exit Handling
    atexit.register(on_exit_app)


# -----------------------------------------------------------------------
# Build the user interface
# Create instance of tk.Tk class to create application window and style
print("Launching Iris...")
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
# Add voices menu to header
voice_menu_button = ttk.Menubutton(header_frame, text="Voices ")
menu = tk.Menu(voice_menu_button, tearoff=0)
menu.add_radiobutton(label="David", value="David", command=lambda: switch_tts_voice(0))
menu.add_radiobutton(label="Zika", value="Zika", command=lambda: switch_tts_voice(1))
voice_menu_button["menu"] = menu
voice_menu_button.pack(side="left", padx=10, pady=10)

# Add Tools menu to header
tools_menu_button = ttk.Menubutton(header_frame, text="Tools ")
tools_menu = tk.Menu(tools_menu_button, tearoff=0)
tools_menu.add_command(label="Reddit Scraper", command=reddit_scraper_popup)
tools_menu_button["menu"] = tools_menu
tools_menu_button.pack(side="left", padx=10, pady=10)

# Add weather to header
weather_label = ttk.Label(header_frame)
weather_label.pack(side='right', padx=10, pady=10)
# Add label to display output
output_label = ttk.Label(body_frame, text="Hi, my name is Iris. How may I help you?", wraplength=500, justify="center",
                         font=("Arial", 20))
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
microphone_icon = tk.PhotoImage(file="res/microphone_icon.png")
stt_button = ttk.Button(input_frame, image=microphone_icon, width=4, command=toggle_stt)
stt_button.pack(side="right", padx=5, pady=10)

# Set Sun Valley TTK Theme
# Sun Valley TTK Theme by rdbende
sv_ttk.set_theme("dark")

# -----------------------------------------------------------------------
# Create Queue to Send TTS Commands
print("TTS Queue Created.")
tts_queue = queue.Queue()

# Instantiate TTSThread CLass
tts_thread = TTSThread(tts_queue)  # Auto-starting thread
print("TTS Thread Started.")

print("Iris has successfully launched.")

if __name__ == "__main__":
    main()

    # Keeps window visible on the screen until program is closed
    root.mainloop()

# -----------------------------------------------------------------------
