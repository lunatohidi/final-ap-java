package Model;


import java.util.List;

public interface CatalogInterface {
    void addProduct(Product product);
    void removeProduct(Product product);
    List<Product> getAllProducts();
}