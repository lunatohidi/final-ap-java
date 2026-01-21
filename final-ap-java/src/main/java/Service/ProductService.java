package Service;
import Model.Catalog;
import Model.Product;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService implements Searchable {
    private Catalog catalog;

    public ProductService(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<Product> searchByName(String name) {
        return catalog.getAllProducts().stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByCategory(String category) {
        return catalog.getAllProducts().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByPriceRange(double minPrice, double maxPrice) {
        return catalog.getAllProducts().stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

}
