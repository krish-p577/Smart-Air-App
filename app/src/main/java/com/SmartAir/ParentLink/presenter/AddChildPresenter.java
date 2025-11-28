package com.SmartAir.ParentLink.presenter;

import androidx.annotation.NonNull;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.ParentLink.view.AddChildView;

import java.util.List;

public class AddChildPresenter {

    private final AddChildView view;
    private final AuthRepository authRepository;

    public AddChildPresenter(@NonNull AddChildView view, @NonNull AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    // ------------------ PASSWORD VALIDATION ------------------

    public void validatePasswordRealtime(@NonNull String password) {
        List<String> failedRules = PasswordValidator.getFailedRules(password);
        List<String> passedRules = PasswordValidator.getPassedRules(password);
        view.updatePasswordRequirements(passedRules, failedRules);
    }

    // ------------------ ADD CHILD ------------------

    public void onAddChildClicked(String name, String age, String dob, String notes,
                                  String username, String password, String confirmPassword) {

        // Simple validation
        if (name.isEmpty() || age.isEmpty() || dob.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setAddChildError("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setAddChildError("Passwords do not match.");
            return;
        }

        List<String> failedRules = PasswordValidator.getFailedRules(password);
        if (!failedRules.isEmpty()) {
            view.setAddChildError("Password does not meet all requirements.");
            return;
        }

        view.setLoading(true);

        authRepository.createChildUser(name, age, dob, notes, username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Refresh parent children list in-memory
                authRepository.refreshCurrentParentChildren(new AuthRepository.ChildrenCallback() {
                    @Override
                    public void onSuccess(List<ChildUser> children) {
                        view.setLoading(false);
                        view.showSuccessMessage("Child added successfully!");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        view.setLoading(false);
                        view.setAddChildError("Child added but failed to refresh children: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.setAddChildError(errorMessage);
            }
        });
    }
}
