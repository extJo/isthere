package com.apptive.joDuo.isthere;

import android.app.Activity;
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
import java.util.List;
import java.util.TimerTask;

/**
 * Created by joseong-yun on 2017. 5. 22..
 */

public class SearchCategory extends Dialog {
    private TextView textView;
    private MaterialSpinner firstSpinner;
    private MaterialSpinner secondSpinner;
    private Button mLeftButton;
    private Button mRightButton;


    private ArrayList<String> Category1;

    private ArrayList<ArrayList<String>> categories;
    private ArrayList<MakeReview.CategoryHolder> categoryHolders = new ArrayList<>();

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    Context context;
    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public SearchCategory(Context context, View.OnClickListener leftListener, View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
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


        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }

        // Get Categories
        AsyncTask.execute(new TimerTask() {
            @Override
            public void run() {
                categories = MainActivity.GetHttpHelper().getCategories();
                Category1 = new ArrayList<String>();
                for(int i = 0; i < categories.size(); i++) {
                    ArrayList<String> aCategories = categories.get(i);
                    Category1.add(categories.get(i).get(0)); // primary category
                    MakeReview.CategoryHolder aHolder = new MakeReview.CategoryHolder(aCategories.get(0), aCategories.subList(1, aCategories.size()));
                    categoryHolders.add(aHolder);
                }
                Category1.add(0, "전체");
                firstSpinner.setItems(Category1);
            }
        });
        firstSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position == 0) {
                    disableSecondSpinner();
                    return;
                }
                List<String> detailCategories = categoryHolders.get(position - 1).detailCategories;
                secondSpinner.setItems(detailCategories);
                secondSpinner.setEnabled(true);
                secondSpinner.expand();
            }
        });
        disableSecondSpinner();
    }

    private void disableSecondSpinner() {
        String[] items = {"전체"};
        secondSpinner.setItems(items);
        secondSpinner.setEnabled(false);
    }



}
