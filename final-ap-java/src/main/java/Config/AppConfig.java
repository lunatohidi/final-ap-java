package Config;

public class AppConfig {

    private static final String DEFAULT_DB_URL = "jdbc:postgresql://etna.liara.cloud:32333/postgres";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "TEzEonU0snvR3z6h9glpq5vO";
    private static final String DEFAULT_BACKGROUND_IMAGE = "background.jpg"; // مسیر فایل پس‌زمینه

    // متد برای دریافت آدرس پایگاه داده
    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault("DB_URL", DEFAULT_DB_URL);
    }

    // متد برای دریافت نام کاربری پایگاه داده
    public static String getDatabaseUser() {
        return System.getenv().getOrDefault("DB_USER", DEFAULT_DB_USER);
    }

    // متد برای دریافت رمز عبور پایگاه داده
    public static String getDatabasePassword() {
        return System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
    }

    // متد برای گرفتن مسیر تصویر پس‌زمینه
    public static String getBackgroundImagePath() {
        return System.getenv().getOrDefault("BG_IMAGE_PATH", DEFAULT_BACKGROUND_IMAGE);
    }
}
