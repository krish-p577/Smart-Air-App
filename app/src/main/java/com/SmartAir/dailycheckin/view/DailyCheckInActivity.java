package com.SmartAir.dailycheckin.view;
import com.SmartAir.R;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.presenter.DailyCheckInPresenter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.SmartAir.onboarding.model.CurrentUser;
import android.os.Handler;

import java.util.ArrayList;

import android.widget.Toast;
import android.os.Bundle;

public class DailyCheckInActivity extends AppCompatActivity implements DailyCheckInContract.View {

    private Button submitBtn;
    private Button exitBtn;

    private CheckBox nightWakingCheckBox;
    private CheckBox limitedAbilityCheckBox;
    private CheckBox sickCheckBox;
    private ChipGroup triggerChipGroup;

    DailyCheckInContract.Presenter presenter;
    CurrentUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_check_in);

        user = CurrentUser.getInstance(); // Get current user

        if (user.getRole().equals("Parent")){
            // TODO: implement parent specialized view
            // TODO: Parent can choose which child to submit daily activity
        }
        else{
            // TODO: implement child specialized view
        }

        presenter = new DailyCheckInPresenter(this);

        exitBtn = findViewById(R.id.dailyCheckInExitBtn);
        submitBtn = findViewById(R.id.dailyCheckInSubmitBtn);
        nightWakingCheckBox = findViewById(R.id.dailyCheckInNightWakingChkBox);
        limitedAbilityCheckBox = findViewById(R.id.dailyCheckInLimitActivityChkBox);
        sickCheckBox = findViewById(R.id.dailyCheckInSickChkBox);
        triggerChipGroup = findViewById(R.id.dailyCheckInTriggerChipGroup);

        submitBtn.setOnClickListener(v -> {
            SubmitDataToPresenter();
        });

        exitBtn.setOnClickListener(v ->{
            finish();
        });
    }

    public void SubmitDataToPresenter(){
        boolean isNightWalking, hasLimitedAbility, isSick;
        String content;
        String role;

        isNightWalking = nightWakingCheckBox.isChecked();
        hasLimitedAbility = limitedAbilityCheckBox.isChecked();
        isSick = sickCheckBox.isChecked();
        role = user.getRole();
        ArrayList<String> triggers = new ArrayList<>();

        for (int id: triggerChipGroup.getCheckedChipIds()) {
            Chip chip = triggerChipGroup.findViewById(id);

            if (chip == null) {
                continue;
            }

            content = chip.getText().toString();

            if (!content.isBlank()) {
                triggers.add(chip.getText().toString());
            }

        }

        presenter.submitDailyCheckIn(role, isNightWalking,hasLimitedAbility,isSick,triggers);
    }

    @Override
    public void showSubmitSuccess(){
        Toast.makeText(this, "Saved successfully! Returning to Dashboard...",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> finish(), 1500);
    }

    @Override
    public void showSubmitFailure(){
        Toast.makeText(this, "Failed to save! Returning to Dashboard...",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> finish(), 1500);
    }

}
