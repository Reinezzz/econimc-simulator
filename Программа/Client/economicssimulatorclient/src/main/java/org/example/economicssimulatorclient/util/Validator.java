package org.example.economicssimulatorclient.util;

/**
 * Набор простых проверок для ввода пользователя.
 * Включает валидацию email и проверку надёжности пароля.
 */
public class Validator {

    /**
     * Проверяет корректность email-адреса по простому регулярному выражению.
     *
     * @param email email пользователя
     * @return {@code true}, если адрес валиден
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Проверяет сложность пароля по длине и наличию разных категорий символов.
     *
     * @param password пароль пользователя
     * @return {@code true}, если пароль достаточно сложный
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()_+-=[]{}|;:'\",.<>/?".indexOf(c) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
