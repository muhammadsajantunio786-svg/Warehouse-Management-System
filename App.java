package ui;

import service.*;

import javax.swing.*;
import java.awt.Font;

/**
 * Application entry point.
 * Wires all services together and launches the GUI.
 */
public class App {

    public static void main(String[] args) {
        // Apply a nicer L&F with larger, clearer fonts for readability
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("PasswordField.font", uiFont);
        UIManager.put("TextArea.font", uiFont);
        UIManager.put("Table.font", uiFont);
        UIManager.put("TableHeader.font", uiFont.deriveFont(Font.BOLD, 13f));
        UIManager.put("TabbedPane.font", uiFont.deriveFont(Font.BOLD, 13f));
        UIManager.put("Menu.font", uiFont);
        UIManager.put("MenuItem.font", uiFont);
        UIManager.put("OptionPane.messageFont", uiFont);
        UIManager.put("OptionPane.buttonFont", uiFont);

        SwingUtilities.invokeLater(App::launch);
    }

    /** Creates services and shows the login dialog. Can be called multiple times (re-login). */
    public static void launch() {
        // Service composition
        DatabaseService     dbService     = new DatabaseService();
        NotificationService notifService  = new NotificationService();
        InventoryService    invService    = new InventoryService(notifService, dbService);
        AuthService         authService   = new AuthService();
        ReportService       reportService = new ReportService(invService);

        // Login dialog
        LoginDialog login = new LoginDialog(null, authService);
        login.setVisible(true);

        if (!login.isLoginSuccess()) {
            System.exit(0);
        }

        // Main window
        MainWindow window = new MainWindow(invService, authService, notifService, reportService);
        window.setVisible(true);
    }
}
