package com.SmartAir.history.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;


import com.SmartAir.R;
import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.FilterDataModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter Dialog used in HistoryActivity that lets users filter their history.
 * This communicates the chosen filter back to HistoryActivity by forwarding filter to
 * HistoryPresenter then receiving the updated results through a callback.
 */
public class FilterDialog implements HistoryContract.FilterDialog{

    private final Context context;
    private final HistoryContract.Presenter presenter;

    public FilterDialog(Context context, HistoryContract.Presenter presenter){
        this.context = context;
        this.presenter = presenter;
    }

    public void show(){

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_history_filter, null);

        ChipGroup chipGroupNightWaking = dialogView.findViewById(R.id.chipGroupNightWaking);
        ChipGroup chipGroupLimitedAbility = dialogView.findViewById(R.id.chipGroupLimitedAbility);
        ChipGroup chipGroupSick = dialogView.findViewById(R.id.chipGroupSick);

        EditText editStartDate = dialogView.findViewById(R.id.editStartDate);
        EditText editEndDate = dialogView.findViewById(R.id.editEndDate);

        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate));
        ChipGroup chipGroupTriggers = dialogView.findViewById(R.id.chipGroupTriggers);


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Apply Filter", (d, which) -> {

                    int id = chipGroupNightWaking.getCheckedChipId();
                    Boolean nightWaking = null;
                    if (id == R.id.chipNightWakingTrue)  nightWaking = Boolean.TRUE;
                    else if (id == R.id.chipNightWakingFalse) nightWaking = Boolean.FALSE;

                    int id2 = chipGroupLimitedAbility.getCheckedChipId();
                    Boolean limitedAbility = null;
                    if (id2 == R.id.chipLimitedAbilityTrue)  limitedAbility = Boolean.TRUE;
                    else if (id2 == R.id.chipLimitedAbilityFalse) limitedAbility = Boolean.FALSE;

                    int id3 = chipGroupSick.getCheckedChipId();
                    Boolean sick = null;
                    if (id3 == R.id.chipSickTrue)  sick = Boolean.TRUE;
                    else if (id3 == R.id.chipSickFalse) sick = Boolean.FALSE;

                    String startDate = editStartDate.getText().toString().trim();
                    String endDate = editEndDate.getText().toString().trim();
                    if (startDate.isEmpty()) startDate = null;
                    if (endDate.isEmpty()) endDate = null;

                    List<String> triggers = new ArrayList<>();
                    for (int i = 0; i < chipGroupTriggers.getChildCount(); i++) {
                        View child = chipGroupTriggers.getChildAt(i);

                        if (child instanceof Chip) {
                            Chip chip = (Chip) child;

                            if (chip.isChecked()) {
                                triggers.add(chip.getText().toString());
                            }
                        }
                    }

                    FilterDataModel filter = new FilterDataModel(
                            nightWaking,
                            limitedAbility,
                            sick,
                            startDate,
                            endDate,
                            triggers
                    );

                    presenter.loadData(filter);

                    if (filter.isInvalidInput()){
                        Toast.makeText(context, "Invalid date Input, Date's " +
                                        "changed to (6 months ago - Today)",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();
        dialog.show();
    }

    private void showDatePicker(EditText targetField) {
        LocalDate today = LocalDate.now();

        DatePickerDialog picker = new DatePickerDialog(
                context,
                (view, year, month, day) -> {
                    String formatted = String.format("%04d-%02d-%02d", year, month + 1, day);
                    targetField.setText(formatted);
                },
                today.getYear(),
                today.getMonthValue() - 1,
                today.getDayOfMonth()
        );

        picker.show();
    }
}
