package Database;

import Model.WalletTopUpRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WalletRequestDAO {

    public WalletRequestDAO() {
        ensureTable();
    }

    private void ensureTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS wallet_requests ("
                + "id SERIAL PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL, "
                + "amount NUMERIC(12,2) NOT NULL, "
                + "status VARCHAR(20) NOT NULL DEFAULT 'pending', "
                + "admin_message TEXT"
                + ")";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            System.out.println("Error ensuring wallet_requests table: " + e.getMessage());
        }
    }

    public void createRequest(String username, double amount) throws SQLException {
        String sql = "INSERT INTO wallet_requests (username, amount, status) VALUES (?, ?, 'pending')";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        }
    }

    public List<WalletTopUpRequest> getRequestsForUser(String username) {
        String sql = "SELECT id, username, amount, status, admin_message FROM wallet_requests "
                + "WHERE username = ? ORDER BY id DESC LIMIT 10";
        List<WalletTopUpRequest> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new WalletTopUpRequest(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getDouble("amount"),
                            rs.getString("status"),
                            rs.getString("admin_message")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user wallet requests: " + e.getMessage());
        }
        return list;
    }

    public List<WalletTopUpRequest> getPendingRequests() {
        String sql = "SELECT id, username, amount, status, admin_message FROM wallet_requests WHERE status = 'pending' ORDER BY id";
        List<WalletTopUpRequest> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new WalletTopUpRequest(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("admin_message")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching wallet requests: " + e.getMessage());
        }
        return list;
    }

    public void approveRequest(int id, String username, double amount, WalletDAO walletDAO) throws SQLException {
        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);
            try {
                double current = walletDAO.getBalance(username);
                walletDAO.updateBalance(username, current + amount);
                String sql = "UPDATE wallet_requests SET status = 'approved', admin_message = 'Approved' WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void rejectRequest(int id, String adminMessage) throws SQLException {
        String sql = "UPDATE wallet_requests SET status = 'rejected', admin_message = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, adminMessage);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
