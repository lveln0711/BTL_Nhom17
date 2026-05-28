package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoice {
    private String invoiceId;
    private Customer customer;
    private LocalDate invoiceDate;
    private List<InvoiceItem> itemList;

    // Constructor không tham số
    public Invoice() {
        itemList = new ArrayList<>();
        invoiceDate = LocalDate.now();
    }

    // Constructor đầy đủ tham số
    public Invoice(String invoiceId, Customer customer, LocalDate invoiceDate) {
        setInvoiceId(invoiceId);
        setCustomer(customer);
        setInvoiceDate(invoiceDate);

        itemList = new ArrayList<>();
    }

    // Getter
    public String getInvoiceId() {
        return invoiceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public List<InvoiceItem> getItemList() {
        return itemList;
    }

    // Setter
    public void setInvoiceId(String invoiceId) {
        if(invoiceId == null || invoiceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }
        this.invoiceId = invoiceId.trim();
    }
    public void setCustomer(Customer customer) {
        if(customer == null) {
        	throw new IllegalArgumentException("Khách hàng không được để trống!");
        }
        this.customer = customer;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        if(invoiceDate == null) {
            throw new IllegalArgumentException("Ngày hóa đơn không hợp lệ");
        }
        this.invoiceDate = invoiceDate;
    }

    // Thêm sản phẩm vào hóa đơn
    public void addItem(InvoiceItem newItem) {
        if(newItem == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ");
        }

        // Kiểm tra sản phẩm đã tồn tại chưa
        for(InvoiceItem item : itemList) {
            if(item.equals(newItem)) {
                int newQuantity = item.getQuantity() + newItem.getQuantity();

                // Kiểm tra tồn kho
                if(newQuantity > item.getProduct().getQuantity()) {
                    throw new IllegalArgumentException("Số lượng vượt quá tồn kho!");
                }
                item.setQuantity(newQuantity);
                return;
            }
        }
        itemList.add(newItem);
    }

    // Xóa sản phẩm khỏi hóa đơn
    public void removeItem(InvoiceItem item) {
        itemList.remove(item);
    }

    // Tính tổng tiền hóa đơn
    public double getTotalAmount() {
        double total = 0;

        for(InvoiceItem item : itemList) {
            total += item.getSubTotal();
        }
        return total;
    }

    // Phục vụ lưu file
    public String toFileString() {
        StringBuilder builder = new StringBuilder();
        builder.append(invoiceId)
                .append(",")
                .append(customer.getCustomerId())
                .append(",")
                .append(invoiceDate)
                .append(",");

        for(int i = 0; i < itemList.size(); i++) {
            builder.append(itemList.get(i).toFileString());
            if(i < itemList.size() - 1) {
                builder.append(";");
            }
        }
        return builder.toString();
    }

    // So sánh hóa đơn theo ID
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Invoice invoice = (Invoice) obj;
        return invoiceId.equals(invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }

    @Override
    public String toString() {

        return "Hóa đơn: " + invoiceId +
                " - Khách hàng: " +
                customer.getCustomerName() +
                " - Tổng tiền: " +
                getTotalAmount();
    }
}