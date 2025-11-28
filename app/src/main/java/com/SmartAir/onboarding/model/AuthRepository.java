package com.SmartAir.onboarding.model;

import androidx.annotation.NonNull;

import com.SmartAir.ParentLink.model.InviteCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.Timestamp;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthRepository {

    private static volatile AuthRepository instance;

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final CurrentUser currentUser;

    // These must reflect the currently-known parent credentials in this app session.
    // They are set in signInUser(...) and can be explicitly stored by the caller via storeParentCredentials(...)
    private String parentEmail;
    private String parentPassword;

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

    // -------------------------------------------------------
    // Helper: allow presenter/activity to store parent creds
    // (call this after the parent successfully logs in).
    // -------------------------------------------------------
    public void storeParentCredentials(@NonNull String email, @NonNull String password) {
        this.parentEmail = email;
        this.parentPassword = password;
    }
// ------------------ CREDENTIAL PERSISTENCE ------------------

    private static final String PREFS = "auth_prefs";
    private static final String KEY_EMAIL = "parent_email";
    private static final String KEY_PASSWORD = "parent_password";

    // Call this after successful login
    public void persistParentCredentials(Context context, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .apply();
    }
    public void ensureParentCredentialsLoaded(Context context) {
        if (parentEmail == null || parentPassword == null) {
            loadPersistedParentCredentials(context);
        }
    }

    // Call this at app startup (e.g., Splash screen / MainActivity)
    public void loadPersistedParentCredentials(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        parentEmail = prefs.getString(KEY_EMAIL, null);
        parentPassword = prefs.getString(KEY_PASSWORD, null);
    }

    // ------------------ CHILD FETCH METHODS ------------------

    public ListenerRegistration listenForChildrenForParent(String parentId, @NonNull final ChildrenCallback callback) {
        return firestore.collection("Users")
                .whereEqualTo("parentId", parentId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        callback.onFailure("Listen failed: " + e.getMessage());
                        return;
                    }

                    if (snapshots != null) {
                        List<ChildUser> children = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            ChildUser child = document.toObject(ChildUser.class);
                            if (child != null) {
                                child.setUid(document.getId());
                                children.add(child);
                            }
                        }
                        callback.onSuccess(children);
                    }
                });
    }

    public void fetchChildrenForParent(String parentId, @NonNull final ChildrenCallback callback) {
        firestore.collection("Users")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<ChildUser> children = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            ChildUser child = document.toObject(ChildUser.class);
                            if (child != null) {
                                child.setUid(document.getId());
                                children.add(child);
                            }
                        }
                        callback.onSuccess(children);
                    } else {
                        callback.onFailure("Failed to fetch children.");
                    }
                });
    }

    public void fetchLinkedChildrenForProvider(String providerId, @NonNull final ChildrenCallback callback) {
        firestore.collection("Users")
                .whereArrayContains("linkedProviders", providerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<ChildUser> children = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            ChildUser child = document.toObject(ChildUser.class);
                            if (child != null) {
                                child.setUid(document.getId());
                                children.add(child);
                            }
                        }
                        callback.onSuccess(children);
                    } else {
                        callback.onFailure("Failed to fetch linked children.");
                    }
                });
    }

    public void fetchChildProfile(String childId, @NonNull final ChildProfileCallback callback) {
        firestore.collection("Users").document(childId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ChildUser child = task.getResult().toObject(ChildUser.class);
                        if (child != null) {
                            child.setUid(task.getResult().getId());
                            callback.onSuccess(child);
                        } else {
                            callback.onFailure("Child profile not found.");
                        }
                    } else {
                        callback.onFailure("Failed to fetch child profile.");
                    }
                });
    }

    public void fetchActiveInviteCodeForChild(String childId, @NonNull final InviteCodeCallback callback) {
        firestore.collection("InviteCodes")
                .whereEqualTo("childId", childId)
                .whereEqualTo("active", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        InviteCode inviteCode = doc.toObject(InviteCode.class);
                        if (inviteCode != null && inviteCode.getExpiresAt().after(new java.util.Date())) {
                            callback.onSuccess(inviteCode.getCode());
                        } else {
                            callback.onFailure("No active invite code found.");
                        }
                    } else {
                        callback.onFailure("No invite codes found for this child.");
                    }
                });
    }
    public void refreshCurrentParentChildren(@NonNull final ChildrenCallback callback) {
        BaseUser user = currentUser.getUserProfile();
        if (user == null || !"parent".equalsIgnoreCase(user.getRole())) {
            callback.onFailure("Current user is not a parent.");
            return;
        }

        fetchChildrenForParent(user.getUid(), new ChildrenCallback() {
            @Override
            public void onSuccess(List<ChildUser> children) {
                // Update currentUser's children list if you have one
                if (currentUser.getUserProfile() instanceof ParentUser) {
                    ((ParentUser) currentUser.getUserProfile()).setChildren(children);
                }
                callback.onSuccess(children);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }


    // ------------------ ONBOARDING ------------------

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

    // ------------------ CREATE USERS ------------------

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
                            userProfile.setUid(newUser.getUid());

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

    // ------------------ CREATE CHILD ------------------

    /**
     * Creates a child account. IMPORTANT:
     * - parentEmail/parentPassword must be available in memory (set via signInUser() or storeParentCredentials()).
     * - If parent credentials are not available, we fail fast to avoid corrupting the auth session.
     */
    public void createChildUser(String name, String age, String dob, String notes,
                                String username, String password, @NonNull final AuthCallback callback) {

        FirebaseUser parentFirebaseUser = getCurrentFirebaseUser();
        if (parentFirebaseUser == null) {
            callback.onFailure("You must be logged in as a parent to add a child.");
            return;
        }

        if (parentEmail == null || parentPassword == null) {
            callback.onFailure("Parent credentials are missing. Please sign in again.");
            return;
        }

        String parentUid = parentFirebaseUser.getUid();
        String fakeEmail = username.toLowerCase() + "@" + parentUid.substring(0, 8) + ".smartair.user";

        firebaseAuth.createUserWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        FirebaseUser newChildUser = task.getResult().getUser();
                        String childUid = newChildUser.getUid();

                        ChildUser childProfile = new ChildUser(fakeEmail, name, parentUid);
                        childProfile.setAge(age);
                        childProfile.setDateOfBirth(dob);
                        childProfile.setNotes(notes);
                        childProfile.setDisplayName(name);

                        WriteBatch batch = firestore.batch();

                        DocumentReference childDocRef = firestore.collection("Users").document(childUid);
                        batch.set(childDocRef, childProfile);

                        DocumentReference parentDocRef = firestore.collection("Users").document(parentUid);
                        batch.update(parentDocRef, "childrenIds", FieldValue.arrayUnion(childUid));

                        batch.commit().addOnSuccessListener(aVoid -> {
                            // REFRESH parent children list
                            refreshCurrentParentChildren(new ChildrenCallback() {
                                @Override
                                public void onSuccess(List<ChildUser> children) {
                                    // Re-auth parent
                                    reauthenticateParent(new AuthCallback() {
                                        @Override
                                        public void onSuccess() {
                                            callback.onSuccess(); // ready, UI can observe updated children list
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            callback.onFailure("Child created, but failed to restore parent session.");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    callback.onFailure("Child created, but failed to refresh children.");
                                }
                            });
                        }).addOnFailureListener(e -> callback.onFailure("Failed to save child data: " + e.getMessage()));
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            callback.onFailure("This username might be taken. Please try another.");
                        } else if (exception != null) {
                            callback.onFailure(exception.getMessage());
                        } else {
                            callback.onFailure("Unknown error creating child.");
                        }
                    }
                });
    }



    // ------------------ SIGN IN ------------------

    /**
     * Sign in as parent (or any generic user). This method captures parent credentials for later re-auth when creating children.
     * Callers should call storeParentCredentials(...) after a successful sign-in if they want to ensure credentials are preserved.
     */
    public void signInUser(String email, String password, @NonNull final AuthCallback callback) {
        // Save credentials in-memory for session use (required to re-auth after createChildUser)
        this.parentEmail = email;
        this.parentPassword = password;

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
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        ChildUser childUser = doc.toObject(ChildUser.class);
                        if (childUser != null && childUser.getEmail() != null && !childUser.getEmail().isEmpty()) {
                            childUser.setUid(doc.getId());
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
        parentEmail = null;
        parentPassword = null;
    }

    // ------------------ USER PROFILE ------------------

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
                            userProfile.setUid(doc.getId());
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

    // ------------------ REAUTH PARENT ------------------

    /**
     * Re-authenticates the parent using the in-memory stored credentials.
     * This is intended to be called immediately after creating a child account (which temporarily signs-in as the child).
     */
    public void reauthenticateParent(AuthCallback callback) {
        if (parentEmail != null && parentPassword != null) {
            firebaseAuth.signInWithEmailAndPassword(parentEmail, parentPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser signedInUser = task.getResult().getUser();
                            if (signedInUser != null) {
                                // Refresh the currentUser object and finish
                                fetchUserProfile(signedInUser, callback);
                            } else {
                                callback.onFailure("Re-authentication failed to get user.");
                            }
                        } else {
                            callback.onFailure("Failed to re-authenticate parent.");
                        }
                    });
        } else {
            callback.onFailure("Parent credentials not available for re-authentication.");
        }
    }

    // ------------------ INVITE CODES ------------------

    public void generateInviteCode(String childId, @NonNull final InviteCodeCallback callback) {
        FirebaseUser currentUser = getCurrentFirebaseUser();
        if (currentUser == null) {
            callback.onFailure("No authenticated user found.");
            return;
        }
        String parentId = currentUser.getUid();
        final String code = UUID.randomUUID().toString().substring(0, 8);

        firestore.collection("InviteCodes")
                .whereEqualTo("childId", childId)
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = firestore.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.update(document.getReference(), "active", false);
                        }

                        Map<String, Object> newCodeData = new HashMap<>();
                        newCodeData.put("code", code);
                        newCodeData.put("childId", childId);
                        newCodeData.put("parentId", parentId);
                        newCodeData.put("createdAt", FieldValue.serverTimestamp());
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 7);
                        newCodeData.put("expiresAt", calendar.getTime());
                        newCodeData.put("active", true);

                        DocumentReference newCodeRef = firestore.collection("InviteCodes").document(code);
                        batch.set(newCodeRef, newCodeData);

                        batch.commit().addOnSuccessListener(aVoid -> callback.onSuccess(code))
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to query existing codes.");
                    }
                });
    }

    public void revokeInviteCode(String code, @NonNull final AuthCallback callback) {
        firestore.collection("InviteCodes").document(code)
                .update("active", false)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void linkProviderToChild(String inviteCode, @NonNull final AuthCallback callback) {
        String providerId = getCurrentFirebaseUser().getUid();
        firestore.collection("InviteCodes").document(inviteCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        InviteCode code = task.getResult().toObject(InviteCode.class);
                        if (code != null && code.isActive() && code.getExpiresAt().after(new java.util.Date())) {
                            String childId = code.getChildId();

                            WriteBatch batch = firestore.batch();
                            DocumentReference childRef = firestore.collection("Users").document(childId);
                            batch.update(childRef, "linkedProviders", FieldValue.arrayUnion(providerId));

                            DocumentReference providerRef = firestore.collection("Users").document(providerId);
                            batch.update(providerRef, "linkedChildren", FieldValue.arrayUnion(childId));

                            DocumentReference codeRef = firestore.collection("InviteCodes").document(inviteCode);
                            batch.update(codeRef, "active", false);

                            batch.commit()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        } else {
                            callback.onFailure("Invalid or expired invite code.");
                        }
                    } else {
                        callback.onFailure("Invite code not found.");
                    }
                });
    }

    // ------------------ CHILD SETTINGS ------------------

    public void updateSharingSettings(String childId, Map<String, Boolean> sharingSettings, @NonNull final AuthCallback callback) {
        firestore.collection("Users").document(childId)
                .update("sharingSettings", sharingSettings)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @SuppressWarnings("unchecked")
    public void revokeAllPermissionsAndUnlink(String childId, @NonNull final AuthCallback callback) {
        final DocumentReference childRef = firestore.collection("Users").document(childId);

        firestore.runTransaction(transaction -> {
                    DocumentSnapshot childSnapshot = transaction.get(childRef);
                    if (!childSnapshot.exists()) {
                        throw new FirebaseFirestoreException("Child document does not exist!",
                                FirebaseFirestoreException.Code.ABORTED);
                    }

                    List<String> linkedProviders = (List<String>) childSnapshot.get("linkedProviders");

                    if (linkedProviders != null && !linkedProviders.isEmpty()) {
                        for (String providerId : linkedProviders) {
                            DocumentReference providerRef = firestore.collection("Users").document(providerId);
                            transaction.update(providerRef, "linkedChildren", FieldValue.arrayRemove(childId));
                        }
                    }

                    transaction.update(childRef, "sharingSettings", new HashMap<String, Boolean>());
                    transaction.update(childRef, "linkedProviders", new ArrayList<String>());

                    return null;
                }).addOnSuccessListener(result -> cleanupInviteCodes(childId, callback))
                .addOnFailureListener(e -> callback.onFailure("Failed to revoke permissions and unlink: " + e.getMessage()));
    }

    private void cleanupInviteCodes(String childId, @NonNull AuthCallback callback) {
        firestore.collection("InviteCodes")
                .whereEqualTo("childId", childId)
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        WriteBatch batch = firestore.batch();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            batch.update(doc.getReference(), "active", false);
                        }
                        batch.commit()
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Failed to clean up invite codes: " + e.getMessage()));
                    } else {
                        callback.onFailure("No active invite codes found or failed to fetch.");
                    }
                });
    }

    // ------------------ LAST LOGIN ------------------

    private void updateLastLogin(String uid) {
        firestore.collection("Users").document(uid)
                .update("lastLogin", Timestamp.now());
    }

    public void updateChildDetails(String childId, String newName, String newAge, String newDob, String newNotes, @NonNull AuthCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", newName);
        updates.put("age", newAge);
        updates.put("dateOfBirth", newDob);
        updates.put("notes", newNotes);

        firestore.collection("Users").document(childId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    // ------------------ CALLBACK INTERFACES ------------------

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface ChildrenCallback {
        void onSuccess(List<ChildUser> children);
        void onFailure(String errorMessage);
    }

    public interface ChildProfileCallback {
        void onSuccess(ChildUser child);
        void onFailure(String errorMessage);
    }

    public interface InviteCodeCallback {
        void onSuccess(String code);
        void onFailure(String errorMessage);
    }
}
