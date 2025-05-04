import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("Company Admin Dashboard");
        setSize(1000, 700); // Adjusted size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Toolbar =====
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton employeeBtn = new JButton("Employees");
        JButton departmentBtn = new JButton("Departments");
        JButton payBtn = new JButton("Pay Groups");
        JButton productsBtn = new JButton("Products");
        JButton assetsBtn = new JButton("Assets");
        JButton officeBtn = new JButton("Offices");
        JButton employeeTypeBtn = new JButton("Employee Types");

        toolBar.add(employeeBtn);
        toolBar.add(departmentBtn);
        toolBar.add(productsBtn);
        toolBar.add(assetsBtn);
        toolBar.add(officeBtn);
        toolBar.add(employeeTypeBtn);
        toolBar.add(payBtn);
        
        add(toolBar, BorderLayout.NORTH);

        // ===== CardLayout Panel =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Scrollable EmployeePanel
        EmployeePanel employeePanel = new EmployeePanel();
        JScrollPane employeeScrollPane = new JScrollPane(employeePanel);
        employeeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        employeeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(employeeScrollPane, "employee");
        contentPanel.add(new JScrollPane(new DepartmentPanel()), "department");
        contentPanel.add(new JScrollPane(new ProductPanel()), "product");
        contentPanel.add(new JScrollPane(new Assets()), "asset");
        contentPanel.add(new JScrollPane(new Office()), "office");
        contentPanel.add(new JScrollPane(new EmployeeType()), "employee type");
        contentPanel.add(new JScrollPane(new PayPanel()), "pay group");


        add(contentPanel, BorderLayout.CENTER);

        // ===== Button Listeners =====
        employeeBtn.addActionListener(e -> cardLayout.show(contentPanel, "employee"));
        departmentBtn.addActionListener(e -> cardLayout.show(contentPanel, "department"));
        payBtn.addActionListener(e -> cardLayout.show(contentPanel, "pay group"));
        productsBtn.addActionListener(e -> cardLayout.show(contentPanel, "product"));
        assetsBtn.addActionListener(e -> cardLayout.show(contentPanel, "asset"));
        officeBtn.addActionListener(e -> cardLayout.show(contentPanel, "office"));
        employeeTypeBtn.addActionListener(e -> cardLayout.show(contentPanel, "employee type"));
    }
}