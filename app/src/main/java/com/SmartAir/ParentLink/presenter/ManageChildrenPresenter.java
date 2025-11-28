package com.SmartAir.ParentLink.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.ParentLink.view.ManageChildrenView;

import java.util.List;

public class ManageChildrenPresenter {

    private ManageChildrenView view;
    private AuthRepository authRepository;

    public ManageChildrenPresenter(ManageChildrenView view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    public void fetchChildren() {
        view.setLoading(true);
        String parentId = authRepository.getCurrentFirebaseUser().getUid();
        authRepository.fetchChildrenForParent(parentId, new AuthRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<ChildUser> children) {
                view.setLoading(false);
                view.displayChildren(children);
            }

            @Override
            public void onFailure(String errorMessage) {
                view.setLoading(false);
                view.displayError(errorMessage);
            }
        });
    }

    public void onAddChildClicked() {
        view.navigateToAddChild();
    }

    public void onChildClicked(ChildUser child) {
        view.navigateToChildDetail(child.getUid());
    }
}
