package cn.xuhongxu.xiaoya.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.xiaoya.Adapter.PlanCourseRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GatewayFragment extends Fragment {

    private SharedPreferences preferences;

    private TextInputEditText editUsername;
    private TextInputEditText editPassword;
    private Switch switchRemember;
    private Button loginButton, logoutButton, forceButton;
    private TextView infoView;

    private ProgressDialog progressDialog;

    private String ip = "";

    public GatewayFragment() {
        // Required empty public constructor
    }

    public static GatewayFragment newInstance() {
        return new GatewayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gateway, container, false);

        editUsername = (TextInputEditText) v.findViewById(R.id.edit_username);
        editPassword = (TextInputEditText) v.findViewById(R.id.edit_password);
        switchRemember = (Switch) v.findViewById(R.id.switch_remember);
        loginButton = (Button) v.findViewById(R.id.login_button);
        logoutButton = (Button) v.findViewById(R.id.logout_button);
        forceButton = (Button) v.findViewById(R.id.force_button);
        infoView = (TextView) v.findViewById(R.id.info);

        preferences =
                getActivity().getSharedPreferences(getString(R.string.gateway_preference_key),
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

        ip = preferences.getString("ip", "");

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
        forceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                force();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(getClass().getName());
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void login() {

        hideKeyboard(getActivity());

        if (!switchRemember.isChecked()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("password", "");
            editor.putBoolean("remember", false);
            editor.apply();
        }

        progressDialog = ProgressDialog.show(getContext(), "登录中", "正在登录网关...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString(),
                "login"
        );
    }

    public void logout() {

        hideKeyboard(getActivity());

        progressDialog = ProgressDialog.show(getContext(), "注销中", "正在注销网关...",
                true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString(),
                "logout"
        );
    }

    public void force() {

        hideKeyboard(getActivity());

        progressDialog = ProgressDialog.show(getContext(), "强制离线中", "正在强制离线网关...",
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
                        Document doc = Jsoup.connect("http://gw.bnu.edu.cn:803/srun_portal_pc.php?ac_id=1&")
                                .timeout(5000)
                                .data("action", "login")
                                .data("ac_id", "1")
                                .data("user_ip", "")
                                .data("nas_ip", "")
                                .data("user_mac", "")
                                .data("url", "")
                                .data("ajax", "1")
                                .data("save_me", "1")
                                .data("username", params[0])
                                .data("password", params[1])
                                .post();
                        String body = doc.text();
                        body = body.split(",")[0];
                        if (body.contains("login_ok")) {

                            String k = "" + Math.floor(Math.random() * (100000 + 1));

                            doc = Jsoup.connect("http://gw.bnu.edu.cn:803/include/auth_action.php?k=" + k)
                                    .timeout(5000)
                                    .data("action", "get_online_info")
                                    .data("key", k)
                                    .post();

                            String res = doc.text();

                            doc = Jsoup.connect("http://ip.bnu.edu.cn")
                                    .timeout(5000)
                                    .get();

                            String where = "";
                            Elements el = doc.getElementsByAttributeValue("color", "blue");
                            if (el.size() > 0) {
                                where = el.text();
                            }

                            doc = Jsoup.connect("http://172.16.202.201:8069/user/status/" + params[0])
                                    .ignoreContentType(true)
                                    .timeout(5000)
                                    .get();
                            JSONObject object = new JSONObject(doc.text());
                            String product = object.getString("ProductsName");

                            return "成功" + res + "," + where + "," + product;
                        } else {
                            return body;
                        }
                    }
                    case "logout": {
                        Document doc = Jsoup.connect("http://gw.bnu.edu.cn:803/srun_portal_pc.php")
                                .timeout(5000)
                                .data("action", "auto_logout")
                                .data("ajax", "1")
                                .data("info", "")
                                .data("user_ip", ip)
                                .post();
                        return doc.text();
                    }
                    case "force": {
                        Document doc = Jsoup.connect("http://gw.bnu.edu.cn:801/include/auth_action.php")
                                .timeout(5000)
                                .data("action", "logout")
                                .data("ajax", "1")
                                .data("username", params[0])
                                .data("password", params[1])
                                .post();
                        return doc.text();
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

            infoView.setText("未登录");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", editUsername.getText().toString());
            editor.apply();
            if (result == null) {
                result = "网络错误";
            }

            View view = getActivity().findViewById(android.R.id.content);
            if (result.contains("成功")) {
                Snackbar.make(view, "成功", Snackbar.LENGTH_SHORT).show();
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
                            "套餐：" + info[7] + "\n" +
                                    "已用流量：" + rest.toString() + "MB\n" +
                                    "已用时长：" + h + "时" + m + "分" + s + "秒\n" +
                                    "账户余额：" + info[2] + "元\n" +
                                    "IP：" + info[5] + "\n" +
                                    "位置：" + info[6]
                    );
                    ip = info[5];
                    editor = preferences.edit();
                    editor.putString("ip", ip);
                    editor.apply();
                } else {
                    editor = preferences.edit();
                    editor.putString("ip", "");
                    editor.apply();
                }
                if (switchRemember.isChecked()) {
                    editor.putString("password", editPassword.getText().toString());
                    editor.putBoolean("remember", true);
                    editor.apply();
                }
            } else {
                // 登录失败
                Snackbar.make(view, result, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
