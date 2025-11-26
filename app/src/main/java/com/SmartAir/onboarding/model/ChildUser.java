package com.SmartAir.onboarding.model;

public class ChildUser extends BaseUser {

    private String parentId;

    // Required empty public constructor for Firestore
    public ChildUser() {
        super();
    }

    public ChildUser(String email, String displayName, String parentId) {
        super("child", email, displayName);
        this.parentId = parentId;
    }

    // Getters and Setters
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
