package com.SmartAir.onboarding.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailOrUsernameEditText;
    private Button sendResetEmailButton;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        authRepository = new AuthRepository();

        emailOrUsernameEditText = findViewById(R.id.email_or_username);
        sendResetEmailButton = findViewById(R.id.send_reset_email_button);

        sendResetEmailButton.setOnClickListener(v -> {
            String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
            if (emailOrUsername.isEmpty()) {
                Toast.makeText(this, "Please enter your email or username", Toast.LENGTH_SHORT).show();
                return;
            }

            sendResetEmailButton.setEnabled(false); // Disable the button to prevent multiple clicks

            authRepository.sendPasswordResetEmail(emailOrUsername, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    sendResetEmailButton.setEnabled(true);
                    Toast.makeText(PasswordResetActivity.this, "Password reset email sent.", Toast.LENGTH_LONG).show();
                    finish(); // Close the activity
                }

                @Override
                public void onFailure(String errorMessage) {
                    sendResetEmailButton.setEnabled(true);
                    Toast.makeText(PasswordResetActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
