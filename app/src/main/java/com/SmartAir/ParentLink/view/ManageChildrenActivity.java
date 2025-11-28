package com.SmartAir.ParentLink.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartAir.R;
import com.SmartAir.homepage.view.ParentChildDetailActivity;
import com.SmartAir.onboarding.model.AuthRepository;
import com.SmartAir.onboarding.model.ChildUser;
import com.SmartAir.ParentLink.presenter.ManageChildrenPresenter;

import java.util.List;

public class ManageChildrenActivity extends AppCompatActivity implements ManageChildrenView, ManageChildrenAdapter.OnChildClickListener {

    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private ManageChildrenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.children_recycler_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        Button addChildButton = findViewById(R.id.add_child_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        presenter = new ManageChildrenPresenter(this, AuthRepository.getInstance());

        addChildButton.setOnClickListener(v -> presenter.onAddChildClicked());

        presenter.fetchChildren();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLoading(boolean isLoading) {
        if (loadingIndicator == null) return;
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayChildren(List<ChildUser> children) {
        recyclerView.setAdapter(new ManageChildrenAdapter(children, this));
    }

    @Override
    public void displayError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToChildDetail(String childId) {
        Intent intent = new Intent(this, ParentChildDetailActivity.class);
        intent.putExtra("childId", childId);
        startActivity(intent);
    }

    @Override
    public void navigateToAddChild() {
        startActivity(new Intent(this, AddChildActivity.class));
    }

    @Override
    public void onChildClicked(ChildUser child) {
        presenter.onChildClicked(child);
    }
}
