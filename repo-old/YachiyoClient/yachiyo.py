"""Yachiyo Client - A simple chat client application."""

import json
import requests
import tkinter
import threading

CONFIG = {}
TEXT_VAR = None

def init():
    """Initialize the configuration for the application."""
    global CONFIG
    try:
        with open("config.json", "r", encoding="utf-8") as f:
            CONFIG = json.load(f)
    except FileNotFoundError:
        CONFIG = {"url" : "http://localhost:8080/api/v1/ai/chat"}
        with open("config.json", "w", encoding="utf-8") as f:
            json.dump(CONFIG, f)


class Yachiyo(tkinter.Tk):
    """Yachiyo chat client main window."""
    def __init__(self):
        global TEXT_VAR
        super().__init__()
        self.title("Yachiyo")
        self.geometry("300x300")
        self.resizable(width=False, height=True)
        self.entry = None

        TEXT_VAR = tkinter.StringVar()

        self.build()

    def build(self):
        """Build the UI components for the chat window."""
        self.frame = tkinter.Frame(self)

        tkinter.Label(self.frame, text="Yachiyo").pack(expand=True, fill=tkinter.BOTH)
        tkinter.Label(self.frame, text="Enter your message:").pack(expand=True, fill=tkinter.BOTH)
        self.entry = tkinter.Entry(self.frame)
        self.entry.pack(expand=True, fill=tkinter.BOTH)
        (tkinter.Button(self.frame, text="Send", command=self.send)
         .pack(expand=True, fill=tkinter.BOTH))
        tkinter.Label(self.frame, textvariable=TEXT_VAR).pack(expand=True, fill=tkinter.BOTH)
        self.frame.pack()


    def send(self):
        """Handle the send button click event."""
        message = self.entry.get()
        print(message)
        thread = threading.Thread(target=self.send_message, args=(message,))
        thread.start()

    @staticmethod
    def send_message(message):
        """Send a message to the chat API."""
        url = CONFIG["url"]
        TEXT_VAR.set(requests.post(url, data=message).text)

if __name__ == "__main__":
    init()
    app = Yachiyo()
    app.mainloop()