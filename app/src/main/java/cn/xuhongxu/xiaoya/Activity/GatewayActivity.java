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
import android.widget.ViewFlipper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

import cn.xuhongxu.xiaoya.R;

public class GatewayActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private Switch switchRemember;
    private ViewFlipper viewFlipper;
    private Button loginButton, logoutButton;
    private TextView infoView;

    private ProgressDialog progressDialog;

    private String ip = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editUsername = (TextInputEditText) findViewById(R.id.edit_username);
        editPassword = (TextInputEditText) findViewById(R.id.edit_password);
        switchRemember = (Switch) findViewById(R.id.switch_remember);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        loginButton = (Button) findViewById(R.id.login_button);
        logoutButton = (Button) findViewById(R.id.logout_button);
        infoView = (TextView) findViewById(R.id.info);

        preferences =
                getSharedPreferences(getString(R.string.gateway_preference_key),
                        MODE_PRIVATE);

        editUsername.setText(preferences.getString("username", ""));
        if (preferences.getBoolean("remember", false)) {
            editPassword.setText(preferences.getString("password", ""));
            switchRemember.setChecked(true);
        }

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

        if (preferences.getBoolean("isLogin", false)) {
            ip = preferences.getString("ip", "");
            viewFlipper.showNext();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
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

    private class LoginTask extends AsyncTask<String, Void, String> {

        private String type;

        @Override
        protected String doInBackground(String... params) {
            type = params[2];

            try {
                switch (params[2]) {
                    case "login": {
                        Document doc = Jsoup.connect("http://gw.bnu.edu.cn:803/srun_portal_pc.php?ac_id=1&")
                                .timeout(5000)
                                .data("action", "login")
                                .data("ac_id", "1")
                                .data("user_ip", "")
                                .data("nas_ip", "")
                                .data("user_mac", "")
                                .data("url", "")
                                .data("save_me", "1")
                                .data("username", params[0])
                                .data("password", params[1])
                                .post();
                        String body = doc.text();
                        if (body.contains("登录成功")) {

                            String k = "" + Math.floor(Math.random() * (100000 + 1));

                            doc = Jsoup.connect("http://gw.bnu.edu.cn:803/include/auth_action.php?k=" + k)
                                    .timeout(5000)
                                    .data("action", "get_online_info")
                                    .data("key", k)
                                    .post();


                            return "成功" + doc.text();
                        } else {
                            int i = body.indexOf("<p style=\"color:#FF0;\">");
                            i = body.indexOf("(", i);
                            int t = body.indexOf(")", i);
                            return body.substring(i + 1, t);
                        }
                    }
                    case "logout": {
                        Document doc = Jsoup.connect("http://gw.bnu.edu.cn:803/srun_portal_pc.php")
                                .timeout(5000)
                                .data("action", "auto_logout")
                                .data("info", "")
                                .data("user_ip", ip)
                                .post();
                        return "成功";
                    }
                }
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";

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
            if (result.contains("成功")) {
                if (type.equals("login")) {
                    String r = result.substring(2);
                    String[] info = r.split(",");

                    Double rest = (Double.valueOf(info[0]) / 1024 / 1024);
                    int s = Integer.valueOf(info[1]);
                    int m = s / 60;
                    s %= 60;
                    int h = m / 60;
                    m %= 60;

                    infoView.setText(
                            "已用流量：" + rest.toString() + "MB\n" +
                                    "已用时长：" + h + "时" + m + "分" + s + "秒\n" +
                                    "账户余额：" + info[2] + "元\n" +
                                    "IP：" + info[5]
                    );
                    ip = info[5];
                    editor = preferences.edit();
                    editor.putBoolean("isLogin", true);
                    editor.putString("ip", ip);
                    editor.apply();
                    viewFlipper.showNext();
                } else {
                    editor = preferences.edit();
                    editor.putBoolean("isLogin", false);
                    editor.putString("ip", "");
                    editor.apply();
                    viewFlipper.showPrevious();
                }
                if (switchRemember.isChecked()) {
                    editor.putString("password", editPassword.getText().toString());
                    editor.putBoolean("remember", true);
                    editor.apply();
                }
            } else {
                // 登录失败
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
