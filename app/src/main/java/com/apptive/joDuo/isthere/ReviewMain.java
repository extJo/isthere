package com.apptive.joDuo.isthere;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapLayout;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


/**
 * Created by joseong-yun on 2017. 4. 25..
 */

public class ReviewMain extends AppCompatActivity implements OnMenuItemClickListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);
    private SearchCategory category;

    private IsThereHttpHelper httpHelper = null;
    private ArrayList<IsThereReview> reviews = null;
    private ArrayList<MapPOIItem> markers = null;
    private AQuery aQuery = new AQuery(this);
    private String currentCategory = "전체";
    private String currentDetailCategory = "전체";

    MapPoint pnu;
    MapView mapView;
    RelativeLayout description;
    ViewGroup mapViewContainer;
    TextView numberOfReviewsTV;

    private static final MapPoint PUSAN_UNI_DOOR = MapPoint.mapPointWithGeoCoord(35.2315659, 129.08421629999998);
    private static final MapPoint PUSAN_UNI_STATION = MapPoint.mapPointWithGeoCoord(35.22979, 129.089385);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_main);
        httpHelper = MainActivity.GetHttpHelper();

        // 밑에 뜨는 간단한 설명
        description = (RelativeLayout) findViewById(R.id.review_dsc);
        description.setVisibility(View.INVISIBLE);

        // Showing number of reviews that the marker has
        numberOfReviewsTV = (TextView) findViewById(R.id.review_review);

        /* menu button lib 부분 */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

        // Auto login
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("AUTO", false)) { // if AUTO LOGIN is checked then,
            final String userID = sharedPreferences.getString("ID", "");
            final String userPW = sharedPreferences.getString("PW", "");
            AsyncTask.execute(new TimerTask() {
                @Override
                public void run() {
                    if(httpHelper.getIdToken() == null) {
                        try {
                            final boolean isLoginSucceed = httpHelper.postLogin(userID, userPW);
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    if (isLoginSucceed) {
                                        Toast.makeText(getBaseContext(), "자동로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "자동로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private View.OnClickListener makeDescriptionClickListener(final MapPOIItem poiItem) {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewMain.this, ReviewList.class);

                String markerAddress = ((IsThereReview) poiItem.getUserObject()).getLocation();
                String category = ((IsThereReview) poiItem.getUserObject()).getCategory();
                String detailCategory = ((IsThereReview) poiItem.getUserObject()).getDetailCategory();
                intent.putExtra("address", markerAddress);
                intent.putExtra("category", category);
                intent.putExtra("detailCategory", detailCategory);

                startActivity(intent);
            }
        });
    }


    // 다음맵이 한 앱에서 2개 이상 부를 수 없기때문에 view cycle을 통해서 map view를 동적으로 추가
    @Override
    protected void onResume() {
        super.onResume();
        /* daum map api 부분 */

        pnu = MapPoint.mapPointWithGeoCoord(35.23, 129.07);

        MapLayout mapLayout = new MapLayout(this);
        mapView = mapLayout.getMapView();


        mapView.setDaumMapApiKey(getString(R.string.daum_map_key));
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        mapViewContainer = (ViewGroup) findViewById(R.id.map_layout);
        mapViewContainer.addView(mapLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeAllViews();
    }

    /* map 관련 method */
    private void showAll() {
        int padding = 1;
        float minZoomLevel = 1;
        float maxZoomLevel = 4;
        MapPointBounds bounds = new MapPointBounds(PUSAN_UNI_DOOR, PUSAN_UNI_STATION);
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }


    // 맵이 초기화 된 이후에 마커가 지도에 떠야 오류가 나지 않음
    public void onMapViewInitialized(MapView mapView) {
        // 커스텀 마커 추가
//        MapPOIItem customMarker = new MapPOIItem();
//        customMarker.setItemName("Custom Marker");
//        customMarker.setTag(0);
//        customMarker.setMapPoint(PUSAN_UNI_DOOR);
//        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//        customMarker.setCustomImageResourceId(R.drawable.custom_pin_blue); // 마커 이미지.
//        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
//        customMarker.setShowCalloutBalloonOnTouch(false); // balloon을 보여줄지 말지
//        mapView.addPOIItem(customMarker);
//
//        MapPOIItem customMarker1 = new MapPOIItem();
//        customMarker1.setItemName("Custom Marker1");
//        customMarker1.setTag(1);
//        customMarker1.setMapPoint(PUSAN_UNI_STATION);
//        customMarker1.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
//        customMarker1.setCustomImageResourceId(R.drawable.custom_pin_blue); // 마커 이미지.
//        customMarker1.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
//        customMarker1.setShowCalloutBalloonOnTouch(false); // balloon을 보여줄지 말지
//        mapView.addPOIItem(customMarker1);

        // showAll();

        // Drawing review markers.
        drawReviewMarkers();
        showAll();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    // pin이 터치 되었을 때 call되는 method
    @Override
    public void onPOIItemSelected(MapView mapView, final MapPOIItem mapPOIItem) {

        // show description
        description.setVisibility(View.VISIBLE);
        description.setOnClickListener(makeDescriptionClickListener(mapPOIItem));

        // 핀이 선택되었을 떄, 뷰가 이동하는 코드
        CameraPosition cameraPosition = new CameraPosition(mapPOIItem.getMapPoint(), 0);
        mapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, new CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {
            }
        });

        // 여기서 box의 content를 바꾸면 변경가능
        TextView title = (TextView) findViewById(R.id.review_name);
        ImageView reviewIV = (ImageView) findViewById(R.id.review_image);

        title.setText(mapPOIItem.getItemName());
        // Set review image
        aQuery.id(reviewIV).image(IsThereHttpHelper.basicURLStr + IsThereHttpHelper.gettingImage + String.valueOf(mapPOIItem.getTag()));


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

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

        mToolBarTextView.setText("리뷰 보기");
    }

//    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
//        invalidateOptionsMenu();
//        String backStackName = fragment.getClass().getName();
//        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
//        if (!fragmentPopped) {
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.add(containerId, fragment, backStackName)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            if (addToBackStack)
//                transaction.addToBackStack(backStackName);
//            transaction.commit();
//        }
//    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {

        Toast pleaseLoginMsg = Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT);
        switch (position) {
            case 1:
                break;
            case 2:
                category = new SearchCategory(this, leftListener, rightListener);
                category.show();

                category.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SearchCategory searchCategory = (SearchCategory) dialog;

                        // Update current categories
                        currentCategory = searchCategory.getSelectedCategory();
                        currentDetailCategory = searchCategory.getSelectedDetailCateogry();

                        drawReviewMarkers();
                    }
                });
                break;
            case 3:
                if(httpHelper.getIdToken() != null) {
                    Intent intent1 = new Intent(ReviewMain.this, MakeReview.class);
                    startActivity(intent1);
                } else {
                    pleaseLoginMsg.show();
                }
                break;
            case 4:
                if(httpHelper.getIdToken() != null) {
                    Intent intent2 = new Intent(ReviewMain.this, LikeReview.class);
                    startActivity(intent2);
                } else {
                    pleaseLoginMsg.show();
                }
                break;
            case 5:
                Intent intent3 = new Intent(ReviewMain.this, Setting.class);
                startActivity(intent3);
                break;
            default:
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


    // map의 custom balloon 과 context menu가 혼합되서 사용된다
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
            backPressCloseHandler.onBackPressed();
        }
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

    public ArrayList<IsThereReview> getReviews() {
        return reviews;
    }

    /*
    Draw markers on Map using Thread.
 */
    public void drawReviewMarkers() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String searchingCategory = currentCategory.equals("전체") ? "" : currentCategory;
                    String searchingDetailCategory = currentDetailCategory.equals("전체") ? "" : currentDetailCategory;
                    reviews = httpHelper.getIsThereReviews(searchingCategory, searchingDetailCategory, "");

                } catch (IOException e) {
                    LogDebuger.debugPrinter(LogDebuger.TagType.REVIEW_MAIN, "ERROR: drawReviewMarkers.IOException");
                    Log.e("ReviewMain", "ERROR: drawReviewMarkers.IOException");
                    // e.printStackTrace();
                }

                // Error handling.
                // There is no reviews...
                if (reviews == null) {
                    LogDebuger.debugPrinter(LogDebuger.TagType.REVIEW_MAIN, "ERROR: There is no review");
                    return;
                }

                // Actual drawing markers.
                markers = new ArrayList<MapPOIItem>();
                for (IsThereReview review : reviews) {
                    // 커스텀 마커 추가
                    MapPOIItem newMarker = makeIsThereReviewMarker(review, Integer.parseInt(review.getReviewId()));
                    mapView.addPOIItem(newMarker);
                    markers.add(newMarker);
                }
            }
        });
    }


    /*
        Make Marker that fits on IsThereReview
     */
    private MapPOIItem makeIsThereReviewMarker(IsThereReview review, int tag) {
        // Make MapPoint
        MapPoint newPoint = MapPoint.mapPointWithGeoCoord(review.getCoordX(), review.getCoordY());

        // Make MapMarker
        MapPOIItem newMarker = new MapPOIItem();
        newMarker.setItemName(review.getName());
        newMarker.setUserObject(review);
        newMarker.setTag(tag);
        newMarker.setMapPoint(newPoint);
        newMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        newMarker.setCustomImageResourceId(R.drawable.custom_pin_blue);
        newMarker.setShowCalloutBalloonOnTouch(false);
        newMarker.setCustomImageAnchor(0.5f, 0.5f);

        return newMarker;
    }

}
