package service;

import exception.InsufficientStockException;
import exception.ProductNotFoundException;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service for inventory operations.
 * Holds the in-memory collections and delegates persistence to DatabaseService.
 */
public class InventoryService {
    private final List<Product>     products     = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Category>    categories   = new ArrayList<>();
    private final List<Supplier>    suppliers    = new ArrayList<>();
    private final NotificationService notificationService;
    private final DatabaseService   dbService;

    private int nextProductId     = 1;
    private int nextTransactionId = 1;

    public InventoryService(NotificationService notificationService, DatabaseService dbService) {
        this.notificationService = notificationService;
        this.dbService = dbService;
        loadFromDatabase();
    }

    // ---- Bootstrap ----

    private void loadFromDatabase() {
        categories.addAll(dbService.loadCategories());
        suppliers.addAll(dbService.loadSuppliers());
        products.addAll(dbService.loadProducts(categories, suppliers));
        transactions.addAll(dbService.loadTransactions(products));

        // set next IDs
        products.stream().mapToInt(WarehouseEntity::getId).max()
                .ifPresent(max -> nextProductId = max + 1);
        transactions.stream().mapToInt(WarehouseEntity::getId).max()
                .ifPresent(max -> nextTransactionId = max + 1);
    }

    // ---- Products ----

    public void addProduct(Product p) {
        p.setId(nextProductId++);
        products.add(p);
        dbService.saveProduct(p);
    }

    public void updateProduct(Product p) {
        dbService.saveProduct(p);
    }

    public void deleteProduct(int productId) throws ProductNotFoundException {
        Product p = findProductById(productId);
        products.remove(p);
        dbService.deleteProduct(productId);
    }

    public Product findProductById(int id) throws ProductNotFoundException {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product ID " + id + " not found."));
    }

    public Product findProductBySku(String sku) throws ProductNotFoundException {
        return products.stream()
                .filter(p -> p.getSku().equalsIgnoreCase(sku))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("SKU '" + sku + "' not found."));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> getLowStockProducts() {
        return products.stream()
                .filter(p -> p.isLowStock() || p.isOutOfStock())
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        String kw = keyword.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(kw)
                          || p.getSku().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // ---- Stock Transactions ----

    public Transaction recordStockIn(Product product, int qty, String notes, String performedBy) {
        product.addStock(qty);
        Transaction t = new Transaction(nextTransactionId++, product,
                TransactionType.STOCK_IN, qty,
                java.time.LocalDateTime.now(), notes, performedBy);
        transactions.add(t);
        dbService.saveTransaction(t);
        notificationService.checkAndNotify(product);
        return t;
    }

    public Transaction recordStockOut(Product product, int qty, String notes, String performedBy)
            throws InsufficientStockException {
        product.removeStock(qty);   // throws InsufficientStockException if qty > stock
        Transaction t = new Transaction(nextTransactionId++, product,
                TransactionType.STOCK_OUT, qty,
                java.time.LocalDateTime.now(), notes, performedBy);
        transactions.add(t);
        dbService.saveTransaction(t);
        notificationService.checkAndNotify(product);
        return t;
    }

    // ---- Reports ----

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsForProduct(int productId) {
        return transactions.stream()
                .filter(t -> t.getProduct().getId() == productId)
                .collect(Collectors.toList());
    }

    public double getTotalInventoryValue() {
        return products.stream().mapToDouble(Product::getTotalValue).sum();
    }

    // ---- Categories & Suppliers ----

    public List<Category> getAllCategories() { return new ArrayList<>(categories); }
    public List<Supplier> getAllSuppliers()  { return new ArrayList<>(suppliers); }

    public void addCategory(Category c) {
        c.setCategoryId(categories.size() + 1);
        categories.add(c);
    }

    public void addSupplier(Supplier s) {
        s.setSupplierId(suppliers.size() + 1);
        suppliers.add(s);
    }
}
