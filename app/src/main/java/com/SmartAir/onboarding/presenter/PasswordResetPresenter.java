package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.view.PasswordResetView;

public class PasswordResetPresenter {

    private final PasswordResetView view;
    private final AuthRepository authRepository;

    // Only DI constructor (singleton removed)
    public PasswordResetPresenter(PasswordResetView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void onSendResetClicked(String email) {
        if (email == null || email.trim().isEmpty()) {
            view.showErrorMessage("Please enter your email");
            return;
        }

        // Create a new, final variable for the trimmed email.
        final String trimmedEmail = email.trim();
        view.setLoading(true);

        // Use the final variable in the repository call.
        String finalEmail = email;
        authRepository.sendPasswordResetEmail(trimmedEmail, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.setLoading(false);
                // Use the final variable inside the inner class.
                view.showSuccessMessage("Password reset email sent to " + trimmedEmail);
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.showErrorMessage(errorMessage);
            }
        });
    }
}
