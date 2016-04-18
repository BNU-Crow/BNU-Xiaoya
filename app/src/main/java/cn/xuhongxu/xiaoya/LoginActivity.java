package cn.xuhongxu.xiaoya;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.xuhongxu.Assist.LoginException;
import cn.xuhongxu.Assist.SchoolworkAssist;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private EditText editUsername;
    private EditText editPassword;
    private Switch switchRemember;

    private ProgressDialog progressDialog;

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SchoolworkAssist assist = new SchoolworkAssist(params[0], params[1]);

            try {
                assist.login();
            } catch (LoginException e) {
                return e.getMessage();
            } catch (IOException e) {
                return e.getMessage();
            }

            ((YaApplication)getApplication()).setAssist(assist);
            return getString(R.string.succeed_login);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            if (getString(R.string.succeed_login).contentEquals(result)) {
                loginSucceed();
            } else {
                // 登录失败
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

    // 登录成功
    protected void loginSucceed() {

        // 记住我
        SharedPreferences.Editor editor = preferences.edit();
        if (switchRemember.isChecked()) {
            editor.putString("username", editUsername.getText().toString());
            editor.putString("password", editPassword.getText().toString());
            editor.putBoolean("remember", true);
        } else {
            editor.remove("username");
            editor.remove("password");
            editor.putBoolean("remember", false);
        }
        editor.apply();

        // 启动主活动
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editUsername = (EditText)findViewById(R.id.edit_username);
        editPassword = (EditText)findViewById(R.id.edit_password);
        switchRemember = (Switch)findViewById(R.id.switch_remember);

        preferences =
                getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        if (preferences.getBoolean("remember", false)) {
            switchRemember.setChecked(true);
            editUsername.setText(preferences.getString("username", "?"));
            editPassword.setText(preferences.getString("password", "?"));
            login(null);
        }

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

    }

    public void login(View view) {
        progressDialog = ProgressDialog.show(this, "登录中", "正在登录统一身份认证平台...", true);
        new LoginTask().execute(editUsername.getText().toString(),
                editPassword.getText().toString());
    }
}
