package model;

import exception.InsufficientStockException;

/**
 * Represents a product in the warehouse.
 * Extends WarehouseEntity (abstract class) and implements Stockable (interface).
 * Demonstrates inheritance, encapsulation, and interface implementation.
 */
public class Product extends WarehouseEntity implements Stockable {
    private String sku;
    private String name;
    private int currentStock;
    private int minThreshold;
    private double unitPrice;
    private Category category;
    private Supplier supplier;

    public Product(int productId, String sku, String name, int currentStock,
                   int minThreshold, double unitPrice, Category category, Supplier supplier) {
        super(productId);
        this.sku = sku;
        this.name = name;
        this.currentStock = currentStock;
        this.minThreshold = minThreshold;
        this.unitPrice = unitPrice;
        this.category = category;
        this.supplier = supplier;
    }

    public Product(String sku, String name, int currentStock,
                   int minThreshold, double unitPrice, Category category, Supplier supplier) {
        this.sku = sku;
        this.name = name;
        this.currentStock = currentStock;
        this.minThreshold = minThreshold;
        this.unitPrice = unitPrice;
        this.category = category;
        this.supplier = supplier;
    }

    // --- Stockable interface methods ---

    @Override
    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to add must be positive.");
        this.currentStock += quantity;
    }

    @Override
    public void removeStock(int quantity) throws InsufficientStockException {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to remove must be positive.");
        if (quantity > currentStock) {
            throw new InsufficientStockException(
                "Cannot remove " + quantity + " units of '" + name + "'. Only " + currentStock + " in stock.");
        }
        this.currentStock -= quantity;
    }

    @Override
    public int getCurrentStock() { return currentStock; }

    @Override
    public boolean isLowStock() {
        return currentStock > 0 && currentStock <= minThreshold;
    }

    @Override
    public boolean isOutOfStock() {
        return currentStock == 0;
    }

    // --- WarehouseEntity abstract method ---

    @Override
    public String getSummary() {
        return String.format("[%s] %s | Stock: %d | Price: $%.2f", sku, name, currentStock, unitPrice);
    }

    // --- Stock level label ---

    public String getStockStatus() {
        if (isOutOfStock()) return "OUT OF STOCK";
        if (isLowStock())   return "LOW STOCK";
        return "OK";
    }

    public double getTotalValue() {
        return currentStock * unitPrice;
    }

    // --- Getters and Setters ---

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty.");
        this.name = name.trim();
    }

    public void setCurrentStock(int currentStock) {
        if (currentStock < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.currentStock = currentStock;
    }

    public int getMinThreshold() { return minThreshold; }
    public void setMinThreshold(int minThreshold) {
        if (minThreshold < 0) throw new IllegalArgumentException("Threshold cannot be negative.");
        this.minThreshold = minThreshold;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative.");
        this.unitPrice = unitPrice;
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}
