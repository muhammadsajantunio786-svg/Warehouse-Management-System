package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a stock transaction (in or out) in the system.
 * Extends WarehouseEntity, demonstrating inheritance.
 */
public class Transaction extends WarehouseEntity {
    private Product product;
    private TransactionType type;
    private int quantity;
    private LocalDateTime timestamp;
    private String notes;
    private String performedBy;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(int transactionId, Product product, TransactionType type,
                       int quantity, LocalDateTime timestamp, String notes, String performedBy) {
        super(transactionId);
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.notes = notes;
        this.performedBy = performedBy;
    }

    public Transaction(Product product, TransactionType type, int quantity,
                       String notes, String performedBy) {
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now();
        this.notes = notes;
        this.performedBy = performedBy;
    }

    @Override
    public String getSummary() {
        return String.format("%s | %s | Qty: %d | By: %s | %s",
            product.getName(), type.getLabel(), quantity, performedBy,
            timestamp.format(FORMATTER));
    }

    // Getters
    public Product getProduct()          { return product; }
    public TransactionType getType()     { return type; }
    public int getQuantity()             { return quantity; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public String getNotes()             { return notes; }
    public String getPerformedBy()       { return performedBy; }
    public String getFormattedTimestamp(){ return timestamp.format(FORMATTER); }
}
