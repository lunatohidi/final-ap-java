package Database;

import Model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDAO {

    public ProductDAO() {
        ensureProductsTable();
        ensureRatingsTable();
    }

    private void ensureProductsTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS products ("
                + "id SERIAL PRIMARY KEY, "
                + "name VARCHAR(100) UNIQUE NOT NULL, "
                + "category VARCHAR(100), "
                + "price NUMERIC(12,2) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "image_url TEXT"
                + ")";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            System.out.println("Error ensuring products table: " + e.getMessage());
        }
    }

    private void ensureRatingsTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS product_ratings ("
                + "id SERIAL PRIMARY KEY, "
                + "product_name VARCHAR(100) NOT NULL, "
                + "rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5)"
                + ")";
        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            System.out.println("Error ensuring product_ratings table: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        String query = "SELECT p.name, p.category, p.price, p.quantity, p.image_url, "
                + "COALESCE(avg(r.rating),0) AS avg_rating, "
                + "COUNT(r.rating) AS rating_count "
                + "FROM products p "
                + "LEFT JOIN product_ratings r ON p.name = r.product_name "
                + "GROUP BY p.name, p.category, p.price, p.quantity, p.image_url "
                + "ORDER BY p.name";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_url"));
                product.setAverageRating(rs.getDouble("avg_rating"));
                product.setRatingCount(rs.getInt("rating_count"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, category, price, quantity, image_url) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (name) DO UPDATE SET category = EXCLUDED.category, price = EXCLUDED.price, "
                + "quantity = EXCLUDED.quantity, image_url = EXCLUDED.image_url";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantityStock());
            stmt.setString(5, product.getImageUrl());
            stmt.executeUpdate();
        }
    }

    public boolean removeProductByName(String name) throws SQLException {
        String query = "DELETE FROM products WHERE name = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            return stmt.executeUpdate() > 0;
        }
    }

    public void addRating(String productName, int rating) throws SQLException {
        String sql = "INSERT INTO product_ratings (product_name, rating) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productName);
            stmt.setInt(2, rating);
            stmt.executeUpdate();
        }
    }

    public boolean decrementQuantities(Map<String, Integer> productCounts) throws SQLException {
        if (productCounts == null || productCounts.isEmpty()) {
            return true;
        }
        String updateSql = "UPDATE products SET quantity = quantity - ? WHERE name = ? AND quantity >= ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            connection.setAutoCommit(false);
            for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                String name = entry.getKey();
                int count = entry.getValue();
                stmt.setInt(1, count);
                stmt.setString(2, name);
                stmt.setInt(3, count);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    connection.rollback();
                    return false;
                }
            }
            connection.commit();
            return true;
        }
    }
}
