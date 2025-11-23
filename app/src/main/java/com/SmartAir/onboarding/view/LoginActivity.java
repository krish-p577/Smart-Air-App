package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.features.child.ChildHomeActivity;
import com.SmartAir.features.parent.ParentHomeActivity;
import com.SmartAir.features.provider.ProviderHomeActivity;
import com.SmartAir.onboarding.model.AuthRepository;
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

        presenter = new LoginPresenter(this, new AuthRepository());

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
        // ... existing navigateToHome code ...
    }

    @Override
    public void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        // Forward the USER_ROLE from the current intent to the next one
        intent.putExtra("USER_ROLE", getIntent().getStringExtra("USER_ROLE"));
        startActivity(intent);
    }
}
