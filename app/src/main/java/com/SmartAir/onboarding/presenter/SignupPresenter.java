package com.SmartAir.onboarding.presenter;

import android.util.Patterns;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.SignupView;

public class SignupPresenter {

    private final SignupView view;
    private final AuthRepository authRepository;

    public SignupPresenter(SignupView view) {
        this.view = view;
        this.authRepository = AuthRepository.getInstance();
    }

    public void onSignupClicked(String email, String password, String confirmPassword, String role, String displayName) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setSignupError("All fields must be filled");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.setSignupError("Please enter a valid email address");
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

        if (displayName == null || displayName.isEmpty()) {
            displayName = email.split("@")[0];
        }

        view.setLoading(true); // Inform the view that loading has started
        authRepository.createUser(email, password, role, displayName, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // The view will handle setting loading to false upon navigation
                view.navigateToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                // The view will handle setting loading to false
                view.setSignupError(errorMessage);
            }
        });
    }

    public void onLoginLinkClicked() {
        view.navigateToLogin();
    }
}
