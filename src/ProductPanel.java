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

    private String[] fieldNames = {
        "PayGroup_ID", "SSN", "FName", "MName", "LName", "DOB", "Address", "Sex",
        "Nationality", "Ethnic_ID", "Marital_Status", "Disability_Status", "Location",
        "Status", "Cost_Center", "Seniority", "Job_Code", "Job_Desc", "Last_Hired",
        "SuperSSN", "Product_ID", "Department_ID", "Employee_Type", "Pay_Group", "Office_ID"
    };

    private JTextField NameField, DescriptionField, statusField, versionField, nameField, productIdField, departmentIdField;
    private JButton editBtn, saveBtn;

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
        JButton showEmpPay = new JButton("Type and Pay"); //Instantiate Type and Pay Button


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showEmpPay, selectBtn, showAllBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        navBar.add(showEmpPay); // This can be changed to show Employlees in certain pay groups
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
        searchBtn = new JButton("Search Products By ID");
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
        table.setForeground(Color.GRAY);
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
        inputPanel.setPreferredSize(new Dimension(0, 200)); // Increased height for space
        inputPanel.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better positioning

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton addBtn = new JButton("Add"); // Instantiate Add Button
        editBtn = new JButton("Edit");
        saveBtn = new JButton("Update");
        saveBtn.setVisible(false); // Initially hide the Update button

        // Style buttons to match Add Button
        for (JButton btn : new JButton[]{addBtn, editBtn, saveBtn}) {
            btn.setBackground(DARK_BG);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(100, 30)); // Set uniform size
        }

        // Add buttons to the button panel
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(saveBtn);

        // Initialize the text fields and assign them to class-level variables
        productIdField = new JTextField(15);
        NameField = new JTextField(15); // Assign to class-level variable
        DescriptionField = new JTextField(15); // Assign to class-level variable
        statusField = new JTextField(15); // Assign to class-level variable
        versionField = new JTextField(15); // Assign to class-level variable
        departmentIdField = new JTextField(15);
        departmentIdField.setEditable(false); // Make Department_ID field non-editable

        // Create a panel for input fields and labels
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Grid layout for labels and fields
        fieldsPanel.setOpaque(false); // Make it transparent
        fieldsPanel.setVisible(false); // Initially hidden

        // Add fields and labels to the fields panel
        fieldsPanel.add(new JLabel("Product ID:"));
        fieldsPanel.add(productIdField);
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(NameField);
        fieldsPanel.add(new JLabel("Description:"));
        fieldsPanel.add(DescriptionField);
        fieldsPanel.add(new JLabel("Status:"));
        fieldsPanel.add(statusField);
        fieldsPanel.add(new JLabel("Version:"));
        fieldsPanel.add(versionField);
        fieldsPanel.add(new JLabel("Department ID:"));
        fieldsPanel.add(departmentIdField);

        // Add panels to the input panel
        inputPanel.add(buttonPanel, BorderLayout.NORTH); // Buttons at the top
        inputPanel.add(fieldsPanel, BorderLayout.CENTER); // Fields below the buttons

        // Add action listeners
        selectBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a row in the table first.");
                return;
            }

            // Populate the input fields with the selected row's data
            productIdField.setText((String) tableModel.getValueAt(selectedRow, 0));
            NameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            DescriptionField.setText((String) tableModel.getValueAt(selectedRow, 2));
            statusField.setText((String) tableModel.getValueAt(selectedRow, 3));
            versionField.setText((String) tableModel.getValueAt(selectedRow, 4));
            departmentIdField.setText((String) tableModel.getValueAt(selectedRow, 5));

            // Make the fields visible but not editable
            fieldsPanel.setVisible(true);
            productIdField.setEditable(false);
            NameField.setEditable(false);
            DescriptionField.setEditable(false);
            statusField.setEditable(false);
            versionField.setEditable(false);
            departmentIdField.setEditable(false);

            saveBtn.setVisible(false); // Hide the Update button
        });

        editBtn.addActionListener(e -> {
            // Make fields editable and show the Update button
            fieldsPanel.setVisible(true);
            NameField.setEditable(true);
            DescriptionField.setEditable(true);
            statusField.setEditable(true);
            versionField.setEditable(true);
            departmentIdField.setEditable(true);
            saveBtn.setVisible(true); // Show the Update button
        });

        saveBtn.addActionListener(e -> updateProduct());

        // Add the input panel to the bottom of the table wrapper
        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        searchBtn.addActionListener(e -> searchProduct());
        showEmpPay.addActionListener(e -> standIn());
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
        tableModel.setColumnIdentifiers(new String[]{"Product_ID", "Name", "Description", "Status", "Version", "Department_ID"});
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

    // === CALLED WHEN DEPARTMENTS PRESSED, SHOWS PRODUCT TO DEPARTMENT DETAILS ===

    private void standIn() {

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
                String productId = "Pr-";
                String countQuery = "SELECT COUNT(*) AS Total FROM PRODUCT";
                ResultSet rs = conn.createStatement().executeQuery(countQuery);
                if (rs.next()) {
                    int count = rs.getInt("Total") + 1;
                    productId += String.format("%03d", count); // Format as P-XXX
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

    // === Update the database when the "Update" button is clicked ===
    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a Product to update.");
            return;
        }

        // Get the original values from the table
        String originalProductID = (String) tableModel.getValueAt(selectedRow, 0);
        String originalName = (String) tableModel.getValueAt(selectedRow, 1);
        String originalDescription = (String) tableModel.getValueAt(selectedRow, 2);
        String originalStatus = (String) tableModel.getValueAt(selectedRow, 3);
        String originalVersion = (String) tableModel.getValueAt(selectedRow, 4);
        String originalDepartmentID = (String) tableModel.getValueAt(selectedRow, 5);

        // Get the updated values from the input fields
        String updatedName = NameField.getText();
        String updatedDescription = DescriptionField.getText();
        String updatedStatus = statusField.getText();
        String updatedVersion = versionField.getText();
        String updatedDepartmentID = nameField.getText(); // Assuming this is the Department_ID field

        // Check if any changes were made
        if (originalName.equals(updatedName) &&
            originalDescription.equals(updatedDescription) &&
            originalStatus.equals(updatedStatus) &&
            originalVersion.equals(updatedVersion) &&
            originalDepartmentID.equals(updatedDepartmentID)) {
            showError("No changes made.");
            return;
        }

        // Update the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE PRODUCT SET Name = ?, Description = ?, Status = ?, Version = ?, Department_ID = ? WHERE Product_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, updatedName);
            ps.setString(2, updatedDescription);
            ps.setString(3, updatedStatus);
            ps.setString(4, updatedVersion);
            ps.setString(5, updatedDepartmentID);
            ps.setString(6, originalProductID);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                loadAllProducts(); // Refresh the table
            } else {
                showError("Failed to update Product.");
            }
        } catch (Exception ex) {
            showError("Error updating Product: " + ex.getMessage());
        }

        // Disable editing after update
        NameField.setEditable(false);
        DescriptionField.setEditable(false);
        statusField.setEditable(false);
        versionField.setEditable(false);
        nameField.setEditable(false);

        saveBtn.setEnabled(false); // Disable the Update button
    }
}