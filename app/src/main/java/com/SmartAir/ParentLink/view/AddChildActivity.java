package com.SmartAir.ParentLink.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.ParentLink.presenter.AddChildPresenter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddChildActivity extends AppCompatActivity implements AddChildView {

    private AddChildPresenter presenter;
    private TextInputEditText nameEditText;
    private TextInputEditText ageEditText;
    private DatePicker dobPicker;
    private TextInputEditText notesEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextView passwordFeedbackTextView;
    private View formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        AuthRepository authRepository = AuthRepository.getInstance();
        presenter = new AddChildPresenter(this, authRepository);

        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        dobPicker = findViewById(R.id.dob_picker);
        notesEditText = findViewById(R.id.notes_edit_text);
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
            String name = getSafeText(nameEditText);
            String age = getSafeText(ageEditText);
            String dob = getDobFromPicker();
            String notes = getSafeText(notesEditText);
            String username = getSafeText(usernameEditText);
            String password = getSafeText(passwordEditText);
            String confirmPassword = getSafeText(confirmPasswordEditText);
            presenter.onAddChildClicked(name, age, dob, notes, username, password, confirmPassword);
        });

        backButton.setOnClickListener(v -> finish());
    }

    private String getSafeText(TextInputEditText editText) {
        Editable editable = editText.getText();
        return editable != null ? editable.toString().trim() : "";
    }

    private String getDobFromPicker() {
        int day = dobPicker.getDayOfMonth();
        int month = dobPicker.getMonth();
        int year = dobPicker.getYear();
        return String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, day, year);
    }

    @Override
    public void showSuccessMessage(String message) {
        showSnackbar(message);
        nameEditText.setText("");
        ageEditText.setText("");
        notesEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        passwordFeedbackTextView.setVisibility(View.GONE);

        // Reset DatePicker to current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dobPicker.updateDate(year, month, day);
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
