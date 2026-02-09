/**
 * Patron class represents a library patron as it stores
 * identifying information and tracks overdue fines
 *
 * Validation rules"
 * Patron ID must be exactly 7 digits
 * Name and address cannot be empty
 * Overdue fine must be between $0 and $250
 */


public class Patron {

    // ===== Fields =====
    private final int patronId;      // Unique ID for the patron
    private String name;            // Patron's full name
    private String address;         // Patron's address
    private double overdueFine;     // Fine owed by the patron must be 0 and 250

    // ===== Constructor =====

    /**
     * Constructs a Patron object with validation.
     * @throws IllegalArgumentException if any input is valid.
     */
    public Patron(int patronId, String name, String address, double overdueFine) {
        if (!isValidPatronId(patronId)) {
            throw new IllegalArgumentException("Patron ID must be exactly 7 digits.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        if (!isValidFine(overdueFine)) {
            throw new IllegalArgumentException("Overdue fine must be between 0 and 250.");
        }

        // Assign validated values to fields
        this.patronId = patronId;
        this.name = name.trim();
        this.address = address.trim();
        this.overdueFine = overdueFine;
    }

    // ===== Getters =====

    /**
     * Returns the patron's ID
     * No setter exists because patronId is final.
     */
    public int getPatronId() {
        return patronId;
    }

    // Returns the patron's name
    public String getName() {
        return name;
    }

    // Returns the patron's address
    public String getAddress() {
        return address;
    }

    // Returns the overdue fine amount
    public double getOverdueFine() {
        return overdueFine;
    }

    // ===== Setters =====
    // Updates the patron's name, address, overdue fine after validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address.trim();
    }

    public void setOverdueFine(double overdueFine) {
        if (!isValidFine(overdueFine)) {
            throw new IllegalArgumentException("Overdue fine must be between 0 and 250.");
        }
        this.overdueFine = overdueFine;
    }

    // ===== Static Validation Methods =====
    // Checks if a patron ID is exactly 7 digits
    public static boolean isValidPatronId(int patronId) {
        return patronId >= 1_000_000 && patronId <= 9_999_999;
    }

    // Checks if the fine is within the allowed range
    public static boolean isValidFine(double fine) {
        return fine >= 0.0 && fine <= 250.0;
    }

    // ===== Display =====

    /**
     * Returns a readable string representation of the Patron object.
     * This method is automatically called when printing the object.
     */
    @Override
    public String toString() {
        return "Patron ID: " + patronId +
                ", Name: " + name +
                ", Address: " + address +
                ", Overdue Fine: $" + overdueFine;
    }
}
