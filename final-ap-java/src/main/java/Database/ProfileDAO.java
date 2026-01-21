package Database;

import Model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileDAO {

    public UserProfile getProfile(String username) {
        String sql = "SELECT u.username, u.first_name, u.last_name, u.email, u.role, "
                + "COALESCE(w.balance, 0) AS balance "
                + "FROM users u "
                + "LEFT JOIN wallets w ON u.username = w.username "
                + "WHERE u.username = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("role"),
                            rs.getDouble("balance"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading profile: " + e.getMessage());
        }
        return null;
    }

    public java.util.List<UserProfile> getAllProfiles() {
        String sql = "SELECT u.username, u.first_name, u.last_name, u.email, u.role, "
                + "COALESCE(w.balance, 0) AS balance "
                + "FROM users u "
                + "LEFT JOIN wallets w ON u.username = w.username "
                + "ORDER BY u.username";
        java.util.List<UserProfile> list = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new UserProfile(
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getDouble("balance")));
            }
        } catch (Exception e) {
            System.out.println("Error loading profiles: " + e.getMessage());
        }
        return list;
    }
}
