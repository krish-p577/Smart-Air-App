package com.SmartAir.glossary.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.glossary.model.GlossaryItem;
import com.SmartAir.glossary.presenter.GlossaryPresenter;

import java.util.List;

public class GlossaryActivity extends AppCompatActivity implements GlossaryPresenter.View {

    private RecyclerView recyclerView;
    private GlossaryPresenter presenter;
    private GlossaryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.glossary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.glossaryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GlossaryAdapter();
        recyclerView.setAdapter(adapter);

        presenter = new GlossaryPresenter(this);
        presenter.loadGlossary();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showGlossary(List<GlossaryItem> items) {
        adapter.setItems(items);
    }

    @Override
    public void showError(String message) {

    }
}
