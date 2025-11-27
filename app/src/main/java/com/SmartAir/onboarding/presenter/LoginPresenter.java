package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.LoginView;

public class LoginPresenter {

    private final LoginView view;
    private final AuthRepository authRepository;

    // DI-only constructor
    public LoginPresenter(LoginView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onLoginClicked(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            view.setLoginError("Email and password cannot be empty");
            return;
        }

        email = email.trim();
        password = password.trim();

        authRepository.signInUser(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Navigate based on onboarding status
                if (CurrentUser.getInstance().getUserProfile().isHasCompletedOnboarding()) {
                    view.navigateToHome();
                } else {
                    view.navigateToOnboarding();
                }
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
