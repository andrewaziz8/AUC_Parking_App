# AUC_Parking_App

## **Overview**  

This project is an advanced Parking Management System designed to improve the parking experience by leveraging modern technologies and AI-based predictions. The application offers real-time parking spot availability, notifications, and predictive analytics to enhance user convenience and optimize space utilization.  
It used Java and Android Studio, integrating the Gemini API for AI-based parking availability predictions. The application featured a real-time map interface for displaying parking spot statuses, Firebase for backend data management and synchronization, and a client-server architecture with Python and Flask to handle AI computations. Implemented a notification system that operates even when the app is closed, alerting users when a parking spot becomes available and providing the spot ID upon pressing the "Notify Me" button, which appears only when no spots are available. Utilized Google Gemini AI for predictive analytics, significantly enhancing user experience by reducing parking search times and optimizing space utilization.

---

## **Technologies Used**  

### **Programming Languages**  
- **Java**: For backend development and Android application logic.  
- **Python**: For server-side AI computations and handling complex logic.  

### **Development Tools**  
- **Android Studio**: Used for designing and developing the Android application.  
- **Flask**: A lightweight Python web framework for creating the API to serve AI predictions.  

### **APIs and AI Technologies**  
- **Gemini API**: Used for predictive analytics and machine-learning-driven parking availability forecasts.

### **Backend and Data Management**  
- **Firebase**: For real-time database management, user authentication, and synchronization of parking spot data.  
- **Firestore Database**: To store and manage parking availability records.  

### **Notification System**  
The Parking Management System includes a robust notification feature that ensures users are informed about parking spot availability in **real time**, even when the app is **not actively** in use.
- **Firebase Firestore**: For monitoring real-time updates in the parking spot data (**isAvailable** field).
- **Android Foreground Service**: Ensures persistent monitoring of parking spot availability even when the app is closed or running in the background.
- **Notification Manager**: Sends push notifications to alert users when a parking spot becomes available.
- **NotificationChannel**: Implements high-priority notifications for Android devices.
- **BroadcastReceiver**: Handles notification dismissal when triggered by the user.

### **Testing Frameworks**  
- **JUnit 5**: Used for unit and integration testing in Java.  

### **Client-Server Architecture**  
- **HTTP Requests (RESTful API)**: For communication between the Android client and the Flask server.   

### **Deployment Tools**  
- **Gradle**: Used for building and managing the Android application.  

---
