package com.SmartAir.LogRescue.view;

public interface LogRescueView {
    String getSelectedPreFeeling();

    String getPuffsTakenText();

    String getSelectedPostFeeling();

    void clearPreFeeling();

    void clearPuffsTaken();

    void clearPostFeeling();

    void showChildDashboard();

    void showMessage(String message);
}
