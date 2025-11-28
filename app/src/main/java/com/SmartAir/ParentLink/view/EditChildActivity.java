package com.SmartAir.ParentLink.view;

import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.ParentLink.presenter.EditChildPresenter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class EditChildActivity extends AppCompatActivity implements EditChildView {

    private TextInputEditText nameEditText;
    private TextInputEditText ageEditText;
    private DatePicker dobPicker;
    private TextInputEditText notesEditText;
    private Button saveButton;
    private View loadingLayout;
    private EditChildPresenter presenter;
    private String childId;

    private String initialName;
    private String initialAge;
    private String initialDob;
    private String initialNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        dobPicker = findViewById(R.id.dob_picker);
        dobPicker.setMaxDate(System.currentTimeMillis());
        notesEditText = findViewById(R.id.notes_edit_text);
        saveButton = findViewById(R.id.save_button);
        loadingLayout = findViewById(R.id.loading_layout);

        AuthRepository authRepository = AuthRepository.getInstance();
        presenter = new EditChildPresenter(this, authRepository);

        childId = getIntent().getStringExtra("childId");

        presenter.fetchChild(childId);

        saveButton.setOnClickListener(v -> {
            String newName = getSafeText(nameEditText);
            String newAge = getSafeText(ageEditText);
            String newDob = getDobFromPicker();
            String newNotes = getSafeText(notesEditText);
            presenter.onSaveClicked(childId, newName, newAge, newDob, newNotes);
        });
    }

    private String getSafeText(TextInputEditText editText) {
        Editable editable = editText.getText();
        return editable != null ? editable.toString().trim() : "";
    }

    private String getDobFromPicker() {
        int day = dobPicker.getDayOfMonth();
        int month = dobPicker.getMonth();
        int year = dobPicker.getYear();
        return String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, day, year);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                    .setPositiveButton("Discard", (dialog, which) -> finish())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        String currentName = getSafeText(nameEditText);
        String currentAge = getSafeText(ageEditText);
        String currentDob = getDobFromPicker();
        String currentNotes = getSafeText(notesEditText);

        return !currentName.equals(initialName) || !currentAge.equals(initialAge) || !currentDob.equals(initialDob) || !currentNotes.equals(initialNotes);
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setEditChildError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingLayout.setVisibility(View.VISIBLE);
            nameEditText.setEnabled(false);
            ageEditText.setEnabled(false);
            dobPicker.setEnabled(false);
            notesEditText.setEnabled(false);
            saveButton.setEnabled(false);
        } else {
            loadingLayout.setVisibility(View.GONE);
            nameEditText.setEnabled(true);
            ageEditText.setEnabled(true);
            dobPicker.setEnabled(true);
            notesEditText.setEnabled(true);
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void setChildName(String name) {
        nameEditText.setText(name);
        initialName = name;
    }

    @Override
    public void setChildAge(String age) {
        ageEditText.setText(age);
        initialAge = age;
    }

    @Override
    public void setChildDob(String dob) {
        initialDob = dob;
        if (dob != null && !dob.isEmpty()) {
            String[] dateParts = dob.split("/");
            if (dateParts.length == 3) {
                int month = Integer.parseInt(dateParts[0]) - 1;
                int day = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                dobPicker.updateDate(year, month, day);
            }
        }
    }

    @Override
    public void setChildNotes(String notes) {
        notesEditText.setText(notes);
        initialNotes = notes;
    }
}
