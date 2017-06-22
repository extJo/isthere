package com.apptive.joDuo.isthere;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by joseong-yun on 2017. 6. 22..
 */

public class LogoutPage extends Dialog {
    private final Context context;
    private TextView mTitleView;
    private ImageView mContentView;
    private TextView mLeftButton;
    private TextView mRightButton;

    public LogoutPage(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.messagebox);

        mTitleView = (TextView) findViewById(R.id.message_text);
        mContentView = (ImageView) findViewById(R.id.message_image);
        mLeftButton = (TextView) findViewById(R.id.logout_yes); //기존의 textview
        mRightButton = (TextView) findViewById(R.id.logout_no);


        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("AUTO", false);
                editor.putString("PW", "");
                editor.apply();

                MainActivity.GetHttpHelper().setIdToken();

                dismiss();
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
