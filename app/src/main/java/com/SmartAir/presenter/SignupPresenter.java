package com.SmartAir.presenter;

import com.SmartAir.model.AuthRepository;
import com.SmartAir.view.SignupView;

public class SignupPresenter {

    private final SignupView view;
    private final AuthRepository authRepository;

    public SignupPresenter(SignupView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onSignupClicked(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setSignupError("All fields must be filled");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setSignupError("Passwords do not match");
            return;
        }

        authRepository.createUser(email, password, new AuthRepository.AuthCallback() {
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
