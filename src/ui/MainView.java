package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertHelper;

public class MainView {

    private final Stage stage;
    private final BorderPane root;

    // Khởi tạo 1 lần duy nhất — tránh mất trạng thái khi chuyển tab
    private final ProductView   productView   = new ProductView();
    private final CustomerView  customerView  = new CustomerView();
    private final InvoiceView   invoiceView   = new InvoiceView();
    private final StatisticView statisticView = new StatisticView();

    public MainView(Stage stage) {
        this.stage = stage;
        this.root  = new BorderPane();
        buildUI();
    }

    private void buildUI() {

        // ===== TIÊU ĐỀ =====
        Label title = new Label("HỆ THỐNG QUẢN LÝ BÁN HÀNG ĐIỆN TỬ");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(12));

        // ===== MENU TRÁI =====
        Button btnProduct   = new Button("📦 Sản phẩm");
        Button btnCustomer  = new Button("👤 Khách hàng");
        Button btnInvoice   = new Button("🧾 Hóa đơn");
        Button btnStatistic = new Button("📊 Thống kê");

        for (Button btn : new Button[]{
                btnProduct, btnCustomer, btnInvoice, btnStatistic
        }) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-font-size: 13px;");
        }

        VBox menu = new VBox(8,
                btnProduct, btnCustomer, btnInvoice, btnStatistic
        );
        menu.setPadding(new Insets(10));
        menu.setPrefWidth(150);
        root.setLeft(menu);

        // ===== MÀN HÌNH MẶC ĐỊNH =====
        Label welcome = new Label("👋 Chào mừng! Chọn chức năng bên trái.");
        welcome.setStyle("-fx-font-size: 14px;");
        root.setCenter(welcome);

        // ===== SỰ KIỆN CHUYỂN TAB =====
        btnProduct.setOnAction(e -> {
            productView.refresh();
            root.setCenter(productView.getView());
        });

        btnCustomer.setOnAction(e -> {
            customerView.refresh();
            root.setCenter(customerView.getView());
        });

        btnInvoice.setOnAction(e -> {
            invoiceView.refresh();
            root.setCenter(invoiceView.getView());
        });

        btnStatistic.setOnAction(e -> {
            // Refresh thống kê để lấy số liệu mới nhất
            statisticView.refresh();
            root.setCenter(statisticView.getView());
        });
    }

    public void show() {
        Scene scene = new Scene(root, 1050, 650);
        stage.setTitle("Phần mềm quản lý bán hàng điện tử");
        stage.setScene(scene);

        // ===== XÁC NHẬN TRƯỚC KHI THOÁT =====
        stage.setOnCloseRequest(e -> {
            e.consume(); // Chặn thoát ngay
            boolean confirm = AlertHelper.showConfirm(
                    "Thoát chương trình",
                    "Bạn có chắc muốn thoát không?\n(Dữ liệu đã được lưu tự động)"
            );
            if (confirm) {
                stage.close();
            }
        });

        stage.show();
    }
}