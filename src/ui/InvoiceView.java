package ui;

import manager.AppContext;
import model.*;
import utils.AlertHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InvoiceView {

    private final BorderPane root = new BorderPane();
    private final TableView<Invoice> table = new TableView<>();
    private final ObservableList<Invoice> invoiceData = FXCollections.observableArrayList();

    public InvoiceView() {
        buildUI();
        refresh();
    }

    // ================= UI =================
    private void buildUI() {

        // ===== CỘT BẢNG HÓA ĐƠN =====
        TableColumn<Invoice, String> idCol = new TableColumn<>("Mã HD");
        idCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getInvoiceId())
        );
        idCol.setPrefWidth(120);

        TableColumn<Invoice, String> customerCol = new TableColumn<>("Khách hàng");
        customerCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getCustomer().getCustomerName()
                )
        );
        customerCol.setPrefWidth(160);

        TableColumn<Invoice, String> dateCol = new TableColumn<>("Ngày");
        dateCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getInvoiceDate().toString()
                )
        );
        dateCol.setPrefWidth(100);

        TableColumn<Invoice, String> itemsCol = new TableColumn<>("Số SP");
        itemsCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getItemList().size())
                )
        );
        itemsCol.setPrefWidth(60);

        TableColumn<Invoice, String> totalCol = new TableColumn<>("Tổng tiền (VNĐ)");
        totalCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%,.0f", d.getValue().getTotalAmount())
                )
        );
        totalCol.setPrefWidth(140);

        table.getColumns().setAll(List.of(
                idCol, customerCol, dateCol, itemsCol, totalCol
        ));
        table.setItems(invoiceData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ===== NÚT =====
        Button btnCreate  = new Button("➕ Tạo hóa đơn");
        Button btnDetail  = new Button("🔍 Xem chi tiết");
        Button btnDelete  = new Button("🗑 Xóa");
        Button btnRefresh = new Button("🔄 Tải lại");

        HBox actions = new HBox(10, btnCreate, btnDetail, btnDelete, btnRefresh);
        actions.setPadding(new Insets(8, 0, 0, 0));

        VBox container = new VBox(6, table, actions);
        container.setPadding(new Insets(10));
        root.setCenter(container);

        // ===== SỰ KIỆN =====
        btnRefresh.setOnAction(e -> refresh());

        btnCreate.setOnAction(e -> showCreateInvoiceDialog());

        btnDetail.setOnAction(e -> {
            Invoice selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn hóa đơn!");
                return;
            }
            showInvoiceDetail(selected);
        });

        btnDelete.setOnAction(e -> {
            Invoice selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }
            boolean confirm = AlertHelper.showConfirm(
                    "Xác nhận xóa",
                    "Xóa hóa đơn " + selected.getInvoiceId()
                            + "?\n(Tồn kho sẽ được hoàn lại tự động)"
            );
            if (confirm) {
                // Dùng Manager để xóa — logic hoàn kho trong InvoiceManagerImpl
                boolean ok = AppContext.INVOICE_MANAGER
                        .deleteInvoiceById(selected.getInvoiceId());
                if (ok) {
                    // Lưu lại tồn kho đã hoàn vào file
                    AppContext.PRODUCT_MANAGER.saveToFile();
                    AlertHelper.showInformation("Thành công",
                            "Đã xóa hóa đơn và hoàn kho tự động!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể xóa hóa đơn!");
                }
            }
        });
    }

    // ================= DIALOG TẠO HÓA ĐƠN =================
    private void showCreateInvoiceDialog() {
        List<Customer> customers = AppContext.CUSTOMER_MANAGER.getAllCustomers();
        List<Product>  products  = AppContext.PRODUCT_MANAGER.getAllProducts();

        if (customers.isEmpty()) {
            AlertHelper.showWarning("Cảnh báo",
                    "Chưa có khách hàng nào! Vui lòng thêm khách hàng trước.");
            return;
        }
        if (products.isEmpty()) {
            AlertHelper.showWarning("Cảnh báo",
                    "Chưa có sản phẩm nào! Vui lòng thêm sản phẩm trước.");
            return;
        }

        Dialog<Invoice> dialog = new Dialog<>();
        dialog.setTitle("Tạo hóa đơn mới");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setPrefWidth(600);

        // ---- CHỌN KHÁCH HÀNG ----
        ComboBox<Customer> cbCustomer = new ComboBox<>();
        cbCustomer.getItems().addAll(customers);
        cbCustomer.setPromptText("-- Chọn khách hàng --");
        cbCustomer.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null :
                        c.getCustomerId() + " - " + c.getCustomerName());
            }
        });
        cbCustomer.setButtonCell(cbCustomer.getCellFactory().call(null));

        // ---- CHỌN SẢN PHẨM ----
        ComboBox<Product> cbProduct = new ComboBox<>();
        cbProduct.getItems().addAll(
                products.stream()
                        .filter(p -> p.getQuantity() > 0)
                        .toList()
        );
        cbProduct.setPromptText("-- Chọn sản phẩm --");
        cbProduct.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null :
                        p.getProductId() + " - " + p.getProductName()
                                + " (Tồn: " + p.getQuantity() + ")");
            }
        });
        cbProduct.setButtonCell(cbProduct.getCellFactory().call(null));

        TextField tfQty = new TextField("1");
        tfQty.setPromptText("Số lượng");
        tfQty.setPrefWidth(60);

        Button btnAddItem = new Button("Thêm vào đơn");

        // ---- BẢNG CHI TIẾT HÓA ĐƠN ----
        ObservableList<InvoiceItem> itemData = FXCollections.observableArrayList();
        TableView<InvoiceItem> itemTable = new TableView<>(itemData);

        TableColumn<InvoiceItem, String> colName = new TableColumn<>("Sản phẩm");
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProduct().getProductName())
        );
        colName.setPrefWidth(180);

        TableColumn<InvoiceItem, String> colQty = new TableColumn<>("SL");
        colQty.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getQuantity()))
        );
        colQty.setPrefWidth(50);

        TableColumn<InvoiceItem, String> colPrice = new TableColumn<>("Đơn giá");
        colPrice.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%,.0f", d.getValue().getUnitPrice())
                )
        );
        colPrice.setPrefWidth(110);

        TableColumn<InvoiceItem, String> colSub = new TableColumn<>("Thành tiền");
        colSub.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%,.0f", d.getValue().getSubTotal())
                )
        );
        colSub.setPrefWidth(120);

        itemTable.getColumns().setAll(List.of(colName, colQty, colPrice, colSub));
        itemTable.setPrefHeight(200);

        Label lblTotal = new Label("Tổng tiền: 0 VNĐ");
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnRemoveItem = new Button("Xóa dòng");

        // ---- LAYOUT ----
        HBox rowProduct = new HBox(8, cbProduct,
                new Label("SL:"), tfQty, btnAddItem);
        rowProduct.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox content = new VBox(10,
                new Label("Khách hàng:"), cbCustomer,
                new Separator(),
                new Label("Thêm sản phẩm:"), rowProduct,
                itemTable,
                btnRemoveItem,
                lblTotal
        );
        content.setPadding(new Insets(14));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        // ---- SỰ KIỆN THÊM SẢN PHẨM VÀO ĐƠN ----
        Runnable recalcTotal = () -> {
            double total = itemData.stream()
                    .mapToDouble(InvoiceItem::getSubTotal).sum();
            lblTotal.setText("Tổng tiền: " + String.format("%,.0f", total) + " VNĐ");
        };

        btnAddItem.setOnAction(e -> {
            Product selectedProduct = cbProduct.getValue();
            if (selectedProduct == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn sản phẩm!");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(tfQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                AlertHelper.showError("Lỗi", "Số lượng phải là số nguyên dương!");
                return;
            }

            // Kiểm tra đã có trong đơn chưa
            for (InvoiceItem item : itemData) {
                if (item.getProduct().getProductId()
                        .equals(selectedProduct.getProductId())) {
                    int newQty = item.getQuantity() + qty;
                    if (newQty > selectedProduct.getQuantity()) {
                        AlertHelper.showError("Lỗi",
                                "Vượt quá tồn kho! Còn: " + selectedProduct.getQuantity());
                        return;
                    }
                    item.setQuantity(newQty);
                    itemTable.refresh();
                    recalcTotal.run();
                    return;
                }
            }

            if (qty > selectedProduct.getQuantity()) {
                AlertHelper.showError("Lỗi",
                        "Vượt quá tồn kho! Còn: " + selectedProduct.getQuantity());
                return;
            }
            itemData.add(new InvoiceItem(selectedProduct, qty));
            recalcTotal.run();
        });

        btnRemoveItem.setOnAction(e -> {
            InvoiceItem sel = itemTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                itemData.remove(sel);
                recalcTotal.run();
            }
        });

        // ---- KẾT QUẢ ----
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;

            Customer customer = cbCustomer.getValue();
            if (customer == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn khách hàng!");
                return null;
            }
            if (itemData.isEmpty()) {
                AlertHelper.showWarning("Cảnh báo",
                        "Hóa đơn phải có ít nhất 1 sản phẩm!");
                return null;
            }

            String invoiceId = "HD" + System.currentTimeMillis();
            Invoice invoice = new Invoice(invoiceId, customer, LocalDate.now());
            for (InvoiceItem item : itemData) {
                invoice.addItem(item);
            }
            return invoice;
        });

        Optional<Invoice> result = dialog.showAndWait();
        result.ifPresent(invoice -> {
            try {
                boolean ok = AppContext.INVOICE_MANAGER.addInvoice(invoice);
                if (ok) {
                    // Lưu lại tồn kho đã trừ vào file
                    AppContext.PRODUCT_MANAGER.saveToFile();
                    AlertHelper.showInformation("Thành công",
                            "Đã tạo hóa đơn " + invoice.getInvoiceId() + "!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể tạo hóa đơn!");
                }
            } catch (Exception ex) {
                AlertHelper.showError("Lỗi", ex.getMessage());
            }
        });
    }

    // ================= XEM CHI TIẾT HÓA ĐƠN =================
    private void showInvoiceDetail(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mã hóa đơn : ").append(invoice.getInvoiceId()).append("\n");
        sb.append("Khách hàng : ")
                .append(invoice.getCustomer().getCustomerName()).append("\n");
        sb.append("Ngày       : ").append(invoice.getInvoiceDate()).append("\n");
        sb.append("─────────────────────────────────\n");

        for (InvoiceItem item : invoice.getItemList()) {
            sb.append(String.format("%-25s x%d  %,.0f VNĐ%n",
                    item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getSubTotal()
            ));
        }
        sb.append("─────────────────────────────────\n");
        sb.append(String.format("TỔNG CỘNG  : %,.0f VNĐ", invoice.getTotalAmount()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết hóa đơn");
        alert.setHeaderText("Hóa đơn: " + invoice.getInvoiceId());
        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(javafx.scene.text.Font.font("Monospaced", 13));
        ta.setPrefRowCount(12);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }

    // ================= REFRESH =================
    public void refresh() {
        invoiceData.setAll(AppContext.INVOICE_MANAGER.getAllInvoices());
    }

    // ================= GET VIEW =================
    public Parent getView() {
        return root;
    }
}