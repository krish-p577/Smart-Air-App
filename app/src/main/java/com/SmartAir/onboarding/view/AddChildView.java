package com.SmartAir.onboarding.view;

public interface AddChildView {
    void showSuccessMessage(String message);
    void setAddChildError(String message);
    void setLoading(boolean isLoading);
}
