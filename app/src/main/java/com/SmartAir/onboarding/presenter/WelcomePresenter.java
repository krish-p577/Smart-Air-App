package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.WelcomeView;

public class WelcomePresenter {

    private final WelcomeView view;

    public WelcomePresenter(WelcomeView view) {
        this.view = view;
    }

    public void onContinueClicked() {
        view.navigateNext();
    }
}
