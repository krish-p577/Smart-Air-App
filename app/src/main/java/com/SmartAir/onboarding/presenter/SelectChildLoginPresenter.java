package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.SelectChildLoginView;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SelectChildLoginPresenter {

    private final SelectChildLoginView view;
    private final AuthRepository authRepository;

    public SelectChildLoginPresenter(SelectChildLoginView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void fetchChildren() {
        FirebaseUser parentUser = authRepository.getCurrentFirebaseUser();
        if (parentUser == null) {
            view.displayError("Could not verify parent session. Please try again.");
            view.closeView();
            return;
        }

        String parentId = parentUser.getUid();
        view.setLoading(true);
        authRepository.fetchChildrenForParent(parentId, new AuthRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<ChildUser> children) {
                view.setLoading(false);
                if (children.isEmpty()) {
                    view.displayError("No children found for this account.");
                } else {
                    view.displayChildren(children);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.displayError(errorMessage);
            }
        });
    }

    public void onChildSelected(ChildUser child, String childPassword) {
        if (childPassword == null || childPassword.isEmpty()) {
            view.displayError("Password cannot be empty.");
            return;
        }

        view.setLoading(true);

        authRepository.signInChild(child.getEmail(), childPassword, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                view.setLoading(false);
                ChildUser currentChild = (ChildUser) CurrentUser.getInstance().getUserProfile();
                if (currentChild != null && currentChild.isHasCompletedOnboarding()) {
                    view.navigateToChildHome();
                } else {
                    view.navigateToOnboarding();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.displayError("Failed to log in as child: " + errorMessage);
            }
        });
    }
}
