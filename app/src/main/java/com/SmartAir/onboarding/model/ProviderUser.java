package com.SmartAir.onboarding.model;

public class ProviderUser extends BaseUser {

    // Required empty public constructor for Firestore
    public ProviderUser() {
        super();
    }

    public ProviderUser(String email, String displayName) {
        super("provider", email, displayName);
    }
}
