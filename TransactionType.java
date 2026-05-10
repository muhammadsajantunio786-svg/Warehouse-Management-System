package model;

/**
 * Enum for transaction types — demonstrates enum usage.
 */
public enum TransactionType {
    STOCK_IN("Stock In"),
    STOCK_OUT("Stock Out");

    private final String label;

    TransactionType(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
