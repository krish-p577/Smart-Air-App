package com.SmartAir.view;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.presenter.RoleSelectionPresenter;

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
        // TODO: Implement navigation to parent sign-in screen
    }

    @Override
    public void navigateToProviderSignIn() {
        // TODO: Implement navigation to provider sign-in screen
    }

    @Override
    public void navigateToChildSignIn() {
        // TODO: Implement navigation to child sign-in screen
    }
}
