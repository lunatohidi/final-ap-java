package View;

import Model.UserProfile;

import javax.swing.*;
import java.awt.*;

/**
 * پنل پروفایل - نمایش اطلاعات کامل پروفایل کاربر
 * امکانات: نمایش نام کاربری، نام کامل، ایمیل، نقش و موجودی کیف پول
 */
public class ProfilePanel extends JPanel {

    // برچسب‌های نمایش اطلاعات پروفایل
    private JLabel nameLabel = new JLabel();           // نام کامل (FirstName + LastName)
    private JLabel usernameLabel = new JLabel();       // نام کاربری
    private JLabel emailLabel = new JLabel();          // آدرس ایمیل
    private JLabel roleLabel = new JLabel();           // نقش کاربر (admin/user)
    private JLabel walletLabel = new JLabel();         // موجودی کیف پول

    // دکمه بازگشت
    private JButton backButton = new JButton("Back");

    /**
     * سازنده - ایجاد پنل پروفایل با چیدمان Grid 6x1
     *
     * @param onBack کالبک برای مدیریت عملیات بازگشت (Runnable)
     */
    public ProfilePanel(Runnable onBack) {
        // تنظیمات پنل - شفاف، Grid Layout با فاصله‌گذاری 6 پیکسل
        setLayout(new GridLayout(6, 1, 6, 6));
        setOpaque(false);

        // افزودن برچسب‌ها به پنل به ترتیب
        add(usernameLabel);  // ردیف 1: نام کاربری
        add(nameLabel);      // ردیف 2: نام کامل
        add(emailLabel);     // ردیف 3: ایمیل
        add(roleLabel);      // ردیف 4: نقش
        add(walletLabel);    // ردیف 5: کیف پول

        // تنظیم اکشن لیسنر دکمه بازگشت - اجرای کالبک onBack
        backButton.addActionListener(e -> onBack.run());
        add(backButton);     // ردیف 6: دکمه بازگشت
    }

    /**
     * به‌روزرسانی اطلاعات نمایشی پروفایل
     *
     * رفتار:
     * - اگر profile نال باشد → نمایش مقادیر پیش‌فرض ("-")
     * - اگر معتبر باشد → نمایش اطلاعات واقعی از شیء UserProfile
     *
     * @param profile شیء پروفایل کاربر (می‌تواند null باشد)
     */
    public void setProfile(UserProfile profile) {
        // مدیریت حالت نال - نمایش مقادیر پیش‌فرض
        if (profile == null) {
            usernameLabel.setText("Username: -");
            nameLabel.setText("Name: -");
            emailLabel.setText("Email: -");
            roleLabel.setText("Role: -");
            walletLabel.setText("Wallet: -");
            return;
        }

        // به‌روزرسانی برچسب‌ها با اطلاعات واقعی پروفایل
        usernameLabel.setText("Username: " + profile.getUsername());

        // ترکیب نام و نام خانوادگی برای نمایش نام کامل
        nameLabel.setText("Name: " + profile.getFirstName() + " " + profile.getLastName());

        emailLabel.setText("Email: " + profile.getEmail());
        roleLabel.setText("Role: " + profile.getRole());

        // نمایش موجودی با علامت دلار
        walletLabel.setText("Wallet: $" + profile.getWalletBalance());
    }
}
