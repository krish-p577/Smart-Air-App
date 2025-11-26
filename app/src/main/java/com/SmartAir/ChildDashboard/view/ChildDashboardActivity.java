package com.SmartAir.ChildDashboard.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ChildDashboard.data.ChildDashboardRepository;
import com.SmartAir.ChildDashboard.presenter.ChildDashboardPresenter;
import com.SmartAir.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChildDashboardActivity extends AppCompatActivity implements ChildDashboardView {

    private ChildDashboardPresenter presenter;
    private static final String FIRE_EMOJI = "\uD83D\uDD25";
    private static final String WAVE_EMOJI = "\uD83D\uDC4B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_dashboard);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ChildDashboardRepository repo = new ChildDashboardRepository(db);
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
    public void showControllerInventoryStatus(String controllerStatus) {
        TextView controllerInventoryStatus = findViewById(R.id.tv_controller_inventory_status);

        controllerInventoryStatus.setText(controllerStatus);
    }

    @Override
    public void showRescueInventoryStatus(String rescueStatus) {
        TextView rescueInventoryStatus = findViewById(R.id.tv_rescue_inventory_status);

        rescueInventoryStatus.setText(rescueStatus);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
