package com.SmartAir.ParentLink.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class SharingSettingsActivity extends AppCompatActivity implements SharingSettingsAdapter.OnSharingOptionToggleListener {

    private RecyclerView recyclerView;
    private Button revokeAllButton;
    private TextView childNameTextView;
    private AuthRepository authRepository;
    private ChildUser child;
    private SharingSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.sharing_options_recycler_view);
        revokeAllButton = findViewById(R.id.revoke_all_button);
        childNameTextView = findViewById(R.id.child_name_text_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        authRepository = AuthRepository.getInstance();
        String childId = getIntent().getStringExtra("childId");

        if (childId == null || childId.isEmpty()) {
            Toast.makeText(this, "No child specified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        authRepository.fetchChildProfile(childId, new AuthRepository.ChildProfileCallback() {
            @Override
            public void onSuccess(ChildUser fetchedChild) {
                child = fetchedChild;
                childNameTextView.setText("Sharing Settings for " + child.getDisplayName());
                List<String> sharingOptions = getSharingOptions();
                adapter = new SharingSettingsAdapter(sharingOptions, child.getSharingSettings(), SharingSettingsActivity.this);
                recyclerView.setAdapter(adapter);
                updateRevokeButtonState();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(SharingSettingsActivity.this, "Failed to load child data: " + errorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        revokeAllButton.setOnClickListener(v -> showRevokeAllConfirmationDialog());
    }

    private void updateRevokeButtonState() {
        boolean hasPermissions = false;
        if (child != null && child.getSharingSettings() != null) {
            for (Boolean granted : child.getSharingSettings().values()) {
                if (granted) {
                    hasPermissions = true;
                    break;
                }
            }
        }
        revokeAllButton.setEnabled(hasPermissions);
    }

    private void showRevokeAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Revoke All Permissions")
                .setMessage("Are you sure you want to revoke all permissions and unlink this child from all providers? This action cannot be undone.")
                .setPositiveButton("Yes, Revoke All", (dialog, which) -> {
                    revokeAllPermissionsAndUnlink();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void revokeAllPermissionsAndUnlink() {
        if (child == null) return;
        authRepository.revokeAllPermissionsAndUnlink(child.getUid(), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SharingSettingsActivity.this, "All permissions revoked and child unlinked.", Toast.LENGTH_LONG).show();
                // Optionally, refresh the UI or finish the activity
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(SharingSettingsActivity.this, "Failed to revoke permissions: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getSharingOptions() {
        List<String> options = new ArrayList<>();
        options.add("Rescue logs");
        options.add("Controller adherence summary");
        options.add("Symptoms");
        options.add("Triggers");
        options.add("Peak-flow (PEF)");
        options.add("Triage incidents");
        options.add("Summary charts");
        return options;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharingOptionToggled(String option, boolean isEnabled) {
        if (child == null) return;

        Map<String, Boolean> sharingSettings = child.getSharingSettings();
        sharingSettings.put(option, isEnabled);

        authRepository.updateSharingSettings(child.getUid(), sharingSettings, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                child.setSharingSettings(sharingSettings); // Keep local model in sync
                String status = isEnabled ? "enabled" : "disabled";
                Toast.makeText(SharingSettingsActivity.this, "Sharing for " + option + " is now " + status, Toast.LENGTH_SHORT).show();
                updateRevokeButtonState();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(SharingSettingsActivity.this, "Failed to update setting: " + errorMessage, Toast.LENGTH_SHORT).show();
                // Revert the toggle state in the UI
                sharingSettings.put(option, !isEnabled);
                child.setSharingSettings(sharingSettings);
                int position = getSharingOptions().indexOf(option);
                if (position != -1) {
                    adapter.notifyItemChanged(position);
                }
                updateRevokeButtonState();
            }
        });
    }
}
