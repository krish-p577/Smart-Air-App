package com.SmartAir.ChildDashboard.presenter;

import com.SmartAir.ChildDashboard.view.ChildDashboardView;
import com.SmartAir.ChildDashboard.data.ChildDashboardRepository;

public class ChildDashboardPresenter {

    private final ChildDashboardView view;
    private final ChildDashboardRepository repo;

    public ChildDashboardPresenter(ChildDashboardView view, ChildDashboardRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onScreenStart(String parentId, String childId) {
        repo.getChildName(parentId, childId, view::showWelcomeMessage, view::showMessage);
    }

    public void onDailyCheckInClicked() {
        // TODO: Navigate to daily check in page
    }

    public void onLogControllerClicked() {
        // TODO: Navigate to log controller page
    }

    public void onLogRescueClicked() {
        // TODO: Navigate to log rescue page
    }

    public void onPEFEntryClicked() {
        // TODO: Navigate to PEF entry page
    }

    public void onTriageClicked() {
        // TODO: Navigate to triage page
    }

    public void onPracticeTechniqueClicked() {
        // TODO: Navigate to practice inhaler technique page
    }

    public void onGlossaryClicked() {
        // TODO: Navigate to glossary page
    }

    public void onUpdateInventoryClicked() {
        // TODO: Navigate to update inventory page
    }

    public void onViewBadgesClicked() {
        // TODO: Navigate to badges page
    }
}
