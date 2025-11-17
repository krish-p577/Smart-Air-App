package com.SmartAir.model;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void createUser(String email, String password, @NonNull final AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String errorMessage = "An unknown error occurred.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        callback.onFailure(errorMessage);
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
