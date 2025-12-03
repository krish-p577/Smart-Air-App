package com.SmartAir.LogController.view;

public interface LogControllerView {
    void showChildDashboard();

    String getPuffsTakenText();

    void clearPuffsTaken();

    void showMessage(String message);
}
