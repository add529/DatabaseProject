import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PayPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    public PayPanel() {

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
        JButton showEmpPay = new JButton("Employees"); //Instantiate Type and Pay Button
        JButton showPayET = new JButton("Type and Pay"); //Instantiate Type and Pay Button


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showPayET, showEmpPay, selectBtn, showAllBtn}) {
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
        navBar.add(showEmpPay); // This can be changed to show Employlees in certain pay groups
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showPayET);
        navBar.add(Box.createVerticalStrut(10)); //Spacing


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
        searchBtn = new JButton("Search PayGroup By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("PayGroup ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===
        tableModel = new DefaultTableModel(new String[]{
            "PayGroup_ID", "Pay_Rate", "Pay_Frequency", "Pay_Period", "Overtime_Rate", "Name"
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
        JButton addBtn = new JButton("Add PayGroup"); // Instantiate Add Button
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
        searchBtn.addActionListener(e -> searchPayGroup());
        showEmpPay.addActionListener(e -> showEmpPay());
        showPayET.addActionListener(e -> showPayET());
        showAllBtn.addActionListener(e -> loadAllPayGroups());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllPayGroups();
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

    private void searchPayGroup() {
        String payGroupNo = searchField.getText().trim();
        if (payGroupNo.isEmpty()) { //If nothing in search, error message shows
            showError("Please enter an Pay Group ID to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name FROM PAY_GROUP WHERE PayGroup_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, payGroupNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("PayGroup_ID"),
                    rs.getString("Pay_Rate"),
                    rs.getString("Pay_Frequency"),
                    rs.getString("Pay_Period"),
                    rs.getString("Overtime_Rate"),
                    rs.getString("Name")
                });
            } else {
                showError("No Pay Group found with Pay Group ID: " + payGroupNo);
            }
        } catch (Exception ex) {
            showError("Error searching for payGroup: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PAY GROUP DETAILS ===

    private void loadAllPayGroups() {

        tableModel.setColumnIdentifiers(new String[]{"PayGroup ID", "Pay Rate", "Pay Frequency", "Pay Period", "Overtime Rate", "Name"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name FROM PAY_GROUP";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("PayGroup_ID"),
                    rs.getString("Pay_Rate"),
                    rs.getString("Pay_Frequency"),
                    rs.getString("Pay_Period"),
                    rs.getString("Overtime_Rate"),
                    rs.getString("Name")
                });
            }
        } catch (Exception ex) {
            showError("Error loading payGroups: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        private void showEmpPay() {
            tableModel.setColumnIdentifiers(new String[]{"PayGroup ID", "Pay Rate", "Employee No", "First Name", "Last Name"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                    SELECT 
                        p.PayGroup_ID, 
                        p.Pay_Rate, 
                        e.Employee_No, 
                        e.FName, 
                        e.LName
                    FROM 
                        PAY_GROUP p
                    JOIN 
                        EMPLOYEE e ON p.PayGroup_ID = e.Pay_Group
                """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("PayGroup_ID"),
                        rs.getString("Pay_Rate"),
                        rs.getString("Employee_No"),
                        rs.getString("FName"),
                        rs.getString("LName")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading paygroup-employee data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }
    

        private void showPayET() {
            tableModel.setColumnIdentifiers(new String[]{"Pay Group ID", "Pay Group Name", "Employee Type ID", "Employee Type Name"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                            SELECT 
                                p.PayGroup_ID, 
                                p.Name AS "pgName", 
                                e.Employee_Type_ID, 
                                e.Name AS "etName"
                            FROM 
                                PAY_GROUP p
                            JOIN 
                                EMPLOYEE_TYPE e ON p.PayGroup_ID = e.PayGroup_ID
                        """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("PayGroup_ID"),
                        rs.getString("pgName"),
                        rs.getString("Employee_Type_ID"),
                        rs.getString("etName")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading employee type-pay group data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }
    


    // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Pay Group", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the pay group
        String[] fieldNames = {"Pay Rate", "Pay Frequency", "Pay Period", "Overtime Rate", "Name"};
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
                String maxIdQuery = "SELECT MAX(CAST(SUBSTRING(PayGroup_ID, 4) AS UNSIGNED)) AS MaxID FROM PAY_GROUP";
                ResultSet maxIdResult = conn.createStatement().executeQuery(maxIdQuery);
                String pgID = "PG-";
                
                if (maxIdResult.next()) {
                    int maxId = maxIdResult.getInt("MaxID");
                    pgID += String.format("%03d", maxId + 1);  // Increment the maximum ID and format
                } else {
                    pgID += "001";  // Start from 001 if no records exist
                }

                // Build SQL query
                String sql = "INSERT INTO PAY_GROUP (PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set PaygroupID
                ps.setString(1, pgID);

                // Set other fields
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "PayGroup added successfully!");
                    loadAllPayGroups(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding paygroup: " + ex.getMessage());
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
        String pgID = (String) tableModel.getValueAt(selectedRow, 0);
        String payRate = (String) tableModel.getValueAt(selectedRow, 1);
        String payFreq = (String) tableModel.getValueAt(selectedRow, 2);
        String payPer = (String) tableModel.getValueAt(selectedRow, 3);
        String overtime = (String) tableModel.getValueAt(selectedRow, 4);
        String name = (String) tableModel.getValueAt(selectedRow, 5);
    
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM PAY_GROUP WHERE PayGroup_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pgID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                showError("Could not retrieve full record.");
                return;
            }
    
            String fullPGID = rs.getString("PayGroup_ID");
    
            // === Build Modal Dialog ===
            JLabel idField = new JLabel(fullPGID);
            JTextField rateField = new JTextField(payRate);
            JTextField freqField = new JTextField(payFreq);
            JTextField periodField = new JTextField(payPer);
            JTextField overtimeField = new JTextField(overtime);
            JTextField nameField = new JTextField(name);
    
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Pay Group ID:")); panel.add(idField);
            panel.add(new JLabel("Pay Rate:")); panel.add(rateField);
            panel.add(new JLabel("Pay Frequency:")); panel.add(freqField);
            panel.add(new JLabel("Pay Period:")); panel.add(periodField);
            panel.add(new JLabel("Overtime Rate:")); panel.add(overtimeField);
            panel.add(new JLabel("Name:")); panel.add(nameField);
    
            Object[] options = {"Update", "Delete", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, panel, "Edit PayGroup",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
    
            if (result == JOptionPane.YES_OPTION) {
                // Update
                updatePayGroup(
                    fullPGID,
                    rateField.getText().trim(),
                    freqField.getText().trim(),
                    periodField.getText().trim(),
                    overtimeField.getText().trim(),
                    nameField.getText().trim()
                );

            } else if (result == JOptionPane.NO_OPTION) {
                // Delete
                deletePayGroup(fullPGID);
            }
    
        } catch (Exception ex) {
            showError("Error retrieving paygroup: " + ex.getMessage());
        }
    }

    // === CALLED WHEN EDIT SAVED PRESSED IN MODAL ===

    private void updatePayGroup(String originalId, String pay_rate, String pay_freq, String pay_per, String overtime, String name) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE PAY_GROUP SET PayGroup_ID=?, Pay_Rate=?, Pay_Frequency=?, Pay_Period=?, Overtime_Rate=?, Name=? WHERE PayGroup_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(2, pay_rate);
            stmt.setString(3, pay_freq);
            stmt.setString(4, pay_per);
            stmt.setString(5, overtime);
            stmt.setString(6, name);
            stmt.setString(7, originalId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "PayGroup updated successfully.");
                loadAllPayGroups();
            } else {
                showError("Update failed. PayGroup not found.");
            }
        } catch (Exception ex) {
            showError("Error updating paygroup: " + ex.getMessage());
        }
    }
    

    // === CALLED WHEN DELETE BUTTON PRESSED IN MODAL ===

    private void deletePayGroup(String pgID) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this paygroup?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM PAY_GROUP WHERE PayGroup_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pgID);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "PayGroup deleted successfully.");
                loadAllPayGroups();
            } else {
                showError("Delete failed. PayGroup not found.");
            }
        } catch (Exception ex) {
            showError("Error deleting paygroup: " + ex.getMessage());
        }
    }

}