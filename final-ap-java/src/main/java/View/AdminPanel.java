package View;

import Database.ProductDAO;
import Database.WalletRequestDAO;
import Database.WalletDAO;
import Database.ProfileDAO;
import Model.Product;
import Model.WalletTopUpRequest;
import Model.UserProfile;
import main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * پنل مدیریت ادمین - رابط کاربری اصلی برای مدیریت محصولات، درخواست‌های شارژ کیف پول و پروفایل‌های کاربران
 * این کلاس شامل سه تب اصلی است:
 * 1. مدیریت محصولات (افزودن، ویرایش، حذف)
 * 2. بررسی و تایید/رد درخواست‌های شارژ کیف پول
 * 3. مشاهده و مدیریت پروفایل‌های کاربران
 */
public class AdminPanel extends JPanel {

    // ========== فیلدهای دیتابیس - دسترسی به لایه داده ========== //
    private final ProductDAO productDAO;           // مدیریت عملیات محصولات در دیتابیس
    private final WalletRequestDAO walletRequestDAO; // مدیریت درخواست‌های شارژ کیف پول
    private final WalletDAO walletDAO;             // مدیریت عملیات کیف پول کاربران
    private final ProfileDAO profileDAO;           // مدیریت پروفایل‌های کاربران

    // ========== رفرنس به فریم اصلی ========== //
    private final MainFrame mainFrame;             // رفرنس به پنجره اصلی برای ناوبری بین صفحات

    // ========== فیلدهای فرم مدیریت محصول ========== //
    private final JTextField nameField = new JTextField();        // نام محصول
    private final JTextField categoryField = new JTextField();    // دسته‌بندی محصول
    private final JTextField priceField = new JTextField();       // قیمت محصول
    private final JTextField quantityField = new JTextField();    // تعداد موجودی
    private final JTextField imageUrlField = new JTextField();    // آدرس یا مسیر تصویر محصول

    // ========== کامپوننت‌های لیست محصولات ========== //
    private final DefaultListModel<String> productListModel = new DefaultListModel<>(); // مدل داده برای لیست محصولات
    private final JButton chooseImageButton = new JButton("Choose Image");              // دکمه انتخاب تصویر از سیستم فایل

    // ========== کامپوننت‌های مدیریت درخواست‌های کیف پول ========== //
    private final DefaultListModel<String> requestListModel = new DefaultListModel<>(); // مدل داده برای لیست درخواست‌ها
    private List<WalletTopUpRequest> pendingRequests = new java.util.ArrayList<>();     // لیست درخواست‌های در انتظار تایید

    // ========== کامپوننت‌های مدیریت پروفایل کاربران ========== //
    private final DefaultListModel<String> profileListModel = new DefaultListModel<>(); // مدل داده برای لیست پروفایل‌ها
    private List<UserProfile> profiles = new java.util.ArrayList<>();                   // لیست تمام پروفایل‌های کاربران
    private JLabel profileDetails = new JLabel("Select a user");                        // نمایش جزئیات پروفایل انتخاب شده

    /**
     * سازنده کلاس AdminPanel
     * @param mainFrame رفرنس به فریم اصلی برنامه
     * @param productDAO لایه دسترسی به داده محصولات
     * @param walletRequestDAO لایه دسترسی به درخواست‌های کیف پول
     * @param walletDAO لایه دسترسی به کیف پول کاربران
     * @param profileDAO لایه دسترسی به پروفایل کاربران
     */
    public AdminPanel(MainFrame mainFrame, ProductDAO productDAO, WalletRequestDAO walletRequestDAO, WalletDAO walletDAO, ProfileDAO profileDAO) {
        // مقداردهی اولیه فیلدها
        this.mainFrame = mainFrame;
        this.productDAO = productDAO;
        this.walletRequestDAO = walletRequestDAO;
        this.walletDAO = walletDAO;
        this.profileDAO = profileDAO;

        // تنظیمات اولیه پنل
        setLayout(new BorderLayout());
        setOpaque(false);  // شفاف کردن پنل برای نمایش تصویر پس‌زمینه

        // ایجاد تب‌های مختلف پنل ادمین
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Products", buildProductsPanel());           // تب مدیریت محصولات
        tabs.add("Wallet Requests", buildRequestsPanel());    // تب مدیریت درخواست‌های کیف پول
        tabs.add("Profiles", buildProfilesPanel());           // تب مشاهده پروفایل‌های کاربران
        add(tabs, BorderLayout.CENTER);

        // بارگذاری اولیه داده‌ها
        refreshProductList();  // بارگذاری لیست محصولات
        refreshRequests();     // بارگذاری درخواست‌های در انتظار
        refreshProfiles();     // بارگذاری پروفایل‌های کاربران
    }

    /**
     * ساخت پنل تب محصولات
     * شامل فرم افزودن/ویرایش، لیست محصولات و دکمه‌های کنترلی
     * @return پنل کامل مدیریت محصولات
     */
    private JPanel buildProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(buildFormPanel(), BorderLayout.NORTH);   // فرم ورودی در بالا
        panel.add(buildListPanel(), BorderLayout.CENTER);  // لیست محصولات در وسط
        panel.add(buildFooter(), BorderLayout.SOUTH);      // دکمه‌های کنترلی در پایین
        return panel;
    }

    /**
     * ساخت فرم ورودی اطلاعات محصول
     * شامل فیلدهای: نام، دسته‌بندی، قیمت، تعداد، تصویر و دکمه‌های عملیاتی
     * @return پنل فرم با ۷ ردیف و ۲ ستون
     */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(7, 2, 6, 6));
        form.setOpaque(false);

        // ردیف ۱: نام محصول
        form.add(new JLabel("Name"));
        form.add(nameField);

        // ردیف ۲: دسته‌بندی محصول
        form.add(new JLabel("Category"));
        form.add(categoryField);

        // ردیف ۳: قیمت محصول
        form.add(new JLabel("Price"));
        form.add(priceField);

        // ردیف ۴: تعداد موجودی
        form.add(new JLabel("Quantity"));
        form.add(quantityField);

        // ردیف ۵: آدرس تصویر با قابلیت انتخاب از فایل
        form.add(new JLabel("Image URL/Path"));
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.add(imageUrlField, BorderLayout.CENTER);           // فیلد متنی برای آدرس
        chooseImageButton.addActionListener(e -> handleChooseImage()); // دکمه انتخاب فایل
        imagePanel.add(chooseImageButton, BorderLayout.EAST);
        imagePanel.setOpaque(false);
        form.add(imagePanel);

        // ردیف ۶: دکمه افزودن یا بروزرسانی محصول
        JButton addBtn = new JButton("Add / Update");
        addBtn.addActionListener(e -> handleAddOrUpdate());

        // ردیف ۷: دکمه حذف محصول بر اساس نام
        JButton removeBtn = new JButton("Remove by name");
        removeBtn.addActionListener(e -> handleRemove());

        form.add(addBtn);
        form.add(removeBtn);
        return form;
    }

    /**
     * ساخت پنل نمایش لیست محصولات
     * @return پنل اسکرول‌پذیر حاوی لیست محصولات
     */
    private JScrollPane buildListPanel() {
        JList<String> list = new JList<>(productListModel);
        return new JScrollPane(list);
    }

    /**
     * ساخت پنل پایین صفحه با دکمه‌های کنترلی
     * شامل: رفرش لیست، تغییر پس‌زمینه، بازگشت به کاتالوگ
     * @return پنل حاوی دکمه‌های کنترلی
     */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setOpaque(false);

        // دکمه بارگذاری مجدد لیست محصولات از دیتابیس
        JButton refreshButton = new JButton("Refresh products");
        refreshButton.addActionListener(e -> refreshProductList());

        // دکمه تغییر تصویر پس‌زمینه
        JButton bgButton = new JButton("Set Background");
        bgButton.addActionListener(e -> mainFrame.changeBackgroundWithChooser());

        // دکمه بازگشت به صفحه کاتالوگ محصولات
        JButton backButton = new JButton("Back to Catalog");
        backButton.addActionListener(e -> mainFrame.showCatalogPage());

        footer.add(refreshButton);
        footer.add(bgButton);
        footer.add(backButton);
        return footer;
    }

    /**
     * ساخت پنل تب درخواست‌های شارژ کیف پول
     * شامل لیست درخواست‌ها و دکمه‌های تایید/رد/رفرش
     * @return پنل کامل مدیریت درخواست‌ها
     */
    private JPanel buildRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // نمایش لیست درخواست‌های در انتظار
        JList<String> list = new JList<>(requestListModel);
        JScrollPane scroll = new JScrollPane(list);
        panel.add(scroll, BorderLayout.CENTER);

        // پنل دکمه‌های عملیاتی
        JPanel actions = new JPanel(new FlowLayout());
        JButton approveBtn = new JButton("Approve");  // تایید درخواست
        JButton rejectBtn = new JButton("Reject");    // رد درخواست
        JButton refreshBtn = new JButton("Refresh");  // رفرش لیست
        actions.add(approveBtn);
        actions.add(rejectBtn);
        actions.add(refreshBtn);
        panel.add(actions, BorderLayout.SOUTH);

        // رویداد تایید درخواست
        approveBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) return;  // اگر چیزی انتخاب نشده باشد

            WalletTopUpRequest req = pendingRequests.get(idx);
            try {
                // تایید درخواست و بروزرسانی موجودی کیف پول کاربر
                walletRequestDAO.approveRequest(req.getId(), req.getUsername(), req.getAmount(), walletDAO);
                JOptionPane.showMessageDialog(panel, "Approved and wallet updated.");
                refreshRequests();  // بارگذاری مجدد لیست
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error approving: " + ex.getMessage());
            }
        });

        // رویداد رد درخواست
        rejectBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) return;  // اگر چیزی انتخاب نشده باشد

            WalletTopUpRequest req = pendingRequests.get(idx);
            // دریافت دلیل رد درخواست از ادمین
            String message = JOptionPane.showInputDialog(panel, "Reason for rejection:", "Reject request", JOptionPane.PLAIN_MESSAGE);
            if (message == null) return;  // اگر کنسل شد

            try {
                walletRequestDAO.rejectRequest(req.getId(), message);
                JOptionPane.showMessageDialog(panel, "Request rejected.");
                refreshRequests();  // بارگذاری مجدد لیست
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error rejecting: " + ex.getMessage());
            }
        });

        // رویداد رفرش لیست درخواست‌ها
        refreshBtn.addActionListener(e -> refreshRequests());

        return panel;
    }

    /**
     * ساخت پنل تب پروفایل‌های کاربران
     * شامل لیست کاربران، جزئیات پروفایل و دکمه رفرش
     * @return پنل کامل مشاهده پروفایل‌ها
     */
    private JPanel buildProfilesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // لیست نام کاربری‌ها
        JList<String> list = new JList<>(profileListModel);

        // نمایش جزئیات پروفایل هنگام انتخاب کاربر از لیست
        list.addListSelectionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0 && idx < profiles.size()) {
                UserProfile up = profiles.get(idx);
                // نمایش اطلاعات کامل کاربر به صورت HTML
                profileDetails.setText("<html>User: " + up.getUsername()
                        + "<br>Name: " + up.getFirstName() + " " + up.getLastName()
                        + "<br>Email: " + up.getEmail()
                        + "<br>Role: " + up.getRole()
                        + "<br>Wallet: $" + up.getWalletBalance()
                        + "</html>");
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        // دکمه رفرش لیست پروفایل‌ها
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshProfiles());

        // پنل پایین شامل جزئیات و دکمه رفرش
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(profileDetails, BorderLayout.CENTER);
        bottom.add(refresh, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * بارگذاری مجدد درخواست‌های در انتظار از دیتابیس
     * این متد لیست درخواست‌های کیف پول را از دیتابیس می‌خواند و در لیست نمایش می‌دهد
     */
    private void refreshRequests() {
        requestListModel.clear();  // پاک کردن لیست قبلی
        pendingRequests = walletRequestDAO.getPendingRequests();  // دریافت درخواست‌های جدید

        // افزودن هر درخواست به مدل لیست برای نمایش
        for (WalletTopUpRequest req : pendingRequests) {
            requestListModel.addElement("#" + req.getId() + " | " + req.getUsername() + " | $" + req.getAmount());
        }
    }

    /**
     * بارگذاری مجدد پروفایل‌های کاربران از دیتابیس
     * این متد تمام پروفایل‌ها را از دیتابیس می‌خواند و لیست را بروزرسانی می‌کند
     */
    private void refreshProfiles() {
        profileListModel.clear();  // پاک کردن لیست قبلی
        profiles = profileDAO.getAllProfiles();  // دریافت تمام پروفایل‌ها

        // افزودن نام کاربری هر پروفایل به لیست
        for (UserProfile up : profiles) {
            profileListModel.addElement(up.getUsername());
        }

        profileDetails.setText("Select a user");  // ریست کردن جزئیات نمایشی
    }

    /**
     * هندلر افزودن یا بروزرسانی محصول
     * این متد اطلاعات فرم را اعتبارسنجی کرده و محصول را در دیتابیس ذخیره می‌کند
     */
    private void handleAddOrUpdate() {
        // خواندن و پاکسازی مقادیر ورودی
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String imageUrl = imageUrlField.getText().trim();

        // اعتبارسنجی: بررسی خالی نبودن فیلدهای ضروری
        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, price, and quantity are required.");
            return;
        }

        double price;
        int quantity;
        try {
            // تبدیل رشته به عدد و اعتبارسنجی فرمت
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number and quantity must be an integer.");
            return;
        }

        try {
            // افزودن یا بروزرسانی محصول در دیتابیس
            productDAO.addProduct(new Product(name, category, price, quantity, imageUrl));
            JOptionPane.showMessageDialog(this, "Product saved.");
            refreshProductList();  // رفرش لیست محصولات
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    /**
     * هندلر حذف محصول بر اساس نام
     * این متد محصول با نام مشخص شده را از دیتابیس حذف می‌کند
     */
    private void handleRemove() {
        String name = nameField.getText().trim();

        // اعتبارسنجی: بررسی خالی نبودن نام
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required to remove.");
            return;
        }

        try {
            boolean removed = productDAO.removeProductByName(name);
            if (removed) {
                JOptionPane.showMessageDialog(this, "Product removed.");
            } else {
                JOptionPane.showMessageDialog(this, "No product found with that name.");
            }
            refreshProductList();  // رفرش لیست محصولات
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    /**
     * هندلر انتخاب فایل تصویر از سیستم فایل
     * این متد یک دیالوگ انتخاب فایل را نمایش داده و مسیر فایل انتخابی را در فیلد قرار می‌دهد
     */
    private void handleChooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        // اگر کاربر فایلی انتخاب کرد (نه کنسل)
        if (result == JFileChooser.APPROVE_OPTION) {
            imageUrlField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * بارگذاری مجدد لیست محصولات از دیتابیس
     * این متد تمام محصولات را از دیتابیس می‌خواند و در لیست نمایش می‌دهد
     * فرمت نمایش: نام | دسته‌بندی | قیمت | موجودی | آدرس تصویر
     */
    private void refreshProductList() {
        productListModel.clear();  // پاک کردن لیست قبلی
        List<Product> products = productDAO.getAllProducts();  // دریافت تمام محصولات

        // افزودن هر محصول به مدل لیست با فرمت مشخص
        for (Product product : products) {
            productListModel.addElement(product.getName() + " | " + product.getCategory()
                    + " | $" + product.getPrice() + " | qty: " + product.getQuantityStock()
                    + " | image: " + (product.getImageUrl() == null ? "-" : product.getImageUrl()));
        }
    }
}
