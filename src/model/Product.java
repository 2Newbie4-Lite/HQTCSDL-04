package model;

public class Product {
    private int productID;
    private String name;
    private double price;
    private int stock;
    private String imageURL;
    private String categoryName;

    public Product(int productID, String name, double price, int stock, String imageURL, String categoryName) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageURL = imageURL;
        this.categoryName = categoryName;
    }

    public int getProductID() { return productID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageURL() { return imageURL; }
    public String getCategoryName() { return categoryName; }
}