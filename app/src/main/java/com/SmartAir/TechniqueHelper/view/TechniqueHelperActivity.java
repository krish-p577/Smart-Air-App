package com.SmartAir.TechniqueHelper.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.SmartAir.ChildDashboard.view.ChildDashboardActivity;
import com.SmartAir.R;
import com.SmartAir.TechniqueHelper.model.TechniqueHelperRepository;
import com.SmartAir.TechniqueHelper.presenter.TechniqueHelperPresenter;

public class TechniqueHelperActivity extends AppCompatActivity implements TechniqueHelperView, Player.Listener {

    private TechniqueHelperPresenter presenter;

    private ExoPlayer player;

    private final long[] PAUSE_TIMESTAMPS_MS = {
            13000, // Check dose count
            36000, // Prime inhaler if needed
            40000, // Shake inhaler
            46000, // Ensure mouthpiece is clean
            52000, // Ensure standing/sitting and breathe out
            67000, // Pointer finger on top of inhaler, head tilted back, mouthpiece in mouth
            88000, // Breathe in while pressing inhaler and hold breath for 10s
            91000, // Slowly breathe out through mouth
            95000, // Wait 1 minute and repeat for each puff
            98000, // Rinse mouth with water
            102000 // Place cap back on inhaler and store in cool, dry place
    };
    private int pauseIndex = 0;
    private boolean isChecking = false;
    private boolean isPerfectSession = true;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technique_helper);

        TechniqueHelperRepository repo = new TechniqueHelperRepository();
        presenter = new TechniqueHelperPresenter(this, repo);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> presenter.onBackClicked());

        PlayerView playerView = findViewById(R.id.player_view);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.technique_helper_video);
        player.setMediaItem(mediaItem);
        player.prepare();

        player.addListener(this);

        player.play();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            if (!isChecking) {
                isChecking = true;
                checkPauseNeeded();
            }
        } else {
            isChecking = false;
        }
    }

    private void checkPauseNeeded() {
        handler.postDelayed(() -> {
            if (!isChecking || player == null || pauseIndex >= PAUSE_TIMESTAMPS_MS.length) {
                return;
            }

            long currentTimestamp = player.getCurrentPosition();
            long pauseTimestamp = PAUSE_TIMESTAMPS_MS[pauseIndex];

            if (currentTimestamp >= pauseTimestamp) {
                player.pause();
                isChecking = false;
                showTechniqueHelperDialog(pauseIndex);
            } else {
                checkPauseNeeded();
            }
        }, 200);
    }

    private void showTechniqueHelperDialog(int currentPauseIndex) {
        String currentStepMessage;

        switch (currentPauseIndex) {
            case 0:
                currentStepMessage = "Check if the dose count is greater than 0?";
                break;
            case 1:
                currentStepMessage = "Prime your inhaler if needed? If your inhaler does not need to be primed, click yes.";
                break;
            case 2:
                currentStepMessage = "Shake your inhaler?";
                break;
            case 3:
                currentStepMessage = "Make sure the inhaler's mouthpiece is clean?";
                break;
            case 4:
                currentStepMessage = "Make sure you are standing or sitting up straight?";
                break;
            case 5:
                currentStepMessage = "Place your index finger on the top of the inhaler, tilt your head back, and place the mouthpiece in your mouth?";
                break;
            case 6:
                currentStepMessage = "Breathe in while pressing inhaler and hold your breath for 10 seconds?";
                break;
            case 7:
                currentStepMessage = "Slowly breathe out through your mouth?";
                break;
            case 8:
                currentStepMessage = "Wait 1 minute and repeat the previous steps for each puff? If you are only doing one puff, click yes.";
                break;
            case 9:
                currentStepMessage = "Rinse your mouth with water?";
                break;
            case 10:
                currentStepMessage = "Put the cap back on the inhaler and put it somewhere cool and dry?";
                break;
            default:
                currentStepMessage = "Do the steps shown in the video?";
        }

        new AlertDialog.Builder(this)
            .setTitle("Did you...")
            .setMessage(currentStepMessage)
            .setPositiveButton("Yes", (dialog, which) -> {
                pauseIndex++;
                playAndContinueChecking();
            }).setNegativeButton("No", (dialog, which) -> {
                pauseIndex++;
                isPerfectSession = false;
                playAndContinueChecking();
            }).setCancelable(false)
            .show();
    }

    private void playAndContinueChecking() {
        if (player == null) {
            return;
        }

        if (pauseIndex < PAUSE_TIMESTAMPS_MS.length) {
            isChecking = true;
            checkPauseNeeded();
        } else {
            isChecking = false;

            if (isPerfectSession) {
                presenter.onPerfectSessionCompleted();
            }
        }

        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();

        isChecking = false;

        handler.removeCallbacksAndMessages(null);

        if (player != null) {
            player.removeListener(this);
            player.release();
            player = null;
        }
    }

    @Override
    public void showChildDashboard() {
        Intent intent = new Intent(TechniqueHelperActivity.this, ChildDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
