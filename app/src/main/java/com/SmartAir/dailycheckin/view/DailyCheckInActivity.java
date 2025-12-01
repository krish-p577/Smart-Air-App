package com.SmartAir.dailycheckin.view;
import com.SmartAir.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.CheckBox;
import com.SmartAir.dailycheckin.DailyCheckInContract;
import com.SmartAir.dailycheckin.presenter.DailyCheckInPresenter;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.onboarding.model.ParentUser;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import com.SmartAir.onboarding.model.CurrentUser;
import android.os.Handler;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

/**
 * Activity that displays and handles the Daily Check-In form.
 * Behavior depends on the current user's role:
 * <ul>
 *     <li><b>Parent</b>: can select a child from a spinner and submit a check-in on their
 *     behalf.</li>
 *     <li><b>Child</b>: sees a personalized welcome message and submits their own check-in.
 *     </li>
 * </ul>
 * The Activity collects symptom and trigger data, then delegates validation and saving to
 * DailyCheckInPresenter.
 */
public class DailyCheckInActivity extends AppCompatActivity implements DailyCheckInContract.View {

    private Button submitBtn;
    private ImageButton exitBtn;
    private TextView roleText;
    private TextView statusText;
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

        user = CurrentUser.getInstance();   // get current user
        presenter = new DailyCheckInPresenter(this);

        exitBtn = findViewById(R.id.dailyCheckInExitBtn);
        submitBtn = findViewById(R.id.dailyCheckInSubmitBtn);
        nightWakingCheckBox = findViewById(R.id.dailyCheckInNightWakingChkBox);
        limitedAbilityCheckBox = findViewById(R.id.dailyCheckInLimitActivityChkBox);
        sickCheckBox = findViewById(R.id.dailyCheckInSickChkBox);
        triggerChipGroup = findViewById(R.id.dailyCheckInTriggerChipGroup);
        roleText = findViewById(R.id.roleText);
        childSelector = findViewById(R.id.childSpinner);
        statusText = findViewById(R.id.titleStatusReport);

        if (user.getRole().equals("parent")){
            // Parent view
            roleText.setText("Select Child:");
            presenter.loadChildren(user.getUid());
        }
        else if (user.getRole().equals("child")){
            // Child view
            roleText.setText("Welcome, " + user.getUserProfile().getDisplayName() + "!");
            childSelector.setVisibility(View.GONE);

            // change layout for child
            setupForChildLayout();

            // check if child has already submit daily check-in for today
            presenter.checkIfCanSubmit(user.getUserProfile().getDisplayName());
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
        String childName = "failedToGetName"; // safety net
        String parentId = "failedToGetParentId"; // safety net

        if (user.getRole().equals("child")){
            childName = user.getUserProfile().getDisplayName();
            if (user.getUserProfile() instanceof ChildUser) {
                parentId = ((ChildUser) user.getUserProfile()).getParentId();
            }
        }
        else if (user.getRole().equals("parent")){
            childName = childSelector.getSelectedItem().toString();
            if (user.getUserProfile() instanceof ParentUser) {
                parentId = user.getUid();
            }
        }
        else{
            Toast.makeText(this, "ERROR: Cannot identify user role.",
                    Toast.LENGTH_SHORT).show();
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

    @Override
    public void showSpinnerData(List<String> childrenNames) {
        if (childrenNames.isEmpty()) {
            childrenNames.add("None Remaining for today");
            Toast.makeText(this, "All Children have Submitted their Daily-Check-Ins!",
                    Toast.LENGTH_SHORT).show();
            submitBtn.setEnabled(false); // disable submitting data
        } else {
            childSelector.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_row,
                childrenNames
        );
        adapter.setDropDownViewResource(R.layout.spinner_row);
        childSelector.setAdapter(adapter);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAlreadySubmitted(){
        submitBtn.setEnabled(false);
        Toast.makeText(this, "Already Submit Daily-Check-in! Returning Home....",
                Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> finish(), 3000);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupForChildLayout() {
        childSelector.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams statusParams =
                (ConstraintLayout.LayoutParams) statusText.getLayoutParams();

        statusParams.topToBottom = R.id.roleText;
        statusParams.topMargin = dpToPx(16);

        statusText.setLayoutParams(statusParams);
    }

}
