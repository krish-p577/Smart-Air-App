package com.SmartAir.Badges.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.Badges.model.BadgesRepository;
import com.SmartAir.Badges.presenter.BadgesPresenter;
import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.R;

public class BadgesActivity extends AppCompatActivity implements BadgesView {

    private BadgesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        BadgesRepository repo = new BadgesRepository();
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
    }

    @Override
    public void showBadges(boolean hasControllerBadge, boolean hasTechniqueBadge, boolean hasLowRescueBadge) {
        ImageView controllerBadge = findViewById(R.id.img_controller_badge);
        ImageView techniqueBadge = findViewById(R.id.img_technique_badge);
        ImageView lowRescueBadge = findViewById(R.id.img_low_rescue_badge);

        if (hasControllerBadge) {
            controllerBadge.setImageResource(R.drawable.controller_champion_badge);
        } else {
            controllerBadge.setImageResource(R.drawable.locked_badge);
        }

        if (hasTechniqueBadge) {
            techniqueBadge.setImageResource(R.drawable.technique_master_badge);
        } else {
            techniqueBadge.setImageResource(R.drawable.locked_badge);
        }

        if (hasLowRescueBadge) {
            lowRescueBadge.setImageResource(R.drawable.low_rescue_legend_badge);
        } else {
            lowRescueBadge.setImageResource(R.drawable.locked_badge);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
