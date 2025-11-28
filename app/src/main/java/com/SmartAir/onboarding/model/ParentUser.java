package com.SmartAir.onboarding.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParentUser extends BaseUser {

    private List<String> childrenIds;
    private Date lastLoginAt;

    // Required empty public constructor for Firestore
    public ParentUser() {
        super();
        this.childrenIds = new ArrayList<>();
    }

    public ParentUser(String email, String displayName) {
        super("parent", email, displayName);
        this.childrenIds = new ArrayList<>();
    }

    // Getters and Setters
    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
