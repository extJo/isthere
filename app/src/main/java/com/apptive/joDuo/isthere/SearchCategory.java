package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by joseong-yun on 2017. 4. 24..
 */

public class SearchCategory extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.search_category);

    Button btn_done = (Button) findViewById(R.id.category_done);

    btn_done.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(SearchCategory.this, ReviewMain.class);
        startActivity(intent);
        finish();
      }
    });


  }
}
