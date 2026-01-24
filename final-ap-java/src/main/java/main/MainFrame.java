package main;

import Model.Cart;
import View.LoginPanel;
import javax.swing.*;
import View.SignUpPanel;
import View.CatalogView;
import View.CartView;
import Database.UserDAO;
import Database.ProductDAO;
import View.AdminPanel;
import View.ProfilePanel;
import Model.Wallet;
import View.BackgroundPanel;
import Config.AppConfig;
import Database.WalletDAO;
import Database.WalletRequestDAO;
import Database.ProfileDAO;
import Model.UserProfile;

import java.awt.*;

/**
 * کلاس اصلی فریم برنامه - مدیریت کننده مرکزی رابط کاربری
 *
 * مسئولیت‌ها:
 * - ایجاد و مدیریت پنجره اصلی برنامه (JFrame)
 * - مدیریت ناوبری بین صفحات مختلف با استفاده از CardLayout
 * - مدیریت lifecycle کاربر جاری (لاگین، لاگ‌اوت)
 * - هماهنگی بین لایه‌های View و Database
 * - مدیریت تصویر پس‌زمینه داینامیک
 *
 * معماری:
 * - از الگوی Singleton برای مدیریت وضعیت برنامه استفاده می‌کند
 * - CardLayout برای جابجایی روان بین پنل‌های مختلف
 * - BackgroundPanel برای نمایش تصویر زمینه قابل تغییر
 */
public class MainFrame {

    // ════════════════════════════════════════════════════════════════
    // بخش اول: متغیرهای رابط کاربری
    // ════════════════════════════════════════════════════════════════

    /**
     * پنجره اصلی برنامه
     */
    private JFrame frame;

    /**
     * پنل اصلی که تمام پنل‌های دیگر را در خود جای می‌دهد
     */
    private JPanel mainPanel;

    /**
     * مدیر چیدمان کارتی - برای جابجایی بین صفحات
     */
    private CardLayout cardLayout;

    /**
     * پنل پس‌زمینه با قابلیت تغییر تصویر
     */
    private BackgroundPanel backgroundPanel;

    // ════════════════════════════════════════════════════════════════
    // بخش دوم: متغیرهای لایه دسترسی به داده (DAO)
    // ════════════════════════════════════════════════════════════════

    /**
     * دسترسی به جدول کاربران
     */
    private UserDAO userDAO;

    /**
     * دسترسی به جدول محصولات
     */
    private ProductDAO productDAO;

    /**
     * دسترسی به جدول کیف پول
     */
    private WalletDAO walletDAO;

    /**
     * دسترسی به جدول درخواست‌های کیف پول (افزایش موجودی)
     */
    private WalletRequestDAO walletRequestDAO;

    /**
     * دسترسی به جدول پروفایل کاربران
     */
    private ProfileDAO profileDAO;

    // ════════════════════════════════════════════════════════════════
    // بخش سوم: متغیرهای View (پنل‌های رابط کاربری)
    // ════════════════════════════════════════════════════════════════

    /**
     * پنل مدیریتی (مخصوص ادمین)
     */
    private AdminPanel adminPanel;

    /**
     * پنل نمایش کاتالوگ محصولات
     */
    private CatalogView catalogView;

    /**
     * پنل نمایش سبد خرید
     */
    private CartView cartView;

    /**
     * پنل نمایش پروفایل کاربر
     */
    private ProfilePanel profilePanel;

    // ════════════════════════════════════════════════════════════════
    // بخش چهارم: متغیرهای Model (منطق کسب‌وکار)
    // ════════════════════════════════════════════════════════════════

    /**
     * سبد خرید کاربر جاری
     */
    private Cart cart;

    /**
     * کیف پول کاربر جاری
     */
    private Wallet wallet;

    /**
     * نام کاربری کاربر فعلی (null اگر لاگین نکرده باشد)
     */
    private String currentUsername;

    // ════════════════════════════════════════════════════════════════
    // بخش پنجم: نقطه ورود برنامه (Entry Point)
    // ════════════════════════════════════════════════════════════════

    /**
     * متد main - نقطه شروع اجرای برنامه
     *
     * از SwingUtilities.invokeLater استفاده می‌شود تا:
     * - رابط کاربری در Event Dispatch Thread (EDT) اجرا شود
     * - از مشکلات Thread Safety جلوگیری شود
     * - رفتار پیش‌بینی‌پذیر Swing تضمین شود
     *
     * @param args آرگومان‌های خط فرمان (استفاده نمی‌شود)
     */
    public static void main(String[] args) {
        // اجرای ایجاد رابط کاربری در EDT
        SwingUtilities.invokeLater(() -> {
            new MainFrame().createAndShowGUI();
        });
    }

    // ════════════════════════════════════════════════════════════════
    // بخش ششم: ایجاد و راه‌اندازی رابط کاربری
    // ════════════════════════════════════════════════════════════════

    /**
     * ایجاد و نمایش رابط کاربری کامل برنامه
     *
     * مراحل اجرا:
     * 1. ایجاد و پیکربندی JFrame اصلی
     * 2. مقداردهی اولیه تمام DAO ها
     * 3. مقداردهی اولیه Model ها (Cart, Wallet)
     * 4. ایجاد BackgroundPanel و CardLayout
     * 5. ساخت تمام پنل‌های View
     * 6. اضافه کردن پنل‌ها به CardLayout با نام‌های منحصربه‌فرد
     * 7. نمایش فریم
     *
     * نکته امنیتی: currentUsername به null مقداردهی می‌شود
     * تا کاربر مجبور به لاگین شود
     */
    public void createAndShowGUI() {
        // ──────────────────────────────────────────────────────────
        // مرحله 1: ایجاد و تنظیمات پنجره اصلی
        // ──────────────────────────────────────────────────────────
        frame = new JFrame("Mall Shopping");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // بستن برنامه با کلیک ✕
        frame.setSize(600, 400);  // ابعاد پیش‌فرض: 600×400 پیکسل

        // ──────────────────────────────────────────────────────────
        // مرحله 2: مقداردهی اولیه لایه دسترسی به داده
        // ──────────────────────────────────────────────────────────
        userDAO = new UserDAO();                    // مدیریت کاربران
        productDAO = new ProductDAO();              // مدیریت محصولات
        walletDAO = new WalletDAO();                // مدیریت کیف پول
        walletRequestDAO = new WalletRequestDAO();  // مدیریت درخواست‌های کیف پول
        profileDAO = new ProfileDAO();              // مدیریت پروفایل‌ها

        // ──────────────────────────────────────────────────────────
        // مرحله 3: مقداردهی اولیه مدل‌های کسب‌وکار
        // ──────────────────────────────────────────────────────────
        cart = new Cart();          // سبد خرید خالی
        wallet = new Wallet();      // کیف پول با موجودی صفر
        currentUsername = null;     // هیچ کاربری لاگین نکرده

        // ──────────────────────────────────────────────────────────
        // مرحله 4: ایجاد سیستم ناوبری CardLayout
        // ──────────────────────────────────────────────────────────
        cardLayout = new CardLayout();

        // ایجاد پنل پس‌زمینه با تصویر از تنظیمات
        backgroundPanel = new BackgroundPanel(AppConfig.getBackgroundImagePath());

        // استفاده از backgroundPanel به عنوان mainPanel
        mainPanel = backgroundPanel;
        mainPanel.setLayout(cardLayout);  // تنظیم CardLayout روی پنل اصلی

        // ──────────────────────────────────────────────────────────
        // مرحله 5: ایجاد تمام پنل‌های View
        // ──────────────────────────────────────────────────────────

        // پنل لاگین - دریافت UserDAO و مرجع MainFrame برای ناوبری
        LoginPanel loginPanel = new LoginPanel(this, userDAO);

        // پنل ثبت‌نام - دریافت UserDAO برای ساخت کاربر جدید
        SignUpPanel signUpPanel = new SignUpPanel(this, userDAO);

        // پنل کاتالوگ - نمایش محصولات + امکان افزودن به سبد خرید
        catalogView = new CatalogView(this, productDAO, profileDAO, cart);

        // پنل سبد خرید - نمایش محصولات انتخابی + پرداخت
        cartView = new CartView(cart, wallet, walletDAO, walletRequestDAO, productDAO, this);

        // پنل ادمین - مدیریت محصولات، درخواست‌ها، و کاربران
        adminPanel = new AdminPanel(this, productDAO, walletRequestDAO, walletDAO, profileDAO);

        // پنل پروفایل - نمایش اطلاعات کاربر + دکمه بازگشت به کاتالوگ
        profilePanel = new ProfilePanel(() -> switchToPanel("Catalog"));

        // ──────────────────────────────────────────────────────────
        // مرحله 6: ثبت پنل‌ها در CardLayout با نام‌های منحصربه‌فرد
        // ──────────────────────────────────────────────────────────
        mainPanel.add(loginPanel, "Login");                         // صفحه ورود
        mainPanel.add(signUpPanel, "SignUp");                       // صفحه ثبت‌نام
        mainPanel.add(catalogView.createCatalogPanel(), "Catalog"); // صفحه کاتالوگ
        mainPanel.add(cartView.createCartPanel(), "Cart");          // صفحه سبد خرید
        mainPanel.add(adminPanel, "Admin");                         // صفحه مدیریت
        mainPanel.add(profilePanel, "Profile");                     // صفحه پروفایل

        // ──────────────────────────────────────────────────────────
        // مرحله 7: نمایش فریم
        // ──────────────────────────────────────────────────────────
        frame.add(mainPanel);    // اضافه کردن پنل اصلی به فریم
        frame.setVisible(true);  // نمایش پنجره (پیش‌فرض: صفحه Login)
    }

    // ════════════════════════════════════════════════════════════════
    // بخش هفتم: متدهای ناوبری (Navigation Methods)
    // ════════════════════════════════════════════════════════════════

    /**
     * جابجایی به پنل مشخص شده
     *
     * این متد core method ناوبری است که توسط تمام متدهای دیگر
     * برای تغییر صفحه استفاده می‌شود.
     *
     * @param panelName نام پنل مقصد (باید در createAndShowGUI ثبت شده باشد)
     *                  مقادیر معتبر: "Login", "SignUp", "Catalog", "Cart", "Admin", "Profile"
     */
    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * نمایش صفحه کاتالوگ محصولات
     *
     * معمولاً پس از لاگین موفقیت‌آمیز کاربر عادی فراخوانی می‌شود
     */
    public void showCatalogPage() {
        switchToPanel("Catalog");
    }

    /**
     * نمایش صفحه سبد خرید
     *
     * قبل از نمایش، محتویات سبد را refresh می‌کند تا:
     * - تغییرات قیمت محصولات اعمال شود
     * - محصولات حذف‌شده از دیتابیس پاک شوند
     * - مجموع قیمت به‌روز شود
     */
    public void showCartPage() {
        if (cartView != null) {
            cartView.refreshCartPanel();  // به‌روزرسانی محتویات سبد
        }
        switchToPanel("Cart");
    }

    /**
     * نمایش صفحه پنل مدیریتی
     *
     * فقط برای کاربران با نقش "admin" قابل دسترسی است
     * (کنترل دسترسی در LoginPanel انجام می‌شود)
     */
    public void showAdminPage() {
        switchToPanel("Admin");
    }

    /**
     * نمایش صفحه پروفایل کاربر جاری
     *
     * مراحل:
     * 1. بررسی لاگین بودن کاربر (currentUsername != null)
     * 2. دریافت اطلاعات پروفایل از دیتابیس
     * 3. تنظیم اطلاعات در ProfilePanel
     * 4. نمایش صفحه پروفایل
     */
    public void showProfile() {
        if (currentUsername != null) {
            // دریافت پروفایل کامل کاربر از دیتابیس
            UserProfile profile = profileDAO.getProfile(currentUsername);

            // تنظیم اطلاعات در پنل پروفایل
            profilePanel.setProfile(profile);
        }
        switchToPanel("Profile");
    }

    // ════════════════════════════════════════════════════════════════
    // بخش هشتم: متدهای مدیریت وضعیت کاربر (User State Management)
    // ════════════════════════════════════════════════════════════════

    /**
     * مدیریت فرآیند پس از لاگین موفقیت‌آمیز
     *
     * این متد توسط LoginPanel پس از احراز هویت موفق فراخوانی می‌شود
     * و وضعیت برنامه را برای کاربر جدید آماده می‌کند.
     *
     * مراحل:
     * 1. ذخیره نام کاربری جاری
     * 2. پاک کردن سبد خرید (جلوگیری از اشتراک داده بین کاربران)
     * 3. بارگذاری موجودی کیف پول از دیتابیس
     * 4. به‌روزرسانی اطلاعات پروفایل
     *
     * @param username نام کاربری که با موفقیت لاگین کرده است
     */
    public void handleLoginSuccess(String username) {
        // ذخیره نام کاربری برای استفاده در سراسر برنامه
        this.currentUsername = username;

        // پاک کردن سبد خرید کاربر قبلی (امنیت + جلوگیری از باگ)
        cart.clear();

        // بارگذاری موجودی کیف پول از دیتابیس
        loadWalletBalance();

        // به‌روزرسانی پنل پروفایل با اطلاعات کاربر جدید
        if (profilePanel != null) {
            profilePanel.setProfile(profileDAO.getProfile(username));
        }
    }

    /**
     * بارگذاری موجودی کیف پول کاربر جاری از دیتابیس
     *
     * فرآیند:
     * 1. بررسی لاگین بودن کاربر
     * 2. دریافت موجودی از WalletDAO
     * 3. تنظیم موجودی در شیء Wallet
     *
     * مدیریت خطا:
     * - اگر کاربر لاگین نکرده → موجودی = 0
     * - اگر خطای دیتابیس رخ دهد → موجودی = 0 + لاگ خطا
     */
    private void loadWalletBalance() {
        // اگر کاربری لاگین نکرده، موجودی صفر
        if (currentUsername == null) {
            wallet.setBalance(0);
            return;
        }

        try {
            // دریافت موجودی از دیتابیس
            double balance = walletDAO.getBalance(currentUsername);
            wallet.setBalance(balance);

        } catch (Exception e) {
            // در صورت بروز خطا، لاگ و تنظیم موجودی صفر
            System.out.println("Error loading wallet balance: " + e.getMessage());
            wallet.setBalance(0);
        }
    }

    /**
     * دریافت نام کاربری کاربر جاری
     *
     * @return نام کاربری کاربر لاگین‌شده، یا null اگر کسی لاگین نکرده
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    // ════════════════════════════════════════════════════════════════
    // بخش نهم: متدهای مدیریت محتوا (Content Management)
    // ════════════════════════════════════════════════════════════════

    /**
     * به‌روزرسانی صفحه کاتالوگ
     *
     * این متد توسط AdminPanel پس از افزودن/حذف/ویرایش محصول
     * فراخوانی می‌شود تا تغییرات بلافاصله در کاتالوگ نمایش داده شوند.
     */
    public void refreshCatalog() {
        if (catalogView != null) {
            catalogView.refresh();
        }
    }

    // ════════════════════════════════════════════════════════════════
    // بخش دهم: متدهای مدیریت تصویر پس‌زمینه (Background Management)
    // ════════════════════════════════════════════════════════════════

    /**
     * تغییر تصویر پس‌زمینه برنامه
     *
     * این متد به صورت داینامیک تصویر زمینه را تغییر می‌دهد
     * بدون نیاز به restart برنامه.
     *
     * @param imagePath مسیر کامل فایل تصویر
     *                  (می‌تواند مسیر محلی یا URL باشد)
     */
    public void changeBackground(String imagePath) {
        backgroundPanel.setBackgroundImage(imagePath);
    }

    /**
     * انتخاب و تنظیم تصویر پس‌زمینه از طریق File Chooser
     *
     * مراحل:
     * 1. نمایش دیالوگ انتخاب فایل (JFileChooser)
     * 2. در صورت انتخاب فایل، دریافت مسیر مطلق
     * 3. فراخوانی changeBackground با مسیر انتخاب‌شده
     *
     * این متد معمولاً توسط دکمه‌های "Set Background" در
     * CatalogView و AdminPanel فراخوانی می‌شود.
     */
    public void changeBackgroundWithChooser() {
        // ایجاد دیالوگ انتخاب فایل
        JFileChooser chooser = new JFileChooser();

        // نمایش دیالوگ و دریافت نتیجه
        int res = chooser.showOpenDialog(frame);

        // اگر کاربر فایلی انتخاب کرد (نه Cancel)
        if (res == JFileChooser.APPROVE_OPTION) {
            // تغییر پس‌زمینه به تصویر انتخاب‌شده
            changeBackground(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
