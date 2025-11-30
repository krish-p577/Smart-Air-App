package com.SmartAir.onboarding.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.PbPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class PbActivity extends AppCompatActivity implements PbView{
    private PbPresenter presenter;
    FirebaseFirestore firestore;

    double PbNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb_setting);
        presenter = new PbPresenter(this);

        Button pefButton = findViewById(R.id.PBNumber);
        pefButton.setOnClickListener(v -> presenter.onPBClicked());
    }
    public void popOut(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popout_pb);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.ic_launcher_background);
        Button submit = dialog.findViewById(R.id.Submit_Button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

                EditText pefVal = dialog.findViewById(R.id.PBNumber);
                String value= pefVal.getText().toString();
                PbNumber = Integer.parseInt(value);
                Toast.makeText(getApplicationContext(), "got value", Toast.LENGTH_LONG).show();

                firestore = FirebaseFirestore.getInstance();
                HashMap<Object, Object> ved_test = new HashMap<>();
                ved_test.put("name", "parent1");
                ved_test.put("dateOfBirth", "child1");
                ved_test.put("notes", "2025-02-21T07:45:00Z");
                ved_test.put("createdAt", "timestamp");
                ved_test.put("archived", false);
                ved_test.put("personalBestPEF", PbNumber);
                ved_test.put("Zones", getZoneMap());
                ved_test.put("controllerSchedule", false);
                Toast.makeText(getApplicationContext(), "About to log", Toast.LENGTH_LONG).show();

                firestore.collection("pefLogs").add(ved_test).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
        });

        dialog.show();
    }
    public HashMap<String, Double> getZoneMap(){
        HashMap<String, Double> map = new HashMap<String, Double>();
        map.put("Green", ( 0.2 * PbNumber));
        map.put("Yellow", ( 0.51 * PbNumber));
        map.put("Red", ( 0.5 * PbNumber));
        return map;
    }
}
