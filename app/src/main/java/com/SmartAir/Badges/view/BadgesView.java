package com.SmartAir.Badges.view;

public interface BadgesView {
    void showChildDashboard();

    void showBadges(boolean hasControllerBadge, boolean hasTechniqueBadge, boolean hasLowRescueBadge);

    void showMessage(String message);
}
