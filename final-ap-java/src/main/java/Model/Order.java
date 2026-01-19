package Model;
import java.util.ArrayList;
import java.util.List;


public class Order {
    private User user;
    private List<Product> products;  // لیست محصولات سفارش
    private double totalPrice;

    public Order(User user) {
        this.user = user;
        this.products = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // متد برای دریافت کاربر
    public User getUser() {
        return user;
    }

    // متد برای دریافت قیمت کل سفارش
    public double getTotalPrice() {
        return totalPrice;
    }

    // متد برای دریافت محصولات سفارش
    public List<Product> getProducts() {
        return products;
    }

    // متد برای اضافه کردن محصول به سفارش
    public void addProductToOrder(Product product) {
        this.products.add(product);
        this.totalPrice += product.getPrice();  // به‌روز کردن قیمت کل با اضافه شدن محصول جدید
    }

    // متد برای محاسبه قیمت کل بر اساس محصولات
    private double calculateTotalPrice() {
        double total = 0.0;
        for (Product product : products) {
            total += product.getPrice();  // جمع قیمت محصولات
        }
        return total;
    }

    // نمایش جزئیات سفارش
    public void displayOrder() {
        System.out.println("Order for user: " + user.getUsername());
        System.out.println("Total price: " + totalPrice);
        for (Product product : products) {
            System.out.println("Product: " + product.getName() + ", Price: " + product.getPrice());
        }
    }

    // متد برای حذف محصول از سفارش
    public void removeProductFromOrder(Product product) {
        if (this.products.remove(product)) {
            this.totalPrice -= product.getPrice();  // به‌روز کردن قیمت کل بعد از حذف محصول
            System.out.println("Product removed from order: " + product.getName());
        }
    }
}