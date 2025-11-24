package com.SmartAir.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.presenter.WelcomePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
