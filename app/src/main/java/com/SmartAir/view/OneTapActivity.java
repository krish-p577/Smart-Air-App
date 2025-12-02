package com.SmartAir.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.presenter.OneTapPresenter;
import com.SmartAir.presenter.RoleSelectionPresenter;
import com.SmartAir.presenter.WelcomePresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class OneTapActivity extends AppCompatActivity implements OneTapView{
    RadioButton greyLips, blueLips, otherLips, greyNails, blueNails, otherNails,yesChest,noChest, yesSpeak, noSpeak,  noAttempts;
    Button help, submit;
    EditText PEF, ifYes;
    private OneTapPresenter presenter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ;
    boolean cyanosis, chest, speaking, emerg;
    int redNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_tap_triage);

        presenter = new OneTapPresenter(this);


        greyLips = findViewById(R.id.greyLips);
        greyLips.setOnClickListener(v -> presenter.cyanosisClicked());

        blueLips = findViewById(R.id.blueLips);
        blueLips.setOnClickListener(v -> presenter.cyanosisClicked());

        yesChest = findViewById(R.id.yesChest);
        yesChest.setOnClickListener(v -> presenter.chestClicked());

        noSpeak = findViewById(R.id.noSpeak);
        noSpeak.setOnClickListener(v -> presenter.speakClicked());

        greyNails = findViewById(R.id.greyNails);
        greyNails.setOnClickListener(v -> presenter.cyanosisClicked());

        blueNails = findViewById(R.id.blueNails);
        blueNails.setOnClickListener(v -> presenter.cyanosisClicked());

        help = findViewById(R.id.helpButton);
        help.setOnClickListener(v -> presenter.helpClicked());

        submit = findViewById(R.id.submitButton);
        submit.setOnClickListener(v -> presenter.submitClicked());

    }
    @Override
    public void callEmergency() {
        Toast.makeText(getApplicationContext(), "start calling", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.setData(Uri.parse("tel:7809648081"));
        startActivity(i);
        emerg = true;
    }

    @Override
    public void cyanosisTrue() {
        Toast.makeText(getApplicationContext(), "cyanosisTrue", Toast.LENGTH_LONG).show();
        cyanosis = true;
    }

    @Override
    public void chestTrue() {
        Toast.makeText(getApplicationContext(), "chestTrue", Toast.LENGTH_LONG).show();
        chest = true;
    }

    @Override
    public void speakingTrue() {
        Toast.makeText(getApplicationContext(), "speakingTrue", Toast.LENGTH_LONG).show();
        speaking = true;
    }

    @Override
    public void logForm() {
        Toast.makeText(getApplicationContext(), "start logging", Toast.LENGTH_LONG).show();
        PEF = findViewById(R.id.currentPEF);
        String value = PEF.getText().toString();

        HashMap<Object, Object> ved_test = new HashMap<>();
        ved_test.put("parentId", "parent1");
        ved_test.put("childId", "child1");
        ved_test.put("timestamp", "2025-02-22T01:15:00Z");
        ved_test.put("redFlags", getRed());
        Toast.makeText(getApplicationContext(), "after get red", Toast.LENGTH_LONG).show();
        ved_test.put("recentRescuePuffs", "idk how this should look like");
        ved_test.put("optionalPEF", Integer.parseInt(value));
        ved_test.put("initialZone", "red");
        ved_test.put( "outcome", getOutCome());
        ved_test.put("escalation", didEsclate());
        ved_test.put("Logs", "p");
        ved_test.put("NumberReds", redNumber);
        firestore.collection("triageIncidents").add(ved_test).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    @Override
    public void incCount() {
        Toast.makeText(getApplicationContext(), "inc red count", Toast.LENGTH_LONG).show();
        redNumber += 1;
    }

    @Override
    public void homeSteps() {

    }

    public String getOutCome(){
        Toast.makeText(getApplicationContext(), "get outcome", Toast.LENGTH_LONG).show();
        if(emerg){
            Toast.makeText(getApplicationContext(), "return call_emergency", Toast.LENGTH_LONG).show();
            return "call_emergency";
        }
        return "Home_Steps";
    }
    public HashMap<String, Boolean> getRed(){
        Toast.makeText(getApplicationContext(), "getRed", Toast.LENGTH_LONG).show();
        HashMap<String, Boolean> flags = new HashMap<>();
        if(speaking){
            flags.put("difficultySpeaking", true);
        }else{
            flags.put("difficultySpeaking", false);
        }
        if(cyanosis){
            flags.put("cyanosis", true);
        }else{
            flags.put("cyanosis", false);
        }
        if(chest){
            flags.put("chest", true);
        }else{
            flags.put("chest", false);
        }
        Toast.makeText(getApplicationContext(), "return", Toast.LENGTH_LONG).show();
        return flags;
    }

    public boolean didEsclate(){
        //need to review older log and compare current. if current count > prev log than return true
        return true;
    }

}
