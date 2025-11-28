package com.SmartAir.homepage.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.SmartAir.ParentLink.view.SharingSettingsActivity;
import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;

public class ParentChildDetailActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private String childId;
    private ChildUser child;
    private Button generateInviteCodeButton;
    private Button revokeInviteCodeButton;
    private Button copyInviteCodeButton;
    private TextView inviteCodeDisplay;
    private LinearLayout inviteCodeLayout;
    private String activeInviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView childNameHeader = findViewById(R.id.child_name_header);
        Button manageSharingButton = findViewById(R.id.manage_sharing_button);
        generateInviteCodeButton = findViewById(R.id.generate_invite_code_button);
        revokeInviteCodeButton = findViewById(R.id.revoke_invite_code_button);
        copyInviteCodeButton = findViewById(R.id.copy_invite_code_button);
        inviteCodeDisplay = findViewById(R.id.invite_code_display);
        inviteCodeLayout = findViewById(R.id.invite_code_layout);

        inviteCodeDisplay.setTextIsSelectable(true);

        authRepository = AuthRepository.getInstance();
        childId = getIntent().getStringExtra("childId");

        if (childId == null || childId.isEmpty()) {
            Toast.makeText(this, "No child specified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadChildDetails();

        manageSharingButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentChildDetailActivity.this, SharingSettingsActivity.class);
            intent.putExtra("childId", childId);
            startActivity(intent);
        });

        generateInviteCodeButton.setOnClickListener(v -> generateInviteCode());
        revokeInviteCodeButton.setOnClickListener(v -> revokeInviteCode());
        copyInviteCodeButton.setOnClickListener(v -> copyInviteCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildDetails();
    }

    private void loadChildDetails() {
        authRepository.fetchChildProfile(childId, new AuthRepository.ChildProfileCallback() {
            @Override
            public void onSuccess(ChildUser fetchedChild) {
                child = fetchedChild;
                ((TextView) findViewById(R.id.child_name_header)).setText(child.getDisplayName() + "'s Dashboard");
                updateInviteCodeUI();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ParentChildDetailActivity.this, "Failed to load child data: " + errorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void updateInviteCodeUI() {
        authRepository.fetchActiveInviteCodeForChild(childId, new AuthRepository.InviteCodeCallback() {
            @Override
            public void onSuccess(String code) {
                activeInviteCode = code;
                inviteCodeDisplay.setText(code);
                generateInviteCodeButton.setVisibility(View.GONE);
                inviteCodeLayout.setVisibility(View.VISIBLE);
                revokeInviteCodeButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String errorMessage) {
                activeInviteCode = null;
                generateInviteCodeButton.setVisibility(View.VISIBLE);
                inviteCodeLayout.setVisibility(View.GONE);
                revokeInviteCodeButton.setVisibility(View.GONE);
            }
        });
    }

    private void generateInviteCode() {
        if (child == null) return;
        authRepository.generateInviteCode(child.getUid(), new AuthRepository.InviteCodeCallback() {
            @Override
            public void onSuccess(String code) {
                activeInviteCode = code;
                inviteCodeDisplay.setText(code);
                generateInviteCodeButton.setVisibility(View.GONE);
                inviteCodeLayout.setVisibility(View.VISIBLE);
                revokeInviteCodeButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ParentChildDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void revokeInviteCode() {
        if (activeInviteCode != null) {
            authRepository.revokeInviteCode(activeInviteCode, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess() {
                    updateInviteCodeUI();
                    Toast.makeText(ParentChildDetailActivity.this, "Invite code revoked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ParentChildDetailActivity.this, "Failed to revoke code: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void copyInviteCode() {
        if (activeInviteCode != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Invite Code", activeInviteCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Invite code copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
