package com.SmartAir.homepage.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProviderChildDetailActivity extends AppCompatActivity {

    private TextView childNameTextView;
    private RecyclerView sharedDataRecyclerView;
    private AuthRepository authRepository;
    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_child_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        childNameTextView = findViewById(R.id.child_name_text_view);
        sharedDataRecyclerView = findViewById(R.id.shared_data_recycler_view);
        sharedDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        authRepository = AuthRepository.getInstance();
        childId = getIntent().getStringExtra("childId");

        if (childId == null || childId.isEmpty()) {
            Toast.makeText(this, "No child ID provided.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadChildDetails();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void loadChildDetails() {
        authRepository.fetchChildProfile(childId, new AuthRepository.ChildProfileCallback() {
            @Override
            public void onSuccess(ChildUser child) {
                childNameTextView.setText(getString(R.string.provider_child_detail_title, child.getDisplayName()));

                List<String> sharedItems = new ArrayList<>();
                if (child.getSharingSettings() != null) {
                    for (Map.Entry<String, Boolean> entry : child.getSharingSettings().entrySet()) {
                        if (entry.getValue()) { // If the sharing setting is true
                            sharedItems.add(entry.getKey());
                        }
                    }
                }

                ProviderSharedDataAdapter adapter = new ProviderSharedDataAdapter(sharedItems);
                sharedDataRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProviderChildDetailActivity.this, "Failed to load child details: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
