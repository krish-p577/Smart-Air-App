package com.SmartAir.onboarding.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public abstract class BaseUser implements Serializable {

    private String uid; // Unique ID from Firebase Auth
    private String role;
    private String email;
    private String displayName;
    private boolean hasCompletedOnboarding = false;

    // Required empty public constructor for Firestore
    public BaseUser() {}

    public BaseUser(String role, String email, String displayName) {
        this.role = role;
        this.email = email;
        this.displayName = displayName;
    }

    @Exclude // Exclude from Firestore serialization, as the UID is the document ID.
    public String getUid() {
        return uid;
    }

    // Package-private setter allows only classes in the same package (e.g., AuthRepository) to set the ID.
    void setUid(String uid) {
        this.uid = uid;
    }

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
