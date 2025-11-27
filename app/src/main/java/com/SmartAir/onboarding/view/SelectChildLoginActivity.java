package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.presenter.SelectChildLoginPresenter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class SelectChildLoginActivity extends AppCompatActivity implements SelectChildLoginView {

    private SelectChildLoginPresenter presenter;
    private ProgressBar loadingIndicator;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child_login);

        loadingIndicator = findViewById(R.id.loading_indicator);
        recyclerView = findViewById(R.id.child_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        presenter = new SelectChildLoginPresenter(this, AuthRepository.getInstance());
        presenter.fetchChildren();
    }

    @Override
    public void displayChildren(List<ChildUser> children) {
        recyclerView.setAdapter(new ChildLoginAdapter(children, this));
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

    @Override
    public void displayError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void closeView() {
        finish();
    }

    @Override
    public void promptForChildPassword(ChildUser child) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_child_password, null);
        final TextInputLayout passwordLayout = dialogView.findViewById(R.id.password_layout);
        final EditText passwordEditText = passwordLayout.getEditText();

        new AlertDialog.Builder(this)
            .setTitle("Login as " + child.getDisplayName())
            .setMessage("Please enter the password for " + child.getDisplayName())
            .setView(dialogView)
            .setPositiveButton("Login", (dialog, which) -> {
                if (passwordEditText != null) {
                    String password = passwordEditText.getText().toString();
                    presenter.onChildSelected(child, password);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
