package org.example.economicssimulatorclient.util;

/**
 * Валидатор данных пользователя: email и пароля.
 */
public class Validator {

    /**
     * Простая email-проверка (наличие @ и домена).
     * @param email email-адрес для проверки
     * @return true если email выглядит валидно
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Проверка надежности пароля: минимум 8 символов, цифра, верхний/нижний регистр, спецсимвол.
     * @param password пароль для проверки
     * @return true если пароль соответствует требованиям безопасности
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
