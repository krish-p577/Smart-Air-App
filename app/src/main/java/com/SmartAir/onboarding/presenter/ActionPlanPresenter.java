package com.SmartAir.presenter;

import com.SmartAir.view.ActionPlanView;

public class ActionPlanPresenter {
    private final ActionPlanView view;

    public ActionPlanPresenter(ActionPlanView view) {
        this.view = view;
    }

    public void onSubmitClicked() {
        view.sendPlan();
    }
}
