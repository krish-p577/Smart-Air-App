package com.SmartAir.onboarding.view;

import java.util.List;

public interface SignupView {
    void setSignupError(String message);
    void navigateToHome();
    void navigateToLogin();
    void setLoading(boolean isLoading);
    void updatePasswordRequirements(List<String> unmetRequirements, List<String> metRequirements);
}
