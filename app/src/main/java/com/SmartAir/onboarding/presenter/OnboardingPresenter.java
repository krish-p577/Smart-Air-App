package com.SmartAir.onboarding.presenter;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.OnboardingStep;
import com.SmartAir.onboarding.view.OnboardingView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingPresenter {

    private final OnboardingView view;
    private final AuthRepository authRepository;

    public OnboardingPresenter(OnboardingView view) {
        this.view = view;
        this.authRepository = AuthRepository.getInstance();
    }

    public void onViewCreated() {
        List<OnboardingStep> steps = buildOnboardingSteps();
        view.displayOnboardingSteps(steps);
    }

    private List<OnboardingStep> buildOnboardingSteps() {
        List<OnboardingStep> steps = new ArrayList<>();
        String role = CurrentUser.getInstance().getRole();
        if (role == null) role = "parent"; // Default for safety

        steps.add(new OnboardingStep("Welcome to Smart Air!", "This app helps you understand and manage asthma by logging symptoms, practicing inhaler technique, and sharing progress."));

        switch (role.toLowerCase()) {
            case "parent":
                steps.add(new OnboardingStep("Privacy and Sharing", "By default, your child\'s data is private. You can choose to share specific information with a healthcare provider using a secure, one-time invite code."));
                steps.add(new OnboardingStep("Managing Your Child", "You can add multiple children, view their progress dashboards, set up their action plans, and receive important alerts."));
                break;
            case "provider":
                steps.add(new OnboardingStep("Read-Only Access", "You can only view information that a parent has explicitly shared with you. This access can be changed or revoked by the parent at any time."));
                break;
            case "child":
                steps.add(new OnboardingStep("Rescue vs. Controller", "A rescue inhaler is for when you feel symptoms now. A controller medicine is taken every day to help prevent symptoms."));
                break;
        }

        steps.add(new OnboardingStep("Glossary", "For more details on terms like \"PEF\" or \"Triggers\", you can visit the glossary in the side menu at any time."));

        return steps;
    }

    public void onFinished() {
        authRepository.markOnboardingAsCompleted();
        view.navigateToHome();
    }
}
