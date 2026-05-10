package service;

import model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages stock alert notifications using the Observer pattern.
 * After every stock change, InventoryService calls checkAndNotify()
 * to fire alerts to all registered observers.
 */
public class NotificationService {
    private final List<NotificationObserver> observers = new ArrayList<>();
    private final List<String> alertLog = new ArrayList<>();

    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    /**
     * Checks the product's stock level and fires the appropriate notification.
     */
    public void checkAndNotify(Product product) {
        if (product.isOutOfStock()) {
            String msg = "OUT OF STOCK: " + product.getName() + " (SKU: " + product.getSku() + ")";
            alertLog.add(msg);
            for (NotificationObserver obs : observers) {
                obs.onOutOfStock(product);
            }
        } else if (product.isLowStock()) {
            String msg = "LOW STOCK: " + product.getName()
                + " — only " + product.getCurrentStock() + " left (threshold: "
                + product.getMinThreshold() + ")";
            alertLog.add(msg);
            for (NotificationObserver obs : observers) {
                obs.onLowStock(product);
            }
        }
    }

    public List<String> getAlertLog() {
        return new ArrayList<>(alertLog);
    }

    public void clearLog() {
        alertLog.clear();
    }
}
