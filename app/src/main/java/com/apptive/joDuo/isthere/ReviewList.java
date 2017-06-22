package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.apptive.joDuo.isthere.reviewlist.ReviewItemAdaptor;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by joseong-yun on 2017. 6. 21..
 */

public class ReviewList extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private IsThereHttpHelper httpHelper = MainActivity.GetHttpHelper();
    private ListView listview;
    private ReviewItemAdaptor adapter;

    private String address = null;
    private String category = null;
    private String detailCategory = null;
    private ArrayList<IsThereReview> selectedReviews;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_list);

        Intent intent = getIntent(); // from ReviewMain
        address = intent.getStringExtra("address");
        category = intent.getStringExtra("category");
        detailCategory = intent.getStringExtra("detailCategory");

        // Adapter 생성
        adapter = new ReviewItemAdaptor(this) ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.review_listview);
        AsyncTask.execute(new TimerTask() {
            @Override
            public void run() {
                try {
                    selectedReviews = httpHelper.getIsThereReviews(category, detailCategory, address);
                    adapter.setListViewItemList(selectedReviews);

                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            listview.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    selectedReviews = null;
                    e.printStackTrace();
                }
            }
        });



        /* menu button lib */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /* menu button lib method */

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mToolBarTextView.setText(address);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

}
