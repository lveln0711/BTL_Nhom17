package model;

import java.util.Objects;

public class Customer {

    private String customerId;
    private String customerName;
    private String phoneNumber;
    private String address;

    // Constructor không tham số
    public Customer() {}

    // Constructor đầy đủ tham số
    public Customer(String customerId, String customerName,
                    String phoneNumber, String address) {
        setCustomerId(customerId);
        setCustomerName(customerName);
        setPhoneNumber(phoneNumber);
        setAddress(address);
    }

    // Getter
    public String getCustomerId()   { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber()  { return phoneNumber; }
    public String getAddress()      { return address; }

    // Setter
    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty())
            throw new IllegalArgumentException("Mã khách hàng không được để trống!");
        this.customerId = customerId.trim();
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty())
            throw new IllegalArgumentException("Tên khách hàng không được để trống!");
        this.customerName = customerName.trim();
    }

    /**
     * Đồng bộ với Validation.isValidPhone():
     * 10-11 chữ số bắt đầu bằng 0, hoặc +84 + 9 số.
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            throw new IllegalArgumentException("Số điện thoại không được để trống!");
        phoneNumber = phoneNumber.trim();
        if (!phoneNumber.matches("^(0\\d{9,10}|\\+84\\d{9})$"))
            throw new IllegalArgumentException(
                    "Số điện thoại không hợp lệ! (10-11 số bắt đầu bằng 0, hoặc +84...)");
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
        this.address = address.trim();
    }

    // Phục vụ lưu file
    public String toFileString() {
        return customerId + "," + customerName + "," + phoneNumber + "," + address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId.equals(customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return customerId + " - " + customerName + " - " + phoneNumber;
    }
}