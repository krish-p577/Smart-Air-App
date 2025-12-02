package com.SmartAir.homepage.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.ParentDashboard.view.ParentDashboardActivity;
import com.SmartAir.ParentLink.view.ManageChildrenActivity;
import com.SmartAir.R;
import com.SmartAir.glossary.view.GlossaryActivity;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.view.SelectChildLoginActivity;
import com.SmartAir.onboarding.view.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ParentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView parentDashboardRecyclerView;
    private ParentDashboardAdapter adapter;
    private AuthRepository authRepository;
    private final List<ChildUser> childrenList = new ArrayList<>();
    private boolean isTestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        isTestMode = getIntent().getBooleanExtra("testMode", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        parentDashboardRecyclerView = findViewById(R.id.parent_dashboard_recycler_view);
        parentDashboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParentDashboardAdapter(childrenList);
        parentDashboardRecyclerView.setAdapter(adapter);

        authRepository = AuthRepository.getInstance();

        // Setup Menu
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_manage_children).setVisible(true);
        navMenu.findItem(R.id.nav_child_login).setVisible(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenForChildrenUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ListenerRegistration childrenListener = (ListenerRegistration) parentDashboardRecyclerView.getTag();
        if (childrenListener != null) {
            childrenListener.remove();
        }
    }

    private void listenForChildrenUpdates() {
        String uid = isTestMode ? "voS60SSmSSZL9j3XGKyhHNSs4LR2" : authRepository.getCurrentFirebaseUser().getUid();
        ListenerRegistration childrenListener = authRepository.listenForChildrenForParent(uid, new AuthRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<ChildUser> children) {
                int oldSize = childrenList.size();
                childrenList.clear();
                childrenList.addAll(children);
                int newSize = childrenList.size();
                if (oldSize > newSize) {
                    adapter.notifyItemRangeRemoved(newSize, oldSize - newSize);
                }
                adapter.notifyItemRangeChanged(0, newSize);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error
            }
        });
        parentDashboardRecyclerView.setTag(childrenListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_logout) {
            AuthRepository.getInstance().logout();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.nav_child_login) {
            startActivity(new Intent(this, SelectChildLoginActivity.class));
        } else if (itemId == R.id.nav_manage_children) {
            startActivity(new Intent(this, ManageChildrenActivity.class));
        } else if (itemId == R.id.nav_dashboard) {
            Intent intent = new Intent(this, ParentDashboardActivity.class);
            intent.putExtra("testMode", isTestMode);
            startActivity(intent);
        } else if (itemId == R.id.nav_glossary) {
            startActivity(new Intent(this, GlossaryActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
