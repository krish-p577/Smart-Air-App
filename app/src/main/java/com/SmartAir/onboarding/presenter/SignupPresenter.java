package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignupPresenter {

    private final SignupView view;
    private final AuthRepository authRepository;

    // Standard email regex pattern - no Android dependency.
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    // Only constructor with dependency injection
    public SignupPresenter(SignupView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    boolean isValidEmail(String email) {
        return email != null && EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public void onSignupClicked(String email, String password, String confirmPassword, String role, String displayName) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty() || displayName == null || displayName.trim().isEmpty()) {
            view.setSignupError("All fields must be filled");
            return;
        }

        String passwordValidationMessage = getPasswordValidationMessage(password);
        if (passwordValidationMessage != null) {
            view.setSignupError(passwordValidationMessage);
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setSignupError("Passwords do not match");
            return;
        }

        if (!isValidEmail(email)) {
            view.setSignupError("Please enter a valid email address");
            return;
        }

        view.setLoading(true);
        authRepository.createUser(email, password, role, displayName, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.navigateToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setSignupError(errorMessage);
            }
        });
    }

    public void validatePasswordRealtime(String password) {
        List<String> passedRules = new ArrayList<>();
        List<String> failedRules = new ArrayList<>();

        if (password.length() < 6) {
            failedRules.add("Password must be at least 6 characters long");
        } else {
            passedRules.add("Password must be at least 6 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            failedRules.add("Password must contain at least one uppercase letter");
        } else {
            passedRules.add("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            failedRules.add("Password must contain at least one lowercase letter");
        } else {
            passedRules.add("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            failedRules.add("Password must contain at least one digit");
        } else {
            passedRules.add("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            failedRules.add("Password must contain at least one special character (!@#$%^&*())");
        } else {
            passedRules.add("Password must contain at least one special character (!@#$%^&*())");
        }

        view.updatePasswordRequirements(passedRules, failedRules);
    }

    private String getPasswordValidationMessage(String password) {
        if (password.length() < 6) return "Password must be at least 6 characters long";
        if (!password.matches(".*[A-Z].*")) return "Password must contain at least one uppercase letter";
        if (!password.matches(".*[a-z].*")) return "Password must contain at least one lowercase letter";
        if (!password.matches(".*\\d.*")) return "Password must contain at least one digit";
        if (!password.matches(".*[!@#$%^&*()].*")) return "Password must contain at least one special character (!@#$%^&*())";
        return null; // all rules passed
    }

    public void onLoginLinkClicked() {
        view.navigateToLogin();
    }
}
