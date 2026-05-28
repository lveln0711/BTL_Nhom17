package utils;

import java.util.List;

public class Validation {

    // Kiểm tra chuỗi rỗng
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Kiểm tra số dương (> 0)
    public static boolean isPositiveDouble(double value) {
        return value > 0;
    }

    // Kiểm tra số nguyên >= 0
    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    /**
     * Kiểm tra số điện thoại Việt Nam.
     * Đồng bộ với Customer.setPhoneNumber():
     *   - 10 hoặc 11 chữ số
     *   - Bắt đầu bằng 0 (hoặc +84 rồi 9 số tiếp theo)
     * Regex: ^(0\d{9,10}|\+84\d{9})$
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.trim().matches("^(0\\d{9,10}|\\+84\\d{9})$");
    }

    // Kiểm tra ID trùng trong danh sách
    public static boolean isDuplicateId(List<String> idList, String id) {
        if (idList == null || isEmpty(id)) return false;
        for (String item : idList) {
            if (item.equalsIgnoreCase(id.trim())) return true;
        }
        return false;
    }

    // Kiểm tra chuỗi chỉ chứa chữ, số và khoảng trắng
    public static boolean isAlphaNumeric(String value) {
        if (isEmpty(value)) return false;
        return value.matches("^[a-zA-Z0-9 ]+$");
    }

    // Constructor private — chống tạo object
    private Validation() {}
}