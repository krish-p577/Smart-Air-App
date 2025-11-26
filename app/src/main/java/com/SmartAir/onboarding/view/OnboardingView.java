package com.SmartAir.onboarding.view;

import com.SmartAir.onboarding.model.OnboardingStep;

import java.util.List;

public interface OnboardingView {
    void displayOnboardingSteps(List<OnboardingStep> steps);
    void navigateToHome();
    void navigateToWelcomeAndLogout();
}
