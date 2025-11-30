package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.AddChildView;

import java.util.ArrayList;
import java.util.List;

public class AddChildPresenter {

    private final AddChildView view;
    private final AuthRepository authRepository;

    // Only one constructor: everything must be injected
    public AddChildPresenter(AddChildView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onAddChildClicked(String username, String password, String confirmPassword) {
        if (username == null || password == null || confirmPassword == null || username.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setAddChildError("All fields must be filled");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setAddChildError("Passwords do not match");
            return;
        }

        String passwordValidationMessage = getPasswordValidationMessage(password);
        if (passwordValidationMessage != null) {
            view.setAddChildError(passwordValidationMessage);
            return;
        }

        username = username.trim();

        view.setLoading(true);

        authRepository.createChildUser(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.setLoading(false);
                view.showSuccessMessage("Child account created successfully!");
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.setAddChildError(errorMessage);
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
}
