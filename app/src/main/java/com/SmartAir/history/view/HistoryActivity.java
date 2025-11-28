package com.SmartAir.history.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.history.HistoryContract;
import com.SmartAir.history.presenter.FilterDataModel;
import com.SmartAir.history.presenter.HistoryItem;
import com.SmartAir.history.presenter.HistoryPresenter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity  implements HistoryContract.View{
    private RecyclerView recyclerView;
    private HistoryContract.Presenter presenter;
    private HistoryContract.Adapter adapter;
    private ImageButton exitBtn;
    private Button filterBtn;
    private CircularProgressIndicator loading;

    private FilterDataModel filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loading = findViewById(R.id.historyLoading);
        filterBtn = findViewById(R.id.filterBtn);
        exitBtn = findViewById(R.id.historyBtnClose);
        recyclerView = findViewById(R.id.historyRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter();
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);

        filterBtn.setOnClickListener(v-> showFilterDialog());
        exitBtn.setOnClickListener(v-> finish());

        presenter = new HistoryPresenter(this);
        filter = new FilterDataModel(null,null,null,
                null, null, new ArrayList<String>());

        presenter.loadData(filter);
    }

    @Override
    public void showHistory(List<HistoryItem> items){
        adapter.setItems(items);
    }
    @Override
    public void showLoadError(String message){
        Toast.makeText(this, "Failed to Fetch Data, Please Try Again Later.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        loading.show();
    }

    @Override
    public void hideLoading() {
        loading.hide();
        loading.setVisibility(View.GONE);
    }

    private void showFilterDialog() {

        // TODO: Make so you can choose child to filter by

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_history_filter, null);

        ChipGroup chipGroupNightWaking = dialogView.findViewById(R.id.chipGroupNightWaking);
        ChipGroup chipGroupLimitedAbility = dialogView.findViewById(R.id.chipGroupLimitedAbility);
        ChipGroup chipGroupSick = dialogView.findViewById(R.id.chipGroupSick);

        EditText editStartDate = dialogView.findViewById(R.id.editStartDate);
        EditText editEndDate = dialogView.findViewById(R.id.editEndDate);

        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate));
        ChipGroup chipGroupTriggers = dialogView.findViewById(R.id.chipGroupTriggers);


        AlertDialog dialog = new AlertDialog.Builder(this)
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

                    filter = new FilterDataModel(
                            nightWaking,
                            limitedAbility,
                            sick,
                            startDate,
                            endDate,
                            triggers
                    );

                    presenter.loadData(filter);

                    if (filter.isInvalidInput()){
                        Toast.makeText(this, "Invalid date Input, Your input was " +
                                        "changed to meet requirements",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (d, which) -> {
                    d.dismiss();
                })
                .create();
        dialog.show();
    }

    private void showDatePicker(EditText targetField) {
        LocalDate today = LocalDate.now();

        DatePickerDialog picker = new DatePickerDialog(
                this,
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
