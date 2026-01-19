package Model;
import java.util.ArrayList;
import java.util.List;

public class Catalog implements CatalogInterface {
    private List<Product> products;

    public Catalog() {
        this.products = new ArrayList<>();
    }

    @Override
    public void addProduct(Product product) {
        products.add(product);
    }

    @Override
    public void removeProduct(Product product) {
        products.remove(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return products;
    }
}