package com.SmartAir.ChildDashboard.view;

public interface ChildDashboardView {
    void showWelcomeMessage(String name);

    void showSecondaryMessage(String secondaryMessage);

    void showControllerStreak(int numDays);

    void showTechniqueStreak(int numDays);

    void showMessage(String message);

    void showDailyCheckIn();

    void showLogController();

    void showLogRescue();

    void showPEFEntry();

    void showTriage();

    void showPracticeTechnique();

    void showGlossary();

    void showUpdateInventory();

    void showBadges();
}
