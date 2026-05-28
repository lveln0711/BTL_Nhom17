package model;

import java.util.Objects;

public abstract class Product {
    private String productId;
    private String productName;
    private String brand;
    private double productPrice;
    private int quantity;

    // Constructor không tham số
    public Product() {

    }

    // Constructor đầy đủ tham số
    public Product(String productId, String productName, String brand, double productPrice,int quantity) {
        setProductId(productId);
        setProductName(productName);
        setBrand(brand);
        setProductPrice(productPrice);
        setQuantity(quantity);
    }

    // Getter
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setter
    public void setProductId(String productId) {
        if(productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        }
        this.productId = productId.trim();
    }
    public void setProductName(String productName) {
        if(productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        this.productName = productName.trim();
    }
    public void setBrand(String brand) {
        if(brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Hãng sản xuất không được để trống!");
        }
        this.brand = brand.trim();
    }
    public void setProductPrice(double productPrice) {
        if(productPrice < 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải >= 0");
        }
        this.productPrice = productPrice;
    }

    public void setQuantity(int quantity) {
        if(quantity < 0) {
            throw new IllegalArgumentException("Số lượng phải >= 0");
        }
        this.quantity = quantity;
    }

    // Phục vụ lưu file
    protected String getBaseFileString() {
        return getProductType() + "," +
                productId + "," +
                productName + "," +
                brand + "," +
                productPrice + "," +
                quantity;
    }

    // Xác định loại sản phẩm
    public abstract String getProductType();

    // So sánh sản phẩm theo ID
    @Override
    public boolean equals(Object obj) {

        if(this == obj) {
            return true;
        }

        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Product product = (Product) obj;
        return productId.equals(product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return productId + " - " +
                productName + " - " +
                brand + " - " +
                productPrice;
    }
}