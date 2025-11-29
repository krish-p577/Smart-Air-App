package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.presenter.LoginPresenter;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private LoginPresenter presenter;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Group formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inject the singleton repository
        presenter = new LoginPresenter(this, AuthRepository.getInstance());

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);
        TextView signupLink = findViewById(R.id.signup_link);
        TextView forgotPasswordLink = findViewById(R.id.forgot_password_link);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            setLoading(true);
            presenter.onLoginClicked(email, password);
        });

        signupLink.setOnClickListener(v -> presenter.onSignupLinkClicked());

        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(this, PasswordResetActivity.class));
        });
    }


    @Override
    public void setLoginError(String message) {
        setLoading(false);
        showSnackbar(message);
    }

    @Override
    public void navigateToHome() {
        String role = CurrentUser.getInstance().getRole();
        Intent intent;

        if (role == null) {
            setLoading(false);
            showSnackbar("Error: User role not found.");
            return;
        }

        switch (role.toLowerCase()) {
            case "parent":
                intent = new Intent(this, ParentHomeActivity.class);
                break;
            case "provider":
                intent = new Intent(this, ProviderHomeActivity.class);
                break;
            case "child":
                intent = new Intent(this, ChildDashboardActivity.class);
                break;
            default:
                setLoading(false);
                showSnackbar("Invalid user role: " + role);
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("USER_ROLE", getIntent().getStringExtra("USER_ROLE"));
        startActivity(intent);
    }

    @Override
    public void navigateToOnboarding() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
