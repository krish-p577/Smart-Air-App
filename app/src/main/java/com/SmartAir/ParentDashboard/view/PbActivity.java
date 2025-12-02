package com.SmartAir.ParentDashboard.view;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.ParentDashboard.presenter.PbPresenter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.firebase.firestore.FieldValue;

public class PbActivity extends AppCompatActivity implements PbView{
    private PbPresenter presenter;
    FirebaseFirestore firestore;

    double PbNumber;
    String childId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);
        presenter = new PbPresenter(this);

        childId = getIntent().getStringExtra("childId");

        Button pefButton = findViewById(R.id.SetPBButtons);
        pefButton.setOnClickListener(v -> presenter.onPBClicked());
    }
    public void popOut(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popout_pb);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.ic_launcher_background);

        Button submit = dialog.findViewById(R.id.submitPBButton);

        submit.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

            EditText pefVal = dialog.findViewById(R.id.PBNumber);
            String value= pefVal.getText().toString();
            PbNumber = Integer.parseInt(value);
            Toast.makeText(getApplicationContext(), "got value", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), childId, Toast.LENGTH_LONG).show();
            firestore = FirebaseFirestore.getInstance();

            HashMap<Object, Object> ved_test = new HashMap<>();
            ved_test.put("createdAt", FieldValue.serverTimestamp());
            ved_test.put("personalBestPEF", PbNumber);
            ved_test.put("Zones", getZoneMap());
            Toast.makeText(getApplicationContext(), "About to log", Toast.LENGTH_LONG).show();

            firestore.collection("Users").document(childId).update("personalBestPEF", ved_test).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Succcess", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_LONG).show());

        });

        dialog.show();
    }
    public Map<String, Double> getZoneMap(){
        Map<String, Double> map = new HashMap<>();
        map.put("Green", ( 0.8 * PbNumber));
        map.put("Yellow", ( 0.51 * PbNumber));
        map.put("Red", ( 0.5 * PbNumber));
        return map;
    }
}
