package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.PbView;

public class PbPresenter {
    private final PbView view;
    public PbPresenter(PbView view) {
        this.view = view;
    }
    public void onPBClicked() {
        view.popOut();
    }
}
