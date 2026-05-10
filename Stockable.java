package model;

/**
 * Interface for any item that can have stock added or removed.
 * Demonstrates interface usage in OOP design.
 */
public interface Stockable {
    void addStock(int quantity);
    void removeStock(int quantity) throws exception.InsufficientStockException;
    int getCurrentStock();
    boolean isLowStock();
    boolean isOutOfStock();
}
