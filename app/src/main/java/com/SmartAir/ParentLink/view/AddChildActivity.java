package com.SmartAir.ParentLink.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.ParentLink.presenter.AddChildPresenter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddChildActivity extends AppCompatActivity implements AddChildView {

    private AddChildPresenter presenter;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextView passwordFeedbackTextView;
    private ConstraintLayout formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // Inject dependencies manually
        AuthRepository authRepository = AuthRepository.getInstance();
        presenter = new AddChildPresenter(this, authRepository);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        passwordFeedbackTextView = findViewById(R.id.password_feedback_text);
        Button addChildButton = findViewById(R.id.add_child_button);
        ImageButton backButton = findViewById(R.id.back_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.validatePasswordRealtime(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        addChildButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            presenter.onAddChildClicked(username, password, confirmPassword);
        });

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    public void showSuccessMessage(String message) {
        showSnackbar(message);
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        passwordFeedbackTextView.setVisibility(View.GONE);
    }

    @Override
    public void setAddChildError(String message) {
        showSnackbar(message);
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
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
