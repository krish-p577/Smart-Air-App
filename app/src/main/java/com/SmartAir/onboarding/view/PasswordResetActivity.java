package com.SmartAir.onboarding.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.presenter.PasswordResetPresenter;
import com.google.android.material.snackbar.Snackbar;

public class PasswordResetActivity extends AppCompatActivity implements PasswordResetView {

    private EditText emailEditText;
    private PasswordResetPresenter presenter;
    private ProgressBar loadingIndicator;
    private Button sendResetEmailButton;
    private View emailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        presenter = new PasswordResetPresenter(this, AuthRepository.getInstance());

        emailEditText = findViewById(R.id.email);
        sendResetEmailButton = findViewById(R.id.send_reset_email_button);
        ImageButton backButton = findViewById(R.id.back_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        emailLayout = findViewById(R.id.email_layout);

        sendResetEmailButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            presenter.onSendResetClicked(email);
        });

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void showSuccessMessage(String message) {
        setLoading(false);
        showSnackbar(message);
    }

    @Override
    public void showErrorMessage(String message) {
        setLoading(false);
        showSnackbar("Error: " + message);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            sendResetEmailButton.setVisibility(View.GONE);
            emailLayout.setVisibility(View.GONE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            sendResetEmailButton.setVisibility(View.VISIBLE);
            emailLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
