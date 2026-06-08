package hr.algebra.hotel.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public final class DatabaseUtil {

    private static DatabaseUtil instance;
    private Connection connection;

    private DatabaseUtil() {
        try {
            ConfigUtil config = ConfigUtil.getInstance();
            String url = config.getDbUrl();
            String username = config.getDbUsername();
            String password = config.getDbPassword();
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    // Lazy Singleton
    public static DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = null;
                instance = new DatabaseUtil();
                return instance.connection;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check database connection", e);
        }
        return connection;
    }
}