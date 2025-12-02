package com.SmartAir.glossary.view;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.SmartAir.R;

/**
 * Activity that displays the glossary page.
 * Simple screen with an exit button to return to previous activity.
 */
public class GlossaryActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        ImageButton exitBtn = findViewById(R.id.glossaryExitBtn);

        exitBtn.setOnClickListener(v -> finish());
    }
}

