// 位於: tool
package util; // <-- 已修改為您的 package 名稱

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 資料庫連線管理器 (使用 HikariCP 連線池)
 * 負責整個應用程式的資料庫連線。
 */
public class DatabaseManager {

    private static HikariDataSource dataSource;

    // 使用靜態區塊，在類別被載入時，僅執行一次初始化
    static {
        try {
            HikariConfig config = new HikariConfig();
            // 【重要】設定資料庫連線資訊
            config.setJdbcUrl("jdbc:mysql://localhost:3306/travel_agency_db?useSSL=false&serverTimezone=UTC");
            config.setUsername("root");        // <-- 請填寫您的 MySQL 使用者名稱
            config.setPassword("1234");        // <-- 請填寫您的 MySQL 密碼

            // --- HikariCP 效能優化設定 ---
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // --- HikariCP 連線池穩定性設定 ---
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(20);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000); // 30 分鐘

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool.", e);
        }
    }

    private DatabaseManager() {}

    /**
     * 獲取一個資料庫連線。
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
