package ui;

import service.AuthService;
import exception.AuthenticationException;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login dialog shown at startup.
 */
public class LoginDialog extends JDialog {
    private final AuthService authService;
    private boolean loginSuccess = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginDialog(Frame parent, AuthService authService) {
        super(parent, "WMS — Login", true);
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        setSize(400, 260);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Warehouse Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0x1565C0));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(userLabel);
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(passLabel);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(passwordField);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        form.add(new JLabel());
        form.add(statusLabel);
        main.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBackground(new Color(0x1565C0));
        loginBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Exit");
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);

        JLabel hint = new JLabel("Tip: use admin/admin123 or worker/worker123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(0x666666));

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.add(btnPanel, BorderLayout.NORTH);
        south.add(hint, BorderLayout.SOUTH);
        main.add(south, BorderLayout.SOUTH);

        add(main);

        loginBtn.addActionListener(e -> attemptLogin());
        cancelBtn.addActionListener(e -> dispose());
        passwordField.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void attemptLogin() {
        try {
            authService.login(usernameField.getText().trim(),
                              new String(passwordField.getPassword()));
            loginSuccess = true;
            dispose();
        } catch (AuthenticationException ex) {
            statusLabel.setText(ex.getMessage());
            passwordField.setText("");
        }
    }

    public boolean isLoginSuccess() { return loginSuccess; }
}
