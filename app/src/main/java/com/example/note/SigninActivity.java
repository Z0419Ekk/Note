package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;


    private UserDAO userDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        usernameEditText = findViewById(R.id.et_account);
        passwordEditText = findViewById(R.id.et_password);


        userDAO = new UserDAO(this);
        userDAO.open();

        findViewById(R.id.tv_Back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        findViewById(R.id.btn_Sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                long result = userDAO.addUser(new User(0, username, password));
                if (result != -1) {
                    Toast.makeText(SigninActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SigninActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDAO.close();
    }
}