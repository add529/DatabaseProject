import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmployeePanel extends JPanel {

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private JTextField empNoField, nameField, addressField, jobDescField;
    private JButton editButton, updateButton;
    private boolean isEditMode = false;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));

        // ===== LEFT CONTROL PANEL =====
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        JButton deleteBtn = new JButton("Delete");

        controlPanel.add(searchBtn);
        controlPanel.add(showAllBtn);
        controlPanel.add(deleteBtn);

        add(controlPanel, BorderLayout.WEST);

        // ===== CENTER TABLE PANEL =====
        tableModel = new DefaultTableModel(new String[]{"Employee No", "Name", "Address", "Job Desc"}, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM INFO + BUTTONS =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        empNoField = new JTextField();
        empNoField.setEditable(false);
        nameField = new JTextField();
        addressField = new JTextField();
        jobDescField = new JTextField();

        nameField.setEditable(false);
        addressField.setEditable(false);
        jobDescField.setEditable(false);

        fieldsPanel.add(new JLabel("Employee No:")); fieldsPanel.add(empNoField);
        fieldsPanel.add(new JLabel("Name:")); fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Address:")); fieldsPanel.add(addressField);
        fieldsPanel.add(new JLabel("Job Desc:")); fieldsPanel.add(jobDescField);

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
        showAllBtn.addActionListener(e -> loadAllEmployees());
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                int row = employeeTable.getSelectedRow();
                empNoField.setText(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                addressField.setText(tableModel.getValueAt(row, 2).toString());
                jobDescField.setText(tableModel.getValueAt(row, 3).toString());
                disableEditing();
            }
        });

        editButton.addActionListener(e -> enableEditing());
        updateButton.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteSelectedEmployee());

        // Initial load
        loadAllEmployees();
    }

    private void loadAllEmployees() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Employee_No, CONCAT(FName, ' ', MName, ' ', LName) AS FullName, Address, Job_Desc FROM EMPLOYEE ORDER BY Employee_No DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_No"),
                    rs.getString("FullName"),
                    rs.getString("Address"),
                    rs.getString("Job_Desc")
                });
            }
        } catch (Exception ex) {
            showError("Error loading employees: " + ex.getMessage());
        }
    }

    private void enableEditing() {
        nameField.setEditable(true);
        addressField.setEditable(true);
        jobDescField.setEditable(true);
        updateButton.setEnabled(true);
    }

    private void disableEditing() {
        nameField.setEditable(false);
        addressField.setEditable(false);
        jobDescField.setEditable(false);
        updateButton.setEnabled(false);
    }

    private void updateEmployee() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String[] nameParts = nameField.getText().split(" ", 3);
            String fName = nameParts.length > 0 ? nameParts[0] : "";
            String mName = nameParts.length > 1 ? nameParts[1] : "";
            String lName = nameParts.length > 2 ? nameParts[2] : "";

            String sql = "UPDATE EMPLOYEE SET FName = ?, MName = ?, LName = ?, Address = ?, Job_Desc = ? WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fName);
            ps.setString(2, mName);
            ps.setString(3, lName);
            ps.setString(4, addressField.getText());
            ps.setString(5, jobDescField.getText());
            ps.setString(6, empNoField.getText());

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
            String empNo = empNoField.getText();
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
        empNoField.setText("");
        nameField.setText("");
        addressField.setText("");
        jobDescField.setText("");
        disableEditing();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}