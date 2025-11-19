package com.SmartAir.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.model.AuthRepository;
import com.SmartAir.presenter.LoginPresenter;

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

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            presenter.onLoginClicked(email, password);
        });

        signupLink.setOnClickListener(v -> presenter.onSignupLinkClicked());
    }

    @Override
    public void setLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        // TODO: Implement navigation to the correct home screen based on role
    }

    @Override
    public void navigateToSignup() {
        startActivity(new Intent(this, SignupActivity.class));
    }
}
