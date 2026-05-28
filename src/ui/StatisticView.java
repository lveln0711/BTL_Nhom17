package ui;

import manager.AppContext;
import model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticView {

    private final BorderPane root = new BorderPane();

    public StatisticView() {
        refresh();
    }

    // ================= REFRESH (gọi mỗi khi chuyển sang tab Thống kê) =================
    public void refresh() {
        var productManager  = AppContext.PRODUCT_MANAGER;
        var customerManager = AppContext.CUSTOMER_MANAGER;
        var invoiceManager  = AppContext.INVOICE_MANAGER;

        Label title = new Label("THỐNG KÊ HỆ THỐNG");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label productCount = new Label(
                "Tổng sản phẩm  : " + productManager.getTotalProducts()
        );
        Label customerCount = new Label(
                "Tổng khách hàng: " + customerManager.getTotalCustomers()
        );
        Label invoiceCount = new Label(
                "Tổng hóa đơn   : " + invoiceManager.getTotalInvoices()
        );
        Label revenue = new Label(
                String.format("Tổng doanh thu : %,.0f VNĐ",
                        invoiceManager.getTotalRevenue())
        );
        revenue.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        for (Label lbl : List.of(productCount, customerCount, invoiceCount, revenue)) {
            lbl.setStyle(lbl.getStyle() + "-fx-font-size: 13px;");
        }

        PieChart chart = new PieChart(
                buildChart(productManager.getAllProducts())
        );
        chart.setTitle("Cơ cấu sản phẩm theo loại");
        chart.setPrefHeight(300);

        VBox box = new VBox(10,
                title,
                productCount,
                customerCount,
                invoiceCount,
                revenue,
                chart
        );
        box.setPadding(new Insets(16));
        root.setCenter(box);
    }

    // ================= CHART =================
    private ObservableList<PieChart.Data> buildChart(List<Product> products) {
        Map<String, Integer> map = new HashMap<>();
        for (Product p : products) {
            String type = p.getProductType();
            map.put(type, map.getOrDefault(type, 0) + 1);
        }
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            data.add(new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + ")",
                    entry.getValue()
            ));
        }
        return data;
    }

    // ================= GET VIEW =================
    public Parent getView() {
        return root;
    }
}