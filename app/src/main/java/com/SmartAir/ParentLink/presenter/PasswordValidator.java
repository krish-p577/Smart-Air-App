package com.SmartAir.ParentLink.presenter;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {

    // Example rules: at least 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
    public static List<String> getFailedRules(String password) {
        List<String> failedRules = new ArrayList<>();

        if (password.length() < 8) {
            failedRules.add("At least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            failedRules.add("At least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            failedRules.add("At least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            failedRules.add("At least one number");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            failedRules.add("At least one special character (!@#$%^&*())");
        }

        return failedRules;
    }

    public static List<String> getPassedRules(String password) {
        List<String> passedRules = new ArrayList<>();

        if (password.length() >= 8) {
            passedRules.add("At least 8 characters");
        }
        if (password.matches(".*[A-Z].*")) {
            passedRules.add("At least one uppercase letter");
        }
        if (password.matches(".*[a-z].*")) {
            passedRules.add("At least one lowercase letter");
        }
        if (password.matches(".*\\d.*")) {
            passedRules.add("At least one number");
        }
        if (password.matches(".*[!@#$%^&*()].*")) {
            passedRules.add("At least one special character (!@#$%^&*())");
        }

        return passedRules;
    }
}
