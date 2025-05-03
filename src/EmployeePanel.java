import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmployeePanel extends JPanel {

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField[] fields;
    private String[] fieldNames = {
        "Employee_No", "SSN", "FName", "MName", "LName", "DOB", "Address", "Sex",
        "Nationality", "Ethnic_ID", "Marital_Status", "Disability_Status", "Location",
        "Status", "Cost_Center", "Seniority", "Job_Code", "Job_Desc", "Last_Hired",
        "SuperSSN", "Product_ID", "Department_ID", "Employee_Type", "Pay_Group", "Office_ID"
    };

    private JButton editButton, updateButton, addButton;
    private JTextField searchField;
    private JComboBox<String> searchAttributeBox;
    private JPanel bottomPanel;
    private boolean isEditable = false; // Track whether the form is in "edit" mode

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));

        // ===== LEFT CONTROL PANEL =====
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchByLabel = new JLabel("Search by:");
        searchByLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchByLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        controlPanel.add(searchByLabel);

        searchAttributeBox = new JComboBox<>(new String[] {
            "Employee_No", "FName", "LName", "Job_Desc", "Status"
        });
        searchAttributeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchAttributeBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchAttributeBox.setMaximumSize(new Dimension(200, 25));
        controlPanel.add(searchAttributeBox);
        controlPanel.add(Box.createVerticalStrut(10));

        JLabel searchFieldLabel = new JLabel("Search value:");
        searchFieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchFieldLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        controlPanel.add(searchFieldLabel);

        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(200, 25));
        controlPanel.add(searchField);
        controlPanel.add(Box.createVerticalStrut(10));

        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton deleteBtn = new JButton("Delete");
        JButton selectBtn = new JButton("Select");
        addButton = new JButton("Add"); // Add button for creating a new employee

        searchBtn.setMaximumSize(new Dimension(200, 30));
        showAllBtn.setMaximumSize(new Dimension(200, 30));
        deleteBtn.setMaximumSize(new Dimension(200, 30));
        selectBtn.setMaximumSize(new Dimension(200, 30));
        addButton.setMaximumSize(new Dimension(200, 30)); // Set size for Add button

        controlPanel.add(searchBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(showAllBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(deleteBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(selectBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(addButton); // Add the Add button to the control panel

        add(controlPanel, BorderLayout.WEST);

        // ===== CENTER TABLE PANEL =====
        tableModel = new DefaultTableModel(new String[]{
            "Employee_No", "FName", "LName", "Job_Desc", "Status"
        }, 0);

        employeeTable = new JTable(tableModel);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adjust columns to fit the table width
        employeeTable.setRowHeight(20);

        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        employeeTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setPreferredSize(new Dimension(10, 10));

        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM FORM PANEL (Initially hidden) =====
        bottomPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        fields = new JTextField[fieldNames.length];

        for (int i = 0; i < fieldNames.length; i++) {
            formPanel.add(new JLabel(fieldNames[i] + ":"));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        JPanel buttonPanel = new JPanel();
        editButton = new JButton("Edit");
        updateButton = new JButton("Update");
        buttonPanel.add(editButton);
        buttonPanel.add(updateButton);

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomPanel.setVisible(false);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Event Listeners =====
        addButton.addActionListener(e -> openCreationWizard()); // Add action listener for Add button
        searchBtn.addActionListener(e -> {
            String attr = (String) searchAttributeBox.getSelectedItem();
            String value = searchField.getText().trim();
            if (value.isEmpty()) {
                showError("Please enter a value to search.");
                return;
            }
            searchEmployee(attr, value);
        });

        showAllBtn.addActionListener(e -> loadAllEmployees());
        deleteBtn.addActionListener(e -> deleteSelectedEmployee());
        selectBtn.addActionListener(e -> showSelectedEmployeeDetails());
        editButton.addActionListener(e -> toggleEditMode());
        updateButton.addActionListener(e -> updateEmployee());

        loadAllEmployees();
    }

    private void toggleEditMode() {
        isEditable = !isEditable; // Toggle between edit and view mode
        for (JTextField field : fields) {
            field.setEditable(isEditable);
        }

        if (isEditable) {
            editButton.setText("Save");
        } else {
            editButton.setText("Edit");
        }
    }

    private void updateEmployee() {
        String empNo = fields[0].getText(); // Employee_No is assumed to be primary key
        if (empNo.isEmpty()) {
            showError("No employee selected to update.");
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE EMPLOYEE SET ");
        for (int i = 1; i < fieldNames.length; i++) {
            sql.append(fieldNames[i]).append(" = ?");
            if (i < fieldNames.length - 1) sql.append(", ");
        }
        sql.append(" WHERE Employee_No = ?");

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 1; i < fieldNames.length; i++) {
                ps.setString(i, fields[i].getText());
            }
            ps.setString(fieldNames.length, empNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Employee updated.");
                loadAllEmployees();
                toggleEditMode(); // Exit edit mode after update
            } else {
                showError("Update failed.");
            }
        } catch (Exception ex) {
            showError("Update error: " + ex.getMessage());
        }
    }

    private void loadAllEmployees() {
        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM EMPLOYEE ORDER BY FName ASC";
                    ResultSet rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        publish(new Object[]{
                            rs.getString("Employee_No"),
                            rs.getString("FName"),
                            rs.getString("LName"),
                            rs.getString("Job_Desc"),
                            rs.getString("Status")
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> showError("Error loading employees: " + ex.getMessage()));
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    tableModel.addRow(row);
                }
            }

            @Override
            protected void done() {
                // Optionally do something after all rows are loaded
            }
        };
        tableModel.setRowCount(0); // Clear table before loading
        worker.execute();
    }

    private void searchEmployee(String attr, String value) {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM EMPLOYEE WHERE " + attr + " = ? ORDER BY FName ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_No"),
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Job_Desc"),
                    rs.getString("Status")
                });
                found = true;
            }
            if (!found) {
                showError("No employee found with " + attr + " = " + value);
            }
        } catch (Exception ex) {
            showError("Search error: " + ex.getMessage());
        }
    }

    private void deleteSelectedEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            showError("Select an employee to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String empNo = (String) employeeTable.getValueAt(row, 0);
            String sql = "DELETE FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted.");
                loadAllEmployees();
            }
        } catch (Exception ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }

    private void showSelectedEmployeeDetails() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            showError("Select an employee from the table.");
            return;
        }

        String empNo = (String) employeeTable.getValueAt(row, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                for (int i = 0; i < fieldNames.length; i++) {
                    fields[i].setText(rs.getString(fieldNames[i]));
                }
                bottomPanel.setVisible(true);
                revalidate();
                repaint();
            }
        } catch (Exception ex) {
            showError("Error retrieving employee details: " + ex.getMessage());
        }
    }

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Employee", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // ===== CardLayout for Multi-Page Wizard =====
        JPanel cardPanel = new JPanel(new CardLayout(10, 10)); // Add padding between cards
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

        // Page Counter
        JLabel pageCounter = new JLabel("Page 1 of X", JLabel.CENTER);
        pageCounter.setFont(new Font("Tahoma", Font.BOLD, 14));

        // Split fields into pages
        int fieldsPerPage = 6; // Number of fields per page
        java.util.List<String> visibleFields = new java.util.ArrayList<>();
        for (String fieldName : fieldNames) {
            if (!fieldName.equals("Employee_No") && !fieldName.equals("Last_Hired") && !fieldName.equals("Status") && !fieldName.equals("Pay_Group")) {
                // Explicitly include Department_ID as an exception
                if (fieldName.contains("ID") && !fieldName.equals("Department_ID")) {
                    continue; // Skip other fields containing "ID"
                }
                visibleFields.add(fieldName);
            }
        }
        int totalPages = (int) Math.ceil((double) visibleFields.size() / fieldsPerPage);
        JTextField[] wizardFields = new JTextField[fieldNames.length];
        JComboBox<String> departmentDropdown = new JComboBox<>(); // Dropdown for Department_IDs
        JComboBox<String> sexDropdown = new JComboBox<>(new String[]{"M", "F"}); // Dropdown for Sex
        JComboBox<String> employeeTypeDropdown = new JComboBox<>(new String[]{"ET-001", "ET-002", "ET-003"}); // Dropdown for Employee_Type

        // Marital Status Radio Buttons
        ButtonGroup maritalStatusGroup = new ButtonGroup();
        JRadioButton maritalYes = new JRadioButton("Yes");
        JRadioButton maritalNo = new JRadioButton("No");
        maritalStatusGroup.add(maritalYes);
        maritalStatusGroup.add(maritalNo);

        // Disability Status Radio Buttons and Conditional Text Field
        ButtonGroup disabilityStatusGroup = new ButtonGroup();
        JRadioButton disabilityYes = new JRadioButton("Yes");
        JRadioButton disabilityNo = new JRadioButton("No");
        disabilityStatusGroup.add(disabilityYes);
        disabilityStatusGroup.add(disabilityNo);

        JTextField disabilityDescription = new JTextField();
        disabilityDescription.setVisible(false); // Initially hidden

        // Show/Hide Disability Description Field Based on Selection
        disabilityYes.addActionListener(e -> disabilityDescription.setVisible(true));
        disabilityNo.addActionListener(e -> disabilityDescription.setVisible(false));

        // Populate department dropdown with Department_IDs
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Department_ID FROM DEPARTMENT";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                String departmentId = rs.getString("Department_ID"); // Fetch as String
                departmentDropdown.addItem(departmentId); // Add Department_ID to dropdown
            }
        } catch (Exception ex) {
            showError("Error loading departments: " + ex.getMessage());
        }

        for (int page = 0; page < totalPages; page++) {
            JPanel pagePanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Add padding between fields
            pagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the page

            for (int i = page * fieldsPerPage; i < Math.min((page + 1) * fieldsPerPage, visibleFields.size()); i++) {
                String fieldName = visibleFields.get(i);
                pagePanel.add(new JLabel(fieldName + ":"));
                if (fieldName.equals("Sex")) {
                    pagePanel.add(sexDropdown); // Add dropdown for Sex
                } else if (fieldName.equals("Employee_Type")) {
                    pagePanel.add(employeeTypeDropdown); // Add dropdown for Employee_Type
                } else if (fieldName.equals("Department_ID")) { // Match Department_ID
                    pagePanel.add(departmentDropdown); // Add dropdown for Department_IDs
                } else if (fieldName.equals("Marital_Status")) {
                    JPanel maritalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    maritalPanel.add(maritalYes);
                    maritalPanel.add(maritalNo);
                    pagePanel.add(maritalPanel); // Add marital status radio buttons
                } else if (fieldName.equals("Disability_Status")) {
                    JPanel disabilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    disabilityPanel.add(disabilityYes);
                    disabilityPanel.add(disabilityNo);
                    pagePanel.add(disabilityPanel); // Add disability status radio buttons
                    pagePanel.add(new JLabel("If Yes, Describe:"));
                    pagePanel.add(disabilityDescription); // Add conditional text field
                } else {
                    JTextField field = new JTextField();
                    wizardFields[i] = field;
                    pagePanel.add(field);
                }
            }

            cardPanel.add(pagePanel, "Page" + page);
        }

        // ===== Navigation Buttons =====
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 10)); // Add padding around buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton finishButton = new JButton("Finish");

        backButton.setEnabled(false); // Disable "Back" on the first page
        finishButton.setVisible(false); // Hide "Finish" until the last page

        // Add action listeners for navigation
        final int[] currentPage = {0};
        backButton.addActionListener(e -> {
            currentPage[0]--;
            cardLayout.show(cardPanel, "Page" + currentPage[0]);
            nextButton.setVisible(true);
            finishButton.setVisible(false);
            pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
            if (currentPage[0] == 0) {
                backButton.setEnabled(false);
            }
        });

        nextButton.addActionListener(e -> {
            currentPage[0]++;
            cardLayout.show(cardPanel, "Page" + currentPage[0]);
            backButton.setEnabled(true);
            pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
            if (currentPage[0] == totalPages - 1) {
                nextButton.setVisible(false);
                finishButton.setVisible(true);
            }
        });

        finishButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Generate Employee_No
                String employeeNo = "Emp-";
                String countQuery = "SELECT COUNT(*) AS Total FROM EMPLOYEE";
                ResultSet rs = conn.createStatement().executeQuery(countQuery);
                if (rs.next()) {
                    int count = rs.getInt("Total") + 1;
                    employeeNo += String.format("%03d", count); // Format as Emp-XXX
                }

                // Build SQL query
                StringBuilder sql = new StringBuilder("INSERT INTO EMPLOYEE (");
                for (String fieldName : fieldNames) {
                    if (!fieldName.equals("Employee_No") && !fieldName.contains("ID") && !fieldName.equals("Last_Hired") && !fieldName.equals("Status") && !fieldName.equals("Pay_Group")) {
                        sql.append(fieldName).append(", ");
                    }
                }
                sql.append("Employee_No, Department_ID, Product_ID, Last_Hired, Status, Pay_Group) VALUES (");
                for (int i = 0; i < visibleFields.size(); i++) {
                    sql.append("?, ");
                }
                sql.append("?, ?, ?, ?, ?, ?)");

                PreparedStatement ps = conn.prepareStatement(sql.toString());

                // Set visible fields
                for (int i = 0; i < visibleFields.size(); i++) {
                    ps.setString(i + 1, wizardFields[i].getText());
                }

                // Set hidden fields
                ps.setString(visibleFields.size() + 1, employeeNo); // Employee_No
                String selectedDepartmentId = (String) departmentDropdown.getSelectedItem(); // Get selected Department_ID
                ps.setString(visibleFields.size() + 2, selectedDepartmentId); // Department_ID
                ps.setInt(visibleFields.size() + 3, 0); // Product_ID (default or fetched separately)
                ps.setDate(visibleFields.size() + 4, new java.sql.Date(System.currentTimeMillis())); // Last_Hired
                ps.setString(visibleFields.size() + 5, "Active"); // Status

                // Determine Pay_Group based on Employee_Type
                String selectedEmployeeType = (String) employeeTypeDropdown.getSelectedItem();
                String payGroup = selectedEmployeeType.equals("ET-001") ? "PG-001" : "PG-002";
                ps.setString(visibleFields.size() + 6, payGroup); // Pay_Group

                // Set Marital_Status
                ps.setInt(visibleFields.size() + 7, maritalYes.isSelected() ? 0 : 1);

                // Set Disability_Status
                if (disabilityYes.isSelected()) {
                    ps.setString(visibleFields.size() + 8, disabilityDescription.getText());
                } else {
                    ps.setNull(visibleFields.size() + 8, java.sql.Types.VARCHAR);
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Employee added successfully!");
                    loadAllEmployees(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding employee: " + ex.getMessage());
            }
        });

        navButtons.add(backButton);
        navButtons.add(nextButton);
        navButtons.add(finishButton);

        buttonPanel.add(pageCounter, BorderLayout.NORTH); // Add page counter
        buttonPanel.add(navButtons, BorderLayout.SOUTH);

        // ===== Add Components to Wizard =====
        wizard.add(cardPanel, BorderLayout.CENTER);
        wizard.add(buttonPanel, BorderLayout.SOUTH);
        wizard.setVisible(true);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
