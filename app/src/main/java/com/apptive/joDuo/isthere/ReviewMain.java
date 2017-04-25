package com.apptive.joDuo.isthere;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapView;


/**
 * Created by joseong-yun on 2017. 4. 25..
 */

public class ReviewMain extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.review_main);


    MapView mapView = new MapView(this);
    mapView.setDaumMapApiKey(getString(R.string.daum_map_key));

    //xml에 선언된 map_view 레이아웃을 찾아온 후, 생성한 MapView객체 추가
    RelativeLayout container = (RelativeLayout) findViewById(R.id.map_layout);
    container.addView(mapView);


  }


}
