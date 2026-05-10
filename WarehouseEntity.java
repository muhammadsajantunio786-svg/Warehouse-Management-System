package model;

/**
 * Abstract base class for all persistable warehouse entities.
 * Demonstrates abstract classes and polymorphism.
 */
public abstract class WarehouseEntity {
    private int id;

    public WarehouseEntity(int id) {
        this.id = id;
    }

    public WarehouseEntity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /**
     * Every entity must be able to provide a display-friendly summary.
     */
    public abstract String getSummary();

    @Override
    public String toString() {
        return getSummary();
    }
}
