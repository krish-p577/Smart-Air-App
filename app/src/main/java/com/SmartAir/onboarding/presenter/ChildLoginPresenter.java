package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.ChildLoginView;

public class ChildLoginPresenter {

    private final ChildLoginView view;
    private final AuthRepository authRepository;

    public ChildLoginPresenter(ChildLoginView view) {
        this.view = view;
        this.authRepository = AuthRepository.getInstance();
    }

    public void onLoginClicked(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.setLoginError("Username and password cannot be empty");
            return;
        }

        authRepository.signInChild(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Check if user has completed onboarding
                if (CurrentUser.getInstance().getUserProfile() != null && CurrentUser.getInstance().getUserProfile().isHasCompletedOnboarding()) {
                    view.navigateToChildHome();
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
}
