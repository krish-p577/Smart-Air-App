package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.SmartAir.ParentDashboard.view.ParentDashboardActivity;
import com.SmartAir.R;
import com.SmartAir.onboarding.view.AddChildActivity;
import com.SmartAir.onboarding.model.AuthRepository;
import com.google.android.material.navigation.NavigationView;

public class ParentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Ensure the correct menu items are visible for parents
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.nav_child_login).setVisible(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Button addChildButton = findViewById(R.id.add_child_button);
        addChildButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddChildActivity.class));
        });

        Button dashboard_button = findViewById(R.id.dashboard_button);
        dashboard_button.setOnClickListener(v -> {
            startActivity(new Intent(this, ParentDashboardActivity.class));
        });

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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
