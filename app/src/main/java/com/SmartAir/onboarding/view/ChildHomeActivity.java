package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.google.android.material.navigation.NavigationView;

public class ChildHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private boolean isDelegatedMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu navMenu = navigationView.getMenu();
        MenuItem logoutItem = navMenu.findItem(R.id.nav_logout);
        navMenu.findItem(R.id.nav_child_login).setVisible(false); // Always hide for child view

        if (getIntent().hasExtra("CHILD_USER_PROFILE")) {
            isDelegatedMode = true;
            ChildUser child = (ChildUser) getIntent().getSerializableExtra("CHILD_USER_PROFILE");
            if (child != null && child.getDisplayName() != null) {
                // Assuming you have a TextView with id welcome_text in your activity_child_home.xml
                TextView welcomeText = findViewById(R.id.welcome_text);
                if(welcomeText != null) {
                    welcomeText.setText("Viewing as " + child.getDisplayName());
                }
            }
            logoutItem.setTitle("Exit Child View");
        } else {
            logoutItem.setTitle("Logout");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            if (isDelegatedMode) {
                finish(); // Simply close the activity to return to the parent's view
            } else {
                AuthRepository.getInstance().logout();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
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
