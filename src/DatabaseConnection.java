import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Class responsible for managing the database connection
class DatabaseConnection {
    // DB credentials and URL
    private static final String USERNAME = "sschatz1";
    private static final String PASSWORD = "COSC*8sm0s";
    private static final String URL = "jdbc:mysql://triton.towson.edu:3360/sschatz1db"; //can use anyone's towsondb, I just put mine

    // Returns a MySQL database connection
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver"); // Load JDBC driver
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}