package com.SmartAir.ChildDashboard.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

public class ChildDashboardRepository {

    private final FirebaseFirestore db;

    public ChildDashboardRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void getChildName(String parentId, String childId, Consumer<String> onSuccess, Consumer<String> onError) {
        db.collection("users").document(parentId).collection("children").document(childId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("name");
                    onSuccess.accept(name);
                } else {
                    onError.accept("Child not found.");
                }
        }).addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    public void getControllerInhalerStatus(String itemId, Consumer<String> onSuccess, Consumer<String> onError) {
        // TODO: Query database to check all controller inhalers. If there is at least 1 that is not expired or then status is good.
    }
}
