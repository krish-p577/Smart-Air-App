package com.SmartAir.onboarding.view;

public interface PasswordResetView {
    void showSuccessMessage(String message);
    void showErrorMessage(String message);
    void setLoading(boolean isLoading);
}
