package com.SmartAir.LogRescue.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class LogRescueRepository {
    private final FirebaseFirestore db;

    private final CurrentUser user;

    public LogRescueRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public Task<Void> logRescueInhalerUse(Map<String, Object> fieldsToLog) {
        String childId = user.getUid();

        return db.collection("inhalerLogs")
            .document(childId)
            .collection("rescueEntries")
            .add(fieldsToLog)
            .continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                return Tasks.forResult(null);
            });
    }
}
