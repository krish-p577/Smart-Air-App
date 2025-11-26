package com.SmartAir.onboarding.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.google.android.material.snackbar.Snackbar;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailOrUsernameEditText;
    private AuthRepository authRepository;
    private Group formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        authRepository = AuthRepository.getInstance();

        emailOrUsernameEditText = findViewById(R.id.email_or_username);
        Button sendResetEmailButton = findViewById(R.id.send_reset_email_button);
        ImageButton backButton = findViewById(R.id.back_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);

        sendResetEmailButton.setOnClickListener(v -> {
            String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
            if (emailOrUsername.isEmpty()) {
                showSnackbar("Please enter your email or username");
                return;
            }
            setLoading(true);
            authRepository.sendPasswordResetEmail(emailOrUsername, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    setLoading(false);
                    // Check if the input was a username (for a child)
                    if (!emailOrUsername.contains("@")) {
                        showSnackbar("Your parent has been notified to reset your password.");
                    } else {
                        showSnackbar("Password reset email sent.");
                    }
                    // Optionally finish after a delay to allow user to see message
                }

                @Override
                public void onFailure(String errorMessage) {
                    setLoading(false);
                    showSnackbar("Error: " + errorMessage);
                }
            });
        });

        backButton.setOnClickListener(v -> {
            finish(); // Simply close the activity
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            formContent.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            formContent.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
