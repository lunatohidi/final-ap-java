package View;
import Model.Order;
import Model.Product;
import javax.swing.*;
import java.awt.*;

/**
 * نمای سفارش - نمایش جزئیات یک سفارش شامل محصولات و قیمت کل
 * این کلاس مسئول رندر کردن اطلاعات سفارش در یک JPanel است
 */
public class OrderView {

    /**
     * ایجاد پنل نمایش جزئیات سفارش
     *
     * ساختار پنل:
     * - عنوان: نام کاربر سفارش‌دهنده
     * - لیست محصولات: هر محصول در یک ردیف جداگانه با نام و قیمت
     * - قیمت کل: مجموع قیمت تمام محصولات
     *
     * @param order شیء سفارش حاوی اطلاعات کاربر، لیست محصولات و قیمت کل
     * @return JPanel حاوی تمام اطلاعات سفارش با چیدمان عمودی
     */
    public JPanel createOrderPanel(Order order) {
        // ایجاد پنل اصلی با چیدمان عمودی (BoxLayout)
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // نمایش عنوان - شامل نام کاربر سفارش‌دهنده
        JLabel titleLabel = new JLabel("Order Details for " + order.getUser().getUsername());
        panel.add(titleLabel);

        // حلقه برای نمایش هر محصول در سفارش
        for (Product product : order.getProducts()) {
            // پنل جداگانه برای هر محصول با چیدمان افقی
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new FlowLayout());

            // برچسب نام محصول
            JLabel nameLabel = new JLabel("Product: " + product.getName());

            // برچسب قیمت محصول
            JLabel priceLabel = new JLabel("Price: " + product.getPrice());

            // افزودن برچسب‌ها به پنل محصول
            productPanel.add(nameLabel);
            productPanel.add(priceLabel);

            // افزودن پنل محصول به پنل اصلی
            panel.add(productPanel);
        }

        // نمایش قیمت کل سفارش (از متد getTotalPrice)
        JLabel totalPriceLabel = new JLabel("Total Price: " + order.getTotalPrice());
        panel.add(totalPriceLabel);

        return panel;
    }
}
