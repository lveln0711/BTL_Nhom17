package manager;

public class AppContext {

    public static final ProductManager PRODUCT_MANAGER = new ProductManagerImpl();

    public static final CustomerManager CUSTOMER_MANAGER = new CustomerManagerImpl();

    public static final InvoiceManager INVOICE_MANAGER = new InvoiceManagerImpl();

    private AppContext() {
        // chống new
    }
}