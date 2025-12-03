package com.SmartAir.ParentDashboard.presenter;

import com.SmartAir.ParentDashboard.view.PbView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PbPresenter {
    private final PbView view;
    private final FirebaseFirestore firestore;

    public PbPresenter(PbView view) {
        this.view = view;
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void onPBClicked() {
        view.popOut();
    }

    public void submitPersonalBest(String childId, String pbValue) {
        if (childId == null || childId.isEmpty() || pbValue == null || pbValue.isEmpty()) {
            view.showToast("Invalid input");
            return;
        }

        try {
            int pbNumber = Integer.parseInt(pbValue);
            Map<String, Object> updates = new HashMap<>();
            updates.put("personalBestPEF", pbNumber);
            updates.put("zones", getZoneMap(pbNumber));
            updates.put("personalBestPEFUpdatedAt", FieldValue.serverTimestamp());

            firestore.collection("Users").document(childId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> view.showToast("Personal Best updated successfully"))
                    .addOnFailureListener(e -> view.showToast("Failed to update Personal Best"));
        } catch (NumberFormatException e) {
            view.showToast("Invalid number format");
        }
    }

    private Map<String, Integer> getZoneMap(int pbNumber) {
        Map<String, Integer> map = new HashMap<>();
        map.put("Green", (int) (0.8 * pbNumber));
        map.put("Yellow", (int) (0.51 * pbNumber));
        map.put("Red", (int) (0.5 * pbNumber));
        return map;
    }
}
