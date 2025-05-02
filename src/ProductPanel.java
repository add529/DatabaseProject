import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton showAllBtn;

    public ProductPanel() {
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{
            "Product_ID", "Name", "Description", "Status", "Version", "Department_ID"
        }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchBtn = new JButton("Search Product");
        showAllBtn = new JButton("Show All");

        searchPanel.add(new JLabel("Product ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Button actions
        searchBtn.addActionListener(e -> searchProduct());
        showAllBtn.addActionListener(e -> loadAllProducts());

        // Initial data load
        loadAllProducts();
    }

    private void searchProduct() {
        String productId = searchField.getText().trim();
        if (productId.isEmpty()) {
            showError("Please enter a Product ID to search.");
            return;
        }

        tableModel.setRowCount(0); // Clear table

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Product_ID, Name, Description, Status, Version, Department_ID FROM PRODUCT WHERE Product_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Product_ID"),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Status"),
                    rs.getString("Version"),
                    rs.getString("Department_ID")
                });
            } else {
                showError("No product found with Product ID: " + productId);
            }
        } catch (Exception ex) {
            showError("Error searching product: " + ex.getMessage());
        }
    }

    private void loadAllProducts() {
        tableModel.setRowCount(0); // Clear table

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Product_ID, Name, Description, Status, Version, Department_ID FROM PRODUCT";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Product_ID"),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Status"),
                    rs.getString("Version"),
                    rs.getString("Department_ID")
                });
            }
        } catch (Exception ex) {
            showError("Error loading products: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
