/**
 * Name: Kaveen Amin
 * Course: Software Development I - 23586
 *
 * LMS App is the main user interface for the Library Management System.
 * This directly is customized according to librarians.
 *
 * This class:
 *      - Displays a menu to the user
 *      - Handles user input
 *      - Delegates patron operations to PatronManager
 *      - Maintain the currently loaded file name for saving updates
 *
 * This is a console-based (text-based) application
 */


import java.util.Scanner;

public class LMSApp {

    //Manages all patron operations (add, remove, find, load, save, display)
    private final PatronManager manager;

    //Used to read user input from the console
    private final Scanner scanner;

    //Stores the currently loaded file name so we know where to save updates
    private String currentFileName = null;

    //Constructor initializes PatronManager and Scanner
    public LMSApp() {
        manager = new PatronManager();
        scanner = new Scanner(System.in);
    }

    /**
     * Program entry point
     * Creates the app and starts the main loop
     */
    public static void main(String[] args) {
        LMSApp app = new LMSApp();
        app.run();
    }

    /**
     * Runs the main application loop until the user chooses Exit
     * Displays the menu, processes user choice, and calls the appropriate methods
     */
    public void run() {
        boolean running = true;

        //Process menu option using a switch statement
        while (running) {
            displayMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    loadPatronsFromFile();
                    break;
                case 2:
                    addPatronAndSave();
                    break;
                case 3:
                    removePatronAndSave();
                    break;
                case 4:
                    findPatron();
                    break;
                case 5:
                    manager.displayAllPatrons();
                    break;
                case 6:
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 6.");
            }

            System.out.println();
        }

        // Close scanner resource when program finishes
        scanner.close();
    }

    /**
     * Displays the main menu to the user
     * Shows the current loaded file so the user knows where autosave will go
     */
    private void displayMenu() {
        System.out.println("===== Library Management System (LMS) =====");
        System.out.println("Current file: " + (currentFileName == null ? "(none loaded)" : currentFileName));
        System.out.println("1. Load patrons from file");
        System.out.println("2. Add a patron (auto-save)");
        System.out.println("3. Remove a patron (auto-save)");
        System.out.println("4. Find a patron by ID");
        System.out.println("5. Display all patrons");
        System.out.println("6. Exit");
        System.out.println("==========================================");
    }

    /**
     * Prompts the user for a file name and loads patrons into memory.
     * After successful input, sets currentFilename so future know where to write
     */
    private void loadPatronsFromFile() {
        System.out.print("Enter the file name (example: PatronData.txt): ");
        String fileName = scanner.nextLine().trim();

        // Validate file name
        if (fileName.isEmpty()) {
            System.out.println("File name cannot be empty.");
            return;
        }

        //Store file name for future auto-save operations
        currentFileName = fileName;
        //Load patron records from file into PatronManager
        manager.loadFromFile(fileName);
    }

    /**
     * Collects patron details from the user, validates them, adds the patron,
     * and immediately appends the new patron to the currently loaded file
     */
    private void addPatronAndSave() {
        if (currentFileName == null) {
            System.out.println("Please load a file first (Option 1) so the system knows where to save.");
            return;
        }

        int patronId = readValidPatronId();

        //Prevent duplicate IDs (must be unique)
        if (manager.isDuplicateId(patronId)) {
            System.out.println("That Patron ID already exists. Duplicate IDs are not allowed.");
            return;
        }

        // Read patron name
        System.out.print("Enter patron name: ");
        String name = scanner.nextLine().trim();

        // Read patron address
        System.out.print("Enter patron address: ");
        String address = scanner.nextLine().trim();

        // Read and validate fine amount (0 to 250)
        double fine = readValidFine();

        try {
            //Create Patron object (constructor may validate and throw an exception)
            Patron patron = new Patron(patronId, name, address, fine);

            boolean added = manager.addPatron(patron);
            if (!added) {
                System.out.println("Patron could not be added.");
                return;
            }

            // Append patron to file so it persists even after program exits
            boolean saved = manager.appendPatronToFile(currentFileName, patron);

            if (saved) {
                System.out.println("Patron added and saved successfully.");
            } else {
                System.out.println("Warning: Patron added in memory but could not be saved to the file.");
            }

        } catch (IllegalArgumentException e) {
            // Catches validation errors thrown from Patron constructor
            System.out.println("Error adding patron: " + e.getMessage());
        }
    }

    /**
     * Removes a patron by ID from memory and then overwrites the file
     * so the file matches the updated in-memory list
     */
    private void removePatronAndSave() {
        // Must load a file first so we know which file to update
        if (currentFileName == null) {
            System.out.println("Please load a file first (Option 1) so the system knows which file to update.");
            return;
        }

        int patronId = readValidPatronId();

        boolean removed = manager.removePatronById(patronId);
        if (!removed) {
            System.out.println("No patron found with that ID.");
            return;
        }

        // Rewrite the file so the removed patron is deleted from the file too
        boolean saved = manager.saveAllToFile(currentFileName);

        if (saved) {
            System.out.println("Patron removed and file updated successfully.");
        } else {
            System.out.println("Warning: Patron removed in memory but file could not be updated.");
        }
    }

    //Finds a patron by ID and displays the result
    private void findPatron() {
        int patronId = readValidPatronId();

        Patron p = manager.findPatronById(patronId);
        if (p == null) {
            System.out.println("No patron found with that ID.");
        } else {
            System.out.println("Patron found:");
            System.out.println(p);
        }
    }

    // ===== Input Helpers =====

    /**
     * Repeatedly prompts the user until a valid 7-digit patron ID is entered
     *
     * @return valid fine amount (0 to 250)
     */
    private int readValidPatronId() {
        while (true) {
            int id = readInt("Enter 7-digit Patron ID: ");
            if (Patron.isValidPatronId(id)) {
                return id;
            }
            System.out.println("Invalid ID. Patron ID must be exactly 7 digits (1000000 to 9999999).");
        }
    }

    /**
     * Reads an integer from the user with error handling.
     * Keeps prompting until the user enters a valid integer.
     * Prompt message shown to the user
     * @return integer value entered by the user
     */
    private double readValidFine() {
        while (true) {
            double fine = readDouble("Enter overdue fine amount (0 to 250): ");
            if (Patron.isValidFine(fine)) {
                return fine;
            }
            System.out.println("Invalid fine amount. Must be between 0 and 250.");
        }
    }

    /**
     * Reads a double from the user with error handling.
     * Keeps prompting until the user enters a valid number
     *
     * @param prompt message shown to the user
     * @return integer value entered by the user
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a double from the user with error handling.
     * Keeps prompting until the user enters a valid number.
     *
     * @param prompt message shown to the user
     * @return double value entered by the user
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
