package com.SmartAir.Badges.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BadgesRepository {

    private final FirebaseFirestore db;

    private final CurrentUser user;

    private static final int DEFAULT_MIN_CONTROLLER_STREAK_THRESHOLD = 7;
    private static final int DEFAULT_MIN_TECHNIQUE_SESSIONS_THRESHOLD = 10;
    private static final int DEFAULT_MAX_RESCUE_DAYS_THRESHOLD = 4;

    public BadgesRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public Task<Integer> getMinControllerStreakThreshold() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long minControllerStreakThreshold = doc.getLong("minControllerStreakThreshold");
                    if (minControllerStreakThreshold != null) {
                        return minControllerStreakThreshold.intValue();
                    }
                }

                return DEFAULT_MIN_CONTROLLER_STREAK_THRESHOLD;
            });
    }

    public Task<Integer> getMinTechniqueSessionsThreshold() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long minTechniqueSessionsThreshold = doc.getLong("minTechniqueSessionsThreshold");
                    if (minTechniqueSessionsThreshold != null) {
                        return minTechniqueSessionsThreshold.intValue();
                    }
                }

                return DEFAULT_MIN_TECHNIQUE_SESSIONS_THRESHOLD;
            });
    }

    public Task<Integer> getMaxRescueDaysThreshold() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long maxRescueDaysThreshold = doc.getLong("maxRescueDaysThreshold");
                    if (maxRescueDaysThreshold != null) {
                        return maxRescueDaysThreshold.intValue();
                    }

                }

                return DEFAULT_MAX_RESCUE_DAYS_THRESHOLD;
            });
    }

    public void updateBadges() {
        // TODO: update badges awarded based on badge thresholds
    }

    public Task<List<String>> getBadges() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    List<String> badges = (List<String>) doc.get("badges");

                    if (badges == null) {
                        throw new Exception("Badges not found.");
                    }

                    return badges;
                } else {
                    throw new Exception("Badges not found.");
                }
            });
    }

    public Task<List<Date>> getBadgeEarnedDates() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    List<Timestamp> badgesEarnedAt = (List<Timestamp>) doc.get("earnedAt");

                    if (badgesEarnedAt == null) {
                        throw new Exception("Badge earned dates not found.");
                    }

                    List<Date> badgesEarnedAtDates = new ArrayList<>(badgesEarnedAt.size());

                    for (int i = 0; i < badgesEarnedAt.size(); i++) {
                        badgesEarnedAtDates.add(badgesEarnedAt.get(i).toDate());
                    }

                    return badgesEarnedAtDates;
                } else {
                    throw new Exception("Badges not found.");
                }
            });
    }
}
