package com.SmartAir.onboarding.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.AddChildPresenter;
import com.google.android.material.snackbar.Snackbar;

public class AddChildActivity extends AppCompatActivity implements AddChildView {

    private AddChildPresenter presenter;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Group formContent;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        presenter = new AddChildPresenter(this);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button addChildButton = findViewById(R.id.add_child_button);
        ImageButton backButton = findViewById(R.id.back_button);
        formContent = findViewById(R.id.form_content);
        loadingLayout = findViewById(R.id.loading_layout);

        addChildButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            presenter.onAddChildClicked(username, password);
        });

        backButton.setOnClickListener(v -> {
            finish(); // Simply close the activity
        });
    }

    @Override
    public void showSuccessMessage(String message) {
        showSnackbar(message);
        usernameEditText.setText("");
        passwordEditText.setText("");
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
