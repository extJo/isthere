package com.apptive.joDuo.isthere;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.apptive.joDuo.isthere.reviewlist.ReviewItemAdaptor;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;

/**
 * Created by joseong-yun on 2017. 6. 21..
 */

public class ReviewList extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_list);

        ListView listview ;
        ReviewItemAdaptor adapter;

        // Adapter 생성
        adapter = new ReviewItemAdaptor() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.review_listview);
        listview.setAdapter(adapter);

        adapter.addItem(getResources().getDrawable(R.drawable.ic_picture), "가나다라 마바사", "아자차카타바파", false);
        adapter.addItem(getResources().getDrawable(R.drawable.ic_picture), "가나다asdd", "아Asdads바파", false);
        adapter.addItem(getResources().getDrawable(R.drawable.ic_picture), "가adsd바사", "아asdsaddadasaddas", false);




        /* menu button lib */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
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

        mToolBarTextView.setText("주소 블라블라");
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
