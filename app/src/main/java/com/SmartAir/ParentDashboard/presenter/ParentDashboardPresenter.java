package com.SmartAir.ParentDashboard.presenter;

import com.SmartAir.ParentDashboard.view.ParentDashboardView;

public class ParentDashboardPresenter {

    private final ParentDashboardView view;
    public ParentDashboardPresenter(ParentDashboardView view) {this.view = view;}
    public void onpdfclicked(){view.navToPdf();}
}
