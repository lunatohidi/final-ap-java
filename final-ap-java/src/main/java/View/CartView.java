package View;

import main.MainFrame;
import Model.Cart;
import Model.Product;
import Model.Wallet;
import Database.ProductDAO;
import Database.WalletDAO;
import Database.WalletRequestDAO;
import Model.WalletTopUpRequest;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * نمای سبد خرید - مدیریت محصولات، کیف پول و تسویه‌حساب
 */
public class CartView {

    // مدل‌های داده
    private Cart cart;
    private Wallet wallet;

    // مرجع به فریم اصلی برای ناوبری
    private MainFrame mainFrame;

    // کامپوننت‌های رابط کاربری
    private JPanel panel;
    private JLabel balanceLabel;
    private JTextField chargeField;
    private DefaultListModel<String> requestListModel = new DefaultListModel<>();
    private JList<String> requestList = new JList<>(requestListModel);

    // DAOها برای دسترسی به دیتابیس
    private ProductDAO productDAO;
    private WalletDAO walletDAO;
    private WalletRequestDAO walletRequestDAO;

    /**
     * سازنده - مقداردهی اولیه فیلدها
     */
    public CartView(Cart cart, Wallet wallet, WalletDAO walletDAO, WalletRequestDAO walletRequestDAO,
                    ProductDAO productDAO, MainFrame mainFrame) {
        this.cart = cart;
        this.wallet = wallet;
        this.walletDAO = walletDAO;
        this.walletRequestDAO = walletRequestDAO;
        this.productDAO = productDAO;
        this.mainFrame = mainFrame;
    }

    /**
     * ایجاد پنل سبد خرید
     * @return پنل با چیدمان عمودی (BoxLayout)
     */
    public JPanel createCartPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        renderCart();
        return panel;
    }

    /**
     * بروزرسانی نمای سبد خرید
     */
    public void refreshCartPanel() {
        if (panel != null) {
            renderCart();
        }
    }

    /**
     * ایجاد درخواست شارژ کیف پول
     * @param amount مبلغ مورد نظر برای شارژ
     */
    private void createTopUpRequest(double amount) {
        String username = mainFrame.getCurrentUsername();
        if (username == null || username.isBlank()) {
            JOptionPane.showMessageDialog(panel, "No user logged in.");
            return;
        }
        try {
            walletRequestDAO.createRequest(username, amount);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Failed to create request: " + e.getMessage());
        }
    }

    /**
     * بارگذاری لیست درخواست‌های شارژ کاربر جاری
     */
    private void loadUserRequests() {
        requestListModel.clear();
        String username = mainFrame.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return;
        }
        for (WalletTopUpRequest req : walletRequestDAO.getRequestsForUser(username)) {
            String msg = "#" + req.getId() + " | $" + req.getAmount() + " | " + req.getStatus();
            if (req.getAdminMessage() != null) {
                msg += " | msg: " + req.getAdminMessage();
            }
            requestListModel.addElement(msg);
        }
    }

    /**
     * رسم و نمایش کامل سبد خرید شامل:
     * - لیست محصولات با دکمه حذف
     * - قیمت کل
     * - اطلاعات کیف پول و شارژ
     * - لیست درخواست‌های شارژ
     * - دکمه‌های ناوبری
     */
    private void renderCart() {
        panel.removeAll();
        panel.add(new JLabel("Shopping Cart"));
        loadUserRequests();

        // نمایش محصولات در سبد
        List<Product> products = cart.getProducts();
        for (Product product : products) {
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new FlowLayout());
            productPanel.setOpaque(false);

            JLabel nameLabel = new JLabel("Product: " + product.getName());
            JLabel priceLabel = new JLabel("Price: " + product.getPrice());
            JButton removeButton = new JButton("Remove");

            // حذف محصول از سبد
            removeButton.addActionListener(e -> {
                cart.removeProduct(product);
                JOptionPane.showMessageDialog(panel, product.getName() + " removed from cart!");
                renderCart();
            });

            productPanel.add(nameLabel);
            productPanel.add(priceLabel);
            productPanel.add(removeButton);
            panel.add(productPanel);
        }

        // نمایش قیمت کل
        JLabel totalPriceLabel = new JLabel("Total Price: " + cart.getTotalPrice());
        panel.add(totalPriceLabel);

        // بخش کیف پول - نمایش موجودی، شارژ و پرداخت
        JPanel walletPanel = new JPanel(new FlowLayout());
        walletPanel.setOpaque(false);
        balanceLabel = new JLabel("Wallet: $" + wallet.getBalance());
        chargeField = new JTextField(8);
        JButton chargeButton = new JButton("Charge Wallet");
        chargeButton.addActionListener(e -> handleCharge());
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> handleCheckout());
        walletPanel.add(balanceLabel);
        walletPanel.add(new JLabel("Amount:"));
        walletPanel.add(chargeField);
        walletPanel.add(chargeButton);
        walletPanel.add(checkoutButton);
        panel.add(walletPanel);

        // بخش نمایش درخواست‌های شارژ کیف پول
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setOpaque(false);
        requestsPanel.add(new JLabel("Wallet requests (latest):"), BorderLayout.NORTH);
        requestList.setOpaque(false);
        requestList.setFocusable(false);
        JScrollPane reqScroll = new JScrollPane(requestList);
        reqScroll.setOpaque(false);
        reqScroll.getViewport().setOpaque(false);
        requestsPanel.add(reqScroll, BorderLayout.CENTER);
        JButton refreshReqBtn = new JButton("Refresh requests");
        refreshReqBtn.addActionListener(e -> loadUserRequests());
        requestsPanel.add(refreshReqBtn, BorderLayout.SOUTH);
        panel.add(requestsPanel);

        // دکمه‌های ناوبری
        JButton backToCatalogButton = new JButton("Back to Catalog");
        backToCatalogButton.addActionListener(e -> mainFrame.showCatalogPage());
        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> mainFrame.showProfile());
        JPanel nav = new JPanel(new FlowLayout());
        nav.setOpaque(false);
        nav.add(backToCatalogButton);
        nav.add(profileButton);
        panel.add(nav);

        panel.revalidate();
        panel.repaint();
    }

    /**
     * مدیریت درخواست شارژ کیف پول
     * - اعتبارسنجی مبلغ (عددی و مثبت)
     * - ارسال درخواست به ادمین
     */
    private void handleCharge() {
        String amountText = chargeField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Enter amount to charge.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(panel, "Amount must be positive.");
                return;
            }
            createTopUpRequest(amount);
            chargeField.setText("");
            JOptionPane.showMessageDialog(panel, "Request sent to admin. Wait for approval.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Amount must be a number.");
        }
    }

    /**
     * مدیریت فرآیند پرداخت و تسویه‌حساب
     * مراحل:
     * 1. بررسی خالی نبودن سبد
     * 2. بررسی کافی بودن موجودی کیف پول
     * 3. بررسی موجودی محصولات در انبار
     * 4. کسر موجودی محصولات از انبار
     * 5. کسر مبلغ از کیف پول و ذخیره در دیتابیس
     * 6. خالی کردن سبد خرید
     */
    private void handleCheckout() {
        double total = cart.getTotalPrice();
        if (total <= 0) {
            JOptionPane.showMessageDialog(panel, "Cart is empty.");
            return;
        }
        if (wallet.getBalance() < total) {
            JOptionPane.showMessageDialog(panel, "Not enough balance. Please charge your wallet.");
            return;
        }

        // شمارش تعداد هر محصول در سبد
        Map<String, Integer> counts = cart.countByName();

        try {
            // کاهش موجودی محصولات - در صورت عدم موجودی کافی، false برمی‌گرداند
            boolean stockOk = productDAO.decrementQuantities(counts);
            if (!stockOk) {
                JOptionPane.showMessageDialog(panel, "Not enough stock for one or more items.");
                return;
            }
            wallet.pay(total);
            persistWallet();
            cart.clear();
            JOptionPane.showMessageDialog(panel, "Payment successful! Thank you.");
            renderCart();
            mainFrame.refreshCatalog();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "DB error during checkout: " + ex.getMessage());
        } finally {
            balanceLabel.setText("Wallet: $" + wallet.getBalance());
        }
    }

    /**
     * ذخیره موجودی کیف پول در دیتابیس
     */
    private void persistWallet() {
        String username = mainFrame.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return;
        }
        try {
            walletDAO.updateBalance(username, wallet.getBalance());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Failed to save wallet balance: " + e.getMessage());
        }
    }
}
