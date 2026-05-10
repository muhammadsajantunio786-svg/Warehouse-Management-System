package exception;

/**
 * Thrown when a product lookup fails.
 */
public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
