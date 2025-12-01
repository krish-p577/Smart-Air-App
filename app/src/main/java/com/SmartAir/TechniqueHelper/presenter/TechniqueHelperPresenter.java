package com.SmartAir.TechniqueHelper.presenter;

import com.SmartAir.TechniqueHelper.model.TechniqueHelperRepository;
import com.SmartAir.TechniqueHelper.view.TechniqueHelperView;

public class TechniqueHelperPresenter {

    private final TechniqueHelperView view;
    private final TechniqueHelperRepository repo;

    public TechniqueHelperPresenter(TechniqueHelperView view, TechniqueHelperRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }
}
