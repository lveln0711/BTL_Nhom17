package ui;

import javafx.application.Application;
import javafx.stage.Stage;

import manager.*;
import utils.RandomDataGenerator;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ===================== MANAGERS =====================
        ProductManager pm = AppContext.PRODUCT_MANAGER;
        CustomerManager cm = AppContext.CUSTOMER_MANAGER;
        InvoiceManager im = AppContext.INVOICE_MANAGER;

        // ===================== INIT DATA =====================
        if (pm.isEmpty()) {
            RandomDataGenerator.generateProducts(20).forEach(pm::addProduct);
        }

        if (cm.isEmpty()) {
            RandomDataGenerator.generateCustomers(10).forEach(cm::addCustomer);
        }

        if (im.isEmpty()) {
            // optional future seed
        }

        // ===================== SHOW UI =====================
        new MainView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}