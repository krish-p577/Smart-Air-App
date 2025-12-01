package com.SmartAir.LogController.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.LogController.model.LogControllerRepository;
import com.SmartAir.LogController.presenter.LogControllerPresenter;
import com.SmartAir.R;

public class LogControllerActivity extends AppCompatActivity implements LogControllerView {

    private LogControllerPresenter presenter;

    private EditText puffsTakenTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_controller);

        LogControllerRepository repo = new LogControllerRepository();
        presenter = new LogControllerPresenter(this, repo);

        puffsTakenTextBox = findViewById(R.id.et_puffs_taken);

        Button submitButton = findViewById(R.id.btn_submit_log);
        submitButton.setOnClickListener(v -> presenter.onSubmitButtonClicked());

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> presenter.onBackClicked());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public String getPuffsTakenText() {
        return puffsTakenTextBox.getText().toString();
    }

    @Override
    public void clearPuffsTaken() {
        puffsTakenTextBox.setText("");
    }

    @Override
    public void showChildDashboard() {
        Intent intent = new Intent(LogControllerActivity.this, ChildDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
