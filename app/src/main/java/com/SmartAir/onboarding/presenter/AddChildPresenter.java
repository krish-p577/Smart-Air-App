package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.AddChildView;

public class AddChildPresenter {

    private final AddChildView view;
    private final AuthRepository authRepository;

    public AddChildPresenter(AddChildView view) {
        this.view = view;
        this.authRepository = AuthRepository.getInstance();
    }

    public void onAddChildClicked(String username, String password) {
        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            view.setAddChildError("Username and password cannot be empty.");
            return;
        }
        if (password.length() < 6) {
            view.setAddChildError("Password must be at least 6 characters.");
            return;
        }

        view.setLoading(true);

        authRepository.createChildUser(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.setLoading(false);
                view.showSuccessMessage("Child account created successfully!");
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.setAddChildError(errorMessage);
            }
        });
    }
}
