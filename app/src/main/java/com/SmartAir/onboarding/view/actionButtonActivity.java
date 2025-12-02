package com.SmartAir.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.presenter.actionButtonPresenter;

public class actionButtonActivity extends AppCompatActivity implements actionButtonView {
    private actionButtonPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_for_action);

        presenter = new actionButtonPresenter(this);

        Button continueButton = findViewById(R.id.startActionPlan);
        continueButton.setOnClickListener(v -> presenter.onContinueClicked());
    }

    @Override
    public void navigateNext() {
        startActivity(new Intent(this, ActionPlanActivity.class));
    }
}

