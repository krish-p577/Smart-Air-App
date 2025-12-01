package com.SmartAir.LogController.presenter;

import com.SmartAir.LogController.model.LogControllerRepository;
import com.SmartAir.LogController.view.LogControllerView;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LogControllerPresenter {

    private final LogControllerView view;
    private final LogControllerRepository repo;

    public LogControllerPresenter(LogControllerView view, LogControllerRepository repo) {
        this.view = view;
        this.repo = repo;
    }

    public void onBackClicked() {
        view.showChildDashboard();
    }

    public void onSubmitButtonClicked() {
        String puffsTakenText = view.getPuffsTakenText();
        int puffsTaken;


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

        Map<String, Object> fieldsToLog = new HashMap<>();
        fieldsToLog.put("puffsTaken", puffsTaken);
        fieldsToLog.put("timestamp", Timestamp.now());

        repo.logControllerInhalerUser(fieldsToLog)
            .continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            return repo.updateControllerStreak();
        }).addOnSuccessListener(v -> {
            view.clearPuffsTaken();
            view.showMessage("Controller inhaler logged successfully.");
        }).addOnFailureListener(e -> view.showMessage("Error: " + e.getMessage()));
    }
}
