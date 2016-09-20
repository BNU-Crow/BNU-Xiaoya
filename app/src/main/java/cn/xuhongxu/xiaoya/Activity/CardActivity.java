package cn.xuhongxu.xiaoya.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.xuhongxu.Card.CardAssist;
import cn.xuhongxu.xiaoya.Adapter.ViewPagerFragmentAdapter;
import cn.xuhongxu.xiaoya.Fragment.CardInfoFragment;
import cn.xuhongxu.xiaoya.Fragment.CardTransferFragment;
import cn.xuhongxu.xiaoya.Fragment.ElectiveCourseFragment;
import cn.xuhongxu.xiaoya.Fragment.PlanCourseFragment;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

public class CardActivity extends AppCompatActivity
        implements CardTransferFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SharedPreferences preferences;
    private String username, password;

    private ProgressDialog progressDialog;
    YaApplication app;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                fetchCode();
            } else {
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = (YaApplication) getApplication();


        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        username = preferences.getString("username", "");
        password = preferences.getString("password", "");


        if (username.isEmpty() || password.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("justLogin", true);
            startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
        } else {
            fetchCode();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void fetchCode() {
        new FetchCodeTask().execute(new FetchCodeParams(username, password, new OnLoadListener() {
            @Override
            public void onLoad(String code) {
                progressDialog = ProgressDialog.show(CardActivity.this, "登录中", "正在登录统一身份认证平台...",
                        true);
                new LoginTask().execute(code);
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onTransfer(final String money, final String pwd) {
        new FetchCodeTask().execute(new FetchCodeParams(username, password, new OnLoadListener() {
            @Override
            public void onLoad(String code) {
                new TransferTask().execute(new TransferParams(money, pwd, code));
            }
        }));
    }

    private class FetchCodeParams {
        public String username, password;
        public OnLoadListener listener;

        FetchCodeParams(String username, String password, OnLoadListener listener) {
            this.username = username;
            this.password = password;
            this.listener = listener;
        }
    }

    interface OnLoadListener {
        void onLoad(String code);
    }

    private class TransferParams {
        public String money, query_pwd, code;

        TransferParams(String money, String query_pwd, String code) {
            this.money = money;
            this.query_pwd = query_pwd;
            this.code = code;
        }
    }

    private class TransferTask extends AsyncTask<TransferParams, Void, String> {

        @Override
        protected String doInBackground(TransferParams... params) {
            try {
                return app.getCardAssist().transfer(params[0].query_pwd, params[0].code, params[0].money);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
            builder.setMessage(result);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }
    }

    private class FetchCodeTask extends AsyncTask<FetchCodeParams, Void, Drawable> {


        private OnLoadListener listener;

        @Override
        protected Drawable doInBackground(FetchCodeParams... params) {
            CardAssist assist = new CardAssist(params[0].username, params[0].password);
            listener = params[0].listener;
            app.setCardAssist(assist);

            try {
                return assist.fetchCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(CardActivity.this, "无法获取验证码", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                final Dialog dialog = new Dialog(CardActivity.this);

				//setting custom layout to dialog
				dialog.setContentView(R.layout.valid_code_dialog);
				dialog.setTitle(R.string.code);

                final TextView textView = (TextView) dialog.findViewById(R.id.edit_code);
				ImageView image = (ImageView) dialog.findViewById(R.id.code_view);
				image.setImageDrawable(result);

				//adding button click event
				Button okButton = (Button) dialog.findViewById(R.id.confirm_button);
				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
                        String code = String.valueOf(textView.getText());
                        listener.onLoad(code);
                        dialog.dismiss();
					}
				});
				dialog.show();
            }
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                app.getCardAssist().fetchKeypad()
                app.getCardAssist().login(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result != null) {
                Toast.makeText(CardActivity.this, result, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
                adapter.add(CardInfoFragment.class, getString(R.string.card_basic_info));
                adapter.add(CardTransferFragment.class, getString(R.string.transfer));
                /*
                adapter.add(CardNetFeeFragment.class, getString(R.string.net_fee));
                adapter.add(CardLossFragment.class, getString(R.string.loss));
                adapter.add(CardQueryPayFragment.class, getString(R.string.query_pay));
                */
                viewPager.setAdapter(adapter);
            }
        }
    }
}
