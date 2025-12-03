package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.OneTapPresenter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OneTapActivity extends AppCompatActivity implements OneTapView {
    RadioButton greyLips, no, blueLips, greyNails, blueNails,yesChest, noSpeak;
    Button help, submit;
    int PEFValue = -1; //optional PEF Value
    EditText PEF, ifYes;
    private OneTapPresenter presenter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    boolean cyanosis, chest, speaking, emerg;
    boolean noRescue = false; // did they use rescue or not
    boolean didEscalte = false; // did it get wrose or not
    int redNumber = 0; //number of red flags
    String intialZone = ""; //starting zone
    Map<String, String> mapZones =  new HashMap<>(); //action plan map
    String ChildID, toReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_tap_triage);

        ChildID = getIntent().getStringExtra("childId");

        setMapZones();
        setInitialZone();
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

        no = findViewById(R.id.noAttempts);
        no.setOnClickListener(v->presenter.noClicked());

    }
    //if no rescue clicked no rescue attempts
    public void setNoTrue(){
        noRescue = true;
    }

    //calls ved's phone number and sets emerg to true meaning we needed emergency
    @Override
    public void callEmergency() {
        Toast.makeText(getApplicationContext(), "start calling", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.setData(Uri.parse("tel:7809648081"));
        startActivity(i);
        emerg = true;
    }

    //intialize a map of the actionplan
    public void setMapZones(){
        firestore.collection("actionPlan")
                .document(ChildID)
                .get()
                .addOnSuccessListener(querySnap -> {
                    String emergency = Objects.requireNonNull(querySnap.get("emergency")).toString();
                    String green = Objects.requireNonNull(querySnap.get("green")).toString();
                    String yellow = Objects.requireNonNull(querySnap.get("yellow")).toString();
                    String red = Objects.requireNonNull(querySnap.get("red")).toString();
                    mapZones.put("green", green);
                    mapZones.put("red", red);
                    mapZones.put("yellow", yellow);
                    mapZones.put("emergency", emergency);
                })
                .addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                });
    }

    //if they clicked they have coloured nails or lips
    @Override
    public void cyanosisTrue() {
        Toast.makeText(getApplicationContext(), "cyanosisTrue", Toast.LENGTH_LONG).show();
        cyanosis = true;
    }

    //if they clicked they have hard time breathing
    @Override
    public void chestTrue() {
        Toast.makeText(getApplicationContext(), "chestTrue", Toast.LENGTH_LONG).show();
        chest = true;
    }

    //if they clicked they cant talk
    @Override
    public void speakingTrue() {
        Toast.makeText(getApplicationContext(), "speakingTrue", Toast.LENGTH_LONG).show();
        speaking = true;
    }

    //function does the logging
    @Override
    public void logForm() {
        Toast.makeText(getApplicationContext(), "start logging", Toast.LENGTH_LONG).show();
        //get the optional PEF VAL
        PEF = findViewById(R.id.currentPEF);
        String value = PEF.getText().toString();
        if (value.isEmpty()) {
            PEFValue = -1; // or some default
        } else {
            PEFValue = Integer.parseInt(value);
        }

        //Get number of resuces
        ifYes = findViewById(R.id.currentPEF);
        String rescueAttempts = ifYes.getText().toString();
        int rescueVal = Integer.parseInt(rescueAttempts);

        HashMap<Object, Object> ved_test = new HashMap<>();
        ved_test.put("parentId", "parent1");
        ved_test.put("childId", ChildID);
        ved_test.put("timestamp", FieldValue.serverTimestamp());
        ved_test.put("redFlags", getRed()); // gets the hashmap of red flags
        ved_test.put("recentRescuePuffs", rescueVal); // need this to also connect with arshdeep rescue lops
        ved_test.put("optionalPEF", PEFValue); // optional pef val if not its a -1
        ved_test.put("initialZone", intialZone); //starting zone set on create
        ved_test.put( "outcome", getOutCome()); //homestep action or all emerg.
        ved_test.put("escalation", didEsclate()); // get if it esclated or not from previous logs
        ved_test.put("NumberReds", redNumber); //gets number of red flags detected esier to see escaltion
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

    //increase count of red flags detected
    @Override
    public void incCount() {
        Toast.makeText(getApplicationContext(), "inc red count", Toast.LENGTH_LONG).show();
        redNumber += 1;
    }

    //set intial zone on create
    public String setInitialZone(){
        firestore.collection("pefLogs")
                .whereEqualTo("childId", ChildID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(querySnap -> {
                    DocumentSnapshot doc = querySnap.getDocuments().get(0);
                    intialZone = doc.getString("zone");
                })
                .addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                });;
                return intialZone;
    }

    //if they added a new PEF we might be in a diff zone than when we started.
    public String calcCurrentZone(){
        firestore.collection("Users")
                .document(ChildID)
                .get()
                .addOnSuccessListener(querySnap -> {
                    Map<String, Object> pbMap = (Map<String, Object>) querySnap.get("personalBestPEF");
                    Map<String, Object> zonesMap = (Map<String, Object>) pbMap.get("Zones");
                    assert zonesMap != null;
                    int yellow = ((Number) Objects.requireNonNull(zonesMap.get("Yellow"))).intValue();
                    int red    = ((Number) Objects.requireNonNull(zonesMap.get("Red"))).intValue();
                    if (PEFValue <= red) {
                        toReturn = "Red";
                    } else if (PEFValue <= yellow) {
                        toReturn = "Yellow";
                    } else {
                        toReturn = "Green";
                    }
                }).addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                });
        return toReturn;
    }

    //after submitted run these steps with the action plan or if already called 911 than dont need action plan.
    public String homeSteps(){
        String Zone = "";
        String test_return = null;
        if(PEFValue == -1){
            Zone = intialZone;
        }else if(emerg){
             return mapZones.get("emergency");
        } else{
            Zone = calcCurrentZone();
        }

        if(Objects.equals(Zone, "green")){
            test_return =  mapZones.get("green");
        }else if(Objects.equals(Zone, "red")){
            test_return =  mapZones.get("red");
        }else if(Objects.equals(Zone, "yellow")){
            test_return =  mapZones.get("yellow");
        }else {
            test_return =  mapZones.get("emergency");
        }
        if(test_return == null){
            test_return = "Call 911";
        }
        return test_return;
    }

    //what happened did we call emerge or what action plan did we do.
    public String getOutCome(){
        Toast.makeText(getApplicationContext(), "get outcome", Toast.LENGTH_LONG).show();
        if(emerg){
            Toast.makeText(getApplicationContext(), "return call_emergency", Toast.LENGTH_LONG).show();
            return "call_emergency";
        }
        return homeSteps();
    }

    //create a hashmap of all of the red flags they have
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

    //check old logs if we have more red flags or not.
    public boolean didEsclate(){
        //need to review older log and compare current. if current count > prev log than return true
        firestore.collection("triageIncidents")
                .whereEqualTo("childId", ChildID) // Querying by ID is safer than Name
                .orderBy("timestamp", Query.Direction.DESCENDING) // Get newest first
                .get()
                .addOnSuccessListener(querySnap -> {
                    if (querySnap.isEmpty()) {
                        didEscalte = false;
                        return;
                    }
                    DocumentSnapshot doc = querySnap.getDocuments().get(0);
                    int NumberReds = doc.getLong("NumberReds").intValue();
                    if(redNumber > NumberReds){
                        didEscalte = true;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                });

        return didEscalte;
    }
}
