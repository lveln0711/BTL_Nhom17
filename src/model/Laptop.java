package model;

public class Laptop extends Product {
    private String cpu;
    private int ram;

    // Constructor không tham số
    public Laptop() {

    }

    // Constructor đầy đủ tham số
    public Laptop(String productId, String productName, String brand, double productPrice, int quantity, String cpu, int ram) {
        super(productId, productName, brand, productPrice, quantity);

        setCpu(cpu);
        setRam(ram);
    }

    // Getter
    public String getCpu() {
        return cpu;
    }

    public int getRam() {
        return ram;
    }

    // Setter
    public void setCpu(String cpu) {

        if(cpu == null || cpu.trim().isEmpty()) {
            throw new IllegalArgumentException("CPU không được để trống!");
        }
        this.cpu = cpu.trim();
    }
    public void setRam(int ram) {
        if(ram <= 0) {
            throw new IllegalArgumentException("RAM phải lớn hơn 0");
        }
        this.ram = ram;
    }

    @Override
    public String getProductType() {
        return "LAPTOP";
    }

    // Phục vụ lưu file
    public String toFileString() {
        return getBaseFileString() + "," +
                cpu + "," +
                ram;
    }

    @Override
    public String toString() {

        return super.toString() +
                " - CPU: " + cpu +
                " - RAM: " + ram + "GB";
    }
}