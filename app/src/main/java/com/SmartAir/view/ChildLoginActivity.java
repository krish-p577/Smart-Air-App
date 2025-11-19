package com.SmartAir.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.SmartAir.model.AuthRepository;
import com.SmartAir.presenter.ChildLoginPresenter;

public class ChildLoginActivity extends AppCompatActivity implements ChildLoginView {

    private ChildLoginPresenter presenter;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);

        presenter = new ChildLoginPresenter(this, new AuthRepository());

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            presenter.onLoginClicked(username, password);
        });
    }

    @Override
    public void setLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToChildHome() {
        // TODO: Implement navigation to the child home screen
    }
}
