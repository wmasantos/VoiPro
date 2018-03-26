package br.com.voipro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.voipro.entity.UserEntity;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin;
    private EditText etPassword;
    private EditText etHost;
    private Button btLogin;
    private UserEntity userEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etHost = findViewById(R.id.etHost);

        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                String host = etHost.getText().toString();

                userEntity = new UserEntity(login, password, host);

                Intent intent = new Intent(LoginActivity.this, VoiProService.class);
                intent.putExtra("user", userEntity);
                startService(intent);
            }
        });
    }
}
