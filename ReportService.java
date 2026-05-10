package service;

import model.*;

import java.util.List;

/**
 * Generates reports and analytics from inventory data.
 * Demonstrates separation of concerns (report logic separate from inventory logic).
 */
public class ReportService {
    private final InventoryService inventoryService;

    public ReportService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public String generateLowStockReport() {
        List<Product> low = inventoryService.getLowStockProducts();
        StringBuilder sb = new StringBuilder();
        sb.append("=== LOW STOCK / OUT OF STOCK REPORT ===\n\n");
        if (low.isEmpty()) {
            sb.append("All products are adequately stocked.\n");
        } else {
            sb.append(String.format("%-6s %-12s %-25s %-10s %-10s %-12s\n",
                "ID", "SKU", "Product Name", "Stock", "Threshold", "Status"));
            sb.append("-".repeat(80)).append("\n");
            for (Product p : low) {
                sb.append(String.format("%-6d %-12s %-25s %-10d %-10d %-12s\n",
                    p.getId(), p.getSku(), p.getName(),
                    p.getCurrentStock(), p.getMinThreshold(), p.getStockStatus()));
                if (p.getSupplier() != null) {
                    sb.append(String.format("       Supplier: %s — %s\n",
                        p.getSupplier().getName(), p.getSupplier().getPhone()));
                }
            }
        }
        sb.append("\nTotal flagged items: ").append(low.size());
        return sb.toString();
    }

    public String generateInventoryValueReport() {
        List<Product> all = inventoryService.getAllProducts();
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY VALUATION REPORT ===\n\n");
        sb.append(String.format("%-6s %-25s %-10s %-12s %-14s\n",
            "ID", "Product Name", "Qty", "Unit Price", "Total Value"));
        sb.append("-".repeat(70)).append("\n");
        for (Product p : all) {
            sb.append(String.format("%-6d %-25s %-10d $%-11.2f $%-13.2f\n",
                p.getId(), p.getName(), p.getCurrentStock(),
                p.getUnitPrice(), p.getTotalValue()));
        }
        sb.append("\n").append("-".repeat(70)).append("\n");
        sb.append(String.format("TOTAL INVENTORY VALUE: $%.2f\n",
            inventoryService.getTotalInventoryValue()));
        return sb.toString();
    }

    public String generateTransactionReport() {
        List<Transaction> txs = inventoryService.getAllTransactions();
        StringBuilder sb = new StringBuilder();
        sb.append("=== TRANSACTION HISTORY ===\n\n");
        if (txs.isEmpty()) {
            sb.append("No transactions recorded yet.\n");
        } else {
            sb.append(String.format("%-6s %-20s %-12s %-6s %-22s %-15s\n",
                "ID", "Product", "Type", "Qty", "Timestamp", "By"));
            sb.append("-".repeat(85)).append("\n");
            for (Transaction t : txs) {
                sb.append(String.format("%-6d %-20s %-12s %-6d %-22s %-15s\n",
                    t.getId(), t.getProduct().getName(), t.getType().getLabel(),
                    t.getQuantity(), t.getFormattedTimestamp(), t.getPerformedBy()));
            }
        }
        sb.append("\nTotal transactions: ").append(txs.size());
        return sb.toString();
    }
}
