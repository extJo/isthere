package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseong-yun on 2017. 4. 24..
 */

public class SearchCategory extends AppCompatActivity {
  private Spinner category1, category2;
  private Button btnSubmit;

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
//
//    addItemsOncategory2();
//    addListenerOnButton();
//    addListenerOnSpinnerItemSelection();
  }

  // add items into spinner dynamically
  public void addItemsOncategory2() {

    category2 = (Spinner) findViewById(R.id.category2);
    List<String> list = new ArrayList<String>();
    list.add("list 1");
    list.add("list 2");
    list.add("list 3");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    category2.setAdapter(dataAdapter);
  }

  public void addListenerOnSpinnerItemSelection() {
    category1 = (Spinner) findViewById(R.id.category1);
    category1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
  }

  // get the selected dropdown list value
  public void addListenerOnButton() {

    category1 = (Spinner) findViewById(R.id.category1);
    category2 = (Spinner) findViewById(R.id.category2);

  }


}
