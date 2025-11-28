package com.SmartAir.onboarding.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildUser extends BaseUser {

    private String parentId;
    private Integer personalBestPEF; // Personal Best Peak Expiratory Flow
    private Date dateOfBirth;
    private String notes;
    private Map<String, Boolean> sharingSettings;
    private List<String> linkedProviders;
    private Date lastLoginAt;
    private Object schedule;

    // Required empty public constructor for Firestore
    public ChildUser() {
        super();
        this.sharingSettings = new HashMap<>();
    }

    public ChildUser(String email, String displayName, String parentId) {
        super("child", email, displayName);
        this.parentId = parentId;
        this.sharingSettings = new HashMap<>();
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, Boolean> getSharingSettings() {
        return sharingSettings;
    }

    public void setSharingSettings(Map<String, Boolean> sharingSettings) {
        this.sharingSettings = sharingSettings;
    }

    public List<String> getLinkedProviders() {
        return linkedProviders;
    }

    public void setLinkedProviders(List<String> linkedProviders) {
        this.linkedProviders = linkedProviders;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Object getSchedule() {
        return schedule;
    }

    public Map<String, Object> getScheduleAsMap() {
        if (schedule instanceof Map) {
            return (Map<String, Object>) schedule;
        }
        return new HashMap<>(); // Return an empty map if it's not a map
    }


    public void setSchedule(Object schedule) {
        this.schedule = schedule;
    }
}
