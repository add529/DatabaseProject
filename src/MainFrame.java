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

        JButton employeeBtn = new JButton("Employee");
        JButton departmentBtn = new JButton("Department");
        JButton projectBtn = new JButton("Project");
        JButton productsBtn = new JButton("Products");

        toolBar.add(employeeBtn);
        toolBar.add(departmentBtn);
        toolBar.add(projectBtn);
        toolBar.add(productsBtn);

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
        contentPanel.add(new JLabel("Project Screen (Coming Soon)"), "project");
        contentPanel.add(new JLabel("Products Screen (Coming Soon)"), "products");

        add(contentPanel, BorderLayout.CENTER);

        // ===== Button Listeners =====
        employeeBtn.addActionListener(e -> cardLayout.show(contentPanel, "employee"));
        departmentBtn.addActionListener(e -> cardLayout.show(contentPanel, "department"));
        projectBtn.addActionListener(e -> cardLayout.show(contentPanel, "project"));
        productsBtn.addActionListener(e -> cardLayout.show(contentPanel, "products"));
    }
}
