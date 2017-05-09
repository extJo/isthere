package com.apptive.joDuo.isthere;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.github.florent37.viewanimator.ViewAnimator;

public class MainActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // animation 설정
        logo = (ImageView) findViewById(R.id.logo);
        final ViewAnimator viewAnimator = ViewAnimator.animate(logo)
            .dp().translationY(-300, 0)
            .alpha(0, 1)
            .start();

        // 일정시간 이후에 자동으로 화면 전환 및 애니메이션 중지
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewAnimator.cancel();
                Intent intent = new Intent(MainActivity.this, SearchCategory.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 4000ms
    }
}
