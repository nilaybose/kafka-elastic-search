package nb.edu.kafkaes.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DemoDataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/nbose");
        config.setUsername("appdev");
        config.setPassword("appdev");
        config.setAutoCommit(false);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(4);
        config.setIdleTimeout(300000L);
        ds = new HikariDataSource(config);
    }

    private DemoDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
