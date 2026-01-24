package View;

import main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import Database.UserDAO;

/**
 * پنل ورود - مدیریت فرآیند احراز هویت کاربران
 * امکانات: ورود کاربر، انتقال به صفحه ثبت‌نام، تشخیص نقش کاربر (admin/user)
 */
public class LoginPanel extends JPanel {

    // کامپوننت‌های ورودی
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    // وابستگی‌ها
    private MainFrame mainFrame;
    private UserDAO userDAO;

    /**
     * سازنده - ایجاد پنل ورود با چیدمان Grid 4x2
     * @param mainFrame فریم اصلی برای مدیریت ناوبری
     * @param userDAO دسترسی به پایگاه‌داده کاربران
     */
    public LoginPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;

        // تنظیمات پنل - شفاف و Grid Layout
        setOpaque(false);
        setLayout(new GridLayout(4, 2));

        // ساخت برچسب‌ها
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");

        // ساخت فیلدهای ورودی
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // ساخت دکمه‌ها
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");

        // افزودن کامپوننت‌ها به پنل
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);

        // اکشن لیسنر دکمه ورود - فراخوانی handleLogin
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                handleLogin(username, password);
            }
        });

        // اکشن لیسنر دکمه ثبت‌نام - انتقال به صفحه SignUp
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
    }

    /**
     * مدیریت فرآیند ورود کاربر
     * - اعتبارسنجی ورودی‌ها (نباید خالی باشند)
     * - فراخوانی userDAO.login برای احراز هویت
     * - هدایت به صفحه مناسب بر اساس نقش (Admin/User)
     *
     * @param username نام کاربری
     * @param password رمز عبور
     */
    private void handleLogin(String username, String password) {
        // بررسی خالی نبودن فیلدها
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Username and password are required", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // احراز هویت و دریافت نقش کاربر
            String role = userDAO.login(username, password);

            // در صورت نامعتبر بودن اطلاعات
            if (role == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ثبت ورود موفق در MainFrame
            mainFrame.handleLoginSuccess(username);

            // هدایت به صفحه مناسب بر اساس نقش
            if ("admin".equalsIgnoreCase(role)) {
                mainFrame.showAdminPage();
            } else {
                mainFrame.showCatalogPage();  // کاربر عادی → کاتالوگ
            }
        } catch (SQLException ex) {
            // مدیریت خطای دیتابیس
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * مدیریت درخواست ثبت‌نام - انتقال به صفحه SignUp
     */
    private void handleSignUp() {
        mainFrame.switchToPanel("SignUp");
    }
}
