package cn.xuhongxu.xiaoya.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

import cn.xuhongxu.xiaoya.R;

public class GatewayActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private Switch switchRemember;
    private Button forceButton;

    private ProgressDialog progressDialog;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editUsername = (TextInputEditText) findViewById(R.id.edit_username);
        editPassword = (TextInputEditText) findViewById(R.id.edit_password);
        switchRemember = (Switch) findViewById(R.id.switch_remember);
        forceButton = (Button) findViewById(R.id.force_logout);

        preferences =
                getSharedPreferences(getString(R.string.gateway_preference_key),
                        MODE_PRIVATE);

        editUsername.setText(preferences.getString("username", ""));
        if (preferences.getBoolean("remember", false)) {
            editPassword.setText(preferences.getString("password", ""));
            switchRemember.setChecked(true);
        }

        forceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                force();
            }
        });

        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.getBoolean("isLogin", false)) {
                    logout();
                } else {
                    login();
                }
            }
        });

        if (preferences.getBoolean("isLogin", false)) {
            fab.setBackgroundColor(Color.parseColor("#303F9F"));
        } else {
            fab.setBackgroundColor(Color.parseColor("#FF4081"));
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null
                && activity.getWindow() != null
                && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void login() {

        hideKeyboard(this);

        progressDialog = ProgressDialog.show(this, "登录中", "正在登录网关...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString(),
                "login"
        );
    }

    public void logout() {

        hideKeyboard(this);

        progressDialog = ProgressDialog.show(this, "注销中", "正在注销网关...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString(),
                "logout"
        );
    }

    public void force() {

        hideKeyboard(this);

        progressDialog = ProgressDialog.show(this, "强制离线中", "正在强制离线网关...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString(),
                "force"
        );
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        private String type;

        @Override
        protected String doInBackground(String... params) {
            type = params[2];

            try {
                switch (params[2]) {
                    case "login": {
                        Document doc = Jsoup.connect("http://172.16.202.201/cgi-bin/do_login")
                                .timeout(5000)
                                .data("action", "action")
                                .data("is_pad", "1")
                                .data("drop", "0")
                                .data("type", "1")
                                .data("n", "1")
                                .data("username", params[0])
                                .data("password", params[1])
                                .post();
                        break;
                    }
                    case "logout": {
                        Document doc = Jsoup.connect("http://172.16.202.201/cgi-bin/do_logout?action=logout")
                                .timeout(5000)
                                .get();
                        break;
                    }
                    case "force": {
                        Document doc = Jsoup.connect("http://172.16.202.201/cgi-bin/force_logout")
                                .timeout(5000)
                                .data("action", "x")
                                .data("is_pad", "1")
                                .data("drop", "0")
                                .data("type", "1")
                                .data("n", "1")
                                .data("username", params[0])
                                .data("password", params[1])
                                .post();
                        break;
                    }
                }
            } catch (Exception e) {
                return e.getMessage();
            }

            return getString(R.string.succeed_login);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", editUsername.getText().toString());
            editor.putBoolean("isLogin", false);
            editor.apply();
            if (result == null) {
                result = "网络错误";
            }
            fab.setBackgroundColor(Color.parseColor("#303F9F"));
            if (getString(R.string.succeed_login).contentEquals(result)) {
                editor = preferences.edit();
                editor.putBoolean("isLogin", true);
                editor.apply();
                if (type.equals("login")) {
                    fab.setBackgroundColor(Color.parseColor("#FF4081"));
                } else {
                    fab.setBackgroundColor(Color.parseColor("#303F9F"));
                }
                if (switchRemember.isChecked()) {
                    editor.putString("password", editPassword.getText().toString());
                    editor.putBoolean("remember", true);
                    editor.apply();
                }
            } else {
                // 登录失败
                Toast.makeText(getApplicationContext(), getString(R.string.network_error)
                        + ": " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
