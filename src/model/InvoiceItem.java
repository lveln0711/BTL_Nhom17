package model;

import java.util.Objects;

public class InvoiceItem {

    private Product product;
    private int quantity;
    private double unitPrice;

    // Constructor không tham số
    public InvoiceItem() {}

    // Constructor thông thường — dùng khi tạo hóa đơn mới (có kiểm tra tồn kho)
    public InvoiceItem(Product product, int quantity) {
        setProduct(product);
        setQuantity(quantity);
        this.unitPrice = product.getProductPrice();
    }

    /**
     * Static factory method — chỉ dùng khi LOAD TỪ FILE.
     * Không kiểm tra tồn kho, không ghi đè giá hiện tại.
     * Fix bug: trước đây unitPrice bị bỏ qua khi đọc file.
     */
    public static InvoiceItem loadFromFile(Product product, int quantity, double unitPrice) {
        InvoiceItem item = new InvoiceItem();
        item.product   = product;
        item.quantity  = quantity;
        item.unitPrice = unitPrice; // Giữ nguyên giá tại thời điểm mua
        return item;
    }

    // Getter
    public Product getProduct()   { return product; }
    public int getQuantity()      { return quantity; }
    public double getUnitPrice()  { return unitPrice; }

    // Setter
    public void setProduct(Product product) {
        if (product == null)
            throw new IllegalArgumentException("Sản phẩm không được để trống!");
        this.product = product;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Số lượng mua phải lớn hơn 0");
        if (product != null && quantity > product.getQuantity())
            throw new IllegalArgumentException("Số lượng mua vượt quá tồn kho");
        this.quantity = quantity;
    }

    // Tính thành tiền
    public double getSubTotal() {
        return unitPrice * quantity;
    }

    // Phục vụ lưu file: productId,quantity,unitPrice
    public String toFileString() {
        return product.getProductId() + "," + quantity + "," + unitPrice;
    }

    // So sánh theo mã sản phẩm
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InvoiceItem item = (InvoiceItem) obj;
        return product.getProductId().equals(item.product.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getProductId());
    }

    @Override
    public String toString() {
        return product.getProductName()
                + " - SL: " + quantity
                + " - Thành tiền: " + String.format("%,.0f", getSubTotal());
    }
}