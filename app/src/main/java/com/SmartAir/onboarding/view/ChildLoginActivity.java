package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.R;
import com.SmartAir.homepage.view.ChildHomeActivity;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.presenter.ChildLoginPresenter;
import com.google.android.material.snackbar.Snackbar;

public class ChildLoginActivity extends AppCompatActivity implements ChildLoginView {

    private ChildLoginPresenter presenter;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Group formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);

        // Use DI constructor
        presenter = new ChildLoginPresenter(this, AuthRepository.getInstance());

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            setLoading(true);
            presenter.onLoginClicked(username, password);
        });
    }


    @Override
    public void setLoginError(String message) {
        setLoading(false);
        showSnackbar(message);
    }

    @Override
    public void navigateToChildHome() {
        Intent intent = new Intent(this, ChildHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
