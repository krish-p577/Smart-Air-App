package com.SmartAir.onboarding.model;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private static volatile AuthRepository instance;

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final CurrentUser currentUser;

    // Private constructor to enforce Singleton pattern
    private AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = CurrentUser.getInstance();
    }

    public static AuthRepository getInstance() {
        if (instance == null) {
            synchronized (AuthRepository.class) {
                if (instance == null) {
                    instance = new AuthRepository();
                }
            }
        }
        return instance;
    }

    public void createUser(String email, String password, String role, String displayName, @NonNull final AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser newUser = task.getResult().getUser();
                        if (newUser != null) {
                            User userProfile = new User(role, email, displayName);
                            firestore.collection("Users").document(newUser.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        currentUser.setFirebaseUser(newUser);
                                        currentUser.setUserProfile(userProfile);
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(e -> callback.onFailure("Failed to save user data: " + e.getMessage()));
                        } else {
                            callback.onFailure("Failed to create user.");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "An unknown signup error occurred.");
                    }
                });
    }

    public void signInUser(String email, String password, @NonNull final AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser signedInUser = task.getResult().getUser();
                        if (signedInUser != null) {
                            fetchUserProfile(signedInUser, callback);
                        } else {
                            callback.onFailure("Login failed.");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "An unknown login error occurred.");
                    }
                });
    }

    public void signInChild(String username, String password, @NonNull final AuthCallback callback) {
        firestore.collection("Users")
                .whereEqualTo("displayName", username)
                .whereEqualTo("role", "child")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        String email = userDoc.getString("email");
                        if (email != null && !email.isEmpty()) {
                            signInUser(email, password, callback);
                        } else {
                            callback.onFailure("Child account is not configured correctly for login.");
                        }
                    } else {
                        callback.onFailure("Username not found.");
                    }
                });
    }

    public void sendPasswordResetEmail(String emailOrUsername, @NonNull final AuthCallback callback) {
        if (emailOrUsername.contains("@")) {
            firebaseAuth.sendPasswordResetEmail(emailOrUsername)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to send reset email.");
                        }
                    });
        } else {
            firestore.collection("Users")
                    .whereEqualTo("displayName", emailOrUsername)
                    .whereEqualTo("role", "child")
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot childUserDoc = task.getResult().getDocuments().get(0);
                            String childId = childUserDoc.getId();
                            firestore.collectionGroup("children").whereEqualTo(FieldPath.documentId(), childId)
                                .limit(1)
                                .get()
                                .addOnCompleteListener(parentSearchTask -> {
                                    if (parentSearchTask.isSuccessful() && parentSearchTask.getResult() != null && !parentSearchTask.getResult().isEmpty()) {
                                        DocumentSnapshot childSubcollectionDoc = parentSearchTask.getResult().getDocuments().get(0);
                                        DocumentReference parentRef = childSubcollectionDoc.getReference().getParent().getParent();
                                        if (parentRef != null) {
                                            parentRef.get().addOnSuccessListener(parentDoc -> {
                                                String parentEmail = parentDoc.getString("email");
                                                if (parentEmail != null && !parentEmail.isEmpty()) {
                                                    firebaseAuth.sendPasswordResetEmail(parentEmail)
                                                        .addOnCompleteListener(resetTask -> {
                                                            if (resetTask.isSuccessful()) {
                                                                callback.onSuccess();
                                                            } else {
                                                                callback.onFailure(resetTask.getException() != null ? resetTask.getException().getMessage() : "Failed to send reset email.");
                                                            }
                                                        });
                                                } else {
                                                    callback.onFailure("Parent account does not have an email address.");
                                                }
                                            }).addOnFailureListener(e -> callback.onFailure("Could not find the parent account."));
                                        } else {
                                             callback.onFailure("Could not determine the parent account.");
                                        }
                                    } else {
                                        callback.onFailure("Child is not linked to any parent account.");
                                    }
                                });
                        } else {
                            callback.onFailure("Username not found.");
                        }
                    });
        }
    }

    public void logout() {
        firebaseAuth.signOut();
        currentUser.clear();
    }

    private void fetchUserProfile(FirebaseUser firebaseUser, @NonNull final AuthCallback callback) {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User userProfile = task.getResult().toObject(User.class);
                        if (userProfile != null) {
                            currentUser.setFirebaseUser(firebaseUser);
                            currentUser.setUserProfile(userProfile);
                            updateLastLogin(firebaseUser.getUid());
                            callback.onSuccess();
                        } else {
                            callback.onFailure("User data not found in database.");
                        }
                    } else {
                        callback.onFailure("Failed to fetch user profile.");
                    }
                });
    }

    private void updateLastLogin(String uid) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastLoginAt", Timestamp.now());
        firestore.collection("Users").document(uid).update(updates);
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
