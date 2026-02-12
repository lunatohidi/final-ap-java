package Config;

import java.net.URI;

public class AppConfig {

    private static final String DEFAULT_DB_URI = "postgresql://root:7uZZfbrejMbRrltbPI5MjAG9@fitz-roy.liara.cloud:30193/postgres";
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://fitz-roy.liara.cloud:30193/postgres";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "7uZZfbrejMbRrltbPI5MjAG9";
    private static final String DEFAULT_BACKGROUND_IMAGE = "background.jpg"; // مسیر فایل پس‌زمینه

    // متد برای دریافت آدرس پایگاه داده
    public static String getDatabaseUrl() {
        String raw = System.getenv().getOrDefault("DB_URI",
                System.getenv().getOrDefault("DB_URL", DEFAULT_DB_URI));

        String jdbc;
        if (raw.startsWith("jdbc:postgresql://")) {
            jdbc = normalizeJdbc(raw);
        } else if (raw.startsWith("postgresql://")) {
            jdbc = normalizeJdbc("jdbc:" + raw);
        } else {
            jdbc = raw;
        }

        if (jdbc.startsWith("jdbc:postgresql://") && !jdbc.contains("sslmode=")) {
            jdbc = jdbc + (jdbc.contains("?") ? "&" : "?") + "sslmode=disable";
        }
        return jdbc;
    }

    private static String normalizeJdbc(String jdbcUrl) {
        // Strip userinfo from jdbc:postgresql://user:pass@host:port/db
        String withoutJdbc = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring(5) : jdbcUrl;
        if (!withoutJdbc.startsWith("postgresql://")) {
            return jdbcUrl;
        }
        URI uri = URI.create(withoutJdbc);
        if (uri.getUserInfo() == null) {
            return jdbcUrl;
        }
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String query = uri.getQuery();

        StringBuilder rebuilt = new StringBuilder("jdbc:postgresql://");
        rebuilt.append(host == null ? "" : host);
        if (port != -1) {
            rebuilt.append(":").append(port);
        }
        if (path != null && !path.isEmpty()) {
            rebuilt.append(path);
        }
        if (query != null && !query.isEmpty()) {
            rebuilt.append("?").append(query);
        }
        return rebuilt.toString();
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
