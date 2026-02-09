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
 *
 * This is a console-based (text-based) application
 */


import java.util.Scanner;

public class LMSApp {

    private final PatronManager manager;
    private final Scanner scanner;

    private String currentFileName = null;

    public LMSApp() {
        manager = new PatronManager();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        LMSApp app = new LMSApp();
        app.run();
    }

    public void run() {
        boolean running = true;

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

        scanner.close();
    }

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

    private void loadPatronsFromFile() {
        System.out.print("Enter the file name (example: PatronData.txt): ");
        String fileName = scanner.nextLine().trim();

        if (fileName.isEmpty()) {
            System.out.println("File name cannot be empty.");
            return;
        }

        currentFileName = fileName;
        manager.loadFromFile(fileName);
    }

    private void addPatronAndSave() {
        if (currentFileName == null) {
            System.out.println("Please load a file first (Option 1) so the system knows where to save.");
            return;
        }

        int patronId = readValidPatronId();

        if (manager.isDuplicateId(patronId)) {
            System.out.println("That Patron ID already exists. Duplicate IDs are not allowed.");
            return;
        }

        System.out.print("Enter patron name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter patron address: ");
        String address = scanner.nextLine().trim();

        double fine = readValidFine();

        try {
            Patron patron = new Patron(patronId, name, address, fine);

            boolean added = manager.addPatron(patron);
            if (!added) {
                System.out.println("Patron could not be added.");
                return;
            }

            boolean saved = manager.appendPatronToFile(currentFileName, patron);

            if (saved) {
                System.out.println("Patron added and saved successfully.");
            } else {
                System.out.println("Warning: Patron added in memory but could not be saved to the file.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error adding patron: " + e.getMessage());
        }
    }

    private void removePatronAndSave() {
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

    private int readValidPatronId() {
        while (true) {
            int id = readInt("Enter 7-digit Patron ID: ");
            if (Patron.isValidPatronId(id)) {
                return id;
            }
            System.out.println("Invalid ID. Patron ID must be exactly 7 digits (1000000 to 9999999).");
        }
    }

    private double readValidFine() {
        while (true) {
            double fine = readDouble("Enter overdue fine amount (0 to 250): ");
            if (Patron.isValidFine(fine)) {
                return fine;
            }
            System.out.println("Invalid fine amount. Must be between 0 and 250.");
        }
    }

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
