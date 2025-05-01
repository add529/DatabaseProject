import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("Company Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // optional: prevents it from being dragged

        JButton employeeBtn = new JButton("Employee");
        JButton departmentBtn = new JButton("Department");
        JButton projectBtn = new JButton("Project");
        JButton productsBtn = new JButton("Products");

        toolBar.add(employeeBtn);
        toolBar.add(departmentBtn);
        toolBar.add(projectBtn);
        toolBar.add(productsBtn);

        add(toolBar, BorderLayout.NORTH);

        // CardLayout content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new EmployeePanel(), "employee");
        contentPanel.add(new JLabel("Department Screen (Coming Soon)"), "department");
        contentPanel.add(new JLabel("Project Screen (Coming Soon)"), "project");
        contentPanel.add(new JLabel("Products Screen (Coming Soon)"), "products");

        add(contentPanel, BorderLayout.CENTER);

        // Button actions
        employeeBtn.addActionListener(e -> cardLayout.show(contentPanel, "employee"));
        departmentBtn.addActionListener(e -> cardLayout.show(contentPanel, "department"));
        projectBtn.addActionListener(e -> cardLayout.show(contentPanel, "project"));
        productsBtn.addActionListener(e -> cardLayout.show(contentPanel, "products"));
    }
}
