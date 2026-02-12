package Database;
import Config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection connect() throws SQLException {
        String url = AppConfig.getDatabaseUrl();
        String user = AppConfig.getDatabaseUser();
        try {
            Connection connection = DriverManager.getConnection(
                    url,
                    user,
                    AppConfig.getDatabasePassword());
            System.out.println("Connected to PostgreSQL successfully!");
            return connection;
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println("JDBC URL: " + url);
            System.out.println("DB User: " + user);
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            connect();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
}
