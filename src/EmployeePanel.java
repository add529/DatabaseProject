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

        searchBtn.setMaximumSize(new Dimension(200, 30));
        showAllBtn.setMaximumSize(new Dimension(200, 30));
        deleteBtn.setMaximumSize(new Dimension(200, 30));
        selectBtn.setMaximumSize(new Dimension(200, 30));

        controlPanel.add(searchBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(showAllBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(deleteBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(selectBtn);

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

        for (int i = 0; i < fields.length; i++) {
            fields[i].setEditable(isEditable);
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

    private void loadEmployeeDetails(String empNo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < fieldNames.length; i++) {
                    fields[i].setText(rs.getString(fieldNames[i]));
                    fields[i].setEditable(false);
                }
            }
        } catch (Exception ex) {
            showError("Error loading details: " + ex.getMessage());
        }
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

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
