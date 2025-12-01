package com.SmartAir.LogRescue.presenter;

import com.SmartAir.LogRescue.model.LogRescueRepository;
import com.SmartAir.LogRescue.view.LogRescueView;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LogRescuePresenter {

    private final LogRescueView view;
    private final LogRescueRepository repo;

    public LogRescuePresenter(LogRescueView view, LogRescueRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }

    public void onSubmitButtonClicked() {
        String preFeeling = view.getSelectedPreFeeling();
        String puffsTakenText = view.getPuffsTakenText();
        int puffsTaken;
        String postFeeling = view.getSelectedPostFeeling();

        if (preFeeling == null || preFeeling.trim().isEmpty()) {
            view.showMessage("Please select how your breathing feels before using the inhaler.");
            return;
        }

        if (postFeeling == null || postFeeling.trim().isEmpty()) {
            view.showMessage("Please select how your breathing feels after using the inhaler.");
            return;
        }

        if (puffsTakenText == null || puffsTakenText.trim().isEmpty()) {
            view.showMessage("Please enter the number of puffs taken.");
            return;
        }

        try {
            puffsTaken = Integer.parseInt(puffsTakenText);
        } catch (NumberFormatException e) {
            view.showMessage("Please enter a valid number.");
            return;
        }

        if (puffsTaken < 0) {
            view.showMessage("Number of puffs taken cannot be negative.");
            return;
        }

        Map<String, Object> fieldsToLog = new HashMap<>();
        fieldsToLog.put("preFeeling", preFeeling);
        fieldsToLog.put("puffsTaken", puffsTaken);
        fieldsToLog.put("postFeeling", postFeeling);
        fieldsToLog.put("timestamp", Timestamp.now());

        repo.logRescueInhalerUse(fieldsToLog)
            .addOnSuccessListener(v -> {
                view.clearPreFeeling();
                view.clearPuffsTaken();
                view.clearPostFeeling();
                view.showMessage("Rescue inhaler logged successfully.");
            }).addOnFailureListener(e -> view.showMessage("Error: " + e.getMessage()));
    }
}
