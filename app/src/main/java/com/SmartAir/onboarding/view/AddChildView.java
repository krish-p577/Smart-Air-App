package com.SmartAir.onboarding.view;

import java.util.List;

public interface AddChildView {
    void showSuccessMessage(String message);
    void setAddChildError(String message);
    void setLoading(boolean isLoading);
    void updatePasswordRequirements(List<String> passedRules, List<String> failedRules);
}
