package com.SmartAir;

import android.app.Application;
import android.util.Log;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize Firebase App Check with the debug provider.
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        // Initialize singleton instances
        AuthRepository authRepository = AuthRepository.getInstance();
        CurrentUser.getInstance();

        // Pre-fetch current user profile for faster startup
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Log.d("MyApplication", "Found logged-in user: " + firebaseUser.getEmail());
            authRepository.fetchUserProfile(firebaseUser, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    Log.d("MyApplication", "User profile cached successfully.");
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.w("MyApplication", "Failed to pre-fetch user profile: " + errorMessage);
                }
            });
        }
    }
}
