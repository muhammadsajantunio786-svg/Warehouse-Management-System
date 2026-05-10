package service;

import model.Product;

/**
 * Observer interface for the notification system.
 * Demonstrates the Observer design pattern and polymorphism.
 */
public interface NotificationObserver {
    void onLowStock(Product product);
    void onOutOfStock(Product product);
}
