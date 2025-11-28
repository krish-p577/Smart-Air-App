package com.SmartAir.ChildDashboard.presenter;

import android.content.Intent;

import com.SmartAir.Badges.view.BadgesActivity;
import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.ChildDashboard.view.ChildDashboardView;
import com.SmartAir.ChildDashboard.data.ChildDashboardRepository;
import com.google.android.gms.tasks.Tasks;

public class ChildDashboardPresenter {

    private final ChildDashboardView view;
    private final ChildDashboardRepository repo;

    public ChildDashboardPresenter(ChildDashboardView view, ChildDashboardRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onScreenStart() {
        if (!repo.isChild()) {
            view.showMessage("Error: Child account not found.");
            return;
        }

        repo.getChildName()
            .addOnSuccessListener(childName -> {
                view.showWelcomeMessage(childName);
                view.showSecondaryMessage("Welcome back!");

                repo.getControllerStreak()
                    .addOnSuccessListener(controllerStreak -> {
                        view.showControllerStreak(controllerStreak);

                        repo.getTechniqueStreak()
                            .addOnSuccessListener(techniqueStreak -> {
                                view.showTechniqueStreak(techniqueStreak);

                            }).addOnFailureListener(e -> view.showMessage("Error while getting technique streak: " + e.getMessage()));
                    }).addOnFailureListener(e -> view.showMessage("Error while getting controller streak: " + e.getMessage()));
            }).addOnFailureListener(e -> view.showMessage("Error while getting child name: " + e.getMessage()));
    }

    public void onDailyCheckInClicked() {
        view.showDailyCheckIn();
    }

    public void onLogControllerClicked() {
        view.showLogController();
    }

    public void onLogRescueClicked() {
        view.showLogRescue();
    }

    public void onPEFEntryClicked() {
        view.showPEFEntry();
    }

    public void onTriageClicked() {
        view.showTriage();
    }

    public void onPracticeTechniqueClicked() {
        view.showPracticeTechnique();
    }

    public void onGlossaryClicked() {
        view.showGlossary();
    }

    public void onUpdateInventoryClicked() {
        view.showUpdateInventory();
    }

    public void onViewBadgesClicked() {
        view.showBadges();
    }
}
