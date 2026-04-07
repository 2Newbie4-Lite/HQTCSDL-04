package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import dao.ProductDAO;
import model.Product;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class AppServer {

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new ProductHandler());
        server.start();
        System.out.println("🚀 Server chạy tại http://localhost:8080");
    }

    static class ProductHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.getAllProducts();

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            StringBuilder html = new StringBuilder();
            html.append("""
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>ShopDepTrai</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { font-family: Arial, sans-serif; background: #f5f5f5; }
                        header { background: #e74c3c; color: white; padding: 15px 30px; 
                                 display: flex; justify-content: space-between; align-items: center; }
                        header h1 { font-size: 24px; }
                        nav a { color: white; text-decoration: none; margin-left: 20px; font-size: 16px; }
                        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
                        h2 { margin-bottom: 20px; color: #333; }
                        .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
                        .card { background: white; border-radius: 10px; padding: 15px; 
                                box-shadow: 0 2px 8px rgba(0,0,0,0.1); text-align: center; }
                        .card img { width: 100%; height: 180px; object-fit: cover; 
                                    border-radius: 8px; margin-bottom: 10px; }
                        .card .no-img { width: 100%; height: 180px; background: #eee; 
                                        border-radius: 8px; display: flex; align-items: center; 
                                        justify-content: center; color: #aaa; margin-bottom: 10px; }
                        .card h3 { font-size: 15px; margin-bottom: 8px; color: #333; }
                        .card .category { font-size: 12px; color: #888; margin-bottom: 8px; }
                        .card .price { color: #e74c3c; font-weight: bold; font-size: 16px; margin-bottom: 8px; }
                        .card .stock { font-size: 12px; color: #27ae60; margin-bottom: 12px; }
                        .card button { background: #e74c3c; color: white; border: none; 
                                       padding: 8px 20px; border-radius: 5px; cursor: pointer; 
                                       font-size: 14px; width: 100%; }
                        .card button:hover { background: #c0392b; }
                    </style>
                </head>
                <body>
                    <header>
                        <h1>🛍️ ShopDepTrai</h1>
                        <nav>
                            <a href="/">🏠 Trang chủ</a>
                            <a href="/cart">🛒 Giỏ hàng</a>
                            <a href="/login">👤 Đăng nhập</a>
                        </nav>
                    </header>
                    <div class="container">
                        <h2>Sản phẩm nổi bật</h2>
                        <div class="grid">
                """);

            for (Product p : products) {
                String imgTag = (p.getImageURL() != null && !p.getImageURL().isEmpty())
                    ? "<img src='" + p.getImageURL() + "' alt='" + p.getName() + "'>"
                    : "<div class='no-img'>📦 No Image</div>";

                html.append("<div class='card'>")
                    .append(imgTag)
                    .append("<h3>").append(p.getName()).append("</h3>")
                    .append("<div class='category'>").append(p.getCategoryName()).append("</div>")
                    .append("<div class='price'>").append(formatter.format(p.getPrice())).append(" ₫</div>")
                    .append("<div class='stock'>Còn: ").append(p.getStock()).append(" sản phẩm</div>")
                    .append("<button onclick=\"location.href='/cart?add=").append(p.getProductID()).append("'\">🛒 Thêm vào giỏ</button>")
                    .append("</div>");
            }

            html.append("""
                        </div>
                    </div>
                </body>
                </html>
                """);

            byte[] response = html.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}