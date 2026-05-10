package ui;

import model.Product;
import service.InventoryService;
import service.NotificationObserver;
import service.NotificationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel that shows low-stock / out-of-stock alerts.
 * Implements NotificationObserver to receive real-time alerts.
 */
public class AlertsPanel extends JPanel implements NotificationObserver {
    private final InventoryService inventoryService;
    private JTable alertTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    private static final String[] COLS = {"Time", "Product", "SKU", "Current Stock", "Threshold", "Status"};
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AlertsPanel(InventoryService inventoryService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        notificationService.registerObserver(this);
        buildUI();
        loadAlerts();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        countLabel = new JLabel("", SwingConstants.LEFT);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton refreshBtn = new JButton("⟳ Refresh Alerts");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.addActionListener(e -> loadAlerts());
        top.add(countLabel, BorderLayout.CENTER);
        top.add(refreshBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        alertTable = new JTable(tableModel);
        alertTable.setRowHeight(26);
        alertTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alertTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(new JScrollPane(alertTable), BorderLayout.CENTER);
    }

    public void loadAlerts() {
        tableModel.setRowCount(0);
        List<Product> flagged = inventoryService.getLowStockProducts();
        for (Product p : flagged) {
            tableModel.addRow(new Object[]{
                "—",
                p.getName(),
                p.getSku(),
                p.getCurrentStock(),
                p.getMinThreshold(),
                p.getStockStatus()
            });
        }
        countLabel.setText("⚠  " + flagged.size() + " item(s) need attention");
        countLabel.setForeground(flagged.isEmpty() ? new Color(0x2E7D32) : Color.RED);
    }

    // NotificationObserver callbacks (called in real time when stock changes)

    @Override
    public void onLowStock(Product product) {
        tableModel.insertRow(0, new Object[]{
            LocalDateTime.now().format(FMT),
            product.getName(),
            product.getSku(),
            product.getCurrentStock(),
            product.getMinThreshold(),
            "LOW STOCK"
        });
        countLabel.setText("⚠  Alert: " + product.getName() + " is LOW");
        countLabel.setForeground(Color.ORANGE.darker());
    }

    @Override
    public void onOutOfStock(Product product) {
        tableModel.insertRow(0, new Object[]{
            LocalDateTime.now().format(FMT),
            product.getName(),
            product.getSku(),
            0,
            product.getMinThreshold(),
            "OUT OF STOCK"
        });
        countLabel.setText("🚨 ALERT: " + product.getName() + " is OUT OF STOCK!");
        countLabel.setForeground(Color.RED);
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this,
                "⚠ OUT OF STOCK!\n" + product.getName() + " (SKU: " + product.getSku() + ")\n"
                + "Contact supplier: " + (product.getSupplier() != null ? product.getSupplier().getName() : "N/A"),
                "Stock Alert", JOptionPane.WARNING_MESSAGE));
    }
}
