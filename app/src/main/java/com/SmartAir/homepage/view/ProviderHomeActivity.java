package com.SmartAir.homepage.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.view.WelcomeActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class ProviderHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LinkedChildrenAdapter.OnChildClickListener {

    private DrawerLayout drawerLayout;
    private AuthRepository authRepository;
    private RecyclerView linkedChildrenRecyclerView;
    private LinkedChildrenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        authRepository = AuthRepository.getInstance();

        // Ensure the correct menu items are visible for providers
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_dashboard).setVisible(false);
        navMenu.findItem(R.id.nav_manage_children).setVisible(false);
        navMenu.findItem(R.id.nav_child_login).setVisible(false);
        navMenu.findItem(R.id.nav_logout).setVisible(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Button enterInviteCodeButton = findViewById(R.id.enter_invite_code_button);
        enterInviteCodeButton.setOnClickListener(v -> showInviteCodeDialog());

        linkedChildrenRecyclerView = findViewById(R.id.linked_children_recycler_view);
        linkedChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Handle the back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });

        loadLinkedChildren();
    }

    private void loadLinkedChildren() {
        String providerId = authRepository.getCurrentFirebaseUser().getUid();
        authRepository.fetchLinkedChildrenForProvider(providerId, new AuthRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<ChildUser> children) {
                adapter = new LinkedChildrenAdapter(children, ProviderHomeActivity.this);
                linkedChildrenRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProviderHomeActivity.this, "Failed to load children: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInviteCodeDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Enter Invite Code")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String code = input.getText().toString().trim();
                    if (!code.isEmpty()) {
                        linkProviderToChild(code);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void linkProviderToChild(String code) {
        authRepository.linkProviderToChild(code, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProviderHomeActivity.this, "Successfully linked to child.", Toast.LENGTH_SHORT).show();
                loadLinkedChildren(); // Refresh the list
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProviderHomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            AuthRepository.getInstance().logout();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChildClicked(ChildUser child) {
        Intent intent = new Intent(this, com.SmartAir.homepage.view.ProviderChildDetailActivity.class);
        intent.putExtra("childId", child.getUid());
        startActivity(intent);
    }
}
