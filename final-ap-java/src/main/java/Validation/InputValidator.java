package Validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    // اعتبارسنجی ایمیل
    public static boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // اعتبارسنجی رمز عبور
    public static boolean validatePassword(String password) {
        // رمز عبور باید حداقل 8 کاراکتر، شامل یک عدد و یک حرف باشد
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}