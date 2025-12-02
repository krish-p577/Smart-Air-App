package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.view.startTriageView;

public class startTriagePresenter {
    private final startTriageView view;

    public startTriagePresenter(startTriageView view) { this.view = view; }

    public void onStartClicked() {
        view.openForm();
    }
}
