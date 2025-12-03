package com.SmartAir.onboarding.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildUser extends BaseUser {

    private String parentId;
    private String username;
    private String age;
    private String dateOfBirth;
    private String notes;
    private Map<String, Object> personalBestPEF; // Personal Best Peak Expiratory Flow
    private Map<String, Boolean> sharingSettings;
    private List<String> linkedProviders;
    private Date lastLogin;
    private Object schedule;

    // Required empty public constructor for Firestore
    public ChildUser() {
        super();
        this.sharingSettings = new HashMap<>();
    }

    public ChildUser(String email, String displayName, String parentId, String username) {
        super("child", email, displayName);
        this.parentId = parentId;
        this.username = username;
        this.sharingSettings = new HashMap<>();
    }

    // Getters and Setters
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, Object> getPersonalBestPEF() {
        return personalBestPEF;
    }

    public void setPersonalBestPEF(HashMap<String, Object> personalBestPEF) {
        this.personalBestPEF = personalBestPEF;
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

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
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

    public void setScheduleAsMap(Map<String, Object> schedule) {
        this.schedule = schedule;
    }

    public void setSchedule(Object schedule) {
        this.schedule = schedule;
    }
}
