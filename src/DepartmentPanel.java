import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * A JPanel for managing departments: viewing, searching, adding, and updating department records.
 */
public class DepartmentPanel extends JPanel {

    // ==== UI Components ====
    private JTable departmentTable;
    private DefaultTableModel tableModel;

    // Input fields for department details
    private JTextField idField, nameField, budgetField, employeeCountField, headSSNField, bonusField;

    // Action buttons
    private JButton saveButton, updateButton;

    // Search attributes (column names)
    private static final String[] searchAttributes = {
        "Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"
    };

    /**
     * Constructor: Initializes the UI components and layout.
     */
    public DepartmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createControlPanel(), BorderLayout.WEST);     // Left: Search controls
        add(createTablePanel(), BorderLayout.CENTER);     // Center: Table view
        add(createFormPanel(), BorderLayout.SOUTH);       // Bottom: Input + Action buttons

        attachEventHandlers();                            // Link buttons and table actions
        loadDepartments();                                // Initial data load
    }

    // =========================
    // UI COMPONENT CREATORS
    // =========================

    // Creates the left-hand search/filter panel
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Search by:");
        JComboBox<String> attributeComboBox = new JComboBox<>(searchAttributes);
        JTextField searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");

        // Keep components aligned
        attributeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        // Layout components vertically
        controlPanel.add(searchLabel);
        controlPanel.add(attributeComboBox);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(searchField);
        controlPanel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(searchBtn);
        buttonPanel.add(showAllBtn);
        controlPanel.add(buttonPanel);

        // Event Handlers
        showAllBtn.addActionListener(e -> loadDepartments());
        searchBtn.addActionListener(e -> performSearch(attributeComboBox, searchField));

        return controlPanel;
    }

    // Creates the table area for displaying department records
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{
            "Department ID", "Name", "Budget", "Employee Count", "Head SSN", "Bonus"
        }, 0);

        departmentTable = new JTable(tableModel);
        departmentTable.getSelectionModel().addListSelectionListener(e -> populateFieldsFromSelectedRow());
        return new JScrollPane(departmentTable);
    }

    // Creates the bottom panel with input fields and action buttons
    private JPanel createFormPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize text fields
        idField = new JTextField();          nameField = new JTextField();
        budgetField = new JTextField();      employeeCountField = new JTextField();
        headSSNField = new JTextField();     bonusField = new JTextField();

        // Add fields with labels
        fieldsPanel.add(new JLabel("Department ID:")); fieldsPanel.add(idField);
        fieldsPanel.add(new JLabel("Name:"));          fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Budget:"));        fieldsPanel.add(budgetField);
        fieldsPanel.add(new JLabel("Employee Count:"));fieldsPanel.add(employeeCountField);
        fieldsPanel.add(new JLabel("Head SSN:"));      fieldsPanel.add(headSSNField);
        fieldsPanel.add(new JLabel("Bonus:"));         fieldsPanel.add(bonusField);

        // Save and Update buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        updateButton = new JButton("Update");
        actionButtonPanel.add(saveButton);
        actionButtonPanel.add(updateButton);

        bottomPanel.add(fieldsPanel, BorderLayout.CENTER);
        bottomPanel.add(actionButtonPanel, BorderLayout.SOUTH);
        return bottomPanel;
    }

    // =========================
    // EVENT HANDLERS
    // =========================

    private void attachEventHandlers() {
        saveButton.addActionListener(e -> saveDepartment());
        updateButton.addActionListener(e -> updateDepartment());
    }

    private void performSearch(JComboBox<String> attributeBox, JTextField searchField) {
        String attribute = attributeBox.getSelectedItem().toString();
        String value = searchField.getText().trim();

        if (value.isEmpty()) {
            showError("Please enter a value to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM DEPARTMENT WHERE " + attribute + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            // Convert value type based on column
            switch (attribute) {
                case "Budget":
                case "Dep_Head_Bonus":
                    ps.setFloat(1, Float.parseFloat(value));
                    break;
                case "Employee_Count":
                    ps.setInt(1, Integer.parseInt(value));
                    break;
                default:
                    ps.setString(1, value);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Department_ID"),
                    rs.getString("Name"),
                    rs.getFloat("Budget"),
                    rs.getInt("Employee_Count"),
                    rs.getString("Dep_Head_SSN"),
                    rs.getFloat("Dep_Head_Bonus")
                });
            }

            if (tableModel.getRowCount() == 0) {
                showError("No matching department found.");
            }

        } catch (Exception ex) {
            showError("Search failed: " + ex.getMessage());
        }
    }

    private void populateFieldsFromSelectedRow() {
        int row = departmentTable.getSelectedRow();
        if (row != -1) {
            idField.setText(tableModel.getValueAt(row, 0).toString());
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            budgetField.setText(tableModel.getValueAt(row, 2).toString());
            employeeCountField.setText(tableModel.getValueAt(row, 3).toString());
            headSSNField.setText(tableModel.getValueAt(row, 4).toString());
            bonusField.setText(tableModel.getValueAt(row, 5).toString());
        }
    }

    // =========================
    // DATABASE OPERATIONS
    // =========================

    private void loadDepartments() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus FROM DEPARTMENT";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Department_ID"),
                    rs.getString("Name"),
                    rs.getFloat("Budget"),
                    rs.getInt("Employee_Count"),
                    rs.getString("Dep_Head_SSN"),
                    rs.getFloat("Dep_Head_Bonus")
                });
            }
        } catch (Exception ex) {
            showError("Error loading departments: " + ex.getMessage());
        }
    }

    private void saveDepartment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO DEPARTMENT (Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idField.getText().trim());
            ps.setString(2, nameField.getText().trim());
            ps.setFloat(3, Float.parseFloat(budgetField.getText().trim()));
            ps.setInt(4, Integer.parseInt(employeeCountField.getText().trim()));
            ps.setString(5, headSSNField.getText().trim());
            ps.setFloat(6, Float.parseFloat(bonusField.getText().trim()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Department saved.");
                loadDepartments();
                clearFields();
            }
        } catch (Exception ex) {
            showError("Save failed: " + ex.getMessage());
        }
    }

    private void updateDepartment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE DEPARTMENT SET Name = ?, Budget = ?, Employee_Count = ?, Dep_Head_SSN = ?, Dep_Head_Bonus = ? WHERE Department_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText().trim());
            ps.setFloat(2, Float.parseFloat(budgetField.getText().trim()));
            ps.setInt(3, Integer.parseInt(employeeCountField.getText().trim()));
            ps.setString(4, headSSNField.getText().trim());
            ps.setFloat(5, Float.parseFloat(bonusField.getText().trim()));
            ps.setString(6, idField.getText().trim());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Department updated.");
                loadDepartments();
                clearFields();
            }
        } catch (Exception ex) {
            showError("Update failed: " + ex.getMessage());
        }
    }

    // =========================
    // UTILITY METHODS
    // =========================

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        budgetField.setText("");
        employeeCountField.setText("");
        headSSNField.setText("");
        bonusField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
