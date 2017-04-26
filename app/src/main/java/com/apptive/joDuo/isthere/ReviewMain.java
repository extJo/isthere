package com.apptive.joDuo.isthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


/**
 * Created by joseong-yun on 2017. 4. 25..
 */

public class ReviewMain extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.review_main);

//    MapPoint pnu = MapPoint.mapPointWithGeoCoord(35.23, 129.07);


    MapView mapView = new MapView(this);
    mapView.setDaumMapApiKey(getString(R.string.daum_map_key));

    // 커스텀 마커 추가
    MapPOIItem customMarker = new MapPOIItem();
    customMarker.setItemName("Custom Marker");
    customMarker.setTag(1);
//    customMarker.setMapPoint(pnu);
    customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
    customMarker.setCustomImageResourceId(R.drawable.ic_pin); // 마커 이미지.
    customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
    customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
    mapView.addPOIItem(customMarker);



    //xml에 선언된 map_view 레이아웃을 찾아온 후, 생성한 MapView객체 추가
    RelativeLayout container = (RelativeLayout) findViewById(R.id.map_layout);
    container.addView(mapView);


  }


}
