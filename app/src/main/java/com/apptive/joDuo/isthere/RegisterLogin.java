package com.apptive.joDuo.isthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by joseong-yun on 2017. 6. 4..
 */

public class RegisterLogin extends AppCompatActivity {

    EditText nickname;
    EditText email;
    EditText password;
    EditText passwordConfirm;
    Button registerFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        nickname = (EditText) findViewById(R.id.register_nickname);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        passwordConfirm = (EditText) findViewById(R.id.register_password_confirm);
        registerFinish = (Button) findViewById(R.id.register_finish_button);




    }
}
