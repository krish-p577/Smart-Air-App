package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.RoleSelectionView;

public class RoleSelectionPresenter {

    private final RoleSelectionView view;

    public RoleSelectionPresenter(RoleSelectionView view) {
        this.view = view;
    }

    public void onParentClicked() {
        view.navigateToParentSignIn();
    }

    public void onProviderClicked() {
        view.navigateToProviderSignIn();
    }

    public void onChildClicked() {
        view.navigateToChildSignIn();
    }

}
