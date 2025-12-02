package com.SmartAir.onboarding.presenter;

import android.content.Context;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.LoginView;

public class LoginPresenter {

    private final Context context;
    private final LoginView view;
    private final AuthRepository authRepository;

    // Modified constructor to include Context
    public LoginPresenter(Context context, LoginView view, AuthRepository authRepository) {
        this.context = context;
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onLoginClicked(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            view.setLoginError("Email and password cannot be empty");
            return;
        }

        final String finalEmail = email.trim();
        final String finalPassword = password.trim();

        authRepository.signInUser(finalEmail, finalPassword, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {

                // Save credentials in memory for reauth when adding children
                authRepository.storeParentCredentials(finalEmail, finalPassword);

                // Persist credentials so session survives app restart
                authRepository.persistParentCredentials(context, finalEmail, finalPassword);

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
