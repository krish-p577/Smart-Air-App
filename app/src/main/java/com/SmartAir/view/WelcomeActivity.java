package com.SmartAir.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.presenter.WelcomePresenter;

public class WelcomeActivity extends AppCompatActivity implements WelcomeView {

    private WelcomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        presenter = new WelcomePresenter(this);

        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> presenter.onContinueClicked());
    }

    @Override
    public void navigateNext() {
        startActivity(new Intent(this, RoleSelectionActivity.class));
    }
}
