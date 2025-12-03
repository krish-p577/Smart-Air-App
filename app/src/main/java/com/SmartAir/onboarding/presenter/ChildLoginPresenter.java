package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.ChildLoginView;

public class ChildLoginPresenter {

    private final ChildLoginView view;
    private final AuthRepository authRepository;

    // Constructor requires injection of AuthRepository
    public ChildLoginPresenter(ChildLoginView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onLoginClicked(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            view.setLoginError("Username and password cannot be empty");
            return;
        }

        authRepository.signInChildWithUsername(username.trim(), password.trim(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                if (CurrentUser.getInstance().getUserProfile() != null &&
                        CurrentUser.getInstance().getUserProfile().isHasCompletedOnboarding()) {
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
