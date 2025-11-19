package com.SmartAir.presenter;

import com.SmartAir.model.AuthRepository;
import com.SmartAir.view.LoginView;

public class LoginPresenter {

    private final LoginView view;
    private final AuthRepository authRepository;

    public LoginPresenter(LoginView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onLoginClicked(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.setLoginError("Email and password cannot be empty");
            return;
        }

        authRepository.signInUser(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.navigateToHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoginError(errorMessage);
            }
        });
    }

    public void onSignupLinkClicked() {
        view.navigateToSignup();
    }
}
