package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by joseong-yun on 2017. 5. 15..
 */

public class MakeReview extends AppCompatActivity implements OnMenuItemClickListener {

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private SearchCategory category;
    private IsThereHttpHelper httpHelper = MainActivity.GetHttpHelper();

    ImageView imageView;

    private static int PICK_IMAGE_REQUEST = 1;
    private Uri imageFilePath;
    private Bitmap imageBitmap;

    TextView locationTVButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_review);


        imageView = (ImageView) findViewById(R.id.picture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();

                // Need additional implementations.
            }
        });

        LinearLayout location = (LinearLayout) findViewById(R.id.set_location_layout);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakeReview.this, PresentLocation.class);
                startActivity(intent);
            }
        });


        /* menu button lib */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();




        // for upload testing


    }

    /* menu button lib method */

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
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

        mToolBarTextView.setText("리뷰 작성");
    }




    @Override
    public void onMenuItemClick(View clickedView, int position) {

        switch (position){
            case 1:
                finish();
                break;
            case 2:
                category = new SearchCategory(this, leftListener, rightListener);
                category.show();
                break;
            case 3:
                break;
            case 4:
                Intent intent1 = new Intent(MakeReview.this, LikeReview.class);
                startActivity(intent1);
                finish();
                break;
            case 5:
                Intent intent3 = new Intent(MakeReview.this, Setting.class);
                startActivity(intent3);
                break;
            default:
                finish();
                break;
        }
    }

    public List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.ic_left_arrow);

        MenuObject showReview = new MenuObject("리뷰 보기");
        showReview.setResource(R.drawable.ic_consulting_message);

        MenuObject searchCategory = new MenuObject("카테고리 검색");
        searchCategory.setResource(R.drawable.ic_search);

        MenuObject makeReview = new MenuObject("리뷰 작성");
        makeReview.setResource(R.drawable.ic_new_file);

        MenuObject likeReview = new MenuObject("좋아한 리뷰");
        likeReview.setResource(R.drawable.ic_like);

        MenuObject setting = new MenuObject("설정");
        setting.setResource(R.drawable.ic_settings);

        menuObjects.add(close);
        menuObjects.add(showReview);
        menuObjects.add(searchCategory);
        menuObjects.add(makeReview);
        menuObjects.add(likeReview);
        menuObjects.add(setting);
        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
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


    /**
     * Show image chooser.
     *
     */
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진을 선택해 주세요"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFilePath = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
                imageView.setImageBitmap(imageBitmap);

                // for testing
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    // upload image
    public void uploadImage() {
        String name = "";

        String path = getPath(imageFilePath);

        AsyncTask.execute(new TimerTask() {
            @Override
            public void run() {
                httpHelper.postImage("1", imageBitmap);
            }
        });
    }



    // dialog event listener

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            category.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            category.dismiss();
        }
    };

}
