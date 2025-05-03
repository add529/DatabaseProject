import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PayPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JButton searchBtn;
    private final JButton selectBtn;
    private final Color DARK_BG = new Color (0x0c565f);
    private final Color TOP_GRADIENT = new Color (0x9ed7cf);
    private final Color BOT_GRADIENT = new Color (0xd0e8bd);

    private String[] fieldNames = {
        "PayGroup_ID", "SSN", "FName", "MName", "LName", "DOB", "Address", "Sex",
        "Nationality", "Ethnic_ID", "Marital_Status", "Disability_Status", "Location",
        "Status", "Cost_Center", "Seniority", "Job_Code", "Job_Desc", "Last_Hired",
        "SuperSSN", "Product_ID", "Department_ID", "Employee_Type", "Pay_Group", "Office_ID"
    };

    private JTextField payRateField, payFrequencyField, payPeriodField, overtimeRateField, nameField;
    private JButton editBtn, saveBtn;

    public PayPanel() {

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
        JButton showEmpPay = new JButton("Type and Pay"); //Instantiate Type and Pay Button


        //Navigation Bar Button Formatting
        for (JButton btn : new JButton[]{showEmpPay, selectBtn, showAllBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        searchBtn = new JButton("Search PayGroup By ID");
        searchBtn.setBackground(DARK_BG);
        searchBtn.setForeground(Color.WHITE);

        //Search Panel Formatting
        searchPanel.add(new JLabel("PayGroup ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Top, left, bottom, right
        searchPanel.setOpaque(false);
        mainContent.add(searchPanel, BorderLayout.NORTH);

        // === TABLE FORMATTING ===
        tableModel = new DefaultTableModel(new String[]{
            "PayGroup_ID", "Pay_Rate", "Pay_Frequency", "Pay_Period", "Overtime_Rate", "Name"
        }, 0);
        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.GRAY);
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
        inputPanel.setPreferredSize(new Dimension(0, 160)); // Increased height for space
        inputPanel.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for better positioning

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);

        // Create buttons
        JButton addBtn = new JButton("Add"); // Instantiate Add Button
        editBtn = new JButton("Edit");
        saveBtn = new JButton("Save");

        // Style buttons to match Add Button
        for (JButton btn : new JButton[]{addBtn, editBtn, saveBtn}) {
            btn.setBackground(DARK_BG);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(100, 30)); // Set uniform size
        }

        // Initially hide the Save button
        saveBtn.setVisible(false);

        // Add buttons to the button panel
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(saveBtn);

        // Initialize the text fields
        payRateField = new JTextField(15);
        payRateField.setPreferredSize(new Dimension(200, 500)); // Set height to 30 for better readability

        payFrequencyField = new JTextField(15);
        payFrequencyField.setPreferredSize(new Dimension(200, 30)); // Set height to 30 for better readability

        payPeriodField = new JTextField(15);
        payPeriodField.setPreferredSize(new Dimension(200, 30)); // Set height to 30 for better readability

        overtimeRateField = new JTextField(15);
        overtimeRateField.setPreferredSize(new Dimension(200, 30)); // Set height to 30 for better readability

        nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(200, 30)); // Set height to 30 for better readability

        // Create a panel for input fields and labels
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Grid layout for labels and fields
        fieldsPanel.setOpaque(false); // Make it transparent
        fieldsPanel.setVisible(false); // Initially hidden

        // Add fields and labels to the fields panel
        fieldsPanel.add(new JLabel("Pay Rate:"));
        fieldsPanel.add(payRateField);
        fieldsPanel.add(new JLabel("Pay Frequency:"));
        fieldsPanel.add(payFrequencyField);
        fieldsPanel.add(new JLabel("Pay Period:"));
        fieldsPanel.add(payPeriodField);
        fieldsPanel.add(new JLabel("Overtime Rate:"));
        fieldsPanel.add(overtimeRateField);
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);

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
            payRateField.setText((String) tableModel.getValueAt(selectedRow, 1));
            payFrequencyField.setText((String) tableModel.getValueAt(selectedRow, 2));
            payPeriodField.setText((String) tableModel.getValueAt(selectedRow, 3));
            overtimeRateField.setText((String) tableModel.getValueAt(selectedRow, 4));
            nameField.setText((String) tableModel.getValueAt(selectedRow, 5));

            // Make the fields visible but not editable
            fieldsPanel.setVisible(true);
            payRateField.setEditable(false);
            payFrequencyField.setEditable(false);
            payPeriodField.setEditable(false);
            overtimeRateField.setEditable(false);
            nameField.setEditable(false);
        });

        editBtn.addActionListener(e -> {
            fieldsPanel.setVisible(true); // Show fields and labels
            payRateField.setEditable(true);
            payFrequencyField.setEditable(true);
            payPeriodField.setEditable(true);
            overtimeRateField.setEditable(true);
            nameField.setEditable(true);
            saveBtn.setVisible(true); // Show the Save button
        });

        saveBtn.addActionListener(e -> {
            updatePayGroup();
            fieldsPanel.setVisible(false); // Hide fields and labels after saving
            saveBtn.setVisible(false); // Hide the Save button after saving
        });

        // Add the input panel to the bottom of the table wrapper
        tableWrapper.add(inputPanel, BorderLayout.SOUTH);

        mainContent.add(tableWrapper, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // === ACTION LISTENERS - These say what happens when button is pressed ===

        searchBtn.addActionListener(e -> searchProduct());
        showEmpPay.addActionListener(e -> standIn());
        showAllBtn.addActionListener(e -> loadAllPayGroups());
        addBtn.addActionListener(e -> openCreationWizard());

        // === INITIAL LOAD OF TABLE ===

        loadAllPayGroups();
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
        String payGroupNo = searchField.getText().trim();
        if (payGroupNo.isEmpty()) { //If nothing in search, error message shows
            showError("Please enter an Pay Group ID to search.");
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) { //SQL code and connection for finding row from ID
            String sql = "SELECT PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name FROM PAY_GROUP WHERE PayGroup_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, payGroupNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("PayGroup_ID"),
                    rs.getString("Pay_Rate"),
                    rs.getString("Pay_Frequency"),
                    rs.getString("Pay_Period"),
                    rs.getString("Overtime_Rate"),
                    rs.getString("Name")
                });
            } else {
                showError("No Pay Group found with Pay Group ID: " + payGroupNo);
            }
        } catch (Exception ex) {
            showError("Error searching for payGroup: " + ex.getMessage());
        }

        padTableRows(35); // This keeps the empty rows there for design purposes
    }

    // === CALLED WHEN SHOW ALL PRESSED, SHOWS PRODUCT DETAILS ===

    private void loadAllPayGroups() {

        tableModel.setColumnIdentifiers(new String[]{"PayGroup_ID", "Pay_Rate", "Pay_Frequency", "Pay_Period", "Overtime_Rate", "Name"});
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name FROM PAY_GROUP";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("PayGroup_ID"),
                    rs.getString("Pay_Rate"),
                    rs.getString("Pay_Frequency"),
                    rs.getString("Pay_Period"),
                    rs.getString("Overtime_Rate"),
                    rs.getString("Name")
                });
            }
        } catch (Exception ex) {
            showError("Error loading payGroups: " + ex.getMessage());
        }

        padTableRows(35); // Keeps empty rows for design
        selectBtn.setEnabled(true); // Enable Select button
    }

        // === CALLED WHEN DEPARTMENTS PRESSED, SHOWS PRODUCT TO DEPARTMENT DETAILS ===

        private void standIn() {

        }


    // === CALLED WHEN ADD BUTTON PRESSED, CONTROLS MODAL ===

    private void openCreationWizard() {
        JDialog wizard = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Pay Group", true);
        wizard.setSize(500, 400);
        wizard.setLocationRelativeTo(this);
        wizard.setLayout(new BorderLayout(10, 10)); // Add padding around the dialog

        // ===== CardLayout for Multi-Page Wizard =====
        JPanel cardPanel = new JPanel(new CardLayout(10, 10)); // Add padding between cards
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

        // Page Counter
        JLabel pageCounter = new JLabel("Page 1 of 1", JLabel.CENTER);
        pageCounter.setFont(new Font("Tahoma", Font.BOLD, 14));

        // Only include the specified fields
        String[] visibleFields = {"Pay_Rate", "Pay_Frequency", "Pay_Period", "Overtime_Rate", "Name"};
        JTextField[] wizardFields = new JTextField[visibleFields.length];

        JPanel pagePanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Add padding between fields
        pagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the page

        // Add fields to the wizard
        for (int i = 0; i < visibleFields.length; i++) {
            String fieldName = visibleFields[i];
            pagePanel.add(new JLabel(fieldName + ":"));
            JTextField field = new JTextField();
            wizardFields[i] = field;
            pagePanel.add(field);
        }

        cardPanel.add(pagePanel, "Page1");

        // ===== Navigation Buttons =====
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 10)); // Add padding around buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton finishButton = new JButton("Finish");

        // Add action listener for the Finish button
        finishButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Generate PayGroup_ID
                String payGroupId = "PG-";
                String countQuery = "SELECT COUNT(*) AS Total FROM PAY_GROUP";
                ResultSet rs = conn.createStatement().executeQuery(countQuery);
                if (rs.next()) {
                    int count = rs.getInt("Total") + 1;
                    payGroupId += String.format("%03d", count); // Format as PG-XXX
                }

                // Build SQL query
                String sql = "INSERT INTO PAY_GROUP (PayGroup_ID, Pay_Rate, Pay_Frequency, Pay_Period, Overtime_Rate, Name) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Set PayGroup_ID
                ps.setString(1, payGroupId);

                // Set visible fields
                for (int i = 0; i < visibleFields.length; i++) {
                    ps.setString(i + 2, wizardFields[i].getText());
                }

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Pay Group added successfully!");
                    loadAllPayGroups(); // Refresh the table
                    wizard.dispose(); // Close the wizard
                }
            } catch (Exception ex) {
                showError("Error adding pay group: " + ex.getMessage());
            }
        });

        navButtons.add(finishButton);
        buttonPanel.add(pageCounter, BorderLayout.NORTH); // Add page counter
        buttonPanel.add(navButtons, BorderLayout.SOUTH);

        // ===== Add Components to Wizard =====
        wizard.add(cardPanel, BorderLayout.CENTER);
        wizard.add(buttonPanel, BorderLayout.SOUTH);
        wizard.setVisible(true);
    }

    // Enable editing when the "Edit" button is clicked
    private void enableEditing() {
        payFrequencyField.setVisible(true);
        payRateField.setVisible(true);
        payPeriodField.setVisible(true);
        overtimeRateField.setVisible(true);
        nameField.setVisible(true);

        payRateField.setEditable(true);
        payFrequencyField.setEditable(true);
        payPeriodField.setEditable(true);
        overtimeRateField.setEditable(true);
        nameField.setEditable(true);

        saveBtn.setEnabled(true); // Enable the Update button
    }

    // Update the database when the "Update" button is clicked
    private void updatePayGroup() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a Pay Group to update.");
            return;
        }

        String payGroupId = (String) tableModel.getValueAt(selectedRow, 0); // Get PayGroup_ID from the table
        String payRate = payRateField.getText();
        String payFrequency = payFrequencyField.getText();
        String payPeriod = payPeriodField.getText();
        String overtimeRate = overtimeRateField.getText();
        String name = nameField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE PAY_GROUP SET Pay_Rate = ?, Pay_Frequency = ?, Pay_Period = ?, Overtime_Rate = ?, Name = ? WHERE PayGroup_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, payRate);
            ps.setString(2, payFrequency);
            ps.setString(3, payPeriod);
            ps.setString(4, overtimeRate);
            ps.setString(5, name);
            ps.setString(6, payGroupId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Pay Group updated successfully!");
                loadAllPayGroups(); // Refresh the table
            } else {
                showError("Failed to update Pay Group.");
            }
        } catch (Exception ex) {
            showError("Error updating Pay Group: " + ex.getMessage());
        }

        // Disable editing after update
        payRateField.setEditable(false);
        payFrequencyField.setEditable(false);
        payPeriodField.setEditable(false);
        overtimeRateField.setEditable(false);
        nameField.setEditable(false);

        saveBtn.setEnabled(false); // Disable the Update button
    }
}