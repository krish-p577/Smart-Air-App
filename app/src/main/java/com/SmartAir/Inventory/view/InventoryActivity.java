package com.SmartAir.Inventory.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.Inventory.model.InventoryRepository;
import com.SmartAir.Inventory.presenter.InventoryPresenter;
import com.SmartAir.R;

public class InventoryActivity extends AppCompatActivity implements InventoryView {
    private InventoryPresenter presenter;

    private Spinner inhalerTypeSpinner;
    private EditText remainingAmountTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        InventoryRepository repo = new InventoryRepository();
        presenter = new InventoryPresenter(this, repo);

        inhalerTypeSpinner = findViewById(R.id.spinner_inhaler_type);
        remainingAmountTextBox = findViewById(R.id.et_remaining_amount);

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
    public String getSelectedInhalerType() {
        Object selectedOption = inhalerTypeSpinner.getSelectedItem();

        if (selectedOption == null) {
            return "";
        }

        String selectedOptionText = selectedOption.toString();

        if (selectedOptionText.equals("Select inhaler type")) {
            return "";
        }

        if (selectedOptionText.equals("Controller inhaler")) {
            return "controllerInhaler";
        }

        if (selectedOptionText.equals("Rescue inhaler")) {
            return "rescueInhaler";
        }

        return "";
    }

    @Override
    public String getRemainingAmountText() {
        return remainingAmountTextBox.getText().toString();
    }

    @Override
    public void clearRemainingAmount() {
        remainingAmountTextBox.setText("");
    }

    @Override
    public void clearInhalerType() {
        inhalerTypeSpinner.setSelection(0);
    }

    @Override
    public void showChildDashboard() {
        Intent intent = new Intent(InventoryActivity.this, ChildDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
