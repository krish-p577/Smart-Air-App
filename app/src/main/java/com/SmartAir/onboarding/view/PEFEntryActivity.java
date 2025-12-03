package com.SmartAir.onboarding.view;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.presenter.PEFPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PEFEntryActivity extends Activity implements PEFEntryView {
    private PEFPresenter presenter;
    FirebaseFirestore firestore;
    String parentId;
    int green, red, yellow;
    String zone = "Null";
    CurrentUser user;

    String childID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pef);
        user = CurrentUser.getInstance();
        childID = user.getUid();
        presenter = new PEFPresenter(this);
        Button pefButton = findViewById(R.id.pef_button);
        pefButton.setOnClickListener(v -> presenter.onPEFClicked());
    }
    public void popOut(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_popout_pef);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.ic_launcher_background);
        Button submit = dialog.findViewById(R.id.Submit_Button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_LONG).show();

                EditText pefVal = dialog.findViewById(R.id.PEFNumber);
                String value= pefVal.getText().toString();
                Toast.makeText(getApplicationContext(), "got value", Toast.LENGTH_LONG).show();

                EditText pre = dialog.findViewById(R.id.PEFNumber);
                String preMed =" ";
                String postMed =" ";

                if(pre != null){
                    preMed= pre.getText().toString();
                }
                EditText post = dialog.findViewById(R.id.PEFNumber);
                if(post != null){
                    postMed= post.getText().toString();
                }

                firestore = FirebaseFirestore.getInstance();
                HashMap<Object, Object> ved_test = new HashMap<>();
                ved_test.put("parentId", parentId);
                ved_test.put("childId", childID);
                ved_test.put("timestamp", FieldValue.serverTimestamp());
                ved_test.put("pef", Integer.parseInt(value));
                ved_test.put("pre", preMed);
                ved_test.put("post", postMed);
                ved_test.put("zone", getzone(Integer.parseInt(value)));
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
    public String getzone(int PEF){
        firestore.collection("Users")
                .document(childID)
                .get()
                .addOnSuccessListener(querySnap -> {
                    Map<String, Object> pbMap = (Map<String, Object>) querySnap.get("personalBestPEF");
                    Map<String, Object> zonesMap = (Map<String, Object>) pbMap.get("Zones");
                    assert zonesMap != null;
                    green  = ((Number) Objects.requireNonNull(zonesMap.get("Green"))).intValue();
                    yellow = ((Number) Objects.requireNonNull(zonesMap.get("Yellow"))).intValue();
                    red    = ((Number) Objects.requireNonNull(zonesMap.get("Red"))).intValue();
                    if (PEF <= red) {
                        zone = "Red";
                    } else if (PEF <= yellow) {
                        zone = "Yellow";
                    } else {
                        zone = "Green";
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                });
        return zone;
    }
}
