package ui;

import model.Product;
import service.InventoryService;
import service.NotificationService;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel showing summary KPIs.
 */
public class DashboardPanel extends JPanel {
    private final InventoryService inventoryService;
    private final AuthService authService;

    private JLabel totalProductsLbl;
    private JLabel lowStockLbl;
    private JLabel outOfStockLbl;
    private JLabel totalValueLbl;
    private JLabel userLbl;

    public DashboardPanel(InventoryService inventoryService, AuthService authService) {
        this.inventoryService = inventoryService;
        this.authService = authService;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Warehouse Management System — Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0x1565C0));
        add(title, BorderLayout.NORTH);

        JPanel kpiPanel = new JPanel(new GridLayout(2, 2, 20, 20));

        totalProductsLbl = makeKpiCard("Total Products", "0", new Color(0x1565C0));
        lowStockLbl      = makeKpiCard("Low Stock Items", "0", new Color(0xE65100));
        outOfStockLbl    = makeKpiCard("Out of Stock", "0", new Color(0xB71C1C));
        totalValueLbl    = makeKpiCard("Total Inventory Value", "$0.00", new Color(0x2E7D32));

        kpiPanel.add(totalProductsLbl.getParent());
        kpiPanel.add(lowStockLbl.getParent());
        kpiPanel.add(outOfStockLbl.getParent());
        kpiPanel.add(totalValueLbl.getParent());
        add(kpiPanel, BorderLayout.CENTER);

        userLbl = new JLabel("", SwingConstants.RIGHT);
        userLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        add(userLbl, BorderLayout.SOUTH);
    }

    private JLabel makeKpiCard(String heading, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setOpaque(true);

        JLabel hdr = new JLabel(heading, SwingConstants.CENTER);
        hdr.setFont(new Font("Arial", Font.PLAIN, 13));
        hdr.setForeground(new Color(0xE3F2FD));
        card.add(hdr, BorderLayout.NORTH);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Arial", Font.BOLD, 32));
        val.setForeground(Color.WHITE);
        card.add(val, BorderLayout.CENTER);
        return val;
    }

    public void refresh() {
        List<Product> all = inventoryService.getAllProducts();
        long low    = all.stream().filter(Product::isLowStock).count();
        long outOf  = all.stream().filter(Product::isOutOfStock).count();
        double val  = inventoryService.getTotalInventoryValue();

        totalProductsLbl.setText(String.valueOf(all.size()));
        lowStockLbl.setText(String.valueOf(low));
        outOfStockLbl.setText(String.valueOf(outOf));
        totalValueLbl.setText(String.format("$%.2f", val));

        if (authService.getCurrentUser() != null) {
            userLbl.setText("Logged in as: " + authService.getCurrentUser().getSummary());
        }
    }
}
