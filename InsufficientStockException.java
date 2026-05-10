package exception;

/**
 * Thrown when a stock removal would exceed available quantity.
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
