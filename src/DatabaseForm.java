import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DatabaseForm extends JFrame {
    private JButton saveButton;

    public DatabaseForm() {
        setTitle("Placeholder Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Just a button in the center for now
        saveButton = new JButton("Save to Database");
        add(saveButton, BorderLayout.CENTER);

        saveButton.addActionListener(e -> saveEmployee());
    }

    // Placeholder save logic, connects to database
    private void saveEmployee() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Employee (first_name) VALUES (?)"; // placeholder query
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "Placeholder");
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Saved placeholder data to database!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
