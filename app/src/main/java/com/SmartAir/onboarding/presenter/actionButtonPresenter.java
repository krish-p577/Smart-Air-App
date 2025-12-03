package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.actionButtonView;

public class actionButtonPresenter {
    private final actionButtonView view;

    public actionButtonPresenter(actionButtonView view) {
        this.view = view;
    }

    public void onContinueClicked() {
        view.navigateNext();
    }
}
