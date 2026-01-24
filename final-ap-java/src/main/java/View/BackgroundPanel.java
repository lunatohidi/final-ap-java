package View;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * پنل با قابلیت نمایش تصویر پس‌زمینه
 * - نمایش تصویر در کل سطح پنل (stretched)
 * - تغییر پویای تصویر
 */
public class BackgroundPanel extends JPanel {

    /** تصویر پس‌زمینه - اگر null باشد، فقط رنگ پیش‌فرض نمایش داده می‌شود */
    private Image backgroundImage;

    /**
     * سازنده - بارگذاری تصویر اولیه
     * @param imagePath مسیر فایل تصویر (نسبی یا مطلق)
     */
    public BackgroundPanel(String imagePath) {
        super();
        loadImage(imagePath);
        setOpaque(false); // شفاف برای نمایش صحیح کامپوننت‌های روی آن
    }

    /**
     * بارگذاری تصویر از مسیر
     * @param path مسیر فایل - اگر null یا خالی یا نامعتبر باشد، backgroundImage برابر null می‌شود
     */
    private void loadImage(String path) {
        if (path == null || path.isBlank()) {
            backgroundImage = null;
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            backgroundImage = new ImageIcon(path).getImage();
        } else {
            backgroundImage = null;
        }
    }

    /**
     * تغییر تصویر پس‌زمینه در زمان اجرا
     * @param path مسیر جدید فایل تصویر
     */
    public void setBackgroundImage(String path) {
        loadImage(path);
        repaint(); // رسم مجدد پنل
    }

    /**
     * رسم کامپوننت - تصویر را در کل سطح پنل کش می‌دهد
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // رسم تصویر از (0,0) تا تمام سطح پنل
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
