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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by joseong-yun on 2017. 5. 15..
 */

public class MakeReview extends AppCompatActivity implements OnMenuItemClickListener {



    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private SearchCategory category;
    private boolean titleFlag = false;
    private boolean locationFlag = false;
    private boolean contentFlag = false;
    private boolean firstCat = false;
    private boolean secondCat = false;
    private boolean isImageUploaded = false;
    private boolean isImageSelected = false;

    private EditText title;
    private TextView location;
    private MaterialSpinner firstSpinner;
    private MaterialSpinner secondSpinner;
    private EditText content;
    private Button makeFinishButton;
    private double[] coord = {-1, -1};
    private String currentLocation = "위치가 설정되어있지 않습니다";

    private IsThereHttpHelper httpHelper = MainActivity.GetHttpHelper();

    private ArrayList<String> Category1 = null;
    private ArrayList<String> Category2 = null;

    private ArrayList<ArrayList<String>> categories;
    private ArrayList<CategoryHolder> categoryHolders = new ArrayList<>();

    ImageView imageView;


    private static final int GET_LOCATION = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imageFilePath;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_review);

        title = (EditText) findViewById(R.id.title_review);
        location = (TextView) findViewById(R.id.set_location_text);
        content = (EditText) findViewById(R.id.text_review);
        makeFinishButton = (Button) findViewById(R.id.make_review_finish);
        firstSpinner = (MaterialSpinner) findViewById(R.id.make_firstSpinner);
        secondSpinner = (MaterialSpinner) findViewById(R.id.make_secondSpinner);


        imageView = (ImageView) findViewById(R.id.picture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        // location 설정
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.set_location_layout);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MakeReview.this, PresentLocation.class);
                startActivityForResult(intent, GET_LOCATION);
            }
        });

        // 리뷰작성 완료
        makeFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 버튼 비 활성화
        makeFinishButton.setEnabled(false);
        makeFinishButton.setBackgroundColor(getResources().getColor(R.color.gray_cus));
        makeFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final
                                View v) {
                if (makeFinishButton.isEnabled()) {
                    final String titleValue = title.getText().toString();
                    final String locationValue = location.getText().toString();
                    final String contentValue = content.getText().toString();

                    AsyncTask.execute(new TimerTask() {
                        @Override
                        public void run() {
                            // First, Upload some text of the review
                            long registeredReviewID = httpHelper.postReview(locationValue, coord, titleValue, contentValue, "테스트", "테스트2");
                            if (registeredReviewID != -1) {
                                // Second, Upload the image.
                                uploadMultipart(String.valueOf(registeredReviewID));
                                isImageUploaded = true;
                            }

                            // control Activity
                            if (isImageUploaded) {
                                runOnUiThread(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(v.getContext(), "등록에 성공하였습니다.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(v.getContext(), "등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        // Get Categories
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                categories = MainActivity.GetHttpHelper().getCategories();
                Category1 = new ArrayList<String>();
                for(int i = 0; i < categories.size(); i++) {
                    ArrayList<String> aCategories = categories.get(i);
                    Category1.add(categories.get(i).get(0)); // primary category
                    CategoryHolder aHolder = new CategoryHolder(aCategories.get(0), aCategories.subList(1, aCategories.size()));
                    categoryHolders.add(aHolder);
                }
                firstSpinner.setItems(Category1);

            }
        });
        firstSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                List<String> detailCategories = categoryHolders.get(position).detailCategories;
                secondSpinner.setItems(detailCategories);
                secondSpinner.expand();
            }
        });

        // textwatcher를 통해서, 텍스트 인풋이 있는경우에만, 버튼이 활성화 됨
        title.addTextChangedListener(titleWatcher);
        location.addTextChangedListener(locationWatcher);
        content.addTextChangedListener(contentWatcher);
        firstSpinner.addTextChangedListener(firstWatcher);
        secondSpinner.addTextChangedListener(secondWatcher);

        /* menu button lib */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

    }

    // intent로 결과값을 넘겨받기 위한 method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_LOCATION && resultCode == RESULT_OK) {
            if (data.getExtras().getBoolean("isSucceed")) {
                LogDebuger.debugPrinter(LogDebuger.TagType.MAKE_REVIEW, "Getting location succeed");
                String result = data.getExtras().getString("addressName");
                double latitude = data.getExtras().getDouble("latitude");
                double longitude = data.getExtras().getDouble("longitude");
                coord[0] = latitude;
                coord[1] = longitude;

                currentLocation = result;
                location.setText(currentLocation);
                location.setTextColor(getResources().getColor(R.color.background));
                locationFlag = true;
                setEnableConfirmBT();
            } else {
                currentLocation = "위치가 설정되어있지 않습니다";
                location.setText(currentLocation);
                location.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                locationFlag = false;
                setEnableConfirmBT();
            }
        }
        // for image
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFilePath = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);
                imageView.setImageBitmap(imageBitmap);
                isImageSelected = true;
                setEnableConfirmBT();
            } catch (IOException e) {
                isImageSelected = false;
                setEnableConfirmBT();
                e.printStackTrace();
            }
        }
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

        switch (position) {
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
     */
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진을 선택해 주세요"), PICK_IMAGE_REQUEST);
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

    // textWatcher 부분
    private TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            titleFlag = (charSequence.length() != 0) ? true : false;
            setEnableConfirmBT();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher locationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            locationFlag = (charSequence.length() != 0) ? true : false;
            setEnableConfirmBT();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            contentFlag = (charSequence.length() != 0) ? true : false;
            setEnableConfirmBT();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher firstWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            firstCat = (charSequence.length() != 0) ? true : false;
            setEnableConfirmBT();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher secondWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            secondCat = (charSequence.length() != 0) ? true : false;
            setEnableConfirmBT();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    /*
  * This is the method responsible for image upload
  * We need the full image path and the name for the image in this method
  * */
    public void uploadMultipart(String fileName) {

        //getting the actual path of the image
        String path = getPath(imageFilePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, IsThereHttpHelper.basicURLStr + IsThereHttpHelper.postingImage + fileName)
                .addFileToUpload(path, "image") //Adding file
                .addParameter("name", fileName) //Adding text parameter to the request
                .setNotificationConfig(new UploadNotificationConfig())
                .setMaxRetries(2)
                .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void setEnableConfirmBT() {
        if (titleFlag && locationFlag && contentFlag && firstCat && secondCat && isImageSelected) {
            makeFinishButton.setEnabled(true);
            makeFinishButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            makeFinishButton.setEnabled(false);
            makeFinishButton.setBackgroundColor(getResources().getColor(R.color.gray_cus));
        }
    }

    public static class CategoryHolder {
        public String category;
        public List<String> detailCategories;

        public CategoryHolder(String category, List<String> detailCategories) {
            this.category = category;
            this.detailCategories = detailCategories;
        }
    }
}
