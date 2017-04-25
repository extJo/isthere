package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    /* 로그인 창 애니매이션 구현 */
//    LinearLayout login_layout = (LinearLayout) findViewById(R.id.login_layout);
//    login_layout.setLayoutParams(new LinearLayout.LayoutParams(200, 0, 0));
//
//
//    Timer T = new Timer();
//    T.scheduleAtFixedRate(new TimerTask() {
//      @Override
//      public void run() {
//        runOnUiThread(new Runnable()
//        {
//          int layoutWeight = 0;
//          @Override
//          public void run()
//          {
//
//            layoutWeight++;
//            LinearLayout login_layout = (LinearLayout) findViewById(R.id.login_layout);
//            login_layout.setLayoutParams(new LinearLayout.LayoutParams(200, 0, layoutWeight));
//            login_layout.setAlpha(layoutWeight);
//          }
//        });
//      }
//    }, 5000, 5000);

    Button nonMember = (Button) findViewById(R.id.non_member_button);
    Button Member = (Button) findViewById(R.id.member_button);

    // 비회원 입장
    nonMember.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, SearchCategory.class);
        startActivity(intent);
        finish();
      }
    });

    // 회원 입장
    Member.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, SearchCategory.class);
        startActivity(intent);
        finish();
      }
    });

  }
}
