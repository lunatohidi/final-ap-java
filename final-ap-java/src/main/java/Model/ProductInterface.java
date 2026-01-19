package Model;

public interface ProductInterface {
    String getName();
    void setName(String name);

    String getCategory();
    void setCategory(String category);

    double getPrice();
    void setPrice(double price);

    int getQuantityStock();
    void setQuantityStock(int quantityStock);

    String getImageUrl();
    void setImageUrl(String imageUrl);

    double getAverageRating();
    void setAverageRating(double rating);

    int getRatingCount();
    void setRatingCount(int count);
}
