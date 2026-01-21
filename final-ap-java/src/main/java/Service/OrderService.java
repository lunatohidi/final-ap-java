package Service;
import Model.Order;
import Model.Product;
import Model.User;

public class OrderService {

    // تایید سفارش
    public void confirmOrder(Order order) {
        System.out.println("Order confirmed for user: " + order.getUser().getUsername());
        System.out.println("Total price: " + order.getTotalPrice());
    }

    // محاسبه قیمت کل برای سفارش
    public double calculateTotalPrice(Order order) {
        return order.getTotalPrice();
    }

    // نمایش جزئیات سفارش
    public void displayOrderDetails(Order order) {
        System.out.println("Order Details:");
        order.displayOrder();
    }

    // پردازش پرداخت
    public void processPayment(Order order) {
        System.out.println("Processing payment for order...");
        System.out.println("Amount: " + order.getTotalPrice());
        // فرض کنید پرداخت با موفقیت انجام می‌شود
        System.out.println("Payment successful for user: " + order.getUser().getUsername());
    }

    // تغییر وضعیت سفارش به "در حال پردازش"
    public void updateOrderStatusToProcessing(Order order) {
        System.out.println("Order status updated to 'Processing' for user: " + order.getUser().getUsername());
    }
}