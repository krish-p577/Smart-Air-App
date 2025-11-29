package com.SmartAir.dailycheckin.view;
import com.SmartAir.R;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.presenter.DailyCheckInPresenter;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.model.ParentUser;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.SmartAir.onboarding.model.CurrentUser;
import android.os.Handler;

import java.util.ArrayList;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

public class DailyCheckInActivity extends AppCompatActivity implements DailyCheckInContract.View {

    private Button submitBtn;
    private Button exitBtn;
    private TextView roleText;
    private Spinner childSelector;

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

        // get current user
        user = CurrentUser.getInstance();
        presenter = new DailyCheckInPresenter(this);

        exitBtn = findViewById(R.id.dailyCheckInExitBtn);
        submitBtn = findViewById(R.id.dailyCheckInSubmitBtn);
        nightWakingCheckBox = findViewById(R.id.dailyCheckInNightWakingChkBox);
        limitedAbilityCheckBox = findViewById(R.id.dailyCheckInLimitActivityChkBox);
        sickCheckBox = findViewById(R.id.dailyCheckInSickChkBox);
        triggerChipGroup = findViewById(R.id.dailyCheckInTriggerChipGroup);
        roleText = findViewById(R.id.roleText);
        childSelector = findViewById(R.id.childSpinner);

        if (user.getRole().equals("parent")){
            // TODO: implement parent specialized view
            // TODO: Parent can choose which child to submit daily activity
            roleText.setText("Select Child:");
        }
        else if (user.getRole().equals("child")){
            roleText.setText("Welcome, " + user.getUserProfile().getDisplayName() + "!");
            childSelector.setVisibility(View.GONE);
        }

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
        String childName = "failedToGetName";
        String parentId = "failedToGetParentId";

        // TODO: Make functionality to get childName from spinner when parent

        if (user.getRole().equals("child")){
            childName = user.getUserProfile().getDisplayName();
            if (user.getUserProfile() instanceof ChildUser) {
                parentId = ((ChildUser) user.getUserProfile()).getParentId();
            }
        }
        else if (user.getRole().equals("parent")){

        }

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

        presenter.submitDailyCheckIn(role, childName, parentId, isNightWalking, hasLimitedAbility,
                isSick, triggers);
    }

    @Override
    public void showSubmitSuccess(){
        submitBtn.setEnabled(false);
        Toast.makeText(this, "Saved successfully! Returning to Dashboard...",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> finish(), 1500);
    }

    @Override
    public void showSubmitFailure(){
        submitBtn.setEnabled(false);
        Toast.makeText(this, "Failed to save! Returning to Dashboard...",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> finish(), 1500);
    }

}
