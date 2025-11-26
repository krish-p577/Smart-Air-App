package com.SmartAir.onboarding.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentUser {

    private static CurrentUser instance;
    private FirebaseUser firebaseUser;
    private User userProfile;

    private CurrentUser() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    public boolean isLoggedIn() {
        return firebaseUser != null && userProfile != null;
    }

    public String getRole() {
        if (isLoggedIn()) {
            return userProfile.getRole();
        }
        return null;
    }

    public String getUid() {
        if (isLoggedIn()) {
            return firebaseUser.getUid();
        }
        return null;
    }

    public void clear() {
        firebaseUser = null;
        userProfile = null;
        // logout from firebase
        FirebaseAuth.getInstance().signOut();
    }
}
