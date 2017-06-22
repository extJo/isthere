package com.apptive.joDuo.isthere;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;

/**
 * Created by joseong-yun on 2017. 5. 15..
 */

public class Setting extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private LoginPage loginPage;
    private LogoutPage logoutPage;

    TextView Login;
    TextView Error;
    TextView Version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        Login = (TextView) findViewById(R.id.login);
        Error = (TextView) findViewById(R.id.errorCall);
        Version = (TextView) findViewById(R.id.version);

        final SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        // Login dialog
        loginPage = new LoginPage(this);
        loginPage.setOnLoginListener(new LoginPage.OnLoginListener() {
            @Override
            public void onLoginSucceed() {
                // 쓰레드 내부에서 UI를 직접 그릴 수 없으므로 handler를 통해서 view와 관련된 일을 처리
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToastTest("로그인에 성공하였습니다.");
                        loginPage.dismiss();
                        Login.setText(sharedPreferences.getString("ID", ""));
                    }
                }, 0);
            }

            @Override
            public void onLoginFailed(boolean notMatched, boolean isException) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToastTest("로그인에 실패하였습니다.");
                    }
                }, 0);
            }
        });

        // Logout dialog
        logoutPage = new LogoutPage(this);
        logoutPage.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(MainActivity.GetHttpHelper().getIdToken() == null) { // logout succeed
                    Login.setText("로그인 정보");

                    Login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginPage.show();
                        }
                    });
                }
            }
        });

        // auto login이 성공 한 경우 뷰 변화

        if(sharedPreferences.getBoolean("AUTO", false)){
            Login.setText(sharedPreferences.getString("ID", ""));
            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutPage.show();
                }
            });
        } else {
            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginPage.show();
                }
            });
        }

        // Error feedback
        Error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
            }
        });

        // show version
        String versionName = "v." + BuildConfig.VERSION_NAME;
        Version.setText(versionName);

        // 제작자 정보


        /* menu button lib */
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    /* menu button lib method */

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
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

        mToolBarTextView.setText("거가 거가");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }
    private void showToastTest(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }



}