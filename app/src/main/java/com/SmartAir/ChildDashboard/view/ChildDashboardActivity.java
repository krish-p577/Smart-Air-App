package com.SmartAir.ChildDashboard.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.SmartAir.Badges.view.BadgesActivity;
import com.SmartAir.ChildDashboard.model.ChildDashboardRepository;
import com.SmartAir.ChildDashboard.presenter.ChildDashboardPresenter;
import com.SmartAir.Inventory.view.InventoryActivity;
import com.SmartAir.LogController.view.LogControllerActivity;
import com.SmartAir.LogRescue.view.LogRescueActivity;
import com.SmartAir.R;
import com.SmartAir.TechniqueHelper.view.TechniqueHelperActivity;
import com.SmartAir.glossary.view.GlossaryActivity;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.view.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;

public class ChildDashboardActivity extends AppCompatActivity implements ChildDashboardView, NavigationView.OnNavigationItemSelectedListener {

    private ChildDashboardPresenter presenter;
    private DrawerLayout drawerLayout;
    private boolean isDelegatedMode = false;
    private static final String FIRE_EMOJI = "\uD83D\uDD25";
    private static final String WAVE_EMOJI = "\uD83D\uDC4B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        Menu navMenu = navigationView.getMenu();
        MenuItem logoutItem = navMenu.findItem(R.id.nav_logout);
        navMenu.findItem(R.id.nav_child_login).setVisible(false); // Always hide for child view

        if (getIntent().hasExtra("CHILD_USER_PROFILE")) {
            isDelegatedMode = true;
            ChildUser child = (ChildUser) getIntent().getSerializableExtra("CHILD_USER_PROFILE");
            if (child != null && child.getDisplayName() != null) {
                // Assuming you have a TextView with id welcome_text in your activity_child_dashboard.xml
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

        ChildDashboardRepository repo = new ChildDashboardRepository();
        presenter = new ChildDashboardPresenter(this, repo);

        Button dailyCheckInButton = findViewById(R.id.btn_daily_check_in);
        dailyCheckInButton.setOnClickListener(v -> presenter.onDailyCheckInClicked());

        Button logControllerButton = findViewById(R.id.btn_log_controller);
        logControllerButton.setOnClickListener(v -> presenter.onLogControllerClicked());

        Button logRescueButton = findViewById(R.id.btn_log_rescue);
        logRescueButton.setOnClickListener(v -> presenter.onLogRescueClicked());

        Button pefEntryButton = findViewById(R.id.btn_pef_entry);
        pefEntryButton.setOnClickListener(v -> presenter.onPEFEntryClicked());

        Button triageButton = findViewById(R.id.btn_triage);
        triageButton.setOnClickListener(v -> presenter.onTriageClicked());

        Button practiceTechniqueButton = findViewById(R.id.btn_practice_technique);
        practiceTechniqueButton.setOnClickListener(v -> presenter.onPracticeTechniqueClicked());

        Button glossaryButton = findViewById(R.id.btn_glossary);
        glossaryButton.setOnClickListener(v -> presenter.onGlossaryClicked());

        Button updateInventoryButton = findViewById(R.id.btn_update_inventory);
        updateInventoryButton.setOnClickListener(v -> presenter.onUpdateInventoryClicked());

        Button viewBadgesButton = findViewById(R.id.btn_view_badges);
        viewBadgesButton.setOnClickListener(v -> presenter.onViewBadgesClicked());
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onScreenStart();
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

    @Override
    public void showWelcomeMessage(String name) {
        TextView welcomeMessage = findViewById(R.id.tv_welcome_message);

        welcomeMessage.setText("Hi, " + name + "! " + WAVE_EMOJI);
    }

    @Override
    public void showSecondaryMessage(String secondaryMessage) {
        TextView secondaryMessageText = findViewById(R.id.tv_secondary_message);

        secondaryMessageText.setText(secondaryMessage);
    }

    @Override
    public void showControllerStreak(int numDays) {
        TextView controllerStreak = findViewById(R.id.tv_controller_streak);

        if (numDays == 0) {
            controllerStreak.setText("Controller inhaler streak: " + numDays + " days");
        } else if (numDays < 5) {
            controllerStreak.setText("Controller inhaler streak: " + numDays + " days " + FIRE_EMOJI);
        } else if (numDays < 10) {
            controllerStreak.setText("Controller inhaler streak: " + numDays + " days " + FIRE_EMOJI + FIRE_EMOJI);
        } else {
            controllerStreak.setText("Controller inhaler streak: " + numDays + " days " + FIRE_EMOJI + FIRE_EMOJI + FIRE_EMOJI);
        }
    }

    @Override
    public void showTechniqueStreak(int numDays) {
        TextView techniqueStreak = findViewById(R.id.tv_technique_streak);

        if (numDays == 0) {
            techniqueStreak.setText("Technique streak: " + numDays + " days");
        } else if (numDays < 5) {
            techniqueStreak.setText("Technique streak: " + numDays + " days " + FIRE_EMOJI);
        } else if (numDays < 10) {
            techniqueStreak.setText("Technique streak: " + numDays + " days " + FIRE_EMOJI + FIRE_EMOJI);
        } else {
            techniqueStreak.setText("Technique streak: " + numDays + " days " + FIRE_EMOJI + FIRE_EMOJI + FIRE_EMOJI);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDailyCheckIn() {
        // TODO: wire to daily check in page
    }

    @Override
    public void showLogController() {
        Intent intent = new Intent(ChildDashboardActivity.this, LogControllerActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLogRescue() {
        Intent intent = new Intent(ChildDashboardActivity.this, LogRescueActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPEFEntry() {
        // TODO: wire to PEF entry page
    }

    @Override
    public void showTriage() {
        // TODO: wire to triage page
    }

    @Override
    public void showPracticeTechnique() {
        Intent intent = new Intent(ChildDashboardActivity.this, TechniqueHelperActivity.class);
        startActivity(intent);
    }

    @Override
    public void showGlossary() {
        Intent intent = new Intent(ChildDashboardActivity.this, GlossaryActivity.class);
        startActivity(intent);
    }

    @Override
    public void showUpdateInventory() {
        Intent intent = new Intent(ChildDashboardActivity.this, InventoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void showBadges() {
        Intent intent = new Intent(ChildDashboardActivity.this, BadgesActivity.class);
        startActivity(intent);
    }
}
