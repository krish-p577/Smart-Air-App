package com.SmartAir.ParentDashboard.view;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.ParentDashboard.presenter.PbPresenter;

import java.util.Objects;

public class PbActivity extends AppCompatActivity implements PbView {
    private PbPresenter presenter;
    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb);
        presenter = new PbPresenter(this);

        childId = getIntent().getStringExtra("childId");

        Button pefButton = findViewById(R.id.SetPBButtons);
        pefButton.setOnClickListener(v -> presenter.onPBClicked());
    }

    @Override
    public void popOut() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popout_pb);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.ic_launcher_background);

        Button submit = dialog.findViewById(R.id.submitPBButton);

        submit.setOnClickListener(view -> {
            EditText pefVal = dialog.findViewById(R.id.PBNumber);
            String value = pefVal.getText().toString();
            presenter.submitPersonalBest(childId, value);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
