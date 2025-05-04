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
                JButton showDeptEmpl = new JButton("Employees");
        
        
                //Navigation Bar Button Formatting
                for (JButton btn : new JButton[]{showDeptEmpl, selectBtn, showAllBtn}) {
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
                navBar.add(showDeptEmpl);
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
                searchBtn = new JButton("Search Department By ID");
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
                    "Department ID", "Name", "Budget", "Employee Count", "Head SSN", "Head Bonus"
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
                JButton addBtn = new JButton("Add Department"); // Instantiate Add Button
                addBtn.setBackground(DARK_BG);
                addBtn.setForeground(Color.WHITE);
            
                // Add buttons to the button panel
                buttonPanel.add(addBtn);
        
                // Add panels to the input panel
                inputPanel.add(buttonPanel, BorderLayout.NORTH); // Buttons at the top
            
        
                // Add action listeners
                selectBtn.addActionListener(e -> openEditDialog());
        
                // Add the input panel to the bottom of the table wrapper
                tableWrapper.add(inputPanel, BorderLayout.SOUTH);
        
                mainContent.add(tableWrapper, BorderLayout.CENTER);
                add(mainContent, BorderLayout.CENTER);
        
                // === ACTION LISTENERS - These say what happens when button is pressed ===
        
                searchBtn.addActionListener(e -> searchDept());
                showAllBtn.addActionListener(e -> loadAllDepts());
                addBtn.addActionListener(e -> openCreationWizard());
                showDeptEmpl.addActionListener(e -> showEmpWithDept());
        
                // === INITIAL LOAD OF TABLE ===
        
                loadAllDepts();
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
        
              // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Department", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // Fields for the department
        String[] fieldNames = {"Name", "Budget", "Employee Count", "Department Head SSN", "Department Head Bonus"};
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
                // Generate a unique departmentid
                String departmentId = "D-";
                String countQuery = "SELECT COUNT(*) AS Total FROM DEPARTMENT";
                ResultSet rs = conn.createStatement().executeQuery(countQuery);
                if (rs.next()) {
                    int count = rs.getInt("Total") + 1;
                    departmentId += String.format("%03d", count); // Format as P-XXX
                }

                // Build SQL query
                String sql = "INSERT INTO DEPARTMENT (Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set departmentid
                ps.setString(1, departmentId);

                // Set other fields
                for (int i = 0; i < fieldNames.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Department added successfully!");
                    loadAllDepts(); // Refresh the table
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

            
            // === CALLED WHEN SEARCH BUTTON PRESSED, CONTROLS SEARCH PROCESS ===
        
            private void searchDept() {
                String deptID = searchField.getText().trim();
                if (deptID.isEmpty()) { //If nothing in search, error message shows
                    showError("Please enter a Department ID to search."); 
                    return;
                }
        
                tableModel.setRowCount(0);
                try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
                    String sql = "SELECT Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus FROM DEPARTMENT WHERE Department_ID = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, deptID);
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
                        showError("No department found with Department ID: " + deptID);
                    }
                } catch (Exception ex) {
                    showError("Error searching department: " + ex.getMessage());
                }
        
                padTableRows(35); // This keeps the empty rows there for design purposes
            }
        
            // === CALLED WHEN SHOW ALL PRESSED, SHOWS DEPT DETAILS ===
        
            private void loadAllDepts() { 
        
                tableModel.setColumnIdentifiers(new String[]{"Dept. ID", "Name", "Budget", "Employee Count", "Head SSN", "Head Bonus"});
                tableModel.setRowCount(0);
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT Department_ID, Name, Budget, Employee_Count, Dep_Head_SSN, Dep_Head_Bonus FROM DEPARTMENT";
                    PreparedStatement stmt = conn.prepareStatement(sql);
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
                    showError("Error loading departments: " + ex.getMessage());
                }
        
                padTableRows(35); // Keeps empty rows for design
                selectBtn.setEnabled(true); // Enable Select button
            }
        
                // === CALLED WHEN EMPLOYEES PRESSED, SHOWS EMPLOYEE TO DEPARTMENT DETAILS ===
        
                private void showEmpWithDept() {
                    tableModel.setColumnIdentifiers(new String[]{"Employee No", "First Name", "Last Name", "Department ID", "Department Name"});
                    tableModel.setRowCount(0);
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = """
                            SELECT e.Employee_No, e.FName, e.LName, d.Department_ID, d.Name
                            FROM DEPARTMENT d
                            JOIN EMPLOYEE e ON d.Department_ID = e.Department_ID
                            """;
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            tableModel.addRow(new Object[]{
                                rs.getString("Employee_No"),
                                rs.getString("FName"),
                                rs.getString("LName"),
                                rs.getString("Department_ID"),
                                rs.getString("Name")
                            });
                        }
                    } catch (Exception ex) {
                        showError("Error loading department-employee data: " + ex.getMessage());
                    }
                    padTableRows(35);
                    selectBtn.setEnabled(false); // Disable Select button
                }
            
            // === CALLED WHEN SELECT BUTTON PRESSED, CONTROLS MODAL ===
        
            private void openEditDialog() {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    showError("Please select a row to edit.");
                    return;
                }
            
                // Get visible values from table
                String deptID = (String) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                String budget = (String) tableModel.getValueAt(selectedRow, 2);
                String emp_count = (String) tableModel.getValueAt(selectedRow, 3);
                String dep_head = (String) tableModel.getValueAt(selectedRow, 4);
                String head_bonus = (String) tableModel.getValueAt(selectedRow, 5);
            
            
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM DEPARTMENT WHERE Department_ID = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, deptID);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        showError("Could not retrieve full record.");
                        return;
                    }
            
                    String fullDeptID = rs.getString("Department_ID");
            
                    // === Build Modal Dialog ===
                    JLabel idField = new JLabel(fullDeptID);
                    JTextField nameField = new JTextField(name);
                    JTextField budgetField = new JTextField(budget);
                    JTextField empField = new JTextField(emp_count);
                    JTextField depHeadField = new JTextField(dep_head);
                    JTextField headBonusField = new JTextField(head_bonus);
            
                    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
                    panel.add(new JLabel("Department ID:")); panel.add(idField);
                    panel.add(new JLabel("Name:")); panel.add(nameField);
                    panel.add(new JLabel("Budget:")); panel.add(budgetField);
                    panel.add(new JLabel("Employee Count:")); panel.add(empField);
                    panel.add(new JLabel("Department Head SSN:")); panel.add(depHeadField);
                    panel.add(new JLabel("Department Head Bonus:")); panel.add(headBonusField);
            
                    Object[] options = {"Update", "Delete", "Cancel"};
                    int result = JOptionPane.showOptionDialog(this, panel, "Edit Department",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, options[0]);
            
                    if (result == JOptionPane.YES_OPTION) {
                        // Update
                        updateDepartment(
                            fullDeptID,
                            nameField.getText().trim(),
                            budgetField.getText().trim(),
                            empField.getText().trim(),
                            depHeadField.getText().trim(),
                            headBonusField.getText().trim()
                        );
        
                    } else if (result == JOptionPane.NO_OPTION) {
                        // Delete
                        deleteDepartment(fullDeptID);
                    }
            
                } catch (Exception ex) {
                    showError("Error retrieving department: " + ex.getMessage());
                }
            }
        
            // === CALLED WHEN EDIT SAVED PRESSED IN MODAL ===
        
            private void updateDepartment(String originalId, String name, String budget, String empCount, String headSSN, String headBonus) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "UPDATE DEPARTMENT SET Department_ID=?, Department_ID=?, Name=?, Description=?, Status=?, Version=? WHERE Department_ID=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(2, name);
                    stmt.setString(3, budget);
                    stmt.setString(4, empCount);
                    stmt.setString(5, headSSN);
                    stmt.setString(6, headBonus);
                    stmt.setString(7, originalId);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Department updated successfully.");
                        loadAllDepts();
                    } else {
                        showError("Update failed. Department not found.");
                    }
                } catch (Exception ex) {
                    showError("Error updating department: " + ex.getMessage());
                }
            }
            
        
            // === CALLED WHEN DELETE BUTTON PRESSED IN MODAL ===
        
            private void deleteDepartment(String deptId) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this department?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM DEPARTMENT WHERE Department_ID=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, deptId);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Department deleted successfully.");
                        loadAllDepts();
                    } else {
                        showError("Delete failed. Department not found.");
                    }
                } catch (Exception ex) {
                    showError("Error deleting department: " + ex.getMessage());
                }
            }
        
        }