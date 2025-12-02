package com.SmartAir.onboarding.model;

import java.util.ArrayList;
import java.util.List;

public class ProviderUser extends BaseUser {

    private List<String> sharedChildIds;

    // Required empty public constructor for Firestore
    public ProviderUser() {
        super();
        this.sharedChildIds = new ArrayList<>();
    }

    public ProviderUser(String email, String displayName) {
        super("provider", email, displayName);
        this.sharedChildIds = new ArrayList<>();
    }

    public List<String> getSharedChildIds() {
        return sharedChildIds;
    }

    public void setSharedChildIds(List<String> sharedChildIds) {
        this.sharedChildIds = sharedChildIds;
    }
}
