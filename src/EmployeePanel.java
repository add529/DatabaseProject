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

    private JButton editButton, updateButton;
    private JTextField searchField;
    private JComboBox<String> searchAttributeBox;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));

        // ===== LEFT CONTROL PANEL =====
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to all sides

        // Add "Search by:" label
        JLabel searchByLabel = new JLabel("Search by:");
        searchByLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchByLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        controlPanel.add(searchByLabel);

        // Add the JComboBox for search attributes
        searchAttributeBox = new JComboBox<>(new String[] {
            "Employee_No", "SSN", "Department_ID", "Office_ID", "SuperSSN", "FName", "LName", "Address", "Nationality", "Job_Desc", "Location", "Employee_Type", "Pay_Group", "Status", "Cost_Center", "Seniority", "Job_Code", "Last_Hired", "Product_ID"
        });
        searchAttributeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchAttributeBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        searchAttributeBox.setMaximumSize(new Dimension(200, 25)); // Set consistent width
        controlPanel.add(searchAttributeBox);

        // Add spacing between components
        controlPanel.add(Box.createVerticalStrut(10));

        // Add the search text field
        JLabel searchFieldLabel = new JLabel("Search value:");
        searchFieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchFieldLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        controlPanel.add(searchFieldLabel);

        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(200, 25)); // Set consistent width
        controlPanel.add(searchField);

        // Add spacing between components
        controlPanel.add(Box.createVerticalStrut(10));

        // Add buttons with spacing
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton deleteBtn = new JButton("Delete");

        searchBtn.setMaximumSize(new Dimension(200, 30)); // Set consistent width
        showAllBtn.setMaximumSize(new Dimension(200, 30)); // Set consistent width
        deleteBtn.setMaximumSize(new Dimension(200, 30)); // Set consistent width

        controlPanel.add(searchBtn);
        controlPanel.add(Box.createVerticalStrut(10)); // Add spacing between buttons
        controlPanel.add(showAllBtn);
        controlPanel.add(Box.createVerticalStrut(10)); // Add spacing between buttons
        controlPanel.add(deleteBtn);

        add(controlPanel, BorderLayout.WEST);

        // ===== CENTER TABLE PANEL =====
        tableModel = new DefaultTableModel(new String[]{
            "Employee_No", "SSN", "Department_ID", "Office_ID", "SuperSSN", "FName", "LName",
            "Address", "Nationality", "Job_Desc", "Location", "Employee_Type", "Pay_Group",
            "Status", "Cost_Center", "Seniority", "Job_Code", "Last_Hired", "Product_ID"
        }, 0);

        employeeTable = new JTable(tableModel);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable auto-resizing to allow horizontal scrolling

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Enable horizontal scrolling
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM FORM AND BUTTONS =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel fieldsPanel = new JPanel(new GridLayout(fieldNames.length / 2 + fieldNames.length % 2, 4, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        fields = new JTextField[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            fields[i] = new JTextField();
            fields[i].setEditable(false);
            fieldsPanel.add(new JLabel(fieldNames[i] + ":"));
            fieldsPanel.add(fields[i]);
        }

        bottomPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editButton = new JButton("Edit");
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);

        buttonPanel.add(editButton);
        buttonPanel.add(updateButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Event Listeners =====
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

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                loadEmployeeDetails(employeeTable.getValueAt(employeeTable.getSelectedRow(), 0).toString());
                disableEditing();
            }
        });

        editButton.addActionListener(e -> enableEditing());
        updateButton.addActionListener(e -> updateEmployee());

        loadAllEmployees();
    }

    private void loadAllEmployees() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Employee_No, SSN, Department_ID, Office_ID, SuperSSN, FName, LName, Address, Nationality, Job_Desc, Location, Employee_Type, Pay_Group, Status, Cost_Center, Seniority, Job_Code, Last_Hired, Product_ID FROM EMPLOYEE ORDER BY Employee_No DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{

                    // Identification
                    rs.getString("Employee_No"),
                    rs.getString("SSN"),
                    rs.getString("Department_ID"),
                    rs.getString("Office_ID"),
                    rs.getString("SuperSSN"),

                    // General Information
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Address"),
                    rs.getString("Nationality"),
                    rs.getString("Job_Desc"),
                    rs.getString("Location"),

                    // Employee Information
                    rs.getString("Employee_Type"),
                    rs.getString("Pay_Group"),
                    rs.getString("Status"),
                    rs.getString("Cost_Center"),
                    rs.getString("Seniority"),
                    rs.getString("Job_Code"),
                    rs.getString("Last_Hired"),
                    rs.getString("Product_ID"),

                });
            }
        } catch (Exception ex) {
            showError("Error loading employees: " + ex.getMessage());
        }
    }

    private void searchEmployee(String attr, String value) {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Employee_No, SSN, Department_ID, Office_ID, SuperSSN, FName, LName, Address, Nationality, Job_Desc, Location, Employee_Type, Pay_Group, Status, Cost_Center, Seniority, Job_Code, Last_Hired, Product_ID  FROM EMPLOYEE WHERE " + attr + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    // Identification
                    rs.getString("Employee_No"),
                    rs.getString("SSN"),
                    rs.getString("Department_ID"),
                    rs.getString("Office_ID"),
                    rs.getString("SuperSSN"),

                    // General Information
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Address"),
                    rs.getString("Nationality"),
                    rs.getString("Job_Desc"),
                    rs.getString("Location"),

                    // Employee Information
                    rs.getString("Employee_Type"),
                    rs.getString("Pay_Group"),
                    rs.getString("Status"),
                    rs.getString("Cost_Center"),
                    rs.getString("Seniority"),
                    rs.getString("Job_Code"),
                    rs.getString("Last_Hired"),
                    rs.getString("Product_ID")
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

    private void loadEmployeeDetails(String empNo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < fieldNames.length; i++) {
                    fields[i].setText(rs.getString(fieldNames[i]));
                }
            }
        } catch (Exception ex) {
            showError("Error loading employee details: " + ex.getMessage());
        }
    }

    private void enableEditing() {
        for (JTextField field : fields) {
            field.setEditable(true);
        }
        updateButton.setEnabled(true);
    }

    private void disableEditing() {
        for (JTextField field : fields) {
            field.setEditable(false);
        }
        updateButton.setEnabled(false);
    }

    private void updateEmployee() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE EMPLOYEE SET ");
            for (int i = 1; i < fieldNames.length; i++) {
                sql.append(fieldNames[i]).append(" = ?");
                if (i < fieldNames.length - 1) sql.append(", ");
            }
            sql.append(" WHERE Employee_No = ?");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 1; i < fieldNames.length; i++) {
                ps.setString(i, fields[i].getText());
            }
            ps.setString(fieldNames.length, fields[0].getText());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Update successful.");
                loadAllEmployees();
                disableEditing();
            }
        } catch (Exception ex) {
            showError("Update failed: " + ex.getMessage());
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
            String empNo = fields[0].getText();
            String sql = "DELETE FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted.");
                loadAllEmployees();
                clearFields();
            }
        } catch (Exception ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }

    private void clearFields() {
        for (JTextField field : fields) {
            field.setText("");
        }
        disableEditing();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
