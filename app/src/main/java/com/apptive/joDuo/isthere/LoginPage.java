package com.apptive.joDuo.isthere;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

/**
 * Created by joseong-yun on 2017. 5. 22..
 */

public class LoginPage extends Dialog {

    private EditText userID;
    private EditText userPassword;
    private Button loginButton;
    private Button registerUser;
    private Button close;

    private LinearLayout autoLoginLine;
    private LinearLayout saveIDLine;

    private AnimCheckBox autoLoginBox;
    private AnimCheckBox saveIDBox;

    private View.OnClickListener loginClickListener;

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public LoginPage(Context context, View.OnClickListener loginClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.loginClickListener = loginClickListener;
    }

    private boolean loginEvent(String id, String password) {
        return true;
    }

    public LoginPage() {
        super(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.login_page);

        userID = (EditText) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);
        loginButton = (Button) findViewById(R.id.login_button);
        close = (Button) findViewById(R.id.login_dismiss_button);

        autoLoginLine = (LinearLayout) findViewById(R.id.autoLogin_line);
        autoLoginBox = (AnimCheckBox) findViewById(R.id.autoLogin_button);
        saveIDLine = (LinearLayout) findViewById(R.id.saveID_line);
        saveIDBox = (AnimCheckBox) findViewById(R.id.saveID_button);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // checkbox 와 text를 한꺼번에 묶기위해서 clickListener 설정
        autoLoginLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    autoLoginBox.setChecked(!autoLoginBox.isChecked());
            }
        });
        saveIDLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIDBox.setChecked(!saveIDBox.isChecked());
            }
        });



//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // loginEvent call 해야함
//
//            }
//        });

    }
}
