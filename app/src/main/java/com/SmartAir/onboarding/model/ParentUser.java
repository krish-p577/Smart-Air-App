package com.SmartAir.onboarding.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParentUser extends BaseUser {

    // IDs persisted in Firestore
    private List<String> childrenIds;

    // Actual ChildUser objects stored in memory (not persisted)
    private transient List<ChildUser> children;

    private Date lastLogin;

    // Required empty public constructor for Firestore
    public ParentUser() {
        super();
        this.childrenIds = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public ParentUser(String email, String displayName) {
        super("parent", email, displayName);
        this.childrenIds = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    // Firestore persisted children IDs
    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    // In-memory ChildUser objects
    public List<ChildUser> getChildren() {
        return children;
    }

    public void setChildren(List<ChildUser> children) {
        this.children = children;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
