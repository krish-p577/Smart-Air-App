package com.SmartAir.ParentDashboard.presenter;

import com.SmartAir.ParentDashboard.view.PbView;

public class PbPresenter {
    private final PbView view;
    public PbPresenter(PbView view) {
        this.view = view;
    }
    public void onPBClicked() {
        view.popOut();
    }
}
