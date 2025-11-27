package com.SmartAir.glossary.view;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.SmartAir.R;

public class GlossaryActivity extends AppCompatActivity{

    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        exitBtn = findViewById(R.id.glossaryExitBtn);

        exitBtn.setOnClickListener(v -> {
            finish();
        });
    }
}

