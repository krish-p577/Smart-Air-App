package com.SmartAir.LogRescue.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.LogRescue.model.LogRescueRepository;
import com.SmartAir.LogRescue.presenter.LogRescuePresenter;
import com.SmartAir.R;

public class LogRescueActivity extends AppCompatActivity implements LogRescueView {

    private LogRescuePresenter presenter;

    private Spinner preFeelingSpinner;
    private EditText puffsTakenTextBox;
    private Spinner postFeelingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_rescue);

        LogRescueRepository repo = new LogRescueRepository();
        presenter = new LogRescuePresenter(this, repo);

        preFeelingSpinner = findViewById(R.id.spinner_pre_feeling);
        puffsTakenTextBox = findViewById(R.id.et_rescue_puffs_taken);
        postFeelingSpinner = findViewById(R.id.spinner_post_feeling);

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
    public String getSelectedPreFeeling() {
        Object selectedOption = preFeelingSpinner.getSelectedItem();

        if (selectedOption == null) {
            return "";
        }

        return selectedOption.toString().trim();
    }

    @Override
    public String getPuffsTakenText() {
        return puffsTakenTextBox.getText().toString();
    }

    @Override
    public String getSelectedPostFeeling() {
        Object selectedOption = postFeelingSpinner.getSelectedItem();

        if (selectedOption == null) {
            return "";
        }

        return selectedOption.toString().trim();
    }

    @Override
    public void clearPreFeeling() {
        preFeelingSpinner.setSelection(0);
    }

    @Override
    public void clearPuffsTaken() {
        puffsTakenTextBox.setText("");
    }

    @Override
    public void clearPostFeeling() {
        postFeelingSpinner.setSelection(0);
    }

    @Override
    public void showChildDashboard() {
        Intent intent = new Intent(LogRescueActivity.this, ChildDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
