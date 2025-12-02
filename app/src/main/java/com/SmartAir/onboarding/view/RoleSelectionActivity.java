package com.SmartAir.onboarding.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ParentDashboard.view.ParentDashboardActivity;
import com.SmartAir.R;
import com.SmartAir.onboarding.presenter.RoleSelectionPresenter;

public class RoleSelectionActivity extends AppCompatActivity implements RoleSelectionView {

    private RoleSelectionPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        presenter = new RoleSelectionPresenter(this);

        Button parentButton = findViewById(R.id.parent_button);
        parentButton.setOnClickListener(v -> presenter.onParentClicked());

        Button providerButton = findViewById(R.id.provider_button);
        providerButton.setOnClickListener(v -> presenter.onProviderClicked());

        Button childButton = findViewById(R.id.child_button);
        childButton.setOnClickListener(v -> presenter.onChildClicked());

    }

    @Override
    public void navigateToParentSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("USER_ROLE", "parent");
        startActivity(intent);
    }

    @Override
    public void navigateToProviderSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("USER_ROLE", "provider");
        startActivity(intent);
    }

    @Override
    public void navigateToChildSignIn() {
        startActivity(new Intent(this, ChildLoginActivity.class));
    }
}
