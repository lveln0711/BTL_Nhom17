package model;

public class Phone extends Product {
    private int ram;
    private int storage;

    // Constructor không tham số
    public Phone() {

    }

    // Constructor đầy đủ tham số
    public Phone(String productId, String productName, String brand, double productPrice, int quantity, int ram, int storage) {
        super(productId, productName, brand, productPrice, quantity);
        setRam(ram);
        setStorage(storage);
    }

    // Getter
    public int getRam() {
        return ram;
    }

    public int getStorage() {
        return storage;
    }

    // Setter
    public void setRam(int ram) {
        if(ram <= 0) {
            throw new IllegalArgumentException("RAM phải > 0");
        }
        this.ram = ram;
    }

    public void setStorage(int storage) {
        if(storage <= 0) {
            throw new IllegalArgumentException("Bộ nhớ phải > 0");
        }
        this.storage = storage;
    }

    @Override
    public String getProductType() {
        return "PHONE";
    }

    // Phục vụ lưu file
    public String toFileString() {
        return getBaseFileString() + "," +
                ram + "," +
                storage;
    }

    @Override
    public String toString() {
        return super.toString() +
                " - RAM: " + ram +
                "GB - Storage: " + storage + "GB";
    }
}