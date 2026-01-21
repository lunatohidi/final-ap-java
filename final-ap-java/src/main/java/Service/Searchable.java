package Service;
import Model.Product;
import java.util.List;


public interface Searchable {
    List<Product> searchByName(String name);
    List<Product> searchByCategory(String category);
    List<Product> searchByPriceRange(double minPrice, double maxPrice);
}