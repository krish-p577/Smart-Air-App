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
import com.SmartAir.onboarding.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private LoginPresenter presenter;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        TextView forgotPasswordLink = findViewById(R.id.forgot_password_link);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            presenter.onLoginClicked(email, password);
        });

        signupLink.setOnClickListener(v -> presenter.onSignupLinkClicked());

        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(this, PasswordResetActivity.class));
        });
    }

    @Override
    public void setLoginError(String message) {
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
    public void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("USER_ROLE", getIntent().getStringExtra("USER_ROLE"));
        startActivity(intent);
    }
}
