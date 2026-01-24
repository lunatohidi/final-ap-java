package View;

import Exception.InvalidInputException;
import Validation.InputValidator;
import main.MainFrame;
import Database.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * پنل ثبت‌نام - مدیریت فرآیند ایجاد حساب کاربری جدید
 * امکانات: دریافت اطلاعات کاربر، اعتبارسنجی ورودی‌ها، ذخیره در دیتابیس
 */
public class SignUpPanel extends JPanel {

    // فیلدهای ورودی اطلاعات کاربر
    private JTextField usernameField;        // نام کاربری
    private JPasswordField passwordField;    // رمز عبور
    private JTextField emailField;           // ایمیل
    private JTextField firstNameField;       // نام
    private JTextField lastNameField;        // نام خانوادگی

    // دکمه‌های عملیاتی
    private JButton signUpButton;            // دکمه ثبت‌نام
    private JButton backButton;              // دکمه بازگشت به ورود

    // وابستگی‌ها
    private MainFrame mainFrame;             // فریم اصلی برای ناوبری
    private UserDAO userDAO;                 // دسترسی به پایگاه‌داده کاربران

    /**
     * سازنده - ایجاد پنل ثبت‌نام با چیدمان Grid 8x2
     *
     * @param mainFrame فریم اصلی برای مدیریت انتقال بین صفحات
     * @param userDAO شیء دسترسی به داده کاربران برای ذخیره‌سازی
     */
    public SignUpPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;

        // تنظیمات پنل - شفاف و Grid Layout 8 ردیف × 2 ستون
        setLayout(new GridLayout(8, 2));
        setOpaque(false);

        // ساخت برچسب‌های فیلدها
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel firstNameLabel = new JLabel("First name: ");
        JLabel lastNameLabel = new JLabel("Last name: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel emailLabel = new JLabel("Email: ");

        // ساخت فیلدهای ورودی
        usernameField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();

        // ساخت دکمه‌ها
        signUpButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");

        // افزودن کامپوننت‌ها به پنل به ترتیب (برچسب - فیلد)
        add(usernameLabel);       // ردیف 1: نام کاربری
        add(usernameField);
        add(firstNameLabel);      // ردیف 2: نام
        add(firstNameField);
        add(lastNameLabel);       // ردیف 3: نام خانوادگی
        add(lastNameField);
        add(passwordLabel);       // ردیف 4: رمز عبور
        add(passwordField);
        add(emailLabel);          // ردیف 5: ایمیل
        add(emailField);
        add(signUpButton);        // ردیف 6: دکمه ثبت‌نام
        add(backButton);          // ردیف 6: دکمه بازگشت

        // اکشن لیسنر دکمه ثبت‌نام - دریافت مقادیر و فراخوانی handleSignUp
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                handleSignUp(username, password, email, firstName, lastName);
            }
        });

        // اکشن لیسنر دکمه بازگشت - انتقال به صفحه Login
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.switchToPanel("Login");
            }
        });
    }

    /**
     * مدیریت فرآیند ثبت‌نام کاربر جدید
     *
     * فرآیند:
     * 1. اعتبارسنجی ورودی‌ها (validateInputs)
     * 2. ذخیره کاربر در دیتابیس (userDAO.createUser)
     * 3. نمایش پیام موفقیت
     * 4. انتقال به صفحه Login
     *
     * @param username نام کاربری (نباید خالی باشد)
     * @param password رمز عبور (حداقل 8 کاراکتر، شامل حروف و اعداد)
     * @param email آدرس ایمیل (باید فرمت معتبر داشته باشد)
     * @param firstName نام (نباید خالی باشد)
     * @param lastName نام خانوادگی (نباید خالی باشد)
     */
    private void handleSignUp(String username, String password, String email, String firstName, String lastName) {
        try {
            // اعتبارسنجی تمام ورودی‌ها - در صورت نامعتبر بودن، استثنا پرتاب می‌شود
            validateInputs(username, password, email, firstName, lastName);

            // ذخیره اطلاعات کاربر در پایگاه داده
            userDAO.createUser(username, password, email, firstName, lastName);

            // نمایش پیام موفقیت
            JOptionPane.showMessageDialog(this, "Sign Up Successful! Username: " + username);

            // انتقال به صفحه ورود پس از ثبت‌نام موفق
            mainFrame.switchToPanel("Login");

        } catch (InvalidInputException ex) {
            // مدیریت خطاهای اعتبارسنجی - نمایش پیام خطا
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            // مدیریت خطاهای دیتابیس (مثلاً نام کاربری تکراری)
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Sign Up Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * اعتبارسنجی ورودی‌های کاربر
     *
     * قوانین اعتبارسنجی:
     * - نام کاربری: نباید خالی باشد
     * - نام: نباید خالی باشد
     * - نام خانوادگی: نباید خالی باشد
     * - ایمیل: باید فرمت معتبر داشته باشد (از InputValidator استفاده می‌شود)
     * - رمز عبور: حداقل 8 کاراکتر و شامل حروف و اعداد (از InputValidator استفاده می‌شود)
     *
     * @param username نام کاربری
     * @param password رمز عبور
     * @param email آدرس ایمیل
     * @param firstName نام
     * @param lastName نام خانوادگی
     * @throws InvalidInputException در صورت نامعتبر بودن هر یک از ورودی‌ها
     */
    private void validateInputs(String username, String password, String email, String firstName, String lastName) throws InvalidInputException {
        // بررسی خالی نبودن نام کاربری
        if (username == null || username.isBlank()) {
            throw new InvalidInputException("Username is required.");
        }

        // بررسی خالی نبودن نام
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidInputException("First name is required.");
        }

        // بررسی خالی نبودن نام خانوادگی
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidInputException("Last name is required.");
        }

        // اعتبارسنجی فرمت ایمیل با استفاده از InputValidator
        if (!InputValidator.validateEmail(email)) {
            throw new InvalidInputException("Email is invalid.");
        }

        // اعتبارسنجی قوانین رمز عبور با استفاده از InputValidator
        if (!InputValidator.validatePassword(password)) {
            throw new InvalidInputException("Password must be at least 8 characters and include letters and numbers.");
        }
    }
}
