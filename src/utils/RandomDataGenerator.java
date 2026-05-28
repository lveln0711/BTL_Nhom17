package utils;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDataGenerator {

    private static final Random RANDOM = new Random();

    // ================= PRODUCT =================
    public static List<Product> generateProducts(int amount) {
        List<Product> list = new ArrayList<>();

        String[] phoneNames = {
            "iPhone 15 Pro", "iPhone 14", "Samsung Galaxy S24",
            "Samsung Galaxy A55", "Xiaomi 14 Pro", "OPPO Find X7",
            "Vivo V30", "Realme GT 5"
        };
        String[] laptopNames = {
            "MacBook Air M2", "MacBook Pro M3", "Dell XPS 15",
            "Asus ROG Strix G16", "HP Pavilion 15", "Lenovo ThinkPad X1",
            "Acer Swift 5", "MSI Modern 15"
        };
        String[] tvNames = {
            "Samsung QLED 55\"", "LG OLED C3 65\"", "Sony Bravia XR 75\"",
            "TCL Mini LED 55\"", "Panasonic MX800 65\"", "Philips Ambilight 50\""
        };
        String[] phoneBrands  = {"Apple", "Samsung", "Xiaomi", "OPPO", "Vivo", "Realme"};
        String[] laptopBrands = {"Apple", "Dell", "Asus", "HP", "Lenovo", "Acer", "MSI"};
        String[] tvBrands     = {"Samsung", "LG", "Sony", "TCL", "Panasonic", "Philips"};

        String[] cpus = {
            "Intel Core i5-13500H", "Intel Core i7-13700H",
            "AMD Ryzen 5 7535HS", "AMD Ryzen 7 7745HX",
            "Apple M2", "Apple M3 Pro"
        };
        String[] resolutions = {"Full HD", "4K UHD", "4K OLED", "8K"};
        int[]    phoneSizes  = {55, 65, 75, 85};

        for (int i = 1; i <= amount; i++) {
            int type = RANDOM.nextInt(3);
            switch (type) {
                case 0 -> {
                    int ram     = pickFrom(new int[]{4, 6, 8, 12, 16});
                    int storage = pickFrom(new int[]{64, 128, 256, 512});
                    list.add(new Phone(
                        "DT" + String.format("%03d", i),
                        phoneNames[RANDOM.nextInt(phoneNames.length)],
                        phoneBrands[RANDOM.nextInt(phoneBrands.length)],
                        randomPhonePrice(),
                        RANDOM.nextInt(30) + 5,
                        ram, storage
                    ));
                }
                case 1 -> {
                    int ram = pickFrom(new int[]{8, 16, 32});
                    list.add(new Laptop(
                        "LT" + String.format("%03d", i),
                        laptopNames[RANDOM.nextInt(laptopNames.length)],
                        laptopBrands[RANDOM.nextInt(laptopBrands.length)],
                        randomLaptopPrice(),
                        RANDOM.nextInt(15) + 3,
                        cpus[RANDOM.nextInt(cpus.length)],
                        ram
                    ));
                }
                default -> {
                    double size = phoneSizes[RANDOM.nextInt(phoneSizes.length)];
                    list.add(new TV(
                        "TV" + String.format("%03d", i),
                        tvNames[RANDOM.nextInt(tvNames.length)],
                        tvBrands[RANDOM.nextInt(tvBrands.length)],
                        randomTVPrice(),
                        RANDOM.nextInt(10) + 2,
                        size,
                        resolutions[RANDOM.nextInt(resolutions.length)]
                    ));
                }
            }
        }
        return list;
    }

    // ================= CUSTOMER =================
    public static List<Customer> generateCustomers(int amount) {
        List<Customer> list = new ArrayList<>();

        String[] firstNames = {
            "Nguyễn Văn", "Trần Thị", "Lê Văn", "Phạm Thị",
            "Hoàng Văn", "Vũ Thị", "Đặng Văn", "Bùi Thị",
            "Đỗ Văn", "Hồ Thị"
        };
        String[] lastNames = {
            "An", "Bình", "Chi", "Dung", "Em",
            "Phong", "Giang", "Hà", "Khánh", "Lan",
            "Minh", "Nam", "Oanh", "Phúc", "Quân"
        };
        String[] addresses = {
            "Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng",
            "Hải Phòng", "Cần Thơ", "Huế", "Nha Trang",
            "Vũng Tàu", "Việt Trì", "Thái Nguyên"
        };

        for (int i = 1; i <= amount; i++) {
            String name = firstNames[RANDOM.nextInt(firstNames.length)]
                        + " " + lastNames[RANDOM.nextInt(lastNames.length)];
            // SĐT hợp lệ: 0 + 9 chữ số (đúng format Validation)
            String phone = "0" + (300_000_000 + RANDOM.nextInt(600_000_000));
            list.add(new Customer(
                "KH" + String.format("%03d", i),
                name,
                phone,
                addresses[RANDOM.nextInt(addresses.length)]
            ));
        }
        return list;
    }

    // ================= PRICE HELPERS =================
    private static double randomPhonePrice() {
        // 3 triệu → 35 triệu
        return (3 + RANDOM.nextInt(32)) * 1_000_000.0;
    }

    private static double randomLaptopPrice() {
        // 10 triệu → 50 triệu
        return (10 + RANDOM.nextInt(40)) * 1_000_000.0;
    }

    private static double randomTVPrice() {
        // 8 triệu → 60 triệu
        return (8 + RANDOM.nextInt(52)) * 1_000_000.0;
    }

    private static int pickFrom(int[] arr) {
        return arr[RANDOM.nextInt(arr.length)];
    }

    // Constructor private — chống tạo object
    private RandomDataGenerator() {}
}