package com.SmartAir.onboarding.model;

import java.util.ArrayList;
import java.util.List;

public class ParentUser extends BaseUser {

    private List<String> childrenIds;

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
}
