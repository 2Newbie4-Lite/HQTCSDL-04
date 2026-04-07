import server.AppServer;
import config.DBConnection;
import config.RedisConnection;
import redis.clients.jedis.Jedis;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        // Test kết nối
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ Kết nối SQL Server thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi SQL Server: " + e.getMessage());
        }

        try (Jedis jedis = RedisConnection.getConnection()) {
            System.out.println("✅ Kết nối Redis thành công: " + jedis.ping());
        } catch (Exception e) {
            System.out.println("❌ Lỗi Redis: " + e.getMessage());
        }

        // Khởi động server
        AppServer.start();
    }
}