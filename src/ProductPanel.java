import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    public ProductPanel() {

        setLayout(new BorderLayout());
        setOpaque(false);

        // === LEFT NAVIGATION BAR ===

        //Navigation Bar Formatting
        JPanel navBar = new GradientPanel(); // Navigation Bar Background Gradient
        navBar.setPreferredSize(new Dimension(150, 0));
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setOpaque(false);
        navBar.add(Box.createVerticalStrut(62)); // Navigation Bar Space Above Buttons

        //Navigation Bar Button Creation
        selectBtn = new JButton("Select"); //Instantiate Select Button
        JButton showAllBtn = new JButton("Show All"); //Instantiate Show All Button


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{selectBtn, showAllBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setPreferredSize(new Dimension(120, 40));
            btn.setMinimumSize(new Dimension(120, 40));
            btn.setMaximumSize(new Dimension(120, 40));
            btn.setBackground(DARK_BG);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        //Add Navigation Buttons to Navigation Bar
        navBar.add(selectBtn);
        navBar.add(Box.createVerticalStrut(10)); //Spacing
        navBar.add(showAllBtn);
        navBar.add(Box.createVerticalStrut(10));


        //Add Navigation Bar to Panel
        add(navBar, BorderLayout.WEST);

        // === MAIN CONTENT PANEL ===

        JPanel mainContent = new JPanel(new BorderLayout()); //Instantiate main panel
        mainContent.setOpaque(false);

        // === SEARCH PANEL ===

        JPanel searchPanel = new JPanel(); //Instantiate new panel

        //Search Panel Button Formatting
        searchPanel.setBackground(new Color(230, 255, 245));
        searchField = new JTextField(20);
        searchBtn = new JButton("Search Product By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("Product ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);


        // === TABLE FORMATTING ===
        tableModel = new DefaultTableModel(new String[]{
            "Product_ID", "Name", "Description", "Status", "Version", "Department_ID"
        }, 0);
        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setBackground(DARK_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);

        // === TABLE BORDER FORMATTING ===

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        GradientPanel eastBorder = new GradientPanel();
        eastBorder.setPreferredSize(new Dimension(20, 0));
        tableWrapper.add(eastBorder, BorderLayout.EAST);

        // === BOTTOM PANEL AND INPUTS ===

        JPanel inputPanel = new JPanel(); // Instantiate new panel
        inputPanel.setBackground(BOT_GRADIENT);
        inputPanel.setPreferredSize(new Dimension(0, 80)); // Increased height for space
        inputPanel.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better positioning

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton addBtn = new JButton("Add Product"); // Instantiate Add Button
        addBtn.setBackground(DARK_BG);
        addBtn.setForeground(Color.WHITE);
    
        // Add buttons to the button panel
        buttonPanel.add(addBtn);

        // Add panels to the input panel
        inputPanel.add(buttonPanel, BorderLayout.NORTH); // Buttons at the top
    

        // Add action listeners
        selectBtn.addActionListener(e -> openEditDialog());

        // Add the input panel to the bottom of the table wrapper
        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        searchBtn.addActionListener(e -> searchProduct());
        showAllBtn.addActionListener(e -> loadAllProducts());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllProducts();
    }


    // === This method just deals with the gradient and background ===

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

    // === Gradient Panel for above method ===

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

    // === Creates empty rows for design ===

       private void padTableRows(int minRows) {
        int currentRows = tableModel.getRowCount();
        while (currentRows < minRows) {
            tableModel.addRow(new Object[]{"", "", "", ""});
            currentRows++;
        }
    }

    // === Error Message ===

       private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // === CALLED WHEN SEARCH BUTTON PRESSED, CONTROLS SEARCH PROCESS ===

    private void searchProduct() {
        String productId = searchField.getText().trim();
        if (productId.isEmpty()) {
            showError("Please enter a Product ID to search.");
            return;
        }

        tableModel.setRowCount(0);
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
                showError("No Product found with Product ID: " + productId);
            }
        } catch (Exception ex) {
            showError("Error searching for Product: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PRODUCT DETAILS ===

    private void loadAllProducts() {
        tableModel.setColumnIdentifiers(new String[]{"Product ID", "Name", "Description", "Status", "Version", "Department ID"});
        tableModel.setRowCount(0);
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

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

    // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the product
        String[] fieldNames = {"Name", "Description", "Status", "Version", "Department_ID"};
        JTextField[] wizardFields = new JTextField[fieldNames.length];

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Add padding between fields
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Add fields to the wizard
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            fieldsPanel.add(new JLabel(fieldName + ":"));
            JTextField field = new JTextField();
            wizardFields[i] = field;
            fieldsPanel.add(field);
        }

        JButton finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Generate a unique Product_ID
                String maxIdQuery = "SELECT MAX(CAST(SUBSTRING(Product_ID, 4) AS UNSIGNED)) AS MaxID FROM PRODUCT";
                ResultSet maxIdResult = conn.createStatement().executeQuery(maxIdQuery);
                String productId = "Pr-";
                
                if (maxIdResult.next()) {
                    int maxId = maxIdResult.getInt("MaxID");
                    productId += String.format("%03d", maxId + 1);  // Increment the maximum ID and format
                } else {
                    productId += "001";  // Start from 001 if no records exist
                }

                // Build SQL query
                String sql = "INSERT INTO PRODUCT (Product_ID, Name, Description, Status, Version, Department_ID) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set Product_ID
                ps.setString(1, productId);

                // Set other fields
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                    loadAllProducts(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding product: " + ex.getMessage());
            }
        });

        wizard.add(fieldsPanel, BorderLayout.CENTER);
        wizard.add(finishButton, BorderLayout.SOUTH);
        wizard.setVisible(true);
    }

      // === CALLED WHEN SELECT BUTTON PRESSED, CONTROLS MODAL ===

      private void openEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a row to edit.");
            return;
        }
    
        // Get visible values from table
        String productID = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String desc = (String) tableModel.getValueAt(selectedRow, 2);
        String status = (String) tableModel.getValueAt(selectedRow, 3);
        String version = (String) tableModel.getValueAt(selectedRow, 4);
    
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM PRODUCT WHERE Product_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                showError("Could not retrieve full record.");
                return;
            }
    
            String fullProductId = rs.getString("Product_ID");
            String deptId = rs.getString("Department_ID");
    
            // === Build Modal Dialog ===
            JLabel idField = new JLabel(fullProductId);
            JTextField deptField = new JTextField(deptId);
            JTextField nameField = new JTextField(name);
            JTextField descField = new JTextField(desc);
            JTextField statusField = new JTextField(status);
            JTextField versionField = new JTextField(version);
    
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Product ID:")); panel.add(idField);
            panel.add(new JLabel("Department ID:")); panel.add(deptField);
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Description:")); panel.add(descField);
            panel.add(new JLabel("Status:")); panel.add(statusField);
            panel.add(new JLabel("Version:")); panel.add(versionField);
    
            Object[] options = {"Update", "Delete", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, panel, "Edit Product",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
    
            if (result == JOptionPane.YES_OPTION) {
                // Update
                updateProduct(
                    fullProductId,
                    deptField.getText().trim(),
                    nameField.getText().trim(),
                    descField.getText().trim(),
                    statusField.getText().trim(),
                    versionField.getText().trim()
                );

            } else if (result == JOptionPane.NO_OPTION) {
                // Delete
                deleteProduct(fullProductId);
            }
    
        } catch (Exception ex) {
            showError("Error retrieving product: " + ex.getMessage());
        }
    }

    // === CALLED WHEN EDIT SAVED PRESSED IN MODAL ===

    private void updateProduct(String originalId, String deptId, String name, String desc, String status, String version) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE PRODUCT SET Department_ID=?, Name=?, Description=?, Status=?, Version=? WHERE Product_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, deptId);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.setString(4, status);
            stmt.setString(5, version);
            stmt.setString(6, originalId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully.");
                loadAllProducts();
            } else {
                showError("Update failed. Product not found.");
            }
        } catch (Exception ex) {
            showError("Error updating product: " + ex.getMessage());
        }
    }
    

    // === CALLED WHEN DELETE BUTTON PRESSED IN MODAL ===

    private void deleteProduct(String productId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM PRODUCT WHERE Product_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                loadAllProducts();
            } else {
                showError("Delete failed. Product not found.");
            }
        } catch (Exception ex) {
            showError("Error deleting product: " + ex.getMessage());
        }
    }


}