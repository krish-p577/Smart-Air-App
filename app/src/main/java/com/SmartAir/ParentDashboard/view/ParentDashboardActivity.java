package com.SmartAir.ParentDashboard.view;

import android.annotation.SuppressLint;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ParentDashboard.model.ChildModel;
import com.SmartAir.ParentDashboard.model.ParentModel;
import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.presenter.ParentDashboardPresenter;
import com.SmartAir.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ParentDashboardPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);

        FirebaseApp.initializeApp(this);


        Log.i("TAG", "CREATED PAGE");


        TextView test_text = findViewById(R.id.r6_test);
        TextView box1 = findViewById(R.id.myText);
        TextView box2 = findViewById(R.id.myText3);
        TextView box3 = findViewById(R.id.myText4);

        Spinner spinner = findViewById(R.id.mySpinner);

        List<String> childList = new ArrayList<>();
        List<String> childIdList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                childList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getUserChildren(adapter, childList, childIdList);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedChild = childList.get(position);
                String selectedId = childIdList.get(position);
//                dbTest(box1,selectedChild);

                updateZone(selectedId, box1, box2, box3);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }


    @SuppressLint("SetTextI18n")
    protected void getUserChildren(ArrayAdapter<String> adapter, List<String> childList, List<String> childIdList){
        Log.i("DEBUG", "Get Children");

        db.collection("users").document("1")
                .collection("children")
                .get().
                addOnSuccessListener(queryDocumentSnapshots -> {
                   childList.clear();

                   for (DocumentSnapshot document : queryDocumentSnapshots) {
                       String childName = document.getString("name");
                       if (childName != null) {
                           childList.add(childName);
                           childIdList.add(document.getId());
                       }
                   }

                   for (DocumentSnapshot document : queryDocumentSnapshots) {

                   }

                   adapter.notifyDataSetChanged();
                   Log.i("SPINNER TAG", "Loaded Children" + childList);

                }).addOnFailureListener(e ->{
                    Log.e("SPINNER FAILUE", "ERR", e);
                });

    }

    @SuppressLint("SetTextI18n")
    protected void updateZone(String childID, TextView box1, TextView box2, TextView box3){
        Log.i("DEBUG", "function initilize");

        db.collection("pefLogs").
                document(childID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PefLogsModel info = documentSnapshot.toObject(PefLogsModel.class);
                        assert info != null;
                        box1.setText("Today's Zone:    " + info.getZone());
                    }
                })
                .addOnFailureListener(e ->{

                });

        db.collection("rescueLogs")
                .whereEqualTo("childid", childID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        box2.setText("Last Rescue Time: " + doc.getTimestamp("timestamp"));
                    }
                });

    }


    @SuppressLint("SetTextI18n")
    protected void dbTest(TextView test_text, String selectedChild){
        Log.i("DEBUG", "function initilize");

        db.collection("users").
                document("1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                   if (documentSnapshot.exists()) {
                       ParentModel user = documentSnapshot.toObject((ParentModel.class));
                       String name = documentSnapshot.getString("name");
                       String role = documentSnapshot.getString("role");

                       assert user != null;
                       test_text.setText("Name: " + user.getName() + "Role: " + user.getRole());

                       Log.i("DEBUG", "DEBUG NAME:" + name + "   " + role);
                   }
                })
                .addOnFailureListener(e ->{

                });

    }



}
