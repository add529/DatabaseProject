import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmployeeType extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    public EmployeeType() {

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
        searchBtn = new JButton("Search Employee Type By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("Employee Type ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===
        tableModel = new DefaultTableModel(new String[]{
            "Employee_Type_ID", "Name", "Work_Hours", "Benefit_Eligibility", "Overtime_Eligibility", "Contract_Duration"
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
        JButton addBtn = new JButton("Add Employee Type"); // Instantiate Add Button
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
        searchBtn.addActionListener(e -> searchEmployeeType());
        showEmpPay.addActionListener(e -> showEmp());
        showPayET.addActionListener(e -> showPayET());
        showAllBtn.addActionListener(e -> loadAllET());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllET();
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

    private void searchEmployeeType() {
        String employee = searchField.getText().trim();
        if (employee.isEmpty()) { //If nothing in search, error message shows
            showError("Please enter an Employee Type ID to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT Employee_Type_ID, Name, Work_Hours, Benefit_Eligibility, Overtime_Eligibility, Contract_Duration FROM EMPLOYEE_TYPE WHERE EmployeeType_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, employee);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_Type_ID"),
                    rs.getString("Name"),
                    rs.getString("Work_Hours"),
                    rs.getString("Benefit_Eligibility"),
                    rs.getString("Overtime_Eligibility"),
                    rs.getString("Contract_Duration")
                });
            } else {
                showError("No Employee Type found with Employee Type ID: " + employee);
            }
        } catch (Exception ex) {
            showError("Error searching for employee type: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PAY GROUP DETAILS ===

    private void loadAllET() {

        tableModel.setColumnIdentifiers(new String[]{"Employee Type ID", "Name", "Work Hours", "Benefit Eligibility", "Overtime Eligibility", "Contract Duration"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Employee_Type_ID, Name, Work_Hours, Benefit_Eligibility, Overtime_Eligibility, Contract_Duration FROM EMPLOYEE_TYPE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_Type_ID"),
                    rs.getString("Name"),
                    rs.getString("Work_Hours"),
                    rs.getString("Benefit_Eligibility"),
                    rs.getString("Overtime_Eligibility"),
                    rs.getString("Contract_Duration")
                });
            }
        } catch (Exception ex) {
            showError("Error loading employee types: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        private void showEmp() {
            tableModel.setColumnIdentifiers(new String[]{"Employee Type ID", "Employee Type Name", "Employee No", "First Name", "Last Name"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                    SELECT 
                        t.Employee_Type_ID, 
                        t.Name, 
                        e.Employee_No, 
                        e.FName, 
                        e.LName
                    FROM 
                        EMPLOYEE_TYPE t
                    JOIN 
                        EMPLOYEE e ON t.Employee_Type_ID = e.Employee_Type
                """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Employee_Type_ID"),
                        rs.getString("Name"),
                        rs.getString("Employee_No"),
                        rs.getString("FName"),
                        rs.getString("LName")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading type-employee data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }
    

        private void showPayET() {
            tableModel.setColumnIdentifiers(new String[]{"Employee Type ID", "Employee Type Name", "Pay Group ID", "Pay Group Name"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                            SELECT 
                                e.Employee_Type_ID, 
                                e.Name AS "etName",
                                p.PayGroup_ID, 
                                p.Name AS "pgName"
                            FROM 
                                PAY_GROUP p
                            JOIN 
                                EMPLOYEE_TYPE e ON p.PayGroup_ID = e.PayGroup_ID
                        """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Employee_Type_ID"),
                        rs.getString("etName"),
                        rs.getString("PayGroup_ID"),
                        rs.getString("pgName")
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
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Employee Type", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the type group
        String[] fieldNames = {"Name", "Work Hours", "Benefit Eligibility", "Overtime Eligibility", "Contract Duration"};
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
                // Generate a unique type ID
                String maxIdQuery = "SELECT MAX(CAST(SUBSTRING(Employee_Type_ID, 4) AS UNSIGNED)) AS MaxID FROM EMPLOYEE_TYPE";
                ResultSet maxIdResult = conn.createStatement().executeQuery(maxIdQuery);
                String etID = "ET-";
                
                if (maxIdResult.next()) {
                    int maxId = maxIdResult.getInt("MaxID");
                    etID += String.format("%03d", maxId + 1);  // Increment the maximum ID and format
                } else {
                    etID += "001";  // Start from 001 if no records exist
                }

                // Build SQL query
                String sql = "INSERT INTO EMPLOYEE_TYPE (Employee_Type_ID, Name, Work_Hours, Benefit_Eligibility, Overtime_Eligibility, Contract_Duration) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set PaygroupID
                ps.setString(1, etID);

                // Set other fields
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Employee Type added successfully!");
                    loadAllET(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding employee type: " + ex.getMessage());
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
        String eID = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String hours = (String) tableModel.getValueAt(selectedRow, 2);
        String benefit = (String) tableModel.getValueAt(selectedRow, 3);
        String overtime = (String) tableModel.getValueAt(selectedRow, 4);
        String contract = (String) tableModel.getValueAt(selectedRow, 5);
    
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM EMPLOYEE_TYPE WHERE Employee_Type_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, eID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                showError("Could not retrieve full record.");
                return;
            }
    
            // === Build Modal Dialog ===
            JLabel idField = new JLabel(eID);
            JTextField nameField = new JTextField(name);
            JTextField hoursField = new JTextField(hours);
            JTextField benefitField = new JTextField(benefit);
            JTextField overtimeField = new JTextField(overtime);
            JTextField contractField = new JTextField(contract);
    
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Employee Type ID:")); panel.add(idField);
            panel.add(new JLabel("Employee Type Name:")); panel.add(nameField);
            panel.add(new JLabel("Work Hours:")); panel.add(hoursField);
            panel.add(new JLabel("Benefit Eligibility:")); panel.add(benefitField);
            panel.add(new JLabel("Overtime Eligibility:")); panel.add(overtimeField);
            panel.add(new JLabel("Contract Duration:")); panel.add(contractField);
    
            Object[] options = {"Update", "Delete", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, panel, "Edit Employee Type",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
    
            if (result == JOptionPane.YES_OPTION) {
                // Update
                updateEmployeeType(
                    eID,
                    nameField.getText().trim(),
                    hoursField.getText().trim(),
                    benefitField.getText().trim(),
                    overtimeField.getText().trim(),
                    contractField.getText().trim()
                );

            } else if (result == JOptionPane.NO_OPTION) {
                // Delete
                deleteEmployeeType(eID);
            }
    
        } catch (Exception ex) {
            showError("Error retrieving employee type: " + ex.getMessage());
        }
    }

    // === CALLED WHEN EDIT SAVED PRESSED IN MODAL ===

    private void updateEmployeeType(String originalId, String name, String hours, String benefit, String overtime, String contract) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE EMPLOYEE_TYPE SET Name=?, Work_Hours=?, Benefit_Eligibility=?, Overtime_Eligibility=?, Contract_Duration=? WHERE Employee_Type_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(2, name);
            stmt.setString(3, hours);
            stmt.setString(4, benefit);
            stmt.setString(5, overtime);
            stmt.setString(6, contract);
            stmt.setString(7, originalId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Employee Type updated successfully.");
                loadAllET();
            } else {
                showError("Update failed. Employee Type not found.");
            }
        } catch (Exception ex) {
            showError("Error updating employee type: " + ex.getMessage());
        }
    }
    

    // === CALLED WHEN DELETE BUTTON PRESSED IN MODAL ===

    private void deleteEmployeeType(String eID) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee type?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM EMPLOYEE_TYPE WHERE Employee_Type_ID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, eID);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Employee Type deleted successfully.");
                loadAllET();
            } else {
                showError("Delete failed. Employee Type not found.");
            }
        } catch (Exception ex) {
            showError("Error deleting employee type: " + ex.getMessage());
        }
    }

}