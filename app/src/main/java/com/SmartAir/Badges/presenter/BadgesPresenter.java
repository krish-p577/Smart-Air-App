package com.SmartAir.Badges.presenter;

import com.SmartAir.Badges.model.BadgesRepository;
import com.SmartAir.Badges.view.BadgesView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Date;
import java.util.Map;

public class BadgesPresenter {

    private final BadgesView view;
    private final BadgesRepository repo;

    public BadgesPresenter(BadgesView view, BadgesRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onScreenStart() {
        repo.updateBadges()
            .addOnSuccessListener(v -> {
                Task<Map<String, Date>> badgesTask = repo.getBadges();
                Task<Integer> controllerThresholdTask = repo.getMinControllerStreakThreshold();
                Task<Integer> techniqueThresholdTask = repo.getMinTechniqueSessionsThreshold();
                Task<Integer> rescueThresholdTask = repo.getMaxRescueDaysThreshold();

                Tasks.whenAllSuccess(badgesTask, controllerThresholdTask, techniqueThresholdTask, rescueThresholdTask)
                    .addOnSuccessListener(results -> {
                        Map<String, Date> badges = (Map<String, Date>) results.get(0);
                        int minControllerStreakThreshold = (Integer) results.get(1);
                        int minTechniqueSessionsThreshold = (Integer) results.get(2);
                        int maxRescueDaysThreshold = (Integer) results.get(3);

                        boolean hasControllerBadge = badges.containsKey("perfect_controller_week_badge");
                        boolean hasTechniqueBadge = badges.containsKey("high_quality_technique_sessions_badge");
                        boolean hasLowRescueBadge = badges.containsKey("low_rescue_badge");

                        view.showBadges(hasControllerBadge, hasTechniqueBadge, hasLowRescueBadge, badges, minControllerStreakThreshold, minTechniqueSessionsThreshold, maxRescueDaysThreshold);
                    }).addOnFailureListener(e -> view.showMessage("Error while loading badges: " + e.getMessage()));
            }).addOnFailureListener(e -> {
                Task<Map<String, Date>> badgesTask = repo.getBadges();
                Task<Integer> controllerThresholdTask = repo.getMinControllerStreakThreshold();
                Task<Integer> techniqueThresholdTask = repo.getMinTechniqueSessionsThreshold();
                Task<Integer> rescueThresholdTask = repo.getMaxRescueDaysThreshold();

                Tasks.whenAllSuccess(badgesTask, controllerThresholdTask, techniqueThresholdTask, rescueThresholdTask)
                    .addOnSuccessListener(results -> {
                        Map<String, Date> badges = (Map<String, Date>) results.get(0);
                        int minControllerStreakThreshold = (Integer) results.get(1);
                        int minTechniqueSessionsThreshold = (Integer) results.get(2);
                        int maxRescueDaysThreshold = (Integer) results.get(3);

                        boolean hasControllerBadge = badges.containsKey("perfect_controller_week_badge");
                        boolean hasTechniqueBadge = badges.containsKey("high_quality_technique_sessions_badge");
                        boolean hasLowRescueBadge = badges.containsKey("low_rescue_badge");

                        view.showBadges(hasControllerBadge, hasTechniqueBadge, hasLowRescueBadge, badges, minControllerStreakThreshold, minTechniqueSessionsThreshold, maxRescueDaysThreshold);
                    }).addOnFailureListener(err -> view.showMessage("Error while loading badges: " + err.getMessage()));
            });
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }
}
