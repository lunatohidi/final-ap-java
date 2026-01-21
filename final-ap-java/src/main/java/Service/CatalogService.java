package Service;

import Model.Catalog;
import Model.Product;

public class CatalogService implements Manageable {
    private Catalog catalog;

    public CatalogService(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public void addProduct(Product product) {
        catalog.addProduct(product);
        System.out.println("Product added to catalog: " + product.getName());
    }

    @Override
    public void removeProduct(Product product) {
        catalog.removeProduct(product);
        System.out.println("Product removed from catalog: " + product.getName());
    }

    @Override
    public void updateProduct(Product product) {
        System.out.println("Product updated: " + product.getName());
    }

}