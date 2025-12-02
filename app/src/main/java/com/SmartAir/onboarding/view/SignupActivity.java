package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.presenter.SignupPresenter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SignupActivity extends AppCompatActivity implements SignupView {

    private SignupPresenter presenter;
    private EditText displayNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView passwordFeedbackTextView;
    private ConstraintLayout formContent;
    private View loadingLayout;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        presenter = new SignupPresenter(this, AuthRepository.getInstance());

        displayNameEditText = findViewById(R.id.displayName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        passwordFeedbackTextView = findViewById(R.id.password_requirements_text);
        Button signupButton = findViewById(R.id.signup_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);
        TextView loginLink = findViewById(R.id.login_link);

        userRole = getIntent().getStringExtra("USER_ROLE");

        // Real-time password feedback
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.validatePasswordRealtime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        signupButton.setOnClickListener(v -> {
            String displayName = displayNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            presenter.onSignupClicked(email, password, confirmPassword, userRole, displayName);
        });

        loginLink.setOnClickListener(v -> presenter.onLoginLinkClicked());
    }

    @Override
    public void setSignupError(String message) {
        setLoading(false);
        passwordFeedbackTextView.setText(""); // clear feedback
        passwordFeedbackTextView.setVisibility(View.GONE);
        showSnackbar(message);
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("USER_ROLE", getIntent().getStringExtra("USER_ROLE"));
        startActivity(intent);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            formContent.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            formContent.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updatePasswordRequirements(List<String> passedRules, List<String> failedRules) {
        if (!failedRules.isEmpty()) {
            StringBuilder feedback = new StringBuilder();
            for (String rule : failedRules) {
                feedback.append("• ").append(rule).append("\n");
            }
            passwordFeedbackTextView.setText(feedback.toString().trim());
            passwordFeedbackTextView.setVisibility(View.VISIBLE);
        } else {
            passwordFeedbackTextView.setText("");
            passwordFeedbackTextView.setVisibility(View.GONE);
        }
    }

    private void showSnackbar(String message) {
        // Optional: keep for errors unrelated to password
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
