package com.SmartAir.Badges.presenter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;

import com.SmartAir.Badges.model.BadgesRepository;
import com.SmartAir.Badges.view.BadgesActivity;
import com.SmartAir.Badges.view.BadgesView;
import com.SmartAir.ChildDashboard.data.ChildDashboardRepository;
import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;

import java.util.Date;
import java.util.List;

public class BadgesPresenter {

    private final BadgesView view;
    private final BadgesRepository repo;

    public BadgesPresenter(BadgesView view, BadgesRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onScreenStart() {
        repo.getBadges()
            .addOnSuccessListener(badges -> {
                boolean hasControllerBadge = badges.contains("perfect_controller_week_badge");
                boolean hasTechniqueBadge = badges.contains("high_quality_technique_sessions_badge");
                boolean hasLowRescueBadge = badges.contains("low_rescue_badge");

                view.showBadges(hasControllerBadge, hasTechniqueBadge, hasLowRescueBadge);
            }).addOnFailureListener(e -> {
                view.showMessage("Error while loading badges: " + e.getMessage());
            });
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }
}
