import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmployeePanel extends JPanel implements EmployeeUpdateListener {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    String[] fieldNames = {
        "SSN", "FName", "MName", "LName", "DOB", "Address", "Sex", "Nationality",
        "Ethnic_ID", "Marital_Status", "Disability_Status", "Location",
        "Cost_Center", "Seniority", "Job_Code", "Job_Desc", "Employee_Type",
        "Department_ID", "Office_ID"
    };



    public EmployeePanel() {

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
        JButton showPII = new JButton("PII"); //Instantiate PII Button
        JButton showEmpAssets = new JButton("Assets"); //Instantiate Assets Button
        JButton showEmpSuper = new JButton("Supervisors"); //Instantiate Supervisors Button
        JButton showEmpLoc = new JButton("Location"); //Instantiate Location


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showEmpSuper, showEmpLoc, showPII, showEmpAssets, selectBtn, showAllBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setPreferredSize(new Dimension(120, 40));
            btn.setMinimumSize(new Dimension(120, 40));
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
        navBar.add(showPII);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showEmpSuper);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showEmpLoc);
        navBar.add(Box.createVerticalStrut(10));
        navBar.add(showEmpAssets);

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
        searchBtn = new JButton("Search Employee By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("Employee ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===

        tableModel = new DefaultTableModel(new String[]{
            "Employee No", "First Name", "Last Name", "Job Description", "Status"
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

        JPanel inputPanel = new JPanel(); //Instantiate new panel

        inputPanel.setBackground(BOT_GRADIENT);
        inputPanel.setPreferredSize(new Dimension(0, 80));
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addBtn = new JButton("Add Employee");

        addBtn.setBackground(DARK_BG);
        addBtn.setForeground(Color.WHITE);

        inputPanel.add(addBtn);

        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        searchBtn.addActionListener(e -> searchProduct());
        showPII.addActionListener(e -> showEmployeePII());
        showEmpSuper.addActionListener(e -> showEmployeeSupervisors());
        showEmpAssets.addActionListener(e -> showEmployeeAssets());
        showEmpLoc.addActionListener(e -> showEmployeeLocations());
        showAllBtn.addActionListener(e -> loadAllEmployees());
        selectBtn.addActionListener(e -> {
    // Get the selected row
    int selectedRow = table.getSelectedRow();

    // Check if a row is selected
    if (selectedRow != -1) {
        // Get the employee number from the first column (Employee No)
        String employeeNo = (String) table.getValueAt(selectedRow, 0);

        // Open the EmployeeEditWizard to edit the selected employee's details
        EmployeeEditWizard wizard = new EmployeeEditWizard((JFrame) SwingUtilities.getWindowAncestor(this), employeeNo, this);
        wizard.setVisible(true);
    } else {
        // Show an error message if no row is selected
        JOptionPane.showMessageDialog(this, "Please select an employee to edit.", "No Employee Selected", JOptionPane.WARNING_MESSAGE);
    }
});
        addBtn.addActionListener(e -> {
    // Open the EmployeeCreationWizard to add a new employee
    EmployeeCreationWizard wizard = new EmployeeCreationWizard((JFrame) SwingUtilities.getWindowAncestor(this), this);
    wizard.setVisible(true);
});


        // === INITIAL LOAD OF TABLE ===

        loadAllEmployees();
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
            showError("Please enter an Employee Number to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT Employee_No, FName, LName, Job_Desc, Status FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, employeeNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_No"),
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Job_Desc"),
                    rs.getString("Status")
                });
            } else {
                showError("No Employee found with Employee No: " + employeeNo);
            }
        } catch (Exception ex) {
            showError("Error searching for employee: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PRODUCT DETAILS ===

    private void loadAllEmployees() {

        tableModel.setColumnIdentifiers(new String[]{"Employee No", "First Name", "Last Name", "Job Description", "Status"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT Employee_No, FName, LName, Job_Desc, Status FROM EMPLOYEE";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_No"),
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Job_Desc"),
                    rs.getString("Status")
                });
            }
        } catch (Exception ex) {
            showError("Error loading employees: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        // === CALLED WHEN PII PRESSED, SHOWS EMPLOYEE PERSONALLY IDENTIFIABLE INFORMATION ===

        private void showEmployeePII() {
            tableModel.setColumnIdentifiers(new String[]{"Employee SSN", "First Name", "Last Name", "DOB", "Sex", "Disability Status"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                        SELECT
                            e.SSN,
                            e.FName,
                            e.LName,
                            e.DOB,
                            e.Sex,
                            e.Disability_Status
                        FROM
                            EMPLOYEE e;
                        """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("SSN"),
                        rs.getString("FName"),
                        rs.getString("LName"),
                        rs.getString("DOB"),
                        rs.getString("Sex"),
                        rs.getString("Disability_Status")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading employee-pii data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }

        // === CALLED WHEN SUPERVISORS PRESSED, SHOWS EMPLOYEE SUPERVISORS ===

        private void showEmployeeSupervisors() {
            tableModel.setColumnIdentifiers(new String[]{"Employee No", "First Name", "Last Name", "Supervisor No", "Super First", "Super Last"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                           SELECT
                                e.Employee_No,
                                e.FName,
                                e.LName,
                                e.SuperSSN,
                                s.FName AS "Super_First",
                                s.LName AS "Super_Last"
                            FROM
                                EMPLOYEE e
                            LEFT JOIN
                                EMPLOYEE s
                            ON
                                e.SuperSSN = s.SSN;
                            """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Employee_No"),
                        rs.getString("FName"),
                        rs.getString("LName"),
                        rs.getString("SuperSSN"),
                        rs.getString("Super_First"),
                        rs.getString("Super_Last")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading employee-super data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }

        // === CALLED WHEN LOCATIONS PRESSED, SHOWS EMPLOYEE OFFICES AND LOCATIONS ===

        private void showEmployeeLocations() {
            tableModel.setColumnIdentifiers(new String[]{"Employee No", "First Name", "Last Name", "Home City", "Home Office", "Office Location"});
            tableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                    SELECT
                        e.Employee_No,
                        e.FName,
                        e.LName,
                        e.Location,
                        o.Name,
                        o.Location AS 'Office_Location'
                    FROM
                        EMPLOYEE e
                    JOIN
                        OFFICE o
                    ON
                        e.Office_ID = o.Office_ID;
                """;
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("Employee_No"),
                        rs.getString("FName"),
                        rs.getString("LName"),
                        rs.getString("Location"),
                        rs.getString("Name"),
                        rs.getString("Office_Location")
                    });
                }
            } catch (Exception ex) {
                showError("Error loading employee-office data: " + ex.getMessage());
            }
            padTableRows(35);
            selectBtn.setEnabled(false); // Disable Select button
        }

    // === CALLED WHEN ASSETS PRESSED, EMPLOYEE ASSETS SHOWN ===

    private void showEmployeeAssets() {
        tableModel.setColumnIdentifiers(new String[]{"Employee No", "First Name", "Last Name", "Asset ID", "Asset Type", "Warranty Exp. Date"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT
                    e.Employee_No,
                    e.FName,
                    e.LName,
                    a.Asset_ID,
                    a.Type,
                    a.Warrant_Exp_Date
                FROM
                    EMPLOYEE e
                JOIN
                    ASSET a
                ON
                    e.Employee_No = a.Employee_No;
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Employee_No"),
                    rs.getString("FName"),
                    rs.getString("LName"),
                    rs.getString("Asset_ID"),
                    rs.getString("Type"),
                    rs.getString("Warrant_Exp_Date")
                });
            }
        } catch (Exception ex) {
            showError("Error loading employee-asset data: " + ex.getMessage());
        }
        padTableRows(35);
        selectBtn.setEnabled(false); // Disable Select button
    }

    @Override
    public void onEmployeeDataUpdated() {
        // Refresh the table when employee data is updated
        loadAllEmployees();
    }
}
