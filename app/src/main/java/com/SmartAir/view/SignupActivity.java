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
import com.SmartAir.presenter.SignupPresenter;

public class SignupActivity extends AppCompatActivity implements SignupView {

    private SignupPresenter presenter;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        presenter = new SignupPresenter(this, new AuthRepository());

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginLink = findViewById(R.id.login_link);

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            presenter.onSignupClicked(email, password, confirmPassword);
        });

        loginLink.setOnClickListener(v -> presenter.onLoginLinkClicked());
    }

    @Override
    public void setSignupError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        // TODO: Implement navigation to the correct home screen based on role
    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
