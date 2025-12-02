package com.SmartAir.Badges.view;

import java.util.Date;
import java.util.Map;

public interface BadgesView {
    void showChildDashboard();

    void showBadges(boolean hasControllerBadge, boolean hasTechniqueBadge, boolean hasLowRescueBadge, Map<String, Date> badges, int controllerThreshold, int techniqueThreshold, int maxRescueDaysThreshold);

    void showMessage(String message);
}
