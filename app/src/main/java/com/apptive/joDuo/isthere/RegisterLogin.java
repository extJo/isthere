package com.apptive.joDuo.isthere;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by joseong-yun on 2017. 6. 4..
 */

public class RegisterLogin extends AppCompatActivity {
    EditText nickname;
    EditText email;
    EditText password;
    EditText passwordConfirm;
    Button registerFinish;

    IsThereHttpHelper httpHelper = MainActivity.GetHttpHelper();

    Boolean registerFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        nickname = (EditText) findViewById(R.id.register_nickname);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        passwordConfirm = (EditText) findViewById(R.id.register_password_confirm);
        registerFinish = (Button) findViewById(R.id.register_finish_button);


        registerFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerFlag = TRUE;

                // edit text 의 에러 검증 부분
                if (password.getText().toString().trim().length() < 2
                    || password.getText().toString().trim().length() > 10) {
                    nickname.setError("닉네임을 입력 해 주세요");
                    changeBackground(nickname);
                    registerFlag = FALSE;
                }

                if (!email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@pusan.ac.kr")) {
                    email.setError("이메일을 정확하게 입력 해 주새요");
                    changeBackground(email);
                    registerFlag = FALSE;
                }

                if (password.getText().toString().trim().length() < 5
                    || password.getText().toString().trim().length() > 16) {
                    password.setError("비밀번호를 올바르게 입력 해 주세요");
                    changeBackground(password);
                    registerFlag = FALSE;
                } else {
                    if (!password.getText().toString().trim().equals(passwordConfirm.getText().toString().trim())) {
                        passwordConfirm.setError("입력하신 비밀번호가 동일하지 않습니다");
                        changeBackground(passwordConfirm);
                        registerFlag = FALSE;
                    }
                }


                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        // register 서버에 등록 시도
                        if (registerFlag) {
                            String emailStr = email.getText().toString();
                            emailStr = emailStr.substring(0, emailStr.indexOf('@'));
                            String passwordStr = password.getText().toString();
                            String nicknameStr = nickname.getText().toString();

                            try {
                                if (httpHelper.postCreateNewAccount(emailStr, passwordStr, nicknameStr)) {
//                                    finish();
                                }
                            } catch (IOException e) {
                            }
                        }
                    }
                });


            }
        });

    }

    // 에러 상황에서 background 변경
    private void changeBackground(EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            editText.setBackground(getResources().getDrawable(R.drawable.roundcorner_edge_error));
        } else {
            editText.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundcorner_edge_error));
        }
    }

}
