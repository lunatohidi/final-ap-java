package Service;
import Model.Product;

public interface Manageable {
    void addProduct(Product product);
    void removeProduct(Product product);
    void updateProduct(Product product);
}