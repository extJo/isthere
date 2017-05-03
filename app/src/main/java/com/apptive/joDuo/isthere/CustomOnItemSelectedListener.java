package com.apptive.joDuo.isthere;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by joseong-yun on 2017. 4. 26..
 */

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
            "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
