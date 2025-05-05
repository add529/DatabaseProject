import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Assets extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    public Assets() {

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
        JButton showDates = new JButton("Dates"); //Instantiate Type and Pay Button
        JButton showEmp = new JButton("Employees"); //Instantiate Type and Pay Button


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showDates, showEmp, selectBtn, showAllBtn}) {
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
        navBar.add(showDates); // This can be changed to show Employlees in certain pay groups
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showEmp); // This can be changed to show Employlees in certain pay groups
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
        searchBtn = new JButton("Search Asset By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("Asset ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===
        tableModel = new DefaultTableModel(new String[]{
            "Asset_ID", "Serial_No", "Type", "asset_cond", "brand_model"
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
        JButton addBtn = new JButton("Add Asset"); // Instantiate Add Button
        addBtn.setBackground(DARK_BG);
        addBtn.setForeground(Color.WHITE);

        // Add buttons to the button panel
        buttonPanel.add(addBtn);
         // Add panels to the input panel
         inputPanel.add(buttonPanel, BorderLayout.NORTH); // Buttons at the top
       
        // Add the input panel to the bottom of the table wrapper
        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        selectBtn.addActionListener(e -> openEditDialog());        
        searchBtn.addActionListener(e -> searchAssets());
        showDates.addActionListener(e -> showDates());
        showEmp.addActionListener(e -> showEmp());
        showAllBtn.addActionListener(e -> loadAllAssets());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllAssets();
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

    private void searchAssets() {
        String payGroupNo = searchField.getText().trim();
        if (payGroupNo.isEmpty()) { //If nothing in search, error message shows
            showError("Please enter an Asset ID to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT Asset_ID, Serial_No, Type, asset_cond, brand_model FROM ASSET WHERE Asset_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, payGroupNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Asset_ID"),
                    rs.getString("Serial_No"),
                    rs.getString("Type"),
                    rs.getString("asset_cond"),
                    rs.getString("brand_model")
                });
            } else {
                showError("No Asset found with Asset ID: " + payGroupNo);
            }
        } catch (Exception ex) {
            showError("Error searching for asset: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PAY GROUP DETAILS ===

    private void loadAllAssets() {

        tableModel.setColumnIdentifiers(new String[]{"Asset ID", "Serial No", "Type", "Condition", "Brand/Model"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Asset_ID, Serial_No, Type, asset_cond, brand_model FROM ASSET";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Asset_ID"),
                    rs.getString("Serial_No"),
                    rs.getString("Type"),
                    rs.getString("asset_cond"),
                    rs.getString("brand_model")
                });
            }
        } catch (Exception ex) {
            showError("Error loading assets: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        private void showEmp() {
            tableModel.setColumnIdentifiers(new String[]{"Asset ID", "Type", "Employee No", "First Name", "Last Name"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                    SELECT 
                        a.Asset_ID,
                        a.Type, 
                        e.Employee_No, 
                        e.FName, 
                        e.LName
                    FROM 
                        ASSET a
                    JOIN 
                        EMPLOYEE e ON a.Employee_No = e.Employee_No
                """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Asset_ID"),
                        rs.getString("Type"),
                        rs.getString("Employee_No"),
                        rs.getString("FName"),
                        rs.getString("LName")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading asset-employee data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }
    

        private void showDates() {
            tableModel.setColumnIdentifiers(new String[]{"Asset ID", "Purchase Date", "Warranty Exp Date"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                            SELECT 
                                Asset_ID,
                                Purchase_Date,
                                Warrant_Exp_Date
                            FROM 
                                ASSET
                        """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Asset_ID"),
                        rs.getString("Purchase_Date"),
                        rs.getString("Warrant_Exp_Date"),
                    });
                }
            } catch (Exception ex) {
                showError("Error loading asset date data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }
    


    // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Asset", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the pay group
        String[] fieldNames = {"Serial No", "Type", "Condition", "Brand/Model", "Purchase Date", "Warranty Exp Date"};
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
                // Generate a unique Paygroup ID
                String maxIdQuery = "SELECT MAX(CAST(SUBSTRING(Asset_ID, 4) AS UNSIGNED)) AS MaxID FROM ASSET";
                ResultSet maxIdResult = conn.createStatement().executeQuery(maxIdQuery);
                String aID = "A-";
                
                if (maxIdResult.next()) {
                    int maxId = maxIdResult.getInt("MaxID");
                    aID += String.format("%03d", maxId + 1);  // Increment the maximum ID and format
                } else {
                    aID += "001";  // Start from 001 if no records exist
                }

                // Build SQL query
                String sql = "INSERT INTO ASSET (Asset_ID, Serial_No, Type, asset_cond, brand_model, Purchase_Date, Warrant_Exp_Date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set PaygroupID
                ps.setString(1, aID);

                // Set other fields
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Asset added successfully!");
                    loadAllAssets(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding asset: " + ex.getMessage());
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
        String aID = (String) tableModel.getValueAt(selectedRow, 0);
        String sNo = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 2);
        String cond = (String) tableModel.getValueAt(selectedRow, 3);
        String brand = (String) tableModel.getValueAt(selectedRow, 4);
    
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM ASSET WHERE Asset_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, aID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                showError("Could not retrieve full record.");
                return;
            }
    
            String fullaID = rs.getString("Asset_ID");
            String purchaseD = rs.getString("Purchase_Date");
            String warrantD = rs.getString("Warrant_Exp_Date");
    
            // === Build Modal Dialog ===
            JLabel idField = new JLabel(fullaID);
            JTextField serialField = new JTextField(sNo);
            JTextField typeField = new JTextField(type);
            JTextField condField = new JTextField(cond);
            JTextField brandField = new JTextField(brand);
            JTextField purchaseField = new JTextField(purchaseD);
            JTextField warrantyField = new JTextField(warrantD);
    
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Asset ID:")); panel.add(idField);
            panel.add(new JLabel("Serial No:")); panel.add(serialField);
            panel.add(new JLabel("Type:")); panel.add(typeField);
            panel.add(new JLabel("Condition:")); panel.add(condField);
            panel.add(new JLabel("Brand/Model:")); panel.add(brandField);
            panel.add(new JLabel("Purchase Date:")); panel.add(purchaseField);
            panel.add(new JLabel("Warranty Exp Date:")); panel.add(warrantyField);
    
            Object[] options = {"Update", "Delete", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, panel, "Edit Asset",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
    
            if (result == JOptionPane.YES_OPTION) {
                // Update
                updateAsset(
                    fullaID,
                    serialField.getText().trim(),
                    typeField.getText().trim(),
                    brandField.getText().trim(),
                    purchaseField.getText().trim(),
                    warrantyField.getText().trim()
                );

            } else if (result == JOptionPane.NO_OPTION) {
                // Delete
                deleteAsset(fullaID);
            }
    
        } catch (Exception ex) {
            showError("Error retrieving asset: " + ex.getMessage());
        }
    }

    // === CALLED WHEN EDIT SAVED PRESSED IN MODAL ===

    private void updateAsset(String originalId, String serial_no, String type, String brand, String purchase, String warranty) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE ASSET SET Serial_No=?, Type=?, brand_model=?, Purchase_Date=?, Warrant_Exp_Date=? WHERE Asset_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, serial_no);
            stmt.setString(2, type);
            stmt.setString(3, brand);
            stmt.setString(4, purchase);
            stmt.setString(5, warranty);
            stmt.setString(6, originalId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Asset updated successfully.");
                loadAllAssets();
            } else {
                showError("Update failed. Asset not found.");
            }
        } catch (Exception ex) {
            showError("Error updating asset: " + ex.getMessage());
        }
    }
    

    // === CALLED WHEN DELETE BUTTON PRESSED IN MODAL ===

    private void deleteAsset(String aID) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this asset?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM ASSET WHERE Asset_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, aID);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Asset deleted successfully.");
                loadAllAssets();
            } else {
                showError("Delete failed. Asset not found.");
            }
        } catch (Exception ex) {
            showError("Error deleting asset: " + ex.getMessage());
        }
    }

}