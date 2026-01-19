package Model;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> products;  // لیست محصولات در سبد خرید
    private double totalPrice;

    public Cart() {
        this.products = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // متد برای اضافه کردن محصول به سبد خرید
    public void addProduct(Product product) {
        products.add(product);
        totalPrice += product.getPrice();  // به‌روز کردن قیمت کل
    }

    // شمارش تعداد هر محصول برای کاهش موجودی یا نمایش
    public java.util.Map<String, Integer> countByName() {
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        for (Product p : products) {
            counts.merge(p.getName(), 1, Integer::sum);
        }
        return counts;
    }

    // متد برای حذف محصول از سبد خرید
    public boolean removeProduct(Product product) {
        if (products.remove(product)) {
            totalPrice -= product.getPrice();  // به‌روز کردن قیمت کل بعد از حذف محصول
            return true;
        }
        return false;
    }

    // پاک کردن سبد بعد از پرداخت
    public void clear() {
        products.clear();
        totalPrice = 0.0;
    }

    // متد برای دریافت لیست محصولات سبد خرید
    public List<Product> getProducts() {
        return products;
    }

    // متد برای دریافت قیمت کل سبد خرید
    public double getTotalPrice() {
        return totalPrice;
    }
}
