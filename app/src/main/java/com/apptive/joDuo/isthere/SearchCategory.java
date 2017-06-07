package com.apptive.joDuo.isthere;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

/**
 * Created by joseong-yun on 2017. 5. 22..
 */

public class SearchCategory extends Dialog {
    private TextView textView;
    private MaterialSpinner firstSpinner;
    private MaterialSpinner secondSpinner;
    private Button mLeftButton;
    private Button mRightButton;


    private static final String[] Category1 = {"", "", "", "", "", "", "", "", ""};
    private static final String[] Category2 = Category1;

    private ArrayList<ArrayList<String>> categories;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public SearchCategory(Context context, View.OnClickListener leftListener, View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    public SearchCategory() {
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

        setContentView(R.layout.search_category);

        textView = (TextView) findViewById(R.id.category_text);
        firstSpinner = (MaterialSpinner) findViewById(R.id.firstSpinner);
        secondSpinner = (MaterialSpinner) findViewById(R.id.secondSpinner);
        mLeftButton = (Button) findViewById(R.id.okButton);
        mRightButton = (Button) findViewById(R.id.noButton);

        // 스피너 아이템 설정
        firstSpinner.setItems(Category1);
        secondSpinner.setItems(Category2);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }

        //// for test ////
        //// 미완성 !! ////
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your network logic
                // should be here

                categories = MainActivity.GetHttpHelper().getCategories();
                for (ArrayList<String> aCategories : categories) {
                    for (int i = 0; i < aCategories.size(); i++) {
                        Category1[i] = aCategories.get(i);
                    }
                }
            }
        });

    }



}
