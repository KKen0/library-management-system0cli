/**
 * PatronManager class is responsible for managing Patron objects in memory
 * and synchronizing patron data with a text file
 *
 * Features:
 *  - Add, remove, search, and display patrons
 *  - Load patron data from a CSV-formatted text file
 *  - Save patron data to a file (append or overwrite)
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PatronManager {

    // Stores all patrons currently loaded in memory
    private final ArrayList<Patron> patrons;

    // Constructor initializes the patron list
    public PatronManager() {
        this.patrons = new ArrayList<>();
    }

    // Returns the list of all patrons
    public ArrayList<Patron> getAllPatrons() {
        return patrons;
    }

    // Searches for a patron using their unique patron ID
    public Patron findPatronById(int patronId) {
        for (Patron p : patrons) {
            if (p.getPatronId() == patronId) {
                return p;
            }
        }
        return null;
    }

    // Checks if a patron ID already exists
    public boolean isDuplicateId(int patronId) {
        return findPatronById(patronId) != null;
    }

    // Adds a new patron to memory if the ID is valid and unique
    public boolean addPatron(Patron patron) {
        if (patron == null) return false;

        int id = patron.getPatronId();
        if (isDuplicateId(id)) return false;

        patrons.add(patron);
        return true;
    }

    // Removes a patron from memory using their patron ID
    public boolean removePatronById(int patronId) {
        Patron p = findPatronById(patronId);
        if (p == null) return false;

        patrons.remove(p);
        return true;
    }

    //Dispaly all patrons currently stored in file
    public void displayAllPatrons() {
        if (patrons.isEmpty()) {
            System.out.println("No patrons found.");
            return;
        }

        System.out.println("----- Patron List -----");
        for (Patron p : patrons) {
            System.out.println(p);
        }
        System.out.println("-----------------------");
    }

    /**
     * Loads patron data from a text file formatted with comma-separated values.
     *
     * Expected format per line:
     * patronId,name,address,overdueFine
     *
     * Header row is allowed and will be skipped if present.
     *
     * @param fileName name of the file to load from
     */
    public void loadFromFile(String fileName) {
        int loadedCount = 0;
        int skippedCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) continue;

                // Skip header if first line is not numeric
                if (lineNumber == 1 && looksLikeHeader(line)) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    skippedCount++;
                    continue;
                }

                String idStr = parts[0].trim();
                String name = parts[1].trim();
                String address = parts[2].trim();
                String fineStr = parts[3].trim();

                try {
                    int patronId = Integer.parseInt(idStr);
                    double fine = Double.parseDouble(fineStr);

                    // Validate patron ID and fine amount
                    if (!Patron.isValidPatronId(patronId) || !Patron.isValidFine(fine)) {
                        skippedCount++;
                        continue;
                    }

                    // Prevent duplicate patron IDs
                    if (isDuplicateId(patronId)) {
                        skippedCount++;
                        continue;
                    }

                    Patron patron = new Patron(patronId, name, address, fine);
                    patrons.add(patron);
                    loadedCount++;

                    //Skip rows with invalid numeric values or constructor errors
                } catch (NumberFormatException ex) {
                    skippedCount++;
                } catch (IllegalArgumentException ex) {
                    skippedCount++;
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
            return;
        }

        System.out.println("Loaded patrons: " + loadedCount);
        System.out.println("Skipped rows: " + skippedCount);
    }

    /**
     * Appends a single patron to the file (used to save immediately after add).
     * If the file is missing or empty, it will write a header first.
     *
     * @param fileName file to write to
     * @param patron Patron object to save
     * @return true if saved successfully, false otherwise
     */
    public boolean appendPatronToFile(String fileName, Patron patron) {
        if (fileName == null || fileName.trim().isEmpty() || patron == null) return false;

        File file = new File(fileName);

        try {
            boolean writeHeader = !file.exists() || file.length() == 0;

            try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
                if (writeHeader) {
                    out.println("patronId,name,address,overdueFine");
                }

                out.println(toFileLine(patron));
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving patron to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Overwrites the entire file using the current in-memory list.
     * Use this after removals (and optionally after edits) so the file matches the list.
     *
     * @param fileName file to overwrite
     * @return true if saved successfully, false otherwise
     */
    public boolean saveAllToFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return false;

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, false))) {
            out.println("patronId,name,address,overdueFine");
            for (Patron p : patrons) {
                out.println(toFileLine(p));
            }
            return true;

        } catch (IOException e) {
            System.out.println("Error writing patrons to file: " + e.getMessage());
            return false;
        }
    }

    // ===== Helpers =====
    // Converts a Patron object into a CSV-formatted string
    private String toFileLine(Patron patron) {
        return patron.getPatronId() + "," +
                safeField(patron.getName()) + "," +
                safeField(patron.getAddress()) + "," +
                patron.getOverdueFine();
    }

    // Cleans text fields to prevent CSV formatting issues.
    private String safeField(String value) {
        if (value == null) return "";
        return value.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    /**
     * Determines whether a line appears to be a header row
     * @param line first line of the file
     * @return true if header detected, false otherwise
     */
    private boolean looksLikeHeader(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length == 0) return false;

        try {
            Integer.parseInt(parts[0].trim());
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
