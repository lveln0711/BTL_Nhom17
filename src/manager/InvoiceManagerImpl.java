package manager;

import file.FileHandler;
import model.Invoice;
import model.InvoiceItem;
import model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InvoiceManagerImpl implements InvoiceManager {

    private final List<Invoice> invoiceList = new ArrayList<>();

    private static final String FILE_PATH = "data/invoices.txt";

    public InvoiceManagerImpl() {
        // ❌ KHÔNG auto-load ở đây nữa
    }

    // ================= CREATE =================
    @Override
    public boolean addInvoice(Invoice invoice) {

        if (invoice == null) return false;

        if (containsInvoiceId(invoice.getInvoiceId())) {
            return false;
        }

        if (invoice.getItemList() == null || invoice.getItemList().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất 1 sản phẩm");
        }

        // check tồn kho
        for (InvoiceItem item : invoice.getItemList()) {
            Product p = item.getProduct();

            if (p == null) continue;

            if (item.getQuantity() > p.getQuantity()) {
                throw new IllegalArgumentException(
                        "Không đủ tồn kho: " + p.getProductName()
                );
            }
        }

        // trừ kho
        for (InvoiceItem item : invoice.getItemList()) {
            Product p = item.getProduct();
            if (p != null) {
                p.setQuantity(p.getQuantity() - item.getQuantity());
            }
        }

        boolean added = invoiceList.add(invoice);

        if (added) saveToFile();

        return added;
    }

    // ================= DELETE =================
    @Override
    public boolean deleteInvoiceById(String invoiceId) {

        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            return false;
        }

        Invoice invoice = findInvoiceById(invoiceId);
        if (invoice == null) return false;

        restoreStock(invoice);

        boolean removed = invoiceList.remove(invoice);

        if (removed) saveToFile();

        return removed;
    }

    // ================= FIND =================
    @Override
    public Invoice findInvoiceById(String invoiceId) {

        if (invoiceId == null || invoiceId.trim().isEmpty()) return null;

        String key = invoiceId.trim();

        for (Invoice invoice : invoiceList) {
            if (invoice.getInvoiceId().equalsIgnoreCase(key)) {
                return invoice;
            }
        }
        return null;
    }

    // ================= GET ALL =================
    @Override
    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoiceList);
    }

    // ================= SEARCH =================
    @Override
    public List<Invoice> findInvoicesByCustomerId(String customerId) {

        List<Invoice> result = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return result;

        String key = customerId.trim();

        for (Invoice invoice : invoiceList) {
            if (invoice.getCustomer() != null &&
                invoice.getCustomer().getCustomerId().equalsIgnoreCase(key)) {
                result.add(invoice);
            }
        }

        return result;
    }

    @Override
    public List<Invoice> findInvoicesByDate(LocalDate date) {

        List<Invoice> result = new ArrayList<>();
        if (date == null) return result;

        for (Invoice invoice : invoiceList) {
            if (date.equals(invoice.getInvoiceDate())) {
                result.add(invoice);
            }
        }

        return result;
    }

    // ================= SORT =================
    @Override
    public List<Invoice> sortByTotalAmountAscending() {
        List<Invoice> sorted = new ArrayList<>(invoiceList);
        sorted.sort(Comparator.comparingDouble(Invoice::getTotalAmount));
        return sorted;
    }

    @Override
    public List<Invoice> sortByTotalAmountDescending() {
        List<Invoice> sorted = new ArrayList<>(invoiceList);
        sorted.sort(Comparator.comparingDouble(Invoice::getTotalAmount).reversed());
        return sorted;
    }

    // ================= CHECK =================
    @Override
    public boolean containsInvoiceId(String invoiceId) {
        return findInvoiceById(invoiceId) != null;
    }

    @Override
    public boolean isEmpty() {
        return invoiceList.isEmpty();
    }

    @Override
    public int getTotalInvoices() {
        return invoiceList.size();
    }

    @Override
    public double getTotalRevenue() {

        double total = 0;

        for (Invoice invoice : invoiceList) {
            total += invoice.getTotalAmount();
        }

        return total;
    }

    // ================= CLEAR =================
    @Override
    public void clearAllInvoices() {

        for (Invoice invoice : invoiceList) {
            restoreStock(invoice);
        }

        invoiceList.clear();
    }

    private void restoreStock(Invoice invoice) {

        for (InvoiceItem item : invoice.getItemList()) {

            Product p = item.getProduct();

            if (p != null) {
                p.setQuantity(p.getQuantity() + item.getQuantity());
            }
        }
    }

    // ================= FILE =================
    @Override
    public void loadFromFile() {

        List<Invoice> loaded =
                FileHandler.loadInvoicesFromFile(
                        FILE_PATH,
                        FileHandler.loadCustomersFromFile("data/customers.txt"),
                        FileHandler.loadProductsFromFile("data/products.txt")
                );

        clearAllInvoices();

        if (loaded != null) {
            invoiceList.addAll(loaded);
        }
    }

    @Override
    public void saveToFile() {
        FileHandler.saveInvoicesToFile(FILE_PATH, invoiceList);
    }
}