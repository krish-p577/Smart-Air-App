package com.SmartAir.onboarding.model;

public class ChildUser extends BaseUser {

    private String parentId;
    private Integer personalBestPEF; // Personal Best Peak Expiratory Flow

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

    public Integer getPersonalBestPEF() {
        return personalBestPEF;
    }

    public void setPersonalBestPEF(Integer personalBestPEF) {
        this.personalBestPEF = personalBestPEF;
    }
}
