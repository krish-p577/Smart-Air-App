package com.SmartAir.onboarding.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.presenter.PasswordResetPresenter;
import com.google.android.material.snackbar.Snackbar;

public class PasswordResetActivity extends AppCompatActivity implements PasswordResetView {

    private EditText emailEditText;
    private PasswordResetPresenter presenter;
    private Group formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Provide the AuthRepository to the presenter
        presenter = new PasswordResetPresenter(this, AuthRepository.getInstance());

        emailEditText = findViewById(R.id.email);
        Button sendResetEmailButton = findViewById(R.id.send_reset_email_button);
        Button backButton = findViewById(R.id.back_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);

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
        for (int id : formContent.getReferencedIds()) {
            View child = findViewById(id);
            if (child != null) {
                child.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        }
        if (loadingLayout != null) {
            loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
