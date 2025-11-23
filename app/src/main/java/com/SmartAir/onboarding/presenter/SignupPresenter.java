package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

public class SignupPresenter {

    private final SignupView view;
    private final AuthRepository authRepository;

    public SignupPresenter(SignupView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onSignupClicked(String email, String password, String confirmPassword, String role, String displayName) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setSignupError("All fields must be filled");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setSignupError("Passwords do not match");
            return;
        }

        // Default display name to username part of email if not provided
        if (displayName == null || displayName.isEmpty()) {
            displayName = email.split("@")[0];
        }

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
