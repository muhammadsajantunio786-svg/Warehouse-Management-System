package ui;

import service.ReportService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for viewing text-based reports (low stock, valuation, transactions).
 */
public class ReportsPanel extends JPanel {
    private final ReportService reportService;
    private JTextArea reportArea;

    public ReportsPanel(ReportService reportService) {
        this.reportService = reportService;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton lowStockBtn  = new JButton("📉 Low Stock Report");
        JButton valBtn       = new JButton("💰 Inventory Valuation");
        JButton txnBtn       = new JButton("🧾 Transaction History");

        lowStockBtn.setBackground(new Color(0xB71C1C));
        lowStockBtn.setForeground(Color.WHITE);
        valBtn.setBackground(new Color(0x1565C0));
        valBtn.setForeground(Color.WHITE);
        txnBtn.setBackground(new Color(0x2E7D32));
        txnBtn.setForeground(Color.WHITE);

        JLabel headerLabel = new JLabel("Generate:");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPanel.add(headerLabel);
        btnPanel.add(lowStockBtn);
        btnPanel.add(valBtn);
        btnPanel.add(txnBtn);
        add(btnPanel, BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportArea.setBackground(new Color(0xFCFCFC));
        reportArea.setEditable(false);
        reportArea.setText("Select a report above to generate it.");
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        lowStockBtn.addActionListener(e -> reportArea.setText(reportService.generateLowStockReport()));
        valBtn.addActionListener(e ->      reportArea.setText(reportService.generateInventoryValueReport()));
        txnBtn.addActionListener(e ->      reportArea.setText(reportService.generateTransactionReport()));
    }
}
