package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = 
        "jdbc:sqlserver://2NEWBIE4\\CLCCSDLPTNHOM4;" +
        "databaseName=ShopDepTrai;" +
        "user=shopuser;" +
        "password=123456;" +
        "encrypt=false;" +
        "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy JDBC Driver!", e);
        }
    }
}