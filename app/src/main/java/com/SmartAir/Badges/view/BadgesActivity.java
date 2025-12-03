package com.SmartAir.Badges.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.Badges.model.BadgesRepository;
import com.SmartAir.Badges.presenter.BadgesPresenter;
import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BadgesActivity extends AppCompatActivity implements BadgesView {

    private BadgesPresenter presenter;

    private BadgesRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        repo = new BadgesRepository();
        presenter = new BadgesPresenter(this, repo);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> presenter.onBackClicked());
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onScreenStart();
    }

    @Override
    public void showChildDashboard() {
        Intent intent = new Intent(BadgesActivity.this, ChildDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showBadges(boolean hasControllerBadge, boolean hasTechniqueBadge, boolean hasLowRescueBadge, Map<String, Date> badges, int minControllerThreshold, int minTechniqueThreshold, int maxRescueDaysThreshold) {
        ImageView controllerBadge = findViewById(R.id.img_controller_badge);
        ImageView techniqueBadge = findViewById(R.id.img_technique_badge);
        ImageView lowRescueBadge = findViewById(R.id.img_low_rescue_badge);

        TextView controllerBadgeDesc = findViewById(R.id.tv_controller_badge_desc);
        TextView techniqueBadgeDesc = findViewById(R.id.tv_technique_badge_desc);
        TextView lowRescueBadgeDesc = findViewById(R.id.tv_low_rescue_badge_desc);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        controllerBadgeDesc.setText("Awarded for a streak of " + minControllerThreshold + " controller days.");
        techniqueBadgeDesc.setText("Awarded for your first " + minTechniqueThreshold + " perfect technique sessions.");
        lowRescueBadgeDesc.setText("Awarded for having less than " + maxRescueDaysThreshold + "  rescue days in a single month.");


        if (hasControllerBadge) {
            controllerBadge.setImageResource(R.drawable.controller_champion_badge);

            Date controllerBadgeEarnedAt = badges.get("perfect_controller_week_badge");

            if (controllerBadgeEarnedAt != null) {
                controllerBadgeDesc.setText("Awarded for a streak of " + minControllerThreshold + " controller days. Earned on " + dateFormat.format(controllerBadgeEarnedAt));
            }
        } else {
            controllerBadge.setImageResource(R.drawable.locked_badge);
        }

        if (hasTechniqueBadge) {
            techniqueBadge.setImageResource(R.drawable.technique_master_badge);

            Date techniqueBadgeEarnedAt = badges.get("high_quality_technique_sessions_badge");

            if (techniqueBadgeEarnedAt != null) {
                techniqueBadgeDesc.setText("Awarded for your first " + minTechniqueThreshold + " perfect technique sessions. Earned on " + dateFormat.format(techniqueBadgeEarnedAt));
            }
        } else {
            techniqueBadge.setImageResource(R.drawable.locked_badge);
        }

        if (hasLowRescueBadge) {
            lowRescueBadge.setImageResource(R.drawable.low_rescue_legend_badge);

            Date lowRescueBadgeEarnedAt = badges.get("low_rescue_badge");

            if (lowRescueBadgeEarnedAt != null) {
                lowRescueBadgeDesc.setText("Awarded for having less than " + maxRescueDaysThreshold + "  rescue days in a single month. Earned on " + dateFormat.format(lowRescueBadgeEarnedAt));
            }
        } else {
            lowRescueBadge.setImageResource(R.drawable.locked_badge);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
