package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.presenter.SignupPresenter;

public class SignupActivity extends AppCompatActivity implements SignupView {

    private SignupPresenter presenter;
    private EditText displayNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        String role = CurrentUser.getInstance().getRole();
        Intent intent;

        if (role == null) {
            Toast.makeText(this, "Error: User role not found.", Toast.LENGTH_SHORT).show();
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
                intent = new Intent(this, ChildHomeActivity.class);
                break;
            default:
                Toast.makeText(this, "Invalid user role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
