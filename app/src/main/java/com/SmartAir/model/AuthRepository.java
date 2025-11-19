package com.SmartAir.model;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
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

    public void signInUser(String email, String password, @NonNull final AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
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

    public void signInChild(String username, String password, @NonNull final AuthCallback callback) {
        firestore.collection("child_profiles")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            String dbPassword = snapshot.getDocuments().get(0).getString("password");
                            if (password.equals(dbPassword)) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure("Incorrect password.");
                            }
                        } else {
                            callback.onFailure("Username not found.");
                        }
                    } else {
                        callback.onFailure("An error occurred while trying to log in.");
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
