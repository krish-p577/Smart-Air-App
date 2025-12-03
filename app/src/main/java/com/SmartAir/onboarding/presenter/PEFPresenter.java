package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.PEFEntryView;

public class PEFPresenter {
    private final PEFEntryView view;
    public PEFPresenter(PEFEntryView view) {
        this.view = view;
    }
    public void onPEFClicked() {
        view.popOut();
    }

}
