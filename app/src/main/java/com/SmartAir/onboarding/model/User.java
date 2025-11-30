package com.SmartAir.onboarding.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String role;
    private String email;
    private String displayName;
    private String parentId; // For child users
    private List<String> childrenIds; // For parent users

    // Required empty public constructor for Firestore
    public User() {}

    public User(String role, String email, String displayName) {
        this.role = role;
        this.email = email;
        this.displayName = displayName;
        if ("parent".equalsIgnoreCase(role)) {
            this.childrenIds = new ArrayList<>();
        }
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }
}
