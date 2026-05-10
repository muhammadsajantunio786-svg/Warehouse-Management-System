package ui;

import exception.InsufficientStockException;
import exception.ProductNotFoundException;
import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel showing the full product inventory table with add/edit/stock actions.
 */
public class InventoryPanel extends JPanel {
    private final InventoryService inventoryService;
    private final AuthService authService;
    private final Runnable refreshCallback;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final String[] COLUMNS = {
        "ID", "SKU", "Name", "Category", "Supplier", "Stock", "Min Threshold", "Unit Price ($)", "Status"
    };

    public InventoryPanel(InventoryService inventoryService, AuthService authService, Runnable refreshCallback) {
        this.inventoryService = inventoryService;
        this.authService = authService;
        this.refreshCallback = refreshCallback;
        buildUI();
        loadTable(inventoryService.getAllProducts());
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(10, 5));
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Search by name or SKU");
        JButton searchBtn = new JButton("🔎 Search");
        JButton clearBtn  = new JButton("📃 Show All");
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchBtns.add(searchBtn);
        searchBtns.add(clearBtn);
        searchPanel.add(searchBtns, BorderLayout.EAST);
        topBar.add(searchPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton addBtn      = new JButton("+ Add Product");
        JButton stockInBtn  = new JButton("▲ Stock In");
        JButton stockOutBtn = new JButton("▼ Stock Out");
        JButton deleteBtn   = new JButton("✕ Delete");

        addBtn.setBackground(new Color(0x2E7D32));
        addBtn.setForeground(Color.WHITE);
        stockInBtn.setBackground(new Color(0x1565C0));
        stockInBtn.setForeground(Color.WHITE);
        stockOutBtn.setBackground(new Color(0xE65100));
        stockOutBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(0xB71C1C));
        deleteBtn.setForeground(Color.WHITE);

        actionPanel.add(addBtn);
        actionPanel.add(stockInBtn);
        actionPanel.add(stockOutBtn);
        if (authService.isManager()) actionPanel.add(deleteBtn);
        topBar.add(actionPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(0xE0E0E0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        StockStatusRenderer renderer = new StockStatusRenderer(8); // Status is column 8
        for (int i = 0; i < COLUMNS.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Wire actions
        searchBtn.addActionListener(e -> loadTable(inventoryService.searchProducts(searchField.getText())));
        clearBtn.addActionListener(e -> { searchField.setText(""); loadTable(inventoryService.getAllProducts()); });
        addBtn.addActionListener(e -> showAddProductDialog());
        stockInBtn.addActionListener(e -> doStockTransaction(TransactionType.STOCK_IN));
        stockOutBtn.addActionListener(e -> doStockTransaction(TransactionType.STOCK_OUT));
        deleteBtn.addActionListener(e -> doDelete());
    }

    public void refresh() {
        loadTable(inventoryService.getAllProducts());
    }

    private void loadTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getCategory() != null ? p.getCategory().getName() : "—",
                p.getSupplier()  != null ? p.getSupplier().getName()  : "—",
                p.getCurrentStock(),
                p.getMinThreshold(),
                String.format("%.2f", p.getUnitPrice()),
                p.getStockStatus()
            });
        }
    }

    private int getSelectedProductId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        return (int) tableModel.getValueAt(row, 0);
    }

    private void doStockTransaction(TransactionType type) {
        int productId = getSelectedProductId();
        if (productId < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }

        try {
            Product product = inventoryService.findProductById(productId);
            String title = type == TransactionType.STOCK_IN ? "Stock In" : "Stock Out";
            String qtyStr = JOptionPane.showInputDialog(this,
                "Enter quantity to " + title.toLowerCase() + " for:\n" + product.getName(), title, JOptionPane.PLAIN_MESSAGE);
            if (qtyStr == null || qtyStr.isBlank()) return;

            int qty = Integer.parseInt(qtyStr.trim());
            String notes = JOptionPane.showInputDialog(this, "Notes (optional):", "");
            String by = authService.getCurrentUser().getFullName();

            if (type == TransactionType.STOCK_IN) {
                inventoryService.recordStockIn(product, qty, notes, by);
                JOptionPane.showMessageDialog(this, "Stock added successfully.");
            } else {
                inventoryService.recordStockOut(product, qty, notes, by);
                JOptionPane.showMessageDialog(this, "Stock removed successfully.");
            }
            refresh();
            if (refreshCallback != null) refreshCallback.run();

        } catch (ProductNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (!authService.isManager()) { JOptionPane.showMessageDialog(this, "Manager access required."); return; }
        int productId = getSelectedProductId();
        if (productId < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete product ID " + productId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            inventoryService.deleteProduct(productId);
            refresh();
            if (refreshCallback != null) refreshCallback.run();
        } catch (ProductNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddProductDialog() {
        JTextField skuF    = new JTextField();
        JTextField nameF   = new JTextField();
        JTextField stockF  = new JTextField("0");
        JTextField threshF = new JTextField("5");
        JTextField priceF  = new JTextField("0.00");

        List<Category> cats = inventoryService.getAllCategories();
        List<Supplier>  sups = inventoryService.getAllSuppliers();
        JComboBox<Category> catBox = new JComboBox<>(cats.toArray(new Category[0]));
        JComboBox<Supplier>  supBox = new JComboBox<>(sups.toArray(new Supplier[0]));

        Object[] fields = {
            "SKU:", skuF, "Name:", nameF, "Current Stock:", stockF,
            "Min Threshold:", threshF, "Unit Price ($):", priceF,
            "Category:", catBox, "Supplier:", supBox
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Product p = new Product(
                skuF.getText().trim(), nameF.getText().trim(),
                Integer.parseInt(stockF.getText().trim()),
                Integer.parseInt(threshF.getText().trim()),
                Double.parseDouble(priceF.getText().trim()),
                (Category) catBox.getSelectedItem(),
                (Supplier)  supBox.getSelectedItem()
            );
            inventoryService.addProduct(p);
            refresh();
            if (refreshCallback != null) refreshCallback.run();
            JOptionPane.showMessageDialog(this, "Product added successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Please check Stock, Threshold, and Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
