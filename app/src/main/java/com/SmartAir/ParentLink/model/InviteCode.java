package com.SmartAir.ParentLink.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InviteCode {
    private String code;
    private String childId;
    private String parentId;
    private @ServerTimestamp Date createdAt;
    private Date expiresAt;
    private boolean active;

    // Required for Firestore's toObject() method
    public InviteCode() {}

    public InviteCode(String code, String childId, String parentId, Date expiresAt) {
        this.code = code;
        this.childId = childId;
        this.parentId = parentId;
        this.expiresAt = expiresAt;
        this.active = true; // Codes are active by default
    }

    // Getters
    public String getCode() { return code; }
    public String getChildId() { return childId; }
    public String getParentId() { return parentId; }
    public Date getCreatedAt() { return createdAt; }
    public Date getExpiresAt() { return expiresAt; }
    public boolean isActive() { return active; }

    // Setters
    public void setCode(String code) { this.code = code; }
    public void setChildId(String childId) { this.childId = childId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
    public void setActive(boolean active) { this.active = active; }
}
