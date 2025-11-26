package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

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

    public SignupPresenter(SignupView view) {
        this.view = view;
        this.authRepository = AuthRepository.getInstance();
    }

    boolean isValidEmail(String email) {
        return email != null && EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public void onSignupClicked(String email, String password, String confirmPassword, String role, String displayName) {
        // Added displayName to the required fields check.
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName == null || displayName.trim().isEmpty()) {
            view.setSignupError("All fields must be filled");
            return;
        }

        if (password.length() < 6) {
            view.setSignupError("Password must be at least 6 characters long");
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

    public void onLoginLinkClicked() {
        view.navigateToLogin();
    }
}
