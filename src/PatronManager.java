import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PatronManager {

    private final ArrayList<Patron> patrons;

    public PatronManager() {
        this.patrons = new ArrayList<>();
    }

    public ArrayList<Patron> getAllPatrons() {
        return patrons;
    }

    public Patron findPatronById(int patronId) {
        for (Patron p : patrons) {
            if (p.getPatronId() == patronId) {
                return p;
            }
        }
        return null;
    }

    public boolean isDuplicateId(int patronId) {
        return findPatronById(patronId) != null;
    }

    public boolean addPatron(Patron patron) {
        if (patron == null) return false;

        int id = patron.getPatronId();
        if (isDuplicateId(id)) return false;

        patrons.add(patron);
        return true;
    }

    public boolean removePatronById(int patronId) {
        Patron p = findPatronById(patronId);
        if (p == null) return false;

        patrons.remove(p);
        return true;
    }

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

                    if (!Patron.isValidPatronId(patronId) || !Patron.isValidFine(fine)) {
                        skippedCount++;
                        continue;
                    }

                    if (isDuplicateId(patronId)) {
                        skippedCount++;
                        continue;
                    }

                    Patron patron = new Patron(patronId, name, address, fine);
                    patrons.add(patron);
                    loadedCount++;

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

    private String toFileLine(Patron patron) {
        return patron.getPatronId() + "," +
                safeField(patron.getName()) + "," +
                safeField(patron.getAddress()) + "," +
                patron.getOverdueFine();
    }

    private String safeField(String value) {
        if (value == null) return "";
        return value.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

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
