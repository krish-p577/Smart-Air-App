package com.SmartAir.presenter;

import com.SmartAir.view.RoleSelectionView;

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
