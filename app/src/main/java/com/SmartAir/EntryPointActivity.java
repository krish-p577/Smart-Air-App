package com.SmartAir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

// Corrected imports for Home Activities
import com.SmartAir.onboarding.view.ChildHomeActivity;
import com.SmartAir.onboarding.view.ParentHomeActivity;
import com.SmartAir.onboarding.view.ProviderHomeActivity;

import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.view.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EntryPointActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authStateListener;
    private AuthRepository authRepository;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        authRepository = AuthRepository.getInstance();

        authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        authRepository.fetchUserProfile(firebaseUser, new AuthRepository.AuthCallback() {
                            @Override
                            public void onSuccess() {
                                navigateToHome();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                navigateToWelcome();
                            }
                        });
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        navigateToWelcome();
                    }
                });
            } else {
                navigateToWelcome();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    private void navigateToHome() {
        progressBar.setVisibility(View.GONE);
        if (CurrentUser.getInstance().getUserProfile() == null) {
            navigateToWelcome();
            return;
        }
        String role = CurrentUser.getInstance().getUserProfile().getRole();
        Intent intent;

        if (role == null) {
            navigateToWelcome();
            return;
        }

        switch (role.toLowerCase()) {
            case "parent":
                intent = new Intent(this, ParentHomeActivity.class);
                break;
            case "provider":
                intent = new Intent(this, ProviderHomeActivity.class);
                break;
            case "child":
                intent = new Intent(this, ChildHomeActivity.class);
                break;
            default:
                intent = new Intent(this, WelcomeActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToWelcome() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
