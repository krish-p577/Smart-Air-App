package com.SmartAir.onboarding.model;

import java.io.Serializable;

public abstract class BaseUser implements Serializable {

    private String role;
    private String email;
    private String displayName;
    private boolean hasCompletedOnboarding = false; // New field

    // Required empty public constructor for Firestore
    public BaseUser() {}

    public BaseUser(String role, String email, String displayName) {
        this.role = role;
        this.email = email;
        this.displayName = displayName;
    }

    // Getters and Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isHasCompletedOnboarding() {
        return hasCompletedOnboarding;
    }

    public void setHasCompletedOnboarding(boolean hasCompletedOnboarding) {
        this.hasCompletedOnboarding = hasCompletedOnboarding;
    }
}
