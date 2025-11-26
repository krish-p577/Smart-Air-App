package com.SmartAir.onboarding.model;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private static volatile AuthRepository instance;

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final CurrentUser currentUser;

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

    public void markOnboardingAsCompleted() {
        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null) {
            firestore.collection("Users").document(user.getUid())
                .update("hasCompletedOnboarding", true);
        }
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void createChildUser(String username, String password, @NonNull final AuthCallback callback) {
        FirebaseUser parentFirebaseUser = getCurrentFirebaseUser();
        if (parentFirebaseUser == null) {
            callback.onFailure("You must be logged in as a parent to add a child.");
            return;
        }

        String parentUid = parentFirebaseUser.getUid();
        String fakeEmail = username.toLowerCase() + "@" + parentUid.substring(0, 8) + ".smartair.user";

        firebaseAuth.createUserWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        FirebaseUser newChildUser = task.getResult().getUser();
                        String childUid = newChildUser.getUid();

                        ChildUser childProfile = new ChildUser(fakeEmail, username, parentUid);

                        WriteBatch batch = firestore.batch();

                        DocumentReference childDocRef = firestore.collection("Users").document(childUid);
                        batch.set(childDocRef, childProfile);

                        DocumentReference parentDocRef = firestore.collection("Users").document(parentUid);
                        batch.update(parentDocRef, "childrenIds", FieldValue.arrayUnion(childUid));

                        batch.commit().addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Failed to save user data: " + e.getMessage()));

                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            callback.onFailure("This username might be taken. Please try another.");
                        } else if (exception != null) {
                            callback.onFailure(exception.getMessage());
                        } else {
                            callback.onFailure("An unknown error occurred.");
                        }
                    }
                });
    }

    public void createUser(String email, String password, String role, String displayName, @NonNull final AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser newUser = task.getResult().getUser();
                        if (newUser != null) {
                            BaseUser userProfile;
                            if ("parent".equalsIgnoreCase(role)) {
                                userProfile = new ParentUser(email, displayName);
                            } else {
                                userProfile = new ProviderUser(email, displayName);
                            }

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
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            callback.onFailure("This email is already in use. Please try logging in.");
                        } else if (exception != null) {
                            callback.onFailure(exception.getMessage());
                        } else {
                            callback.onFailure("An unknown signup error occurred.");
                        }
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
                        callback.onFailure("Invalid email or password.");
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
                        ChildUser childUser = task.getResult().getDocuments().get(0).toObject(ChildUser.class);
                        if (childUser != null && childUser.getEmail() != null && !childUser.getEmail().isEmpty()) {
                            signInUser(childUser.getEmail(), password, callback);
                        } else {
                            callback.onFailure("Child account is not configured correctly for login.");
                        }
                    } else {
                        callback.onFailure("Username not found.");
                    }
                });
    }

    public void sendPasswordResetEmail(String email, @NonNull final AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to send reset email.");
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
        currentUser.clear();
    }

    public void fetchUserProfile(FirebaseUser firebaseUser, @NonNull final AuthCallback callback) {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        String role = doc.getString("role");
                        BaseUser userProfile = null;

                        if (role != null) {
                            if ("parent".equalsIgnoreCase(role)) {
                                userProfile = doc.toObject(ParentUser.class);
                            } else if ("child".equalsIgnoreCase(role)) {
                                userProfile = doc.toObject(ChildUser.class);
                            } else if ("provider".equalsIgnoreCase(role)) {
                                userProfile = doc.toObject(ProviderUser.class);
                            }
                        }

                        if (userProfile != null) {
                            currentUser.setFirebaseUser(firebaseUser);
                            currentUser.setUserProfile(userProfile);
                            updateLastLogin(firebaseUser.getUid());
                            callback.onSuccess();
                        } else {
                            callback.onFailure("User data not found or role is missing.");
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
