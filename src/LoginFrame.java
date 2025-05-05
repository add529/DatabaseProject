import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final Color TOP_GRADIENT = new Color(0x9ed7cf);
    private final Color BOT_GRADIENT = new Color(0xd0e8bd);

    public LoginFrame() {

        //Window settings

        setResizable(false); //Makes window not resizable for formatting reasons
        setTitle("Company Admin Login");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Gradient Panel
        GradientPanel gradientPanel = new GradientPanel();
        gradientPanel.setLayout(new BorderLayout());
        setContentPane(gradientPanel);

        // ===== Title Panel =====
        JLabel titleLabel = new JLabel("Welcome to MySequel Solutions Company Portal!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        gradientPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false); // transparent to show gradient
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 20, 0)); // top padding

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        gradientPanel.add(titlePanel, BorderLayout.PAGE_START);

        // ===== Center Form Panel (White Rounded Box) =====
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setOpaque(true);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(255, 255, 255, 200)); // white with ~80% opacity
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setPreferredSize(new Dimension(400, 400)); // increased height of white rectangle
        formPanel.setMaximumSize(new Dimension(400, 400)); // increased height of white rectangle
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add the input components to the form panel
        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField(23);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField(23);
        JButton loginButton = new JButton("Login");
        
        Dimension fieldSize = new Dimension(300, 35); // wider and taller fields
        usernameField.setMaximumSize(fieldSize);
        usernameField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);

        // Reset label colors since they're on white now
        userLabel.setForeground(Color.DARK_GRAY);
        passLabel.setForeground(Color.DARK_GRAY);

        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the form panel
        formPanel.add(Box.createVerticalStrut(30)); // Adds 30px vertical space at the top
        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(loginButton);

        // Wrap form panel in a transparent panel to help center it
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setOpaque(false);
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.add(Box.createVerticalGlue());
        wrapperPanel.add(formPanel);
        wrapperPanel.add(Box.createRigidArea(new Dimension(0, 80))); // Pushes it up

        gradientPanel.add(wrapperPanel, BorderLayout.CENTER);

        // ===== Footer Panel with Version Info =====
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false); // So the gradient shows through
        JLabel versionLabel = new JLabel("version 1.0");
        versionLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        versionLabel.setForeground(Color.BLACK);
        footerPanel.add(versionLabel);

        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        // Connect button to login
        loginButton.addActionListener(e -> handleLogin());

        getRootPane().setDefaultButton(loginButton); //allows pressing enter to login

    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (validateLogin(username, password)) {
            // Login successful
            SwingUtilities.invokeLater(() -> {
                new MainFrame().setVisible(true);
                this.dispose(); // Close login window
            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM LOGIN WHERE Username = ? AND Password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In production, use hashed passwords!

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If a record exists, credentials are correct

        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Gradient panel for background
    class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, TOP_GRADIENT, 0, height, BOT_GRADIENT);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
        }
    }
}
