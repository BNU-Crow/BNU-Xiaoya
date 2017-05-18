package cn.xuhongxu.xiaoya.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.xuhongxu.xiaoya.Adapter.ViewPagerFragmentAdapter;
import cn.xuhongxu.xiaoya.Fragment.BorrowBookFragment;
import cn.xuhongxu.xiaoya.Fragment.ClassroomFragment;
import cn.xuhongxu.xiaoya.Fragment.ExerciseFragment;
import cn.xuhongxu.xiaoya.Fragment.GatewayFragment;
import cn.xuhongxu.xiaoya.Fragment.LibraryFragment;
import cn.xuhongxu.xiaoya.Fragment.LibrarySeatFragment;
import cn.xuhongxu.xiaoya.R;

public class ToolboxActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        adapter.add(GatewayFragment.class, getString(R.string.gateway));
        adapter.add(ClassroomFragment.class, getString(R.string.classroom));
        adapter.add(LibraryFragment.class, getString(R.string.search_library));
        adapter.add(BorrowBookFragment.class, getString(R.string.borrow_book));
        adapter.add(LibrarySeatFragment.class, getString(R.string.library_seat));
        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);
        if (Integer.valueOf(preferences.getString("username", "000000000000").substring(0, 4)) >= 2016) {
            adapter.add(ExerciseFragment.class, getString(R.string.exercise_query));
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login_library) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.login_dialog, null);
            alert.setView(layout);
            alert.setTitle("登录图书馆");
            final SharedPreferences lib_pref =
                    getSharedPreferences(getString(R.string.library_key),
                            Context.MODE_PRIVATE);
            final EditText usernameInput = (EditText) layout.findViewById(R.id.edit_username);
            final EditText passwordInput = (EditText) layout.findViewById(R.id.edit_password);
            final String username = lib_pref.getString("username", "");
            final String password = lib_pref.getString("password", "");
            usernameInput.setText(username);
            passwordInput.setText(password);
            alert.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = lib_pref.edit();
                    String usnm = usernameInput.getText().toString();
                    String pwd = passwordInput.getText().toString();
                    editor.putString("username", usnm);
                    editor.putString("password", pwd);
                    editor.apply();
                    dialogInterface.dismiss();
                }
            });
            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alert.show();
            return true;
        }
        return false;
    }
}
