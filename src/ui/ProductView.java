package ui;

import manager.AppContext;
import model.*;
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

public class ProductView {

    private final BorderPane root = new BorderPane();
    private final TableView<Product> table = new TableView<>();
    private final ObservableList<Product> productData = FXCollections.observableArrayList();

    public ProductView() {
        buildUI();
        refresh();
    }

    // ================= UI =================
    private void buildUI() {

        // ===== CỘT BẢNG =====
        TableColumn<Product, String> typeCol = new TableColumn<>("Loại");
        typeCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductType())
        );
        typeCol.setPrefWidth(70);

        TableColumn<Product, String> idCol = new TableColumn<>("Mã SP");
        idCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductId())
        );
        idCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("Tên sản phẩm");
        nameCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName())
        );
        nameCol.setPrefWidth(180);

        TableColumn<Product, String> brandCol = new TableColumn<>("Hãng");
        brandCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getBrand())
        );
        brandCol.setPrefWidth(90);

        TableColumn<Product, String> priceCol = new TableColumn<>("Giá (VNĐ)");
        priceCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%,.0f", d.getValue().getProductPrice())
                )
        );
        priceCol.setPrefWidth(110);

        TableColumn<Product, String> qtyCol = new TableColumn<>("Tồn kho");
        qtyCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getQuantity())
                )
        );
        qtyCol.setPrefWidth(70);

        TableColumn<Product, String> specCol = new TableColumn<>("Thông số");
        specCol.setCellValueFactory(d -> {
            Product p = d.getValue();
            String spec = "";
            if (p instanceof Phone ph) {
                spec = "RAM " + ph.getRam() + "GB / " + ph.getStorage() + "GB";
            } else if (p instanceof Laptop lp) {
                spec = lp.getCpu() + " / RAM " + lp.getRam() + "GB";
            } else if (p instanceof TV tv) {
                spec = tv.getSize() + "\" " + tv.getResolution();
            }
            return new SimpleStringProperty(spec);
        });
        specCol.setPrefWidth(180);

        table.getColumns().setAll(List.of(
                typeCol, idCol, nameCol, brandCol, priceCol, qtyCol, specCol
        ));
        table.setItems(productData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ===== THANH TÌM KIẾM =====
        TextField tfSearch = new TextField();
        tfSearch.setPromptText("Tìm kiếm theo tên sản phẩm...");
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                refresh();
            } else {
                List<Product> result =
                        AppContext.PRODUCT_MANAGER.searchProductsByName(newVal);
                productData.setAll(result);
            }
        });

        // ===== COMBOBOX LỌC LOẠI =====
        ComboBox<String> cbFilter = new ComboBox<>();
        cbFilter.getItems().addAll("Tất cả", "PHONE", "LAPTOP", "TV");
        cbFilter.setValue("Tất cả");
        cbFilter.setOnAction(e -> {
            String selected = cbFilter.getValue();
            if ("Tất cả".equals(selected)) {
                refresh();
            } else {
                List<Product> filtered = AppContext.PRODUCT_MANAGER
                        .getAllProducts().stream()
                        .filter(p -> p.getProductType().equals(selected))
                        .toList();
                productData.setAll(filtered);
            }
        });

        HBox searchBar = new HBox(10, new Label("Tìm:"), tfSearch,
                new Label("Lọc:"), cbFilter);
        searchBar.setPadding(new Insets(0, 0, 6, 0));

        // ===== NÚT =====
        Button btnAdd    = new Button("➕ Thêm");
        Button btnEdit   = new Button("✏ Sửa");
        Button btnDelete = new Button("🗑 Xóa");
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
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }
            showEditDialog(selected);
        });

        btnDelete.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Cảnh báo", "Vui lòng chọn sản phẩm cần xóa!");
                return;
            }
            boolean confirm = AlertHelper.showConfirm(
                    "Xác nhận xóa",
                    "Bạn có chắc muốn xóa sản phẩm: " + selected.getProductName() + "?"
            );
            if (confirm) {
                boolean ok = AppContext.PRODUCT_MANAGER
                        .deleteProductById(selected.getProductId());
                if (ok) {
                    AlertHelper.showInformation("Thành công", "Đã xóa sản phẩm!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể xóa sản phẩm!");
                }
            }
        });
    }

    // ================= DIALOG THÊM SẢN PHẨM =================
    private void showAddDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Thêm sản phẩm mới");
        dialog.setHeaderText(null);

        // Chọn loại sản phẩm
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("PHONE", "LAPTOP", "TV");
        cbType.setValue("PHONE");

        // Các trường chung
        TextField tfId    = new TextField();  tfId.setPromptText("Mã SP (VD: SP001)");
        TextField tfName  = new TextField();  tfName.setPromptText("Tên sản phẩm");
        TextField tfBrand = new TextField();  tfBrand.setPromptText("Hãng sản xuất");
        TextField tfPrice = new TextField();  tfPrice.setPromptText("Giá (VNĐ)");
        TextField tfQty   = new TextField();  tfQty.setPromptText("Số lượng tồn kho");

        // Trường đặc thù — hiển thị theo loại
        Label    lblSpec1  = new Label("RAM (GB):");
        TextField tfSpec1  = new TextField(); tfSpec1.setPromptText("VD: 8");
        Label    lblSpec2  = new Label("Bộ nhớ (GB):");
        TextField tfSpec2  = new TextField(); tfSpec2.setPromptText("VD: 256");

        // Layout form
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(14));

        grid.add(new Label("Loại SP:"), 0, 0);   grid.add(cbType, 1, 0);
        grid.add(new Label("Mã SP:"), 0, 1);      grid.add(tfId, 1, 1);
        grid.add(new Label("Tên:"), 0, 2);         grid.add(tfName, 1, 2);
        grid.add(new Label("Hãng:"), 0, 3);        grid.add(tfBrand, 1, 3);
        grid.add(new Label("Giá (VNĐ):"), 0, 4);  grid.add(tfPrice, 1, 4);
        grid.add(new Label("Tồn kho:"), 0, 5);    grid.add(tfQty, 1, 5);
        grid.add(lblSpec1, 0, 6);                  grid.add(tfSpec1, 1, 6);
        grid.add(lblSpec2, 0, 7);                  grid.add(tfSpec2, 1, 7);

        // Cập nhật label đặc thù khi đổi loại
        cbType.setOnAction(e -> updateSpecLabels(
                cbType.getValue(), lblSpec1, lblSpec2, tfSpec1, tfSpec2
        ));
        updateSpecLabels("PHONE", lblSpec1, lblSpec2, tfSpec1, tfSpec2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            return buildProductFromForm(
                    cbType.getValue(),
                    tfId, tfName, tfBrand, tfPrice, tfQty,
                    tfSpec1, tfSpec2
            );
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            if (AppContext.PRODUCT_MANAGER.containsProductId(product.getProductId())) {
                AlertHelper.showError("Lỗi", "Mã sản phẩm đã tồn tại!");
                return;
            }
            boolean ok = AppContext.PRODUCT_MANAGER.addProduct(product);
            if (ok) {
                AlertHelper.showInformation("Thành công", "Đã thêm sản phẩm!");
                refresh();
            } else {
                AlertHelper.showError("Lỗi", "Không thể thêm sản phẩm!");
            }
        });
    }

    // ================= DIALOG SỬA SẢN PHẨM =================
    private void showEditDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Sửa sản phẩm: " + product.getProductName());
        dialog.setHeaderText(null);

        Label lblType = new Label(product.getProductType());
        lblType.setStyle("-fx-font-weight: bold;");

        TextField tfName  = new TextField(product.getProductName());
        TextField tfBrand = new TextField(product.getBrand());
        TextField tfPrice = new TextField(String.valueOf(product.getProductPrice()));
        TextField tfQty   = new TextField(String.valueOf(product.getQuantity()));

        // Điền sẵn thông số đặc thù
        Label    lblSpec1 = new Label();
        TextField tfSpec1 = new TextField();
        Label    lblSpec2 = new Label();
        TextField tfSpec2 = new TextField();

        if (product instanceof Phone ph) {
            lblSpec1.setText("RAM (GB):");
            tfSpec1.setText(String.valueOf(ph.getRam()));
            lblSpec2.setText("Bộ nhớ (GB):");
            tfSpec2.setText(String.valueOf(ph.getStorage()));
        } else if (product instanceof Laptop lp) {
            lblSpec1.setText("CPU:");
            tfSpec1.setText(lp.getCpu());
            lblSpec2.setText("RAM (GB):");
            tfSpec2.setText(String.valueOf(lp.getRam()));
        } else if (product instanceof TV tv) {
            lblSpec1.setText("Kích thước (inch):");
            tfSpec1.setText(String.valueOf(tv.getSize()));
            lblSpec2.setText("Độ phân giải:");
            tfSpec2.setText(tv.getResolution());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(14));

        grid.add(new Label("Loại SP:"), 0, 0);    grid.add(lblType, 1, 0);
        grid.add(new Label("Mã SP:"), 0, 1);
        grid.add(new Label(product.getProductId()), 1, 1);
        grid.add(new Label("Tên:"), 0, 2);         grid.add(tfName, 1, 2);
        grid.add(new Label("Hãng:"), 0, 3);        grid.add(tfBrand, 1, 3);
        grid.add(new Label("Giá (VNĐ):"), 0, 4);  grid.add(tfPrice, 1, 4);
        grid.add(new Label("Tồn kho:"), 0, 5);    grid.add(tfQty, 1, 5);
        grid.add(lblSpec1, 0, 6);                  grid.add(tfSpec1, 1, 6);
        grid.add(lblSpec2, 0, 7);                  grid.add(tfSpec2, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK, ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            return buildProductFromForm(
                    product.getProductType(),
                    null, tfName, tfBrand, tfPrice, tfQty,
                    tfSpec1, tfSpec2
            );
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try {
                // Giữ nguyên ID cũ
                updated.setProductId(product.getProductId());
                boolean ok = AppContext.PRODUCT_MANAGER.updateProduct(updated);
                if (ok) {
                    AlertHelper.showInformation("Thành công", "Đã cập nhật sản phẩm!");
                    refresh();
                } else {
                    AlertHelper.showError("Lỗi", "Không thể cập nhật sản phẩm!");
                }
            } catch (Exception ex) {
                AlertHelper.showError("Lỗi dữ liệu", ex.getMessage());
            }
        });
    }

    // ================= HELPER: Cập nhật label đặc thù =================
    private void updateSpecLabels(String type,
                                  Label lbl1, Label lbl2,
                                  TextField tf1, TextField tf2) {
        switch (type) {
            case "PHONE" -> {
                lbl1.setText("RAM (GB):");   tf1.setPromptText("VD: 8");
                lbl2.setText("Bộ nhớ (GB):"); tf2.setPromptText("VD: 256");
            }
            case "LAPTOP" -> {
                lbl1.setText("CPU:");         tf1.setPromptText("VD: Intel Core i5");
                lbl2.setText("RAM (GB):");    tf2.setPromptText("VD: 16");
            }
            case "TV" -> {
                lbl1.setText("Kích thước (inch):"); tf1.setPromptText("VD: 55");
                lbl2.setText("Độ phân giải:");      tf2.setPromptText("VD: 4K");
            }
        }
    }

    // ================= HELPER: Build Product từ form =================
    private Product buildProductFromForm(String type,
                                         TextField tfId,
                                         TextField tfName,
                                         TextField tfBrand,
                                         TextField tfPrice,
                                         TextField tfQty,
                                         TextField tfSpec1,
                                         TextField tfSpec2) {
        try {
            // Validate các trường chung
            if (tfName == null || Validation.isEmpty(tfName.getText()))
                throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
            if (Validation.isEmpty(tfBrand.getText()))
                throw new IllegalArgumentException("Hãng không được để trống!");
            if (Validation.isEmpty(tfPrice.getText()))
                throw new IllegalArgumentException("Giá không được để trống!");
            if (Validation.isEmpty(tfQty.getText()))
                throw new IllegalArgumentException("Số lượng không được để trống!");

            String id    = (tfId != null) ? tfId.getText().trim() : "";
            String name  = tfName.getText().trim();
            String brand = tfBrand.getText().trim();
            double price = Double.parseDouble(tfPrice.getText().trim());
            int    qty   = Integer.parseInt(tfQty.getText().trim());

            if (!Validation.isPositiveDouble(price))
                throw new IllegalArgumentException("Giá phải lớn hơn 0!");
            if (!Validation.isNonNegativeInt(qty))
                throw new IllegalArgumentException("Số lượng phải >= 0!");

            return switch (type) {
                case "PHONE" -> {
                    int ram     = Integer.parseInt(tfSpec1.getText().trim());
                    int storage = Integer.parseInt(tfSpec2.getText().trim());
                    yield new Phone(id, name, brand, price, qty, ram, storage);
                }
                case "LAPTOP" -> {
                    String cpu  = tfSpec1.getText().trim();
                    int    ram  = Integer.parseInt(tfSpec2.getText().trim());
                    yield new Laptop(id, name, brand, price, qty, cpu, ram);
                }
                case "TV" -> {
                    double size = Double.parseDouble(tfSpec1.getText().trim());
                    String res  = tfSpec2.getText().trim();
                    yield new TV(id, name, brand, price, qty, size, res);
                }
                default -> throw new IllegalArgumentException("Loại sản phẩm không hợp lệ!");
            };
        } catch (NumberFormatException e) {
            AlertHelper.showError("Lỗi định dạng", "Vui lòng nhập đúng kiểu số cho các trường số!");
            return null;
        } catch (IllegalArgumentException e) {
            AlertHelper.showError("Lỗi dữ liệu", e.getMessage());
            return null;
        }
    }

    // ================= REFRESH =================
    public void refresh() {
        productData.setAll(AppContext.PRODUCT_MANAGER.getAllProducts());
    }

    // ================= GET VIEW =================
    public Parent getView() {
        return root;
    }
}