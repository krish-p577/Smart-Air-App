package com.SmartAir.onboarding.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class User {

    private String role;
    private String email;
    private String displayName;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;

    // Required empty constructor for Firestore deserialization
    public User() {}

    public User(String role, String email, String displayName) {
        this.role = role;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = Timestamp.now(); // Set creation timestamp on new user object
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

    @ServerTimestamp
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @ServerTimestamp
    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
