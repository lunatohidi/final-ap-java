package View;
import main.MainFrame;
import Model.Product;
import Database.ProductDAO;
import Database.ProfileDAO;
import Model.Cart;
import Model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Comparator;

/**
 * نمای کاتالوگ - نمایش لیست محصولات با قابلیت مرتب‌سازی، رتبه‌دهی و افزودن به سبد
 */
public class CatalogView {

    private MainFrame mainFrame;
    private ProductDAO productDAO;
    private ProfileDAO profileDAO;
    private Cart cart;

    // کامپوننت‌های UI
    private JPanel productsContainer;
    private JComboBox<String> sortBox;
    private JLabel profileSummary = new JLabel();

    /**
     * سازنده - مقداردهی اولیه فیلدها
     */
    public CatalogView(MainFrame mainFrame, ProductDAO productDAO, ProfileDAO profileDAO, Cart cart) {
        this.mainFrame = mainFrame;
        this.productDAO = productDAO;
        this.profileDAO = profileDAO;
        this.cart = cart;
    }

    /**
     * ایجاد پنل کاتالوگ شامل:
     * - خلاصه پروفایل کاربر (نام، نام خانوادگی، موجودی)
     * - لیست محصولات با قابلیت اسکرول
     * - فوتر با دکمه‌های مرتب‌سازی، رفرش، سبد خرید، پروفایل و تغییر پس‌زمینه
     */
    public JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("Welcome to Catalog Page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // نمایش خلاصه پروفایل کاربر جاری
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setOpaque(false);
        profilePanel.add(profileSummary);
        panel.add(profilePanel, BorderLayout.BEFORE_FIRST_LINE);
        refreshProfileSummary();

        // کانتینر محصولات با چیدمان عمودی
        productsContainer = new JPanel();
        productsContainer.setLayout(new BoxLayout(productsContainer, BoxLayout.Y_AXIS));
        productsContainer.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(productsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshProducts();

        // فوتر - دکمه‌های کنترلی و مرتب‌سازی
        JPanel footer = new JPanel(new FlowLayout());
        sortBox = new JComboBox<>(new String[]{"Name A-Z", "Price Low-High", "Price High-Low", "Rating High-Low"});
        sortBox.addActionListener(e -> refreshProducts());
        JButton refreshButton = new JButton("Refresh products");
        refreshButton.addActionListener(e -> refreshProducts());
        JButton viewCartButton = new JButton("Go to Cart");
        viewCartButton.addActionListener(e -> mainFrame.showCartPage());
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> mainFrame.switchToPanel("Login"));
        JButton bgButton = new JButton("Set Background");
        bgButton.addActionListener(e -> chooseBackground());
        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> mainFrame.showProfile());
        footer.add(sortBox);
        footer.add(refreshButton);
        footer.add(viewCartButton);
        footer.add(backButton);
        footer.add(bgButton);
        footer.add(profileButton);

        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * بروزرسانی کامل کاتالوگ (پروفایل + محصولات)
     */
    public void refresh() {
        refreshProfileSummary();
        refreshProducts();
    }

    /**
     * بارگذاری مجدد لیست محصولات و اعمال مرتب‌سازی
     */
    private void refreshProducts() {
        productsContainer.removeAll();
        List<Product> products = productDAO.getAllProducts();
        sortProducts(products);
        for (Product p : products) {
            productsContainer.add(buildProductRow(p));
        }
        productsContainer.revalidate();
        productsContainer.repaint();
    }

    /**
     * بروزرسانی خلاصه اطلاعات پروفایل کاربر
     * نمایش: نام کاربری، نام، نام خانوادگی، موجودی کیف پول
     */
    private void refreshProfileSummary() {
        String username = mainFrame.getCurrentUsername();
        if (username == null || username.isBlank()) {
            profileSummary.setText("Not logged in");
            return;
        }
        UserProfile profile = profileDAO.getProfile(username);
        if (profile == null) {
            profileSummary.setText("Profile not found");
            return;
        }
        profileSummary.setText("User: " + profile.getUsername() + " | " + profile.getFirstName() + " " + profile.getLastName()
                + " | Wallet: $" + profile.getWalletBalance());
    }

    /**
     * مرتب‌سازی محصولات بر اساس گزینه انتخابی
     * گزینه‌ها: نام (A-Z)، قیمت (کم-زیاد، زیاد-کم)، رتبه (زیاد-کم)
     */
    private void sortProducts(List<Product> products) {
        if (sortBox == null) return;
        String selected = (String) sortBox.getSelectedItem();
        if (selected == null) return;
        switch (selected) {
            case "Price Low-High":
                products.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "Price High-Low":
                products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                break;
            case "Rating High-Low":
                products.sort(Comparator.comparingDouble(Product::getAverageRating).reversed());
                break;
            default: // Name A-Z
                products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        }
    }

    /**
     * ساخت ردیف محصول شامل:
     * - تصویر (150x150)
     * - اطلاعات (نام، دسته، قیمت، تعداد، میانگین رتبه)
     * - دکمه‌های عملیات (افزودن به سبد، رتبه‌دهی)
     */
    private JPanel buildProductRow(Product product) {
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        row.setOpaque(false);

        // تصویر محصول
        JLabel imageLabel = createImageLabel(product.getImageUrl());
        row.add(imageLabel, BorderLayout.WEST);

        // اطلاعات محصول
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.add(new JLabel("Name: " + product.getName()));
        info.add(new JLabel("Category: " + product.getCategory()));
        info.add(new JLabel("Price: $" + product.getPrice()));
        info.add(new JLabel("Qty: " + product.getQuantityStock()));
        info.add(new JLabel("Rating: " + String.format("%.2f", product.getAverageRating()) + " (" + product.getRatingCount() + " votes)"));
        row.add(info, BorderLayout.CENTER);

        // دکمه‌های عملیاتی
        JButton addButton = new JButton("Add to Cart");
        addButton.addActionListener(e -> {
            cart.addProduct(product);
            JOptionPane.showMessageDialog(this.productsContainer, product.getName() + " added to cart.");
        });
        JButton rateButton = new JButton("Rate");
        rateButton.addActionListener(e -> handleRate(product));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(addButton);
        actions.add(rateButton);
        row.add(actions, BorderLayout.EAST);

        return row;
    }

    /**
     * ایجاد JLabel برای نمایش تصویر محصول
     * - اندازه ثابت: 150x150
     * - تصویر به صورت smooth scale می‌شود (140x140)
     * - در صورت عدم وجود تصویر: نمایش "No Image"
     */
    private JLabel createImageLabel(String path) {
        JLabel label;
        if (path == null || path.isBlank()) {
            label = new JLabel("No Image");
        } else {
            File file = new File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                label = new JLabel(new ImageIcon(scaled));
            } else {
                label = new JLabel("No Image");
            }
        }
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(150, 150));
        return label;
    }

    /**
     * مدیریت رتبه‌دهی محصول
     * - نمایش JSpinner با مقادیر 1-5
     * - ذخیره رتبه در دیتابیس و رفرش لیست
     */
    private void handleRate(Product product) {
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 5, 1);
        JSpinner spinner = new JSpinner(model);
        int result = JOptionPane.showConfirmDialog(productsContainer, spinner, "Rate " + product.getName(), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int rating = (int) spinner.getValue();
            try {
                productDAO.addRating(product.getName(), rating);
                JOptionPane.showMessageDialog(productsContainer, "Thanks for rating!");
                refreshProducts();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(productsContainer, "Failed to save rating: " + e.getMessage());
            }
        }
    }

    /**
     * باز کردن دیالوگ انتخاب فایل برای تغییر پس‌زمینه
     */
    private void chooseBackground() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(productsContainer);
        if (res == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            mainFrame.changeBackground(path);
        }
    }
}
