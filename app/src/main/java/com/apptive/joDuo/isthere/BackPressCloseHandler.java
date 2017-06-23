package com.apptive.joDuo.isthere;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Apptive on 2016-11-20.
 */

public class BackPressCloseHandler {
  private long backKeyPressedTime = 0;
  private Toast toast;

  private Activity activity;

  public BackPressCloseHandler(Activity context) {
    this.activity = context;
  }

  public void onBackPressed() {
    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
      backKeyPressedTime = System.currentTimeMillis();
      showGuide();
      return;
    }
    if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
      MainActivity.GetHttpHelper().setIdTokenNull();
      activity.finish();
      toast.cancel();
    }
  }

  public void showGuide() {
    toast = Toast.makeText(activity, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
    toast.show();
  }
}
