package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WalletDAO {

    public WalletDAO() {
        ensureWalletTable();
    }

    private void ensureWalletTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS wallets ("
                + "username VARCHAR(50) PRIMARY KEY, "
                + "balance NUMERIC(12,2) NOT NULL DEFAULT 0"
                + ")";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            System.out.println("Error ensuring wallets table: " + e.getMessage());
        }
    }

    //موجودی برمبگرداند یا در صورت نبود، ردیف جدید می‌سازد
    public double getBalance(String username) throws SQLException {
        ensureRow(username);
        String sql = "SELECT balance FROM wallets WHERE username = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
                return 0.0;
            }
        }
    }

    public void updateBalance(String username, double balance) throws SQLException {
        ensureRow(username);
        String sql = "UPDATE wallets SET balance = ? WHERE username = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    private void ensureRow(String username) throws SQLException {
        String insert = "INSERT INTO wallets (username, balance) VALUES (?, 0) ON CONFLICT (username) DO NOTHING";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}
