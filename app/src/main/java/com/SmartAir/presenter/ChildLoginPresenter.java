package com.SmartAir.presenter;

import com.SmartAir.model.AuthRepository;
import com.SmartAir.view.ChildLoginView;

public class ChildLoginPresenter {

    private final ChildLoginView view;
    private final AuthRepository authRepository;

    public ChildLoginPresenter(ChildLoginView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onLoginClicked(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.setLoginError("Username and password cannot be empty");
            return;
        }

        authRepository.signInChild(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.navigateToChildHome();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoginError(errorMessage);
            }
        });
    }
}
