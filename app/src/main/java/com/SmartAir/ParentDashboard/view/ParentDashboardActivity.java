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
import com.SmartAir.ParentDashboard.presenter.ParentDashboardPresenter;
import com.SmartAir.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                childList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getUserChildren(adapter, childList);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedChild = childList.get(i);
                dbTest(box1,selectedChild);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }


    @SuppressLint("SetTextI18n")
    protected void getUserChildren(ArrayAdapter<String> adapter, List<String> childList){
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
                       }
                   }

                   adapter.notifyDataSetChanged();
                   Log.i("SPINNER TAG", "Loaded Children" + childList);

                }).addOnFailureListener(e ->{
                    Log.e("SPINNER FAILUE", "ERR", e);
                });

    }

//    @SuppressLint("SetTextI18n")
//    protected void updateDashboard(String selectedChild, TextView box1){
//        Log.i("DEBUG", "function initilize");
//
//        db.collection("users").
//                document("1")
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        ParentModel user = documentSnapshot.toObject((ParentModel.class));
//                        String name = documentSnapshot.getString("name");
//                        String role = documentSnapshot.getString("role");
//
//                        assert user != null;
//                        test_text.setText("Name: " + user.getName() + "Role: " + user.getRole());
//
//                        Log.i("DEBUG", "DEBUG NAME:" + name + "   " + role);
//                    }
//                })
//                .addOnFailureListener(e ->{
//
//                });
//
//    }


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
