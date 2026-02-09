# library-management-system0cli

This project is a **Java-based Library Management System (LMS)** developed for the **Software Development I** course.  
The application demonstrates core software development concepts, including object-oriented programming, file handling, and basic data persistence using a command-line interface.

The project includes both **source code** and an **executable JAR file**, allowing the program to be run without an IDE.

---

## Project Overview

The LMS allows a librarian to manage patron records by loading data from a text file and performing common operations such as adding, removing, finding, and displaying patrons.

The system does **not** use a database. Instead, it uses a plain text file formatted with comma-separated values to store patron information.

---

## Features

- Load patron data from a text file  
- Add new patrons with validation  
- Automatically save newly added patrons to the file  
- Remove patrons and update the file accordingly  
- Find a patron by ID  
- Display all patrons  
- Input validation for patron ID and overdue fines  
- Runnable executable JAR file  

---

## File Format

Patron data is stored in a text file (for example: `PatronData.txt`) using the following format:
      patronId,name,address,overdueFine
      1234567,John Smith,123 Main St,25.50
      2345678,Alice Brown,55 Pine Ave,0   

- Patron ID must be exactly **7 digits**
- Overdue fine must be between **0 and 250**
- The first line (header) is optional and will be ignored if present

---

## Project Structure
LMS-Project/
├── src/
│   ├── Patron.java
│   ├── PatronManager.java
│   ├── LMSApp.java
│   └── output/
│       └── artifacts/
│           └── LMS_jar/
│               └── LMS.jar
├── PatronData.txt

## How to Run the Program (Executable JAR)

### Option 1: Run using the JAR file (recommended)

1. Make sure Java is installed on your system  
2. Place `PatronData.txt` in the root folder 
3. Open a terminal or command prompt in that folder  
4. Run the following command:
    java -jar LMSApp.jar
5. Follow the on-screen menu options

---

### Option 2: Run from source code (IDE)

1. Open the project in an IDE such as IntelliJ IDEA  
2. Ensure the data file (e.g., `PatronData.txt`) is located in the project root directory  
3. Run `LMSApp.java`  
4. Use the menu options to manage patrons  

---

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- File I/O
- Command-Line Interface (CLI)

---

## Notes

- Patron data is loaded into memory when the program runs
- Changes are automatically saved to the text file when patrons are added or removed
- The project was designed according to an SDLC plan and UML class diagram
- The executable JAR allows the program to be run without an IDE

---

## Author

Kaveen Amin  
Software Development I
CRN 23586
