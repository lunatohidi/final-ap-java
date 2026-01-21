package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public UserDAO() {
        ensureUsersTable();
        seedAdminUser();
    }

    private void ensureUsersTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS users ("
                + "id SERIAL PRIMARY KEY, "
                + "username VARCHAR(50) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "email VARCHAR(255), "
                + "first_name VARCHAR(100), "
                + "last_name VARCHAR(100), "
                + "role VARCHAR(50) DEFAULT 'customer'"
                + ")";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
            stmt.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'customer'");
            stmt.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100)");
            stmt.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100)");
            stmt.execute("UPDATE users SET role = 'customer' WHERE role IS NULL");
        } catch (SQLException e) {
            System.out.println("Error ensuring users table: " + e.getMessage());
        }
    }

    private void seedAdminUser() {
        String insertAdmin = "INSERT INTO users (username, password, email, role) "
                + "VALUES ('Tohidi', 'Lu123456', 'admin@example.com', 'admin') "
                + "ON CONFLICT (username) DO NOTHING";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(insertAdmin);
        } catch (SQLException e) {
            System.out.println("Error seeding admin user: " + e.getMessage());
        }
    }

    // متد برای اعتبارسنجی کاربر
    public String login(String username, String password) throws SQLException {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
                return null;
            }
        }
    }

    // متد برای ایجاد کاربر جدید
    public void createUser(String username, String password, String email, String firstName, String lastName) throws SQLException {
        String query = "INSERT INTO users (username, password, email, first_name, last_name, role) VALUES (?, ?, ?, ?, ?, 'customer')";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);

            stmt.executeUpdate();
        }
    }
}
