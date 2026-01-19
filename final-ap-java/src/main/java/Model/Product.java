package Model;

public class Product implements ProductInterface {
    private String name;
    private String category;
    private double price;
    private int quantityStock;
    private String imageUrl;
    private double averageRating;
    private int ratingCount;

    public Product(String name, String category, double price, int quantityStock, String imageUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantityStock = quantityStock;
        this.imageUrl = imageUrl;
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int getQuantityStock() {
        return quantityStock;
    }

    @Override
    public void setQuantityStock(int quantityStock) {
        this.quantityStock = quantityStock;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public double getAverageRating() {
        return averageRating;
    }

    @Override
    public void setAverageRating(double rating) {
        this.averageRating = rating;
    }

    @Override
    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public void setRatingCount(int count) {
        this.ratingCount = count;
    }
}
