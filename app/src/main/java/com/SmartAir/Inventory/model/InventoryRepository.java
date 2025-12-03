package com.SmartAir.Inventory.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class InventoryRepository {
    private final FirebaseFirestore db;
    private final CurrentUser user;

    public InventoryRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public Task<Void> updateInventory(String inhalerType, Map<String, Object> fieldsToUpdate) {
        String childId = user.getUid();

        return db.collection("inventory")
            .document(childId)
            .collection("inhalers")
            .document(inhalerType)
            .update(fieldsToUpdate);
    }

    public Task<Integer> getRemainingAmount(String inhalerType) {
        String childId = user.getUid();

        return db.collection("inventory")
            .document(childId)
            .collection("inhalers")
            .document(inhalerType)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long remainingAmount = doc.getLong("remainingAmount");
                    if (remainingAmount != null) {
                        return remainingAmount.intValue();
                    }
                }

                throw new Exception("Failed to retrieve remaining amount.");
            });
    }

    public Task<Integer> getMaxAmount(String inhalerType) {
        String childId = user.getUid();

        return db.collection("inventory")
            .document(childId)
            .collection("inhalers")
            .document(inhalerType)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long maxAmount = doc.getLong("maxAmount");
                    if (maxAmount != null) {
                        return maxAmount.intValue();
                    }
                }

                throw new Exception("Failed to retrieve max amount.");
            });
    }

    public Task<Date> getExpirationDate(String inhalerType) {
        String childId = user.getUid();

        return db.collection("inventory")
            .document(childId)
            .collection("inhalers")
            .document(inhalerType)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Timestamp expirationDateTimestamp = doc.getTimestamp("expirationDate");

                    if (expirationDateTimestamp == null) {
                        throw new Exception("Expiration date not found.");
                    }

                    return expirationDateTimestamp.toDate();
                } else {
                    throw new Exception("Inhaler not found in inventory.");
                }
            });
    }
}
