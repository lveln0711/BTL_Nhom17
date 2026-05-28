package ui;

import manager.AppContext;
import model.Customer;
import utils.AlertHelper;
import utils.Validation;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

public class CustomerView {

    private final BorderPane root = new BorderPane();
    private final TableView<Customer> table = new TableView<>();
    private final ObservableList<Customer> customerData = FXCollections.observableArrayList();

    public CustomerView() {
        buildUI();
        refresh();
    }

    // ================= UI =================
    private void buildUI() {

        // ===== CỘT BẢNG =====
        TableColumn<Customer, String> idCol = new TableColumn<>("Mã KH");
        idCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCustomerId())
        );
        idCol.setPrefWidth(90);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Tên khách hàng");
        nameCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCustomerName())
        );
        nameCol.setPrefWidth(180);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Số điện thoại");
        phoneCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPhoneNumber())
        );
        phoneCol.setPrefWidth(120);

        TableColumn<Customer, String> addressCol = new TableColumn<>("Địa chỉ");
        addressCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAddress())
        );
        addressCol.setPrefWidth(200);

        table.getColumns().setAll(List.of(idCol, nameCol, phoneCol, addressCol));
        table.setItems(customerData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ===== THANH TÌM KIẾM =====
        TextField tfSearch = new TextField();
        tfSearch.setPromptText("Tìm kiếm theo tên khách hàng...");
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                refresh();
            } else {
                List<Customer> result =
                        AppContext.CUSTOMER_MANAGER.searchCustomersByName(newVal);
                customerData.setAll(result);
            }
        });

        HBox searchBar = new HBox(10, new Label("Tìm:"), tfSearch);
        searchBar.setPadding(new Insets(0, 0, 6, 0));

        // ===== NÚT =====
        Button btnAdd     = new Button("➕ Thêm");
        Button btnEdit    = new Button("✏ Sửa");
        Button btnDelete  = new Button("🗑 Xóa");
        Button btnRefresh = new Button("🔄 Tải lại");

        HBox actions = new HBox(10, btnAdd, btnEdit, btnDelete, btnRefresh);
        actions.setPadding(new Insets(8, 0, 0, 0));

        VBox container = new VBox(6, searchBar, table, actions);
        container.setPadding(new Insets(10));
        root.setCenter(container);

        // ===== SỰ KIỆN =====
        btnRefresh.setOnAction(e -> refresh());

        btnAdd.setOnAction(e -> showAddDialog());

        btnEdit.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần sửa!");
                return;
            }
            showEditDialog(selected);
        });

        btnDelete.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn khách hàng cần xóa!");
                return;
            }
            boolean confirm = AlertHelper.showConfirm(
                    "Xác nhận xóa",
                    "Bạn có chắc muốn xóa khách hàng: " + selected.getCustomerName() + "?"
            );
            if (confirm) {
                boolean ok = AppContext.CUSTOMER_MANAGER
                        .deleteCustomerById(selected.getCustomerId());
                if (ok) {
                    AlertHelper.showInformation("Thành công", "Đã xóa khách hàng!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể xóa khách hàng!");
                }
            }
        });
    }

    // ================= DIALOG THÊM KHÁCH HÀNG =================
    private void showAddDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Thêm khách hàng mới");
        dialog.setHeaderText(null);

        TextField tfId      = new TextField(); tfId.setPromptText("Mã KH (VD: KH001)");
        TextField tfName    = new TextField(); tfName.setPromptText("Họ và tên");
        TextField tfPhone   = new TextField(); tfPhone.setPromptText("SĐT (10-11 số)");
        TextField tfAddress = new TextField(); tfAddress.setPromptText("Địa chỉ");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(14));

        grid.add(new Label("Mã KH:"),    0, 0); grid.add(tfId,      1, 0);
        grid.add(new Label("Tên:"),      0, 1); grid.add(tfName,    1, 1);
        grid.add(new Label("SĐT:"),      0, 2); grid.add(tfPhone,   1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3); grid.add(tfAddress, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            return buildCustomerFromForm(
                    tfId, tfName, tfPhone, tfAddress
            );
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(customer -> {
            if (AppContext.CUSTOMER_MANAGER.containsCustomerId(customer.getCustomerId())) {
                AlertHelper.showError("Lỗi", "Mã khách hàng đã tồn tại!");
                return;
            }
            boolean ok = AppContext.CUSTOMER_MANAGER.addCustomer(customer);
            if (ok) {
                AlertHelper.showInformation("Thành công", "Đã thêm khách hàng!");
                refresh();
            } else {
                AlertHelper.showError("Lỗi", "Không thể thêm khách hàng!");
            }
        });
    }

    // ================= DIALOG SỬA KHÁCH HÀNG =================
    private void showEditDialog(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Sửa khách hàng: " + customer.getCustomerName());
        dialog.setHeaderText(null);

        TextField tfName    = new TextField(customer.getCustomerName());
        TextField tfPhone   = new TextField(customer.getPhoneNumber());
        TextField tfAddress = new TextField(customer.getAddress());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(14));

        grid.add(new Label("Mã KH:"),    0, 0);
        grid.add(new Label(customer.getCustomerId()), 1, 0);
        grid.add(new Label("Tên:"),      0, 1); grid.add(tfName,    1, 1);
        grid.add(new Label("SĐT:"),      0, 2); grid.add(tfPhone,   1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3); grid.add(tfAddress, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            // tfId = null vì không cho sửa ID
            return buildCustomerFromForm(null, tfName, tfPhone, tfAddress);
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try {
                updated.setCustomerId(customer.getCustomerId());
                boolean ok = AppContext.CUSTOMER_MANAGER.updateCustomer(updated);
                if (ok) {
                    AlertHelper.showInformation("Thành công", "Đã cập nhật khách hàng!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể cập nhật khách hàng!");
                }
            } catch (Exception ex) {
                AlertHelper.showError("Lỗi dữ liệu", ex.getMessage());
            }
        });
    }

    // ================= HELPER: Build Customer từ form =================
    private Customer buildCustomerFromForm(TextField tfId,
                                           TextField tfName,
                                           TextField tfPhone,
                                           TextField tfAddress) {
        try {
            String name    = tfName.getText().trim();
            String phone   = tfPhone.getText().trim();
            String address = tfAddress.getText().trim();
            String id      = (tfId != null) ? tfId.getText().trim() : "";

            if (Validation.isEmpty(name))
                throw new IllegalArgumentException("Tên khách hàng không được để trống!");
            if (!Validation.isValidPhone(phone))
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10-11 số, bắt đầu bằng 0)!");
            if (Validation.isEmpty(address))
                throw new IllegalArgumentException("Địa chỉ không được để trống!");

            return new Customer(id, name, phone, address);

        } catch (IllegalArgumentException e) {
            AlertHelper.showError("Lỗi dữ liệu", e.getMessage());
            return null;
        }
    }

    // ================= REFRESH =================
    public void refresh() {
        customerData.setAll(AppContext.CUSTOMER_MANAGER.getAllCustomers());
    }

    // ================= GET VIEW =================
    public Parent getView() {
        return root;
    }
}