package model;

public class TV extends Product {
    private double size;
    private String resolution;

    // Constructor không tham số
    public TV() {

    }

    // Constructor đầy đủ tham số
    public TV(String productId, String productName, String brand, double productPrice, int quantity, double size, String resolution) {
        super(productId, productName, brand, productPrice, quantity);
        setSize(size);
        setResolution(resolution);
    }

    // Getter
    public double getSize() {
        return size;
    }

    public String getResolution() {
        return resolution;
    }

    // Setter
    public void setSize(double size) {
        if(size <= 0) {
            throw new IllegalArgumentException("Kích thước TV phải lớn hơn 0");
        }
        this.size = size;
    }

    public void setResolution(String resolution) {
        if(resolution == null || resolution.trim().isEmpty()) {
            throw new IllegalArgumentException("Độ phân giải không được để trống!");
        }
        this.resolution = resolution.trim();
    }

    @Override
    public String getProductType() {
        return "TV";
    }

    // Phục vụ lưu file
    public String toFileString() {
        return getBaseFileString() + "," +
                size + "," +
                resolution;
    }

    @Override
    public String toString() {
        return super.toString() +
                " - Size: " + size +
                " inch - Resolution: " + resolution;
    }
}