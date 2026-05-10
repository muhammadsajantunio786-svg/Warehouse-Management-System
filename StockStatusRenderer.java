package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom TableCellRenderer that colours rows red (out of stock) or orange (low stock).
 * Demonstrates polymorphism — overrides DefaultTableCellRenderer.
 */
public class StockStatusRenderer extends DefaultTableCellRenderer {
    private static final Color OUT_OF_STOCK_COLOR = new Color(0xFF, 0xCC, 0xCC);  // light red
    private static final Color LOW_STOCK_COLOR     = new Color(0xFF, 0xE0, 0x80);  // light yellow
    private static final Color OK_COLOR            = Color.WHITE;

    // Column index where the "Status" string lives in the product table
    private final int statusColumnIndex;

    public StockStatusRenderer(int statusColumnIndex) {
        this.statusColumnIndex = statusColumnIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Object statusVal = table.getValueAt(row, statusColumnIndex);
            String status = statusVal != null ? statusVal.toString() : "";
            if ("OUT OF STOCK".equals(status)) {
                c.setBackground(OUT_OF_STOCK_COLOR);
            } else if ("LOW STOCK".equals(status)) {
                c.setBackground(LOW_STOCK_COLOR);
            } else {
                c.setBackground(OK_COLOR);
            }
        }
        return c;
    }
}
