package com.SmartAir.ChildDashboard.view;

import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface ChildDashboardView {
    boolean onNavigationItemSelected(@NonNull MenuItem item);

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
