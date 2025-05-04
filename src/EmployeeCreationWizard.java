import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeCreationWizard extends JDialog {

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

    private static final Color BACKGROUND_COLOR = new Color(188, 223, 216);

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
    private final Map<String, String> employeeTypeToPayGroupMap = new HashMap<>();
    private final Map<String, String> departmentNameToIdMap = new HashMap<>();
    private final Map<String, String> productNameToIdMap = new HashMap<>();
    private final Map<String, String> employeeTypeNameToIdMap = new HashMap<>();
    private final Map<String, String> officeNameToIdMap = new HashMap<>();

    public EmployeeCreationWizard(JFrame parent) {
        super(parent, "Add New Employee", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Load dropdown data
        loadDropdownData();

        JPanel cardPanel = new JPanel(new CardLayout(10, 10));
        cardPanel.setBackground(BACKGROUND_COLOR);
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        

        int fieldsPerPage = 5;
        int totalPages = (int) Math.ceil((double) fieldNames.length / fieldsPerPage);
        JLabel pageCounter = new JLabel("Page 1 of " + totalPages, JLabel.CENTER);

        disabilityDesc.setVisible(false);
        disabilityYes.addActionListener(e -> {
            // Show the disability description label and text field when "Disabled" is selected
            disabilityDesc.setVisible(true);
            disabilityDescLabel.setVisible(true);
        });
        
        disabilityNo.addActionListener(e -> {
            // Hide the disability description label and text field when "Not Disabled" is selected
            disabilityDesc.setVisible(false);
            disabilityDescLabel.setVisible(false);
        });
        disabilityDesc.setVisible(false); // Hide the text field initially
        disabilityDescLabel.setVisible(false); // Hide the label initially

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
            pagePanel.setBackground(BACKGROUND_COLOR);
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
                        maritalPanel.setBackground(BACKGROUND_COLOR);
                        maritalPanel.add(maritalYes);
                        maritalPanel.add(maritalNo);
                        pagePanel.add(maritalPanel);
                        break;
                        case "Disability_Status":
                        JPanel disabilityContainer = new JPanel();
                        disabilityContainer.setBackground(BACKGROUND_COLOR);
                        disabilityContainer.setLayout(new BoxLayout(disabilityContainer, BoxLayout.Y_AXIS));
                    
                        JPanel disabilityButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        disabilityButtonsPanel.setBackground(BACKGROUND_COLOR);
                        disabilityButtonsPanel.add(disabilityYes);
                        disabilityButtonsPanel.add(disabilityNo);
                    
                        disabilityDesc.setPreferredSize(new Dimension(200, 24));

                        JPanel disabilityDescriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        disabilityDescriptionPanel.setBackground(BACKGROUND_COLOR);
                        disabilityDescriptionPanel.add(disabilityDescLabel);
                        disabilityDescriptionPanel.add(disabilityDesc);
                    
                        disabilityDesc.setVisible(false);
                        disabilityDescLabel.setVisible(false);
                        disabilityDescriptionPanel.setVisible(false); // Hide container initially
                    
                        disabilityYes.addActionListener(e -> {
                            disabilityDesc.setVisible(true);
                            disabilityDescLabel.setVisible(true);
                            disabilityDescriptionPanel.setVisible(true); // Show under radio buttons
                            disabilityContainer.revalidate();
                            disabilityContainer.repaint();
                        });
                    
                        disabilityNo.addActionListener(e -> {
                            disabilityDesc.setVisible(false);
                            disabilityDescLabel.setVisible(false);
                            disabilityDescriptionPanel.setVisible(false); // Hide when "No"
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
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navButtons.setBackground(BACKGROUND_COLOR);
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");
        JButton finishButton = new JButton("Finish");

        backButton.setEnabled(false);
        finishButton.setVisible(false);

        final int[] currentPage = {0};

        backButton.addActionListener(e -> {
            currentPage[0]--;
            cardLayout.show(cardPanel, "Page" + currentPage[0]);
            backButton.setEnabled(currentPage[0] > 0);
            nextButton.setVisible(true);
            finishButton.setVisible(false);
            pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
        });

        nextButton.addActionListener(e -> {
            currentPage[0]++;
            cardLayout.show(cardPanel, "Page" + currentPage[0]);
            backButton.setEnabled(true);
            if (currentPage[0] == totalPages - 1) {
                nextButton.setVisible(false);
                finishButton.setVisible(true);
            }
            pageCounter.setText("Page " + (currentPage[0] + 1) + " of " + totalPages);
        });

        finishButton.addActionListener((ActionEvent e) -> saveEmployee());

        navButtons.add(backButton);
        navButtons.add(nextButton);
        navButtons.add(finishButton);

        buttonPanel.add(pageCounter, BorderLayout.WEST);
        buttonPanel.add(navButtons, BorderLayout.EAST);

        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDropdownData() {
        // Clear dropdowns and maps to prevent duplicate entries
        departmentDropdown.removeAllItems();
        departmentToProductsMap.clear();
        departmentNameToIdMap.clear();
        
        productDropdown.removeAllItems(); // Optional: You may want to clear this too
        productNameToIdMap.clear();
    
        employeeTypeDropdown.removeAllItems();
        employeeTypeToPayGroupMap.clear();
        employeeTypeNameToIdMap.clear();
    
        officeDropdown.removeAllItems();
        officeNameToIdMap.clear();
    
        try (Connection conn = DatabaseConnection.getConnection()) {
    
            // Load departments
            ResultSet rs = conn.createStatement().executeQuery("SELECT Department_ID, Name FROM DEPARTMENT");
            while (rs.next()) {
                String id = rs.getString("Department_ID");
                String name = rs.getString("Name");
                departmentDropdown.addItem(name);
                departmentNameToIdMap.put(name, id);
            }
    
            // Load department-to-product map (name-based)
            rs = conn.createStatement().executeQuery("SELECT Department_ID, Product_ID, Name FROM PRODUCT");
            while (rs.next()) {
                String deptId = rs.getString("Department_ID");
                String prodId = rs.getString("Product_ID");
                String prodName = rs.getString("Name");
                productNameToIdMap.put(prodName, prodId);
                departmentToProductsMap
                    .computeIfAbsent(departmentNameFromId(deptId), k -> new ArrayList<>())
                    .add(prodName);
            }
    
            // Load employee types
            rs = conn.createStatement().executeQuery("SELECT Employee_Type_ID, Name, PayGroup_ID FROM EMPLOYEE_TYPE");
            while (rs.next()) {
                String id = rs.getString("Employee_Type_ID");
                String name = rs.getString("Name");
                employeeTypeDropdown.addItem(name);
                employeeTypeNameToIdMap.put(name, id);
                employeeTypeToPayGroupMap.put(id, rs.getString("PayGroup_ID"));
            }
    
            // Load offices
            rs = conn.createStatement().executeQuery("SELECT Office_ID, Name FROM OFFICE");
            while (rs.next()) {
                String id = rs.getString("Office_ID");
                String name = rs.getString("Name");
                officeDropdown.addItem(name);
                officeNameToIdMap.put(name, id);
            }
    
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading dropdowns: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
        departmentDropdown.setSelectedIndex(0); // Triggers product population
    }
    

    private void saveEmployee() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String employeeNo = "Emp-";
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) AS Total FROM EMPLOYEE");
            if (rs.next()) {
                int count = rs.getInt("Total") + 1;
                employeeNo += String.format("%03d", count);
            }
    
            StringBuilder sql = new StringBuilder("INSERT INTO EMPLOYEE (");
            for (String f : fieldNames) {
                sql.append(f).append(", ");
            }
            sql.append("Employee_No, Last_Hired, Status, Pay_Group) VALUES (");
            sql.append("?,".repeat(fieldNames.length + 4));
            sql.setLength(sql.length() - 1); // remove trailing comma
            sql.append(")");
    
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
    
            for (int i = 0; i < fieldNames.length; i++) {
                switch (fieldNames[i]) {
                    case "Sex":
                        ps.setString(idx++, (String) sexDropdown.getSelectedItem());
                        break;
                    case "Employee_Type":
                        String empTypeName = (String) employeeTypeDropdown.getSelectedItem();
                        ps.setString(idx++, employeeTypeNameToIdMap.get(empTypeName)); // use ID
                        break;
                    case "Department_ID":
                        String deptName = (String) departmentDropdown.getSelectedItem();
                        ps.setString(idx++, departmentNameToIdMap.get(deptName)); // use ID
                        break;
                    case "Office_ID":
                        String officeName = (String) officeDropdown.getSelectedItem();
                        ps.setString(idx++, officeNameToIdMap.get(officeName)); // use ID
                        break;
                    case "Product_ID":
                        String productName = (String) productDropdown.getSelectedItem();
                        ps.setString(idx++, productNameToIdMap.get(productName)); // use ID
                        break;
                    case "Marital_Status":
                        ps.setInt(idx++, maritalYes.isSelected() ? 0 : 1);
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
    
            // Auto-filled fields
            ps.setString(idx++, employeeNo); // Employee_No
            ps.setDate(idx++, new java.sql.Date(System.currentTimeMillis())); // Last_Hired
            ps.setString(idx++, "Active"); // Status
    
            String empTypeId = employeeTypeNameToIdMap.get((String) employeeTypeDropdown.getSelectedItem());
            String payGroupId = employeeTypeToPayGroupMap.get(empTypeId);
            ps.setString(idx++, payGroupId); // Pay_Group
    
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();

        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    

    private String departmentNameFromId(String id) {
        return departmentNameToIdMap.entrySet()
            .stream()
            .filter(e -> e.getValue().equals(id))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(id);
    }
}
