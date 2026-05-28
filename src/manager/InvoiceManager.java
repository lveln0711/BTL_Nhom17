package manager;

import model.Invoice;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceManager {

    boolean addInvoice(Invoice invoice);

    boolean deleteInvoiceById(String invoiceId);

    Invoice findInvoiceById(String invoiceId);

    List<Invoice> getAllInvoices();

    List<Invoice> findInvoicesByCustomerId(String customerId);

    List<Invoice> findInvoicesByDate(LocalDate date);

    List<Invoice> sortByTotalAmountAscending();

    List<Invoice> sortByTotalAmountDescending();

    boolean containsInvoiceId(String invoiceId);

    boolean isEmpty();

    int getTotalInvoices();

    double getTotalRevenue();

    void clearAllInvoices();

    void loadFromFile();

    void saveToFile();
}