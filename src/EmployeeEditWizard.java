import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class EmployeeEditWizard extends JDialog {

    private final String[] fieldNames = {
        "SSN", "FName", "MName", "LName", "DOB", "Address", "Sex", "Nationality",
        "Ethnic_ID", "Marital_Status", "Disability_Status", "Location", "Cost_Center",
        "Seniority", "Job_Code", "Job_Desc", "Employee_Type", "Department_ID", "Product_ID", "Office_ID"
    };

    private final Map<String, String> fieldLabels = Map.ofEntries(
    Map.entry("SSN", "SSN (No Hyphens or Spaces)"),
    Map.entry("FName", "First Name"),
    Map.entry("MName", "Middle Name"),
    Map.entry("LName", "Last Name"),
    Map.entry("DOB", "Date of Birth (YYYY-MM-DD)"),
    Map.entry("Address", "Home Address"),
    Map.entry("Sex", "Gender"),
    Map.entry("Nationality", "Nationality"),
    Map.entry("Ethnic_ID", "Ethnic Group"),
    Map.entry("Marital_Status", "Marital Status"),
    Map.entry("Disability_Status", "Disability Status"),
    Map.entry("Location", "Home City"),
    Map.entry("Cost_Center", "Cost Center"),
    Map.entry("Seniority", "Seniority (Senior, Mid, Junior)"),
    Map.entry("Job_Code", "Job Code"),
    Map.entry("Job_Desc", "Job Description"),
    Map.entry("Employee_Type", "Employee Type"),
    Map.entry("Department_ID", "Department"),
    Map.entry("Product_ID", "Product"),
    Map.entry("Office_ID", "Office")
);

    private final JTextField[] wizardFields = new JTextField[fieldNames.length];
    private final JComboBox<String> departmentDropdown = new JComboBox<>();
    private final JComboBox<String> officeDropdown = new JComboBox<>();
    private final JComboBox<String> employeeTypeDropdown = new JComboBox<>();
    private final JComboBox<String> sexDropdown = new JComboBox<>(new String[]{"M", "F"});
    private final JComboBox<String> productDropdown = new JComboBox<>();

    private final ButtonGroup maritalGroup = new ButtonGroup();
    private final JRadioButton maritalYes = new JRadioButton("Married");
    private final JRadioButton maritalNo = new JRadioButton("Single");

    private final ButtonGroup disabilityGroup = new ButtonGroup();
    private final JRadioButton disabilityYes = new JRadioButton("Disabled");
    private final JRadioButton disabilityNo = new JRadioButton("Not Disabled");
    private final JLabel disabilityDescLabel = new JLabel("Describe:");
    private final JTextField disabilityDesc = new JTextField();

    private final Map<String, java.util.List<String>> departmentToProductsMap = new HashMap<>();
    private final Map<String, String> departmentNameToIdMap = new HashMap<>();
    private final Map<String, String> productNameToIdMap = new HashMap<>();
    private final Map<String, String> employeeTypeNameToIdMap = new HashMap<>();
    private final Map<String, String> officeNameToIdMap = new HashMap<>();
    private final Map<String, String> employeeTypeToPayGroupMap = new HashMap<>();

    private final String employeeId;
    private final EmployeeUpdateListener updateListener;

    public EmployeeEditWizard(JFrame parent, String employeeId, EmployeeUpdateListener updateListener) {
        super(parent, "Edit Employee", true);
        this.employeeId = employeeId;
        this.updateListener = updateListener;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Add a WindowListener to invoke the callback when the dialog is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (updateListener != null) {
                    updateListener.onEmployeeDataUpdated();
                }
            }
        });

        // Load dropdown data
        loadDropdownData();

        JPanel cardPanel = new JPanel(new CardLayout(10, 10));
        cardPanel.setBackground(new Color(188, 223, 216));
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

        int fieldsPerPage = 5;
        int totalPages = (int) Math.ceil((double) fieldNames.length / fieldsPerPage);
        JLabel pageCounter = new JLabel("Page 1 of " + totalPages, JLabel.CENTER);

        disabilityDesc.setVisible(false);
        disabilityYes.addActionListener(e -> {
            disabilityDesc.setVisible(true);
            disabilityDescLabel.setVisible(true);
        });

        disabilityNo.addActionListener(e -> {
            disabilityDesc.setVisible(false);
            disabilityDescLabel.setVisible(false);
        });
        disabilityDesc.setVisible(false);
        disabilityDescLabel.setVisible(false);

        departmentDropdown.addActionListener(e -> {
            String selectedName = (String) departmentDropdown.getSelectedItem();
            java.util.List<String> products = departmentToProductsMap.getOrDefault(selectedName, new ArrayList<>());
            productDropdown.removeAllItems();
            for (String product : products) productDropdown.addItem(product);
        });

        maritalGroup.add(maritalYes);
        maritalGroup.add(maritalNo);
        disabilityGroup.add(disabilityYes);
        disabilityGroup.add(disabilityNo);

        // Build form pages
        for (int page = 0; page < totalPages; page++) {
            JPanel pagePanel = new JPanel(new GridLayout(0, 2, 10, 10));
            pagePanel.setBackground(new Color(188, 223, 216));
            pagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            for (int i = page * fieldsPerPage; i < Math.min((page + 1) * fieldsPerPage, fieldNames.length); i++) {
                String field = fieldNames[i];
                String labelText = fieldLabels.getOrDefault(field, field);
                pagePanel.add(new JLabel(labelText + ":"));

                switch (field) {
                    case "Sex":
                        pagePanel.add(sexDropdown);
                        break;
                    case "Employee_Type":
                        pagePanel.add(employeeTypeDropdown);
                        break;
                    case "Department_ID":
                        pagePanel.add(departmentDropdown);
                        break;
                    case "Office_ID":
                        pagePanel.add(officeDropdown);
                        break;
                    case "Marital_Status":
                        JPanel maritalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        maritalPanel.setBackground(new Color(188, 223, 216));
                        maritalPanel.add(maritalYes);
                        maritalPanel.add(maritalNo);
                        pagePanel.add(maritalPanel);
                        break;
                    case "Disability_Status":
                        JPanel disabilityContainer = new JPanel();
                        disabilityContainer.setBackground(new Color(188, 223, 216));
                        disabilityContainer.setLayout(new BoxLayout(disabilityContainer, BoxLayout.Y_AXIS));

                        JPanel disabilityButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        disabilityButtonsPanel.setBackground(new Color(188, 223, 216));
                        disabilityButtonsPanel.add(disabilityYes);
                        disabilityButtonsPanel.add(disabilityNo);

                        disabilityDesc.setPreferredSize(new Dimension(200, 24));

                        JPanel disabilityDescriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        disabilityDescriptionPanel.setBackground(new Color(188, 223, 216));
                        disabilityDescriptionPanel.add(disabilityDescLabel);
                        disabilityDescriptionPanel.add(disabilityDesc);

                        disabilityDesc.setVisible(false);
                        disabilityDescLabel.setVisible(false);
                        disabilityDescriptionPanel.setVisible(false);

                        disabilityYes.addActionListener(e -> {
                            disabilityDesc.setVisible(true);
                            disabilityDescLabel.setVisible(true);
                            disabilityDescriptionPanel.setVisible(true);
                            disabilityContainer.revalidate();
                            disabilityContainer.repaint();
                        });

                        disabilityNo.addActionListener(e -> {
                            disabilityDesc.setVisible(false);
                            disabilityDescLabel.setVisible(false);
                            disabilityDescriptionPanel.setVisible(false);
                            disabilityContainer.revalidate();
                            disabilityContainer.repaint();
                        });

                        disabilityContainer.add(disabilityButtonsPanel);
                        disabilityContainer.add(disabilityDescriptionPanel);

                        pagePanel.add(disabilityContainer);
                        break;
                    case "Product_ID":
                        pagePanel.add(productDropdown);
                        break;
                    default:
                        JTextField input = new JTextField();
                        wizardFields[i] = input;
                        pagePanel.add(input);
                        break;
                }
            }
            cardPanel.add(pagePanel, "Page" + page);
        }

        // === Navigation buttons ===
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 10));
        buttonPanel.setBackground(new Color(188, 223, 216));

        JPanel combinedButtons = new JPanel(new BorderLayout(10, 10));
        combinedButtons.setBackground(new Color(188, 223, 216));

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel editButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navButtons.setBackground(new Color(188, 223, 216));
        editButtons.setBackground(new Color(188, 223, 216));
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");

        backButton.setEnabled(false);

        final int[] currentPage = {0};

        backButton.addActionListener(e -> {
            currentPage[0]--;
            cardLayout.previous(cardPanel);
            pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
            if (currentPage[0] == 0) {
                backButton.setEnabled(false);
            }
            nextButton.setEnabled(true);
        });

        nextButton.addActionListener(e -> {
            currentPage[0]++;
            cardLayout.next(cardPanel);
            if (currentPage[0] < 4)
                pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
            else
                currentPage[0] = 0;
                pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
            backButton.setEnabled(true);
        });

        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());

        editButtons.add(updateButton);
        editButtons.add(deleteButton);

        navButtons.add(backButton);
        navButtons.add(nextButton);

        combinedButtons.add(navButtons, BorderLayout.EAST);
        combinedButtons.add(editButtons, BorderLayout.WEST);

        buttonPanel.add(pageCounter, BorderLayout.NORTH);
        buttonPanel.add(combinedButtons, BorderLayout.SOUTH);

        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        loadEmployeeData();
}


    private void loadDropdownData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load department names into department dropdown
            String departmentQuery = "SELECT Name, Department_ID FROM DEPARTMENT";
            PreparedStatement psDepartment = conn.prepareStatement(departmentQuery);
            ResultSet rsDepartment = psDepartment.executeQuery();
            departmentDropdown.removeAllItems();  // Clear existing items
            departmentNameToIdMap.clear();  // Reset department map
            departmentToProductsMap.clear();  // Reset department to product map

            while (rsDepartment.next()) {
                String departmentName = rsDepartment.getString("Name");
                String departmentId = rsDepartment.getString("Department_ID");
                departmentDropdown.addItem(departmentName);
                departmentNameToIdMap.put(departmentName, departmentId);

                // Load products for each department
                String productQuery = "SELECT Name FROM PRODUCT WHERE Department_ID = ?";
                PreparedStatement psProduct = conn.prepareStatement(productQuery);
                psProduct.setString(1, departmentId);
                ResultSet rsProduct = psProduct.executeQuery();

                // Store the products for the current department
                java.util.List<String> products = new ArrayList<>();
                while (rsProduct.next()) {
                    products.add(rsProduct.getString("Name"));
                }
                departmentToProductsMap.put(departmentName, products);
            }

            // Load office names into office dropdown
            String officeQuery = "SELECT Name, Office_ID FROM OFFICE";
            PreparedStatement psOffice = conn.prepareStatement(officeQuery);
            ResultSet rsOffice = psOffice.executeQuery();
            officeDropdown.removeAllItems();  // Clear existing items
            officeNameToIdMap.clear();  // Reset office map
            while (rsOffice.next()) {
                String officeName = rsOffice.getString("Name");
                String officeId = rsOffice.getString("Office_ID");
                officeDropdown.addItem(officeName);
                officeNameToIdMap.put(officeName, officeId);
            }

            // Load employee types into employee type dropdown
            String employeeTypeQuery = "SELECT Name, Employee_Type_ID FROM EMPLOYEE_TYPE";
            PreparedStatement psEmployeeType = conn.prepareStatement(employeeTypeQuery);
            ResultSet rsEmployeeType = psEmployeeType.executeQuery();
            employeeTypeDropdown.removeAllItems();  // Clear existing items
            employeeTypeNameToIdMap.clear();  // Reset employee type map
            while (rsEmployeeType.next()) {
                String employeeTypeName = rsEmployeeType.getString("Name");
                String employeeTypeId = rsEmployeeType.getString("Employee_Type_ID");
                employeeTypeDropdown.addItem(employeeTypeName);
                employeeTypeNameToIdMap.put(employeeTypeName, employeeTypeId);
            }

            // Load product names into product dropdown
            String productQuery = "SELECT Name, Product_ID FROM PRODUCT";
            PreparedStatement psProduct = conn.prepareStatement(productQuery);
            ResultSet rsProduct = psProduct.executeQuery();
            productDropdown.removeAllItems();  // Clear existing items
            productNameToIdMap.clear();  // Reset product map
            while (rsProduct.next()) {
                String productName = rsProduct.getString("Name");
                String productId = rsProduct.getString("Product_ID");
                productDropdown.addItem(productName);
                productNameToIdMap.put(productName, productId);
            }

        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading dropdown data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void loadEmployeeData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM EMPLOYEE WHERE Employee_No = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Pre-fill fields
                for (int i = 0; i < fieldNames.length; i++) {
                    String field = fieldNames[i];
                    String value = rs.getString(field);
                    if (wizardFields[i] != null) {
                        wizardFields[i].setText(value);
                    }
                }

                // Pre-select options for dropdowns, checkboxes, etc.
                sexDropdown.setSelectedItem(rs.getString("Sex"));

                // Pre-select department by ID if the dropdown is populated
                String departmentId = rs.getString("Department_ID");
                if (departmentDropdown.getItemCount() > 0) {
                    // Find the department name corresponding to the departmentId
                    String departmentName = departmentNameToIdMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(departmentId))
                            .map(Map.Entry::getKey)
                            .findFirst().orElse(null);
                    if (departmentName != null) {
                        departmentDropdown.setSelectedItem(departmentName);
                        // Now update the product dropdown based on the department
                        updateProductDropdown(departmentName); // Update product dropdown
                    } else {
                        departmentDropdown.setSelectedIndex(0); // Default to first item if no match found
                        updateProductDropdown(departmentDropdown.getSelectedItem().toString()); // Update products for the default department
                    }
                }

                // Pre-select employee type by ID if the dropdown is populated
                String employeeTypeId = rs.getString("Employee_Type");
                if (employeeTypeDropdown.getItemCount() > 0) {
                    String employeeTypeName = employeeTypeNameToIdMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(employeeTypeId))
                            .map(Map.Entry::getKey)
                            .findFirst().orElse(null);
                    if (employeeTypeName != null) {
                        employeeTypeDropdown.setSelectedItem(employeeTypeName);
                    } else {
                        employeeTypeDropdown.setSelectedIndex(0); // Default to first item if no match found
                    }
                }

                // Pre-select product by ID if the dropdown is populated
                String productId = rs.getString("Product_ID");
                if (productDropdown.getItemCount() > 0) {
                    String productName = productNameToIdMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(productId))
                            .map(Map.Entry::getKey)
                            .findFirst().orElse(null);
                    if (productName != null) {
                        productDropdown.setSelectedItem(productName);
                    } else {
                        productDropdown.setSelectedIndex(0); // Default to first item if no match found
                    }
                }

                // Pre-select office by ID if the dropdown is populated
                String officeId = rs.getString("Office_ID");
                if (officeDropdown.getItemCount() > 0) {
                    String officeName = officeNameToIdMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(officeId))
                            .map(Map.Entry::getKey)
                            .findFirst().orElse(null);
                    if (officeName != null) {
                        officeDropdown.setSelectedItem(officeName);
                    } else {
                        officeDropdown.setSelectedIndex(0); // Default to first item if no match found
                    }
                }

                // Marital and disability options
                if ("Single".equals(rs.getString("Marital_Status"))) {
                    maritalNo.setSelected(true);
                } else {
                    maritalYes.setSelected(true);
                }

                if ("Disabled".equals(rs.getString("Disability_Status"))) {
                    disabilityYes.setSelected(true);
                } else {
                    disabilityNo.setSelected(true);
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateProductDropdown(String selectedDepartment) {
        java.util.List<String> products = departmentToProductsMap.getOrDefault(selectedDepartment, new ArrayList<>());
        productDropdown.removeAllItems(); // Clear existing items
        for (String product : products) {
            productDropdown.addItem(product); // Add products for the selected department
        }
    }


    private void updateEmployee() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE EMPLOYEE SET ");
            for (String f : fieldNames) {
                sql.append(f).append(" = ?, ");
            }
            sql.append("Pay_Group = ? WHERE Employee_No = ?");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;

            for (int i = 0; i < fieldNames.length; i++) {
                switch (fieldNames[i]) {
                    case "Sex":
                        ps.setString(idx++, (String) sexDropdown.getSelectedItem());
                        break;
                    case "Employee_Type":
                        String empTypeName = (String) employeeTypeDropdown.getSelectedItem();
                        ps.setString(idx++, employeeTypeNameToIdMap.get(empTypeName));
                        break;
                    case "Department_ID":
                        String deptName = (String) departmentDropdown.getSelectedItem();
                        ps.setString(idx++, departmentNameToIdMap.get(deptName));
                        break;
                    case "Office_ID":
                        String officeName = (String) officeDropdown.getSelectedItem();
                        ps.setString(idx++, officeNameToIdMap.get(officeName));
                        break;
                    case "Product_ID":
                        String productName = (String) productDropdown.getSelectedItem();
                        ps.setString(idx++, productNameToIdMap.get(productName));
                        break;
                    case "Marital_Status":
                        ps.setInt(idx++, maritalYes.isSelected() ? 0 : 1); // 0 = Married, 1 = Single
                        break;
                    case "Disability_Status":
                        if (disabilityYes.isSelected()) {
                            String desc = disabilityDesc.getText().isBlank() ? "None" : disabilityDesc.getText();
                            ps.setString(idx++, desc);
                        } else {
                            ps.setString(idx++, "None");
                        }
                        break;
                    default:
                        ps.setString(idx++, wizardFields[i] != null ? wizardFields[i].getText() : null);
                        break;
                }
            }


            String empTypeId = employeeTypeNameToIdMap.get((String) employeeTypeDropdown.getSelectedItem());
            String payGroupId = employeeTypeToPayGroupMap.get(empTypeId);
            ps.setString(idx++, payGroupId); // Pay_Group

            ps.setString(idx++, employeeId); // WHERE clause: Employee_No

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. No matching employee found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            loadEmployeeData();

        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM EMPLOYEE WHERE Employee_No = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, employeeId);

                int deleted = ps.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            loadEmployeeData();
        }
    }


}
