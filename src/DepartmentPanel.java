import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DepartmentPanel extends JPanel {

    private JTable departmentTable;
    private DefaultTableModel tableModel;

    private JTextField[] fields;
    private String[] fieldNames = {
        "Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"
    };

    private JButton editButton, updateButton;
    private JTextField searchField;
    private JComboBox<String> searchAttributeBox;
    private JPanel bottomPanel;
    private boolean isEditable = false; // Track whether the form is in "edit" mode

    public DepartmentPanel() {
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
            "Department_ID", "Name",
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
            "Department_ID", "Name", "Budget", "Employee_Count", "Dep_Head_SSN", "Dep_Head_Bonus"
        }, 0);

        departmentTable = new JTable(tableModel);
        departmentTable.setFillsViewportHeight(true);
        departmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adjust columns to fit the table width
        departmentTable.setRowHeight(20);

        departmentTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        departmentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        departmentTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        departmentTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        departmentTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        departmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        departmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        departmentTable.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(departmentTable);
        scrollPane.setPreferredSize(new Dimension(10, 10));

        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM FORM PANEL (Initially hidden) =====
        bottomPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));
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
            searchDepartment(attr, value);
        });

        showAllBtn.addActionListener(e -> loadAllDepartments());
        deleteBtn.addActionListener(e -> deleteSelectedDepartment());
        selectBtn.addActionListener(e -> showSelectedDepartmentDetails());
        editButton.addActionListener(e -> toggleEditMode());
        updateButton.addActionListener(e -> updateDepartment());

        loadAllDepartments();
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

    private void updateDepartment() {
        String empNo = fields[0].getText(); // Assuming Department_ID is the first field
        if (empNo.isEmpty()) {
            showError("No Deparment selected to update.");
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE DEPARTMENT SET ");
        for (int i = 1; i < fieldNames.length; i++) {
            sql.append(fieldNames[i]).append(" = ?");
            if (i < fieldNames.length - 1) sql.append(", ");
        }
        sql.append(" WHERE Department_ID = ?");

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 1; i < fieldNames.length; i++) {
                ps.setString(i, fields[i].getText());
            }
            ps.setString(fieldNames.length, empNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Department updated.");
                loadAllDepartments();
                toggleEditMode(); // Exit edit mode after update
            } else {
                showError("Update failed.");
            }
        } catch (Exception ex) {
            showError("Update error: " + ex.getMessage());
        }
    }

    private void loadAllDepartments() {
        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM DEPARTMENT ORDER BY Name ASC";
                    ResultSet rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        publish(new Object[]{
                            rs.getString("Department_ID"),
                            rs.getString("Name"),
                            rs.getString("Budget"),
                            rs.getString("Employee_Count"),
                            rs.getString("Dep_Head_SSN"),
                            rs.getString("Dep_Head_Bonus")
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> showError("Error loading Departments: " + ex.getMessage()));
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

    private void searchDepartment(String attr, String value) {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM DEPARTMENT WHERE " + attr + " = ? ORDER BY Name ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Department_ID"),
                    rs.getString("Name"),
                    rs.getString("Budget"),
                    rs.getString("Employee_Count"),
                    rs.getString("Dep_Head_SSN"),
                    rs.getString("Dep_Head_Bonus")
                });
                found = true;
            }
            if (!found) {
                showError("No Department found with " + attr + " = " + value);
            }
        } catch (Exception ex) {
            showError("Search error: " + ex.getMessage());
        }
    }

    private void deleteSelectedDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            showError("Select an Department to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this Department?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String empNo = (String) departmentTable.getValueAt(row, 0);
            String sql = "DELETE FROM DEPARTMENT WHERE Department_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, empNo);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Deaprtment deleted.");
                loadAllDepartments();
            }
        } catch (Exception ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }

    private void showSelectedDepartmentDetails() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            showError("Select a Department from the table.");
            return;
        }

        String empNo = (String) departmentTable.getValueAt(row, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM DEPARTMENT WHERE DEPARTMENT_ID = ?";
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
            showError("Error retrieving Department details: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
