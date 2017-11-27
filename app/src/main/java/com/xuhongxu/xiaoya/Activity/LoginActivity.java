package com.xuhongxu.xiaoya.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;

import com.xuhongxu.Assist.SchoolworkAssist;
import com.xuhongxu.xiaoya.R;
import com.xuhongxu.xiaoya.Service.QueryScoreService;
import com.xuhongxu.xiaoya.YaApplication;

public class LoginActivity extends AppCompatActivity {

    public static final String MESSAGE_LOGOUT
            = "cn.xuhongxu.xiaoya.Activity.LoginActivity.logout";

    YaApplication app;
    private SharedPreferences preferences;

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private Switch switchRemember;

    private ProgressDialog progressDialog;

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SchoolworkAssist assist = new SchoolworkAssist(params[0], params[1], getApplicationContext());
            SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            assist.setTimeout(Integer.valueOf(pf.getString("network_timeout", "30000")));

            try {
                assist.login();
                if (app.getStudentInfo() == null) {
                    app.setStudentInfo(assist.getStudentInfo());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            ((YaApplication) getApplication()).setAssist(assist);
            return getString(R.string.succeed_login);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            View view = findViewById(android.R.id.content);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", editUsername.getText().toString());
            editor.apply();
            if (result == null) {
                result = "网络错误";
            }
            if (getString(R.string.succeed_login).contentEquals(result)) {
                if (switchRemember.isChecked()) {
                    editor.putString("password", editPassword.getText().toString());
                    editor.putBoolean("remember", true);
                    editor.apply();
                }
                loginSucceed();
            } else {
                // 登录失败
                if (getIntent().getBooleanExtra("justLogin", false)) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return;
                }
                if (view == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error)
                            + ": " + result, Toast.LENGTH_LONG).show();
                } else {
                    Snackbar snackbar;
                    snackbar = Snackbar.make(view, result, Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(
                            ContextCompat.getColor(getApplicationContext(), R.color.colorError));
                    TextView textView = (TextView) snackBarView.findViewById(
                            android.support.design.R.id.snackbar_text);
                    textView.setTextColor(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorWhite));
                    snackbar.show();
                }

                checkVersion(false, false);
            }
        }
    }

    private void checkVersion(boolean setTimeout, boolean doLogin)
    {
        int timeout = 0;
        if (setTimeout) {
            timeout = AVOSCloud.getNetworkTimeout();
            AVOSCloud.setNetworkTimeout(500);
        }
        AVQuery<AVObject> avQuery = new AVQuery<>("Version");
        int finalTimeout = timeout;
        final boolean[] finalDoLogin = new boolean[] {doLogin};
        avQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (setTimeout)
                {
                    AVOSCloud.setNetworkTimeout(finalTimeout);
                }
                if (e == null) {
                    String msg = avObject.getString("Message");
                    int code = avObject.getInt("VersionCode");
                    String url = avObject.getString("Url");

                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        if (pInfo.versionCode < code)
                        {
                            finalDoLogin[0] = false;
                            (new AlertDialog.Builder(LoginActivity.this))
                                    .setMessage(msg)
                                    .setPositiveButton("前往下载新版", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            Uri uri = Uri.parse(url);
                                            intent.setData(uri);
                                            startActivity(intent);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    } catch (PackageManager.NameNotFoundException e2) {
                        e2.printStackTrace();
                    }


                } else {
                    e.printStackTrace();

                }

                if (finalDoLogin[0])
                {
                    login(null);
                }

            }
        });

    }

    // 登录成功
    protected void loginSucceed() {

        if (getIntent().getBooleanExtra("justLogin", false)) {
            setResult(RESULT_OK);
            finish();
        } else {
            // 启动主活动
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(getApplicationContext(), QueryScoreService.class));
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                } else {
                    // 保存失败，输出错误信息
                    Log.e(getClass().getName(), e.getMessage());
                }
            }
        });
        PushService.setDefaultPushCallback(this, MainActivity.class);

        app = (YaApplication) getApplication();
        if (app != null) {
            app.clearAll();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editUsername = (TextInputEditText) findViewById(R.id.edit_username);
        editPassword = (TextInputEditText) findViewById(R.id.edit_password);
        switchRemember = (Switch) findViewById(R.id.switch_remember);

        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        editUsername.setText(preferences.getString("username", ""));

        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(v);
                    return true;
                }
                return false;
            }
        });

        boolean doLogin = false;
        if (preferences.getBoolean("remember", false)) {
            AVUser.logOut();
            Intent intent = getIntent();
            if (intent.getBooleanExtra(MESSAGE_LOGOUT, false)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("password");
                editor.putBoolean("remember", false);
                editor.apply();
            } else {
                editPassword.setText(preferences.getString("password", ""));
                switchRemember.setChecked(true);
                doLogin = true;
            }
        }


        checkVersion(true, doLogin);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void login(View view) {

        hideKeyboard(this);

        progressDialog = ProgressDialog.show(this, "登录中", "正在登录统一身份认证平台...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
