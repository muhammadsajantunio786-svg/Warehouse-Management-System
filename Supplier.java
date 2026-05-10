package model;

/**
 * Represents a supplier in the Warehouse Management System.
 */
public class Supplier {
    private int supplierId;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;

    public Supplier(int supplierId, String name, String contactPerson, String email, String phone) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
    }

    public Supplier(String name, String contactPerson, String email, String phone) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Supplier name cannot be empty.");
        this.name = name.trim();
    }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return name + " (" + contactPerson + ")";
    }
}
