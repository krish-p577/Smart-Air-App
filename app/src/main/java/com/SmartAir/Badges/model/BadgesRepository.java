package com.SmartAir.Badges.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    public Task<Integer> getControllerStreak() {
        String childId = user.getUid();

        return db.collection("streaks")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long controllerStreak = doc.getLong("controllerStreak");

                    if (controllerStreak != null) {
                        return controllerStreak.intValue();
                    }
                }

                return 0;
            });
    }

    public Task<Integer> getPerfectTechniqueSessions() {
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
                    Long perfectTechniqueSessions = doc.getLong("perfectTechniqueSessions");

                    if (perfectTechniqueSessions != null) {
                        return perfectTechniqueSessions.intValue();
                    }
                }

                return 0;
            });
    }

    public Task<Integer> getRescueDaysWithinMonth() {
        String childId = user.getUid();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date dateMonthAgo = calendar.getTime();
        Timestamp timeCutoff = new Timestamp(dateMonthAgo);

        return db.collection("inhalerLogs")
            .document(childId)
            .collection("rescueEntries")
            .whereGreaterThan("timestamp", timeCutoff)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                Set<String> rescueDays = new HashSet<>();

                task.getResult().forEach(doc -> {
                    Timestamp rescueEntryTime = doc.getTimestamp("timestamp");

                    if (rescueEntryTime != null) {
                        Date date = rescueEntryTime.toDate();

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);

                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        String rescueEntryDate = year + "-" + month + "-" + day;
                        rescueDays.add(rescueEntryDate);
                    }
                });

                return rescueDays.size();
            });
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

    public Task<Void> updateBadges() {
        String childId = user.getUid();

        Task<Map<String, Date>> badgesTask = getBadges();
        Task<Integer> controllerThresholdTask = getMinControllerStreakThreshold();
        Task<Integer> techniqueThresholdTask = getMinTechniqueSessionsThreshold();
        Task<Integer> rescueThresholdTask = getMaxRescueDaysThreshold();
        Task<Integer> controllerStreakTask = getControllerStreak();
        Task<Integer> techniqueSessionsTask = getPerfectTechniqueSessions();
        Task<Integer> rescueDaysTask = getRescueDaysWithinMonth();

        return Tasks.whenAllSuccess(badgesTask, controllerThresholdTask, techniqueThresholdTask, rescueThresholdTask, controllerStreakTask, techniqueSessionsTask, rescueDaysTask)
            .onSuccessTask(results -> {
                Map<String, Date> currentBadges = (Map<String, Date>) results.get(0);
                int minControllerStreakThreshold = (Integer) results.get(1);
                int minTechniqueSessionsThreshold = (Integer) results.get(2);
                int maxRescueDaysThreshold = (Integer) results.get(3);
                int controllerStreak = (Integer) results.get(4);
                int perfectTechniqueSessions = (Integer) results.get(5);
                int rescueDaysWithinMonth = (Integer) results.get(6);

                Map<String, Object> newBadgesEarned = new HashMap<>();

                for (Map.Entry<String, Date> entry : currentBadges.entrySet()) {
                    Date date = entry.getValue();

                    if (date != null) {
                        newBadgesEarned.put(entry.getKey(), new Timestamp(date));
                    }
                }

                boolean badgesUpdated = false;

                if (controllerStreak >= minControllerStreakThreshold && !currentBadges.containsKey("perfect_controller_week_badge")) {
                    newBadgesEarned.put("perfect_controller_week_badge", Timestamp.now());
                    badgesUpdated = true;
                }

                if (perfectTechniqueSessions >= minTechniqueSessionsThreshold && !currentBadges.containsKey("high_quality_technique_sessions_badge")) {
                    newBadgesEarned.put("high_quality_technique_sessions_badge", Timestamp.now());
                    badgesUpdated = true;
                }

                if (rescueDaysWithinMonth < maxRescueDaysThreshold && !currentBadges.containsKey("low_rescue_badge")) {
                    newBadgesEarned.put("low_rescue_badge", Timestamp.now());
                    badgesUpdated = true;
                }

                if (!badgesUpdated) {
                    return Tasks.forResult(null);
                }

                Map<String, Object> updatedBadges = new HashMap<>();
                updatedBadges.put("badges", newBadgesEarned);

                return db.collection("badges")
                    .document(childId)
                    .set(updatedBadges, SetOptions.merge());
            });
    }

    public Task<Map<String, Date>> getBadges() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (!doc.exists()) {
                    return new HashMap<>();
                }

                Object raw = doc.get("badges");

                if (!(raw instanceof Map)) {
                    return new HashMap<>();
                }

                Map<String, Timestamp> badges = (Map<String, Timestamp>) raw;
                Map<String, Date> result = new HashMap<>();

                if (badges != null) {
                    for (Map.Entry<String, Timestamp> entry : badges.entrySet()) {
                        Timestamp time = entry.getValue();

                        if (time != null) {
                            result.put(entry.getKey(), time.toDate());
                        }
                    }
                }

                return result;
            });
    }
}
