package ui;

import service.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with tabbed navigation.
 */
public class MainWindow extends JFrame {
    private final InventoryService inventoryService;
    private final AuthService authService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    private DashboardPanel dashboardPanel;
    private InventoryPanel inventoryPanel;
    private AlertsPanel alertsPanel;

    public MainWindow(InventoryService inventoryService, AuthService authService,
                      NotificationService notificationService, ReportService reportService) {
        this.inventoryService = inventoryService;
        this.authService = authService;
        this.notificationService = notificationService;
        this.reportService = reportService;
        buildUI();
    }

    private void buildUI() {
        setTitle("Warehouse Management System");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem signOutItem = new JMenuItem("Sign Out");
        JMenuItem exitItem    = new JMenuItem("Exit App");
        signOutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(signOutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Warehouse Management System\nOOP Semester Project\n\nJava Swing | File Persistence | Observer Pattern",
            "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        dashboardPanel = new DashboardPanel(inventoryService, authService);
        inventoryPanel = new InventoryPanel(inventoryService, authService, this::onStockChanged);
        alertsPanel    = new AlertsPanel(inventoryService, notificationService);

        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("📊 Dashboard",  dashboardPanel);
        tabs.addTab("📦 Inventory",  inventoryPanel);
        tabs.addTab("🔔 Alerts",     alertsPanel);
        tabs.addTab("📋 Reports",    new ReportsPanel(reportService));

        add(tabs, BorderLayout.CENTER);

        // Status bar
        JLabel statusBar = new JLabel(
            "  Welcome, " + authService.getCurrentUser().getSummary() + "  |  WMS Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    private void onStockChanged() {
        dashboardPanel.refresh();
        alertsPanel.loadAlerts();
    }

    private void logout() {
        authService.logout();
        dispose();
        // Relaunch login
        SwingUtilities.invokeLater(App::launch);
    }
}
