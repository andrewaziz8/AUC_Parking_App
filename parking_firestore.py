import firebase_admin
from firebase_admin import credentials, firestore
from flask import Flask
import pandas as pd
import google.generativeai as genai

app = Flask(__name__)

# Configure Google Gemini AI
api_key = "AIzaSyCtCsAhHqpsY09wh92R0pORX3wSn35zTrk"
genai.configure(api_key=api_key)

# Initialize Firebase Admin SDK
cred = credentials.Certificate("auc-parking-app-firebase-adminsdk-v5uxz-33ef2c5f6c.json")  # Path to your Firebase service account key
firebase_admin.initialize_app(cred)

# Initialize Firestore client
db = firestore.client()

# Function to load parking data from Firestore
def load_parking_data_from_firestore(collection_name):
    try:
        # Reference the Firestore collection
        docs = db.collection(collection_name).stream()
        data = []

        # Iterate over each document and store it in a list
        for doc in docs:
            doc_data = doc.to_dict()
            doc_data["id"] = doc.id  # Include the document ID
            data.append(doc_data)

        if data:
            df = pd.DataFrame(data)  # Convert the data into a Pandas DataFrame
            print("Loaded Data from Firestore:")
            print(df.head())
            return df
        else:
            print("No data found in Firestore.")
            return None
    except Exception as e:
        print("Error loading data from Firestore:", e)
        return None

# Function to prepare message from data
def prepare_message_from_data(df):
    data_string = df.to_string(index=False)
    message = (
        f"Here is the parking data for multiple spots of different spot ids:\n{data_string}\nPlease provide the expected next available time for each parking spot id. Gimme just id of each parking spot along with the next available for that spot in a dictionary form where the key is the spot id and the value is the expected time to leave. Also, do not print anything else other than disctionary, just the disctionary please." 
    )
    print("Message to be sent:")
    print(message)
    return message

# Function to get the next available time
def get_next_available_time(message):
    try:
        generation_config = {
            "temperature": 1,
            "top_p": 0.95,
            "top_k": 40,
            "max_output_tokens": 8192,
            "response_mime_type": "text/plain",
        }

        model = genai.GenerativeModel(
            model_name="gemini-1.5-flash",
            generation_config=generation_config,
        )

        chat_session = model.start_chat(history=[])
        response = chat_session.send_message(message)

        if hasattr(response, 'text'):
            print("Full response:", response)
            time = response.text.strip()

            # Find the index of the first '{' and the last '}'
            start_index = time.find('{')  # Index of the opening bracket
            end_index = time.rfind('}')   # Index of the closing bracket

            # Extract the dictionary string
            dictionary_str = time[start_index:end_index + 1]

            # time = response.text
            return dictionary_str
        else:
            print("No 'text' attribute in response")
            return None
    except Exception as e:
        print("Error:", e)
        return None

# Default route
@app.route("/")
def home():
    collection_name = "spots"  # Replace with your Firestore collection name
    df = load_parking_data_from_firestore(collection_name)

    if df is not None:
        message = prepare_message_from_data(df)
        next_available_time = get_next_available_time(message)

        if next_available_time:
            return next_available_time  # Return the next available time as plain text

    return "Could not determine the next available time."


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080, debug=True)
