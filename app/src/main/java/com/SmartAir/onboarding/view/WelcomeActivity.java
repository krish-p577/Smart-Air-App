package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.WelcomePresenter;
import com.google.firebase.FirebaseApp;

public class WelcomeActivity extends AppCompatActivity implements WelcomeView {

    private WelcomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Manually initialize Firebase to match working code
        FirebaseApp.initializeApp(this);

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
