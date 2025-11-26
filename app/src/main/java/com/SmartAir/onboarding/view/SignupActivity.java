package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.R;
import com.SmartAir.onboarding.view.ChildHomeActivity;
import com.SmartAir.onboarding.view.ParentHomeActivity;
import com.SmartAir.onboarding.view.ProviderHomeActivity;

import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.presenter.SignupPresenter;
import com.google.android.material.snackbar.Snackbar;

public class SignupActivity extends AppCompatActivity implements SignupView {

    private SignupPresenter presenter;
    private EditText displayNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Group formContent;
    private View loadingLayout;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        presenter = new SignupPresenter(this);

        displayNameEditText = findViewById(R.id.displayName);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        Button signupButton = findViewById(R.id.signup_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);
        TextView loginLink = findViewById(R.id.login_link);

        userRole = getIntent().getStringExtra("USER_ROLE");

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
        showSnackbar(message);
    }

    /**
     * After signup, this method now navigates to the OnboardingActivity.
     */
    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
