package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import net.daum.mf.map.api.CameraPosition;
import net.daum.mf.map.api.CameraUpdate;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.CancelableCallback;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    MapPoint pnu;
    MapView mapView;
    RelativeLayout description;

    private static final MapPoint CUSTOM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.537229, 127.005515);
    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);
    private static final MapPoint Soeul_city_hall = MapPoint.mapPointWithGeoCoord(37.5662952, 126.97794509999994);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_main);

        // 밑에 뜨는 간단한 설명
        description = (RelativeLayout) findViewById(R.id.review_dsc);



        /* daum map api 부분 */

        pnu = MapPoint.mapPointWithGeoCoord(35.23, 129.07);


        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(getString(R.string.daum_map_key));
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        /* menu button lib 부분 */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
    }

    /* map 관련 method */

    private void showAll() {
        int padding = 20;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;
        MapPointBounds bounds = new MapPointBounds(CUSTOM_MARKER_POINT, DEFAULT_MARKER_POINT);
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }


    // 맵이 초기화 된 이후에 마커가 지도에 떠야 오류가 나지 않음
    public void onMapViewInitialized(MapView mapView) {
        // 커스텀 마커 추가
        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("Custom Marker");
        customMarker.setTag(0);
        customMarker.setMapPoint(Soeul_city_hall);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.custom_pin_blue); // 마커 이미지.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        customMarker.setShowCalloutBalloonOnTouch(false); // balloon을 보여줄지 말지
        mapView.addPOIItem(customMarker);

        MapPOIItem customMarker1 = new MapPOIItem();
        customMarker1.setItemName("Custom Marker1");
        customMarker1.setTag(1);
        customMarker1.setMapPoint(CUSTOM_MARKER_POINT);
        customMarker1.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker1.setCustomImageResourceId(R.drawable.custom_pin_blue); // 마커 이미지.
        customMarker1.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        customMarker1.setShowCalloutBalloonOnTouch(false); // balloon을 보여줄지 말지
        mapView.addPOIItem(customMarker1);

        showAll();

        // Drawing review markers.
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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        // 핀이 선택되었을 떄, 뷰가 이동하는 코드
        CameraPosition cameraPosition = new CameraPosition(mapPOIItem.getMapPoint(), 4);
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

        title.setText(mapPOIItem.getItemName());

        // box를 클릭했을 때, 메인 리뷰로 넘어가는 부분
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


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
        switch (position) {
            case 1:
                break;
            case 2:
                category = new SearchCategory(this, leftListener, rightListener);
                category.show();
                break;
            case 3:
                Intent intent1 = new Intent(ReviewMain.this, MakeReview.class);
                startActivity(intent1);
                break;
            case 4:
                Intent intent2 = new Intent(ReviewMain.this, LikeReview.class);
                startActivity(intent2);
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

    /*
    Draw markers on Map using Thread.
 */
    public void drawReviewMarkers(final String category, final String detailCategory, final String userLocation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    reviews = httpHelper.getIsThereReviews(category, detailCategory, userLocation);
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
                for(IsThereReview review: reviews) {
                    review.printValues();

                    // 커스텀 마커 추가
                    MapPOIItem newMarker = makeIsThereReviewMarker(review, reviews.indexOf(review));
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
        newMarker.setTag(tag);
        newMarker.setMapPoint(newPoint);
        newMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        newMarker.setCustomImageResourceId(R.drawable.custom_pin_blue);
        newMarker.setCustomImageAnchor(0.5f, 0.5f);

        return newMarker;
    }

}
