package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.ActionPlanPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ActionPlanActivity extends AppCompatActivity implements ActionPlanView {
    private ActionPlanPresenter presenter;
    EditText redPlan, greenPlan, yellowPlan, emergencyPlan;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_plan);

        presenter = new ActionPlanPresenter(this);

        Button continueButton = findViewById(R.id.submitPlan);
        continueButton.setOnClickListener(v -> presenter.onSubmitClicked());
    }

    @Override
    public void sendPlan() {
        Toast.makeText(getApplicationContext(), "start logging", Toast.LENGTH_LONG).show();
        redPlan = findViewById(R.id.redText);
        String rPlan = redPlan.getText().toString();

        greenPlan = findViewById(R.id.greenText);
        String gPlan = greenPlan.getText().toString();

        yellowPlan = findViewById(R.id.yellowText);
        String yPlan = yellowPlan.getText().toString();

        emergencyPlan = findViewById(R.id.emergText);
        String ePlan = emergencyPlan.getText().toString();

        HashMap<Object, Object> ved_test = new HashMap<>();
        ved_test.put("green", gPlan);
        ved_test.put("yellow", yPlan);
        ved_test.put("red", rPlan);
        ved_test.put("emergency", ePlan);
        Toast.makeText(getApplicationContext(), "after get red", Toast.LENGTH_LONG).show();

        firestore.collection("actionPlan").add(ved_test).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Succcess", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_LONG).show();
            }
        });
    }
}
