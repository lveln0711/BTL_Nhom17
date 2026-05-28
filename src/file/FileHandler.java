package file;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // =========================
    // PRODUCT
    // =========================

    public static void saveProductsToFile(String fileName, List<Product> productList) {
        if (productList == null) return;
        ensureDirectoryExists(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Product product : productList) {
                writer.write(productToData(product));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file sản phẩm: " + e.getMessage());
        }
    }

    public static List<Product> loadProductsFromFile(String fileName) {
        List<Product> productList = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return productList;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Product product = parseProduct(line);
                if (product != null && !containsProductId(productList, product.getProductId())) {
                    productList.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file sản phẩm: " + e.getMessage());
        }
        return productList;
    }

    private static String productToData(Product product) {
        if (product instanceof Phone phone) {
            return "PHONE," + phone.getProductId() + ","
                    + phone.getProductName() + ","
                    + phone.getBrand() + ","
                    + phone.getProductPrice() + ","
                    + phone.getQuantity() + ","
                    + phone.getRam() + ","
                    + phone.getStorage();
        }
        if (product instanceof Laptop laptop) {
            return "LAPTOP," + laptop.getProductId() + ","
                    + laptop.getProductName() + ","
                    + laptop.getBrand() + ","
                    + laptop.getProductPrice() + ","
                    + laptop.getQuantity() + ","
                    + laptop.getCpu() + ","
                    + laptop.getRam();
        }
        if (product instanceof TV tv) {
            return "TV," + tv.getProductId() + ","
                    + tv.getProductName() + ","
                    + tv.getBrand() + ","
                    + tv.getProductPrice() + ","
                    + tv.getQuantity() + ","
                    + tv.getSize() + ","
                    + tv.getResolution();
        }
        return "";
    }

    private static Product parseProduct(String data) {
        try {
            String[] parts = data.split(",");
            if (parts.length < 8) return null;
            String type = parts[0].trim();
            return switch (type.toUpperCase()) {
                case "PHONE" -> new Phone(
                        parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        Double.parseDouble(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()),
                        Integer.parseInt(parts[6].trim()),
                        Integer.parseInt(parts[7].trim())
                );
                case "LAPTOP" -> new Laptop(
                        parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        Double.parseDouble(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()),
                        parts[6].trim(),
                        Integer.parseInt(parts[7].trim())
                );
                case "TV" -> new TV(
                        parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        Double.parseDouble(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()),
                        Double.parseDouble(parts[6].trim()),
                        parts[7].trim()
                );
                default -> null;
            };
        } catch (Exception e) {
            System.out.println("Lỗi parse sản phẩm: " + e.getMessage());
            return null;
        }
    }

    // =========================
    // CUSTOMER
    // =========================

    public static void saveCustomersToFile(String fileName, List<Customer> customerList) {
        if (customerList == null) return;
        ensureDirectoryExists(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Customer customer : customerList) {
                writer.write(customer.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file khách hàng: " + e.getMessage());
        }
    }

    public static List<Customer> loadCustomersFromFile(String fileName) {
        List<Customer> customerList = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return customerList;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 4) continue;
                    Customer customer = new Customer(
                            parts[0].trim(), parts[1].trim(),
                            parts[2].trim(), parts[3].trim()
                    );
                    if (!containsCustomerId(customerList, customer.getCustomerId())) {
                        customerList.add(customer);
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi parse khách hàng: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file khách hàng: " + e.getMessage());
        }
        return customerList;
    }

    // =========================
    // INVOICE
    // =========================

    public static void saveInvoicesToFile(String fileName, List<Invoice> invoiceList) {
        if (invoiceList == null) return;
        ensureDirectoryExists(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Invoice invoice : invoiceList) {
                writer.write(invoice.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file hóa đơn: " + e.getMessage());
        }
    }

    /**
     * Load hóa đơn từ file.
     *
     * FIX BUG: Trước đây unitPrice (trường thứ 3 của mỗi item) bị bỏ qua,
     * constructor InvoiceItem tự lấy giá hiện tại của sản phẩm → sai khi giá đã thay đổi.
     * Nay đọc đủ 3 trường: productId, quantity, unitPrice và gán trực tiếp.
     *
     * FIX BUG 2: Không kiểm tra tồn kho khi load (khác với khi tạo mới),
     * dùng setQuantityDirectly() để bypass validation tồn kho.
     */
    public static List<Invoice> loadInvoicesFromFile(String fileName,
                                                      List<Customer> customerList,
                                                      List<Product> productList) {
        List<Invoice> invoiceList = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return invoiceList;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    // Format: invoiceId,customerId,date,item1;item2;...
                    // Mỗi item: productId,quantity,unitPrice
                    String[] mainParts = line.split(",", 4);
                    if (mainParts.length < 4) continue;

                    String     invoiceId  = mainParts[0].trim();
                    String     customerId = mainParts[1].trim();
                    LocalDate  date       = LocalDate.parse(mainParts[2].trim());
                    Customer   customer   = findCustomerById(customerList, customerId);
                    if (customer == null) continue;

                    Invoice invoice = new Invoice(invoiceId, customer, date);

                    String[] itemParts = mainParts[3].split(";");
                    for (String itemData : itemParts) {
                        String[] f = itemData.split(",");
                        // Cần đủ 3 trường: productId, quantity, unitPrice
                        if (f.length < 3) continue;

                        String    productId = f[0].trim();
                        int       quantity  = Integer.parseInt(f[1].trim());
                        double    unitPrice = Double.parseDouble(f[2].trim());
                        Product   product   = findProductById(productList, productId);
                        if (product == null) continue;

                        // Dùng constructor đặc biệt để load — không kiểm tra tồn kho
                        InvoiceItem item = InvoiceItem.loadFromFile(
                                product, quantity, unitPrice
                        );
                        invoice.getItemList().add(item);
                    }

                    if (!invoice.getItemList().isEmpty()
                            && !containsInvoiceId(invoiceList, invoice.getInvoiceId())) {
                        invoiceList.add(invoice);
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi parse hóa đơn: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file hóa đơn: " + e.getMessage());
        }
        return invoiceList;
    }

    // =========================
    // HELPER
    // =========================

    /** Tạo thư mục data/ nếu chưa tồn tại */
    private static void ensureDirectoryExists(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static boolean containsProductId(List<Product> list, String id) {
        return findProductById(list, id) != null;
    }

    private static Product findProductById(List<Product> list, String id) {
        if (id == null) return null;
        for (Product p : list) {
            if (p.getProductId().equalsIgnoreCase(id.trim())) return p;
        }
        return null;
    }

    private static boolean containsCustomerId(List<Customer> list, String id) {
        return findCustomerById(list, id) != null;
    }

    private static Customer findCustomerById(List<Customer> list, String id) {
        if (id == null) return null;
        for (Customer c : list) {
            if (c.getCustomerId().equalsIgnoreCase(id.trim())) return c;
        }
        return null;
    }

    private static boolean containsInvoiceId(List<Invoice> list, String id) {
        return findInvoiceById(list, id) != null;
    }

    private static Invoice findInvoiceById(List<Invoice> list, String id) {
        if (id == null) return null;
        for (Invoice inv : list) {
            if (inv.getInvoiceId().equalsIgnoreCase(id.trim())) return inv;
        }
        return null;
    }
}