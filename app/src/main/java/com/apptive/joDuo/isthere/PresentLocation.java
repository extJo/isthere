package com.apptive.joDuo.isthere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptive.joDuo.isthere.search.AddressItem;
import com.apptive.joDuo.isthere.search.OnFinishSearchListener;
import com.apptive.joDuo.isthere.search.Search;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

/**
 * Created by joseong-yun on 2017. 6. 7..
 */

public class PresentLocation extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
    MapView locationMapView;

    String apikey;
    double latitude = PUSAN_UNI_DOOR.getMapPointGeoCoord().latitude;
    double longitude = PUSAN_UNI_DOOR.getMapPointGeoCoord().longitude;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

    private static final MapPoint PUSAN_UNI_DOOR = MapPoint.mapPointWithGeoCoord(35.2315659, 129.08421629999998);

    ImageView currentLocation;
    Button setLocationBtn;
    AddressItem addressItem;
    TextView locationName;
    TextView locationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.present_location);

        apikey = getString(R.string.daum_search_key);

        currentLocation = (ImageView) findViewById(R.id.current_location);
        setLocationBtn = (Button) findViewById(R.id.set_location_button);
        locationName = (TextView) findViewById(R.id.location_name);
        locationAddress = (TextView) findViewById(R.id.location_address);

        locationMapView = (MapView) findViewById(R.id.location_map_view);
        locationMapView.setDaumMapApiKey(getString(R.string.daum_map_key));
        locationMapView.setMapViewEventListener(this);
        locationMapView.setPOIItemEventListener(this);


        // permission value check
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // permission check
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // 권한 없음

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            // 권한 있음

        }


        currentLocation.setOnClickListener(findMyLocation);

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("addressName", locationName.getText());

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need implementation
            }
        });
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private void showAll() {
        int padding = 10;
        float minZoomLevel = 2;
        float maxZoomLevel = 5;
        MapPointBounds bounds = new MapPointBounds(PUSAN_UNI_DOOR, PUSAN_UNI_DOOR);
        locationMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.removeAllPOIItems();

        showAll();
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();

        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        doSearch();
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
        // button disable
        setLocationBtn.setEnabled(false);
        setLocationBtn.setBackgroundColor(getResources().getColor(R.color.gray_cus));
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        // button reset
        setLocationBtn.setEnabled(true);
        setLocationBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        doSearch();

        locationName.setText(addressItem.old_name);

        if (addressItem.new_name.equals("")) {
            locationAddress.setText("");
        } else {
            locationAddress.setText("[새 주소] " + addressItem.new_name);
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

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

    public void doSearch() {
        // 맵의 중앙지점을 이용하여 search 함
        Search search = new Search();
        search.searchDetailAddress(getApplicationContext(), latitude, longitude, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(AddressItem itemList) {
                // 성공하면 JSON을 토큰단위로 쪼갠것을 로컬 변수에 저장
                addressItem = itemList;

            }

            @Override
            public void onFail() {
            }
        });
    }

    private View.OnClickListener findMyLocation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 지도화면 중심을 단말의 현재 위치로 이동시켜줌
            locationMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
    };

}
