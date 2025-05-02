import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OfficePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchBtn, showAllBtn;

    public OfficePanel() {
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{
            "Office_ID", "Name", "Location"
        }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchBtn = new JButton("Search Office");
        showAllBtn = new JButton("Show All");

        searchPanel.add(new JLabel("Office ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Button actions
        searchBtn.addActionListener(e -> searchOffice());
        showAllBtn.addActionListener(e -> loadAllOffices());

        // Initial data load
        loadAllOffices();
    }

    private void searchOffice() {
        String officeId = searchField.getText().trim();
        if (officeId.isEmpty()) {
            showError("Please enter an Office ID to search.");
            return;
        }

        tableModel.setRowCount(0); // Clear table

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Office_ID, Name, Location FROM OFFICE WHERE Office_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, officeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Office_ID"),
                    rs.getString("Name"),
                    rs.getString("Location")
                });
            } else {
                showError("No office found with Office ID: " + officeId);
            }
        } catch (Exception ex) {
            showError("Error searching office: " + ex.getMessage());
        }
    }

    private void loadAllOffices() {
        tableModel.setRowCount(0); // Clear table

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Office_ID, Name, Location FROM OFFICE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Office_ID"),
                    rs.getString("Name"),
                    rs.getString("Location")
                });
            }
        } catch (Exception ex) {
            showError("Error loading offices: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
