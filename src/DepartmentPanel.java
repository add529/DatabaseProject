import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DepartmentPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    private JTextField namField, budgetField, empCountField, deptHeadSSNField, bonusField;
    private JButton editBtn, saveBtn;

    private String[] fieldNames = {
        "Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"
    };


    public DepartmentPanel() {

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
        JButton showEmp = new JButton("Show Employees"); //Instantiate Show Employee Button



        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showEmp, selectBtn, showAllBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(120, 40));
            btn.setBackground(DARK_BG);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        //Add Navigation Buttons to Navigation Bar
        navBar.add(selectBtn);  //Add Button
        navBar.add(Box.createVerticalStrut(10)); //Spacing
        navBar.add(showAllBtn);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showEmp);
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
        searchBtn = new JButton("Search Department By ID - change this");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("Department ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===

        tableModel = new DefaultTableModel(new String[]{
            "Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"
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

        // Initialize the text fields
        namField = new JTextField(15);
        budgetField = new JTextField(15);
        empCountField = new JTextField(15);
        deptHeadSSNField = new JTextField(15);
        bonusField = new JTextField(15);

        // Create a panel for input fields and labels
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Grid layout for labels and fields
        fieldsPanel.setOpaque(false); // Make it transparent
        fieldsPanel.setVisible(false); // Initially hidden

        // Add fields and labels to the fields panel
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(namField);
        fieldsPanel.add(new JLabel("Budget:"));
        fieldsPanel.add(budgetField);
        fieldsPanel.add(new JLabel("Employee Count:"));
        fieldsPanel.add(empCountField);
        fieldsPanel.add(new JLabel("Department Head SSN:"));
        fieldsPanel.add(deptHeadSSNField);
        fieldsPanel.add(new JLabel("Department Head Bonus:"));
        fieldsPanel.add(bonusField);

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
            namField.setText((String) tableModel.getValueAt(selectedRow, 1));
            budgetField.setText((String) tableModel.getValueAt(selectedRow, 2));
            empCountField.setText((String) tableModel.getValueAt(selectedRow, 3));
            deptHeadSSNField.setText((String) tableModel.getValueAt(selectedRow, 4));
            bonusField.setText((String) tableModel.getValueAt(selectedRow, 5));

            // Make the fields visible but not editable
            fieldsPanel.setVisible(true);
            namField.setEditable(false);
            budgetField.setEditable(false);
            empCountField.setEditable(false);
            deptHeadSSNField.setEditable(false);
            bonusField.setEditable(false);

            saveBtn.setVisible(false); // Hide the Update button
        });

        editBtn.addActionListener(e -> {
            // Make fields editable and show the Update button
            fieldsPanel.setVisible(true);
            namField.setEditable(true);
            budgetField.setEditable(true);
            empCountField.setEditable(true);
            deptHeadSSNField.setEditable(true);
            bonusField.setEditable(true);
            saveBtn.setVisible(true); // Show the Update button
        });

        saveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a row to update.");
                return;
            }

            // Get the original values from the table
            String originalName = (String) tableModel.getValueAt(selectedRow, 1);
            String originalBudget = (String) tableModel.getValueAt(selectedRow, 2);
            String originalEmpCount = (String) tableModel.getValueAt(selectedRow, 3);
            String originalDeptHeadSSN = (String) tableModel.getValueAt(selectedRow, 4);
            String originalBonus = (String) tableModel.getValueAt(selectedRow, 5);

            // Get the updated values from the input fields
            String updatedName = namField.getText();
            String updatedBudget = budgetField.getText();
            String updatedEmpCount = empCountField.getText();
            String updatedDeptHeadSSN = deptHeadSSNField.getText();
            String updatedBonus = bonusField.getText();

            // Check if any changes were made
            if (originalName.equals(updatedName) &&
                originalBudget.equals(updatedBudget) &&
                originalEmpCount.equals(updatedEmpCount) &&
                originalDeptHeadSSN.equals(updatedDeptHeadSSN) &&
                originalBonus.equals(updatedBonus)) {
                showError("No changes made.");
                fieldsPanel.setVisible(true); // Keep fields visible
                namField.setEditable(false);
                budgetField.setEditable(false);
                empCountField.setEditable(false);
                deptHeadSSNField.setEditable(false);
                bonusField.setEditable(false);
                saveBtn.setVisible(false); // Hide the Update button
                return;
            }

            // Update the database
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE DEPARTMENT SET Name = ?, Budget = ?, Employee_Count = ?, Dep_Head_SSN = ?, Dep_Head_Bonus = ? WHERE Department_ID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, updatedName);
                ps.setString(2, updatedBudget);
                ps.setString(3, updatedEmpCount);
                ps.setString(4, updatedDeptHeadSSN);
                ps.setString(5, updatedBonus);
                ps.setString(6, (String) tableModel.getValueAt(selectedRow, 0)); // Department_ID

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Department updated successfully!");
                    loadAllDepartments(); // Refresh the table
                } else {
                    showError("Failed to update department.");
                }
            } catch (Exception ex) {
                showError("Error updating department: " + ex.getMessage());
            }

            // Disable editing after update
            namField.setEditable(false);
            budgetField.setEditable(false);
            empCountField.setEditable(false);
            deptHeadSSNField.setEditable(false);
            bonusField.setEditable(false);

            saveBtn.setVisible(false); // Hide the Update button
        });

        // Add the input panel to the bottom of the table wrapper
        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        searchBtn.addActionListener(e -> searchProduct());
        showEmp.addActionListener(e -> standIn());
        showAllBtn.addActionListener(e -> loadAllDepartments());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllDepartments();
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
        String employeeNo = searchField.getText().trim();
        if (employeeNo.isEmpty()) { //If nothing in search, error message shows
            showError("Please enter an Department ID to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus FROM DEPARTMENT WHERE Department_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, employeeNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Department_ID"),
                    rs.getString("Name"),
                    rs.getString("Budget"),
                    rs.getString("Employee_Count"),
                    rs.getString("Dep_Head_SSN"),
                    rs.getString("Dep_Head_Bonus")
                });
            } else {
                showError("No Department found with Department_ID: " + employeeNo);
            }
        } catch (Exception ex) {
            showError("Error searching for Department: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PRODUCT DETAILS ===

    private void loadAllDepartments() {
        tableModel.setColumnIdentifiers(new String[]{"Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"});
        tableModel.setRowCount(0); // Clear the table before loading

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus FROM DEPARTMENT";
            PreparedStatement stmt = conn.prepareStatement(sql); // No parameters needed
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Department_ID"),
                    rs.getString("Name"),
                    rs.getString("Budget"),
                    rs.getString("Employee_Count"),
                    rs.getString("Dep_Head_SSN"),
                    rs.getString("Dep_Head_Bonus")
                });
            }
        } catch (Exception ex) {
            showError("Error loading Department: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        // === CALLED WHEN DEPARTMENTS PRESSED, SHOWS PRODUCT TO DEPARTMENT DETAILS ===

        private void standIn() {

        }


    // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Department", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the department
        String[] fieldNames = {"Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"};
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
                // Generate a unique Department_ID
                String departmentId = "D-";
                String countQuery = "SELECT COUNT(*) AS Total FROM DEPARTMENT";
                ResultSet rs = conn.createStatement().executeQuery(countQuery);
                if (rs.next()) {
                    int count = rs.getInt("Total") + 1;
                    departmentId += String.format("%03d", count); // Format as D-XXX
                }

                // Build SQL query
                String sql = "INSERT INTO DEPARTMENT (Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set values for the fields
                ps.setString(1, departmentId); // Department_ID
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText()); // Set other fields
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Department added successfully!");
                    loadAllDepartments(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding department: " + ex.getMessage());
            }
        });

        wizard.add(fieldsPanel, BorderLayout.CENTER);
        wizard.add(finishButton, BorderLayout.SOUTH);
        wizard.setVisible(true);
    }
}