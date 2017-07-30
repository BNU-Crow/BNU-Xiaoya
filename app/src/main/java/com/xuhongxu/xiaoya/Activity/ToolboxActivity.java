package com.xuhongxu.xiaoya.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVAnalytics;

import java.util.HashSet;

import com.xuhongxu.xiaoya.Adapter.ViewPagerFragmentAdapter;
import com.xuhongxu.xiaoya.Fragment.BorrowBookFragment;
import com.xuhongxu.xiaoya.Fragment.ClassroomFragment;
import com.xuhongxu.xiaoya.Fragment.ExerciseFragment;
import com.xuhongxu.xiaoya.Fragment.GatewayFragment;
import com.xuhongxu.xiaoya.Fragment.LibraryFragment;
import com.xuhongxu.xiaoya.Fragment.LibrarySeatFragment;
import com.xuhongxu.xiaoya.R;

public class ToolboxActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SharedPreferences preferences;

    public interface LibraryLoginListener {
        void logined(String usnm, String pwd);
    }

    private HashSet<LibraryLoginListener> listeners = new HashSet<>();

    public void addLibraryLoginListener(LibraryLoginListener listener) {
        listeners.add(listener);
    }

    public void removeLibraryLoginListener(LibraryLoginListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

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
        adapter.add(LibraryFragment.class, getString(R.string.search_library));
        adapter.add(ClassroomFragment.class, getString(R.string.classroom));
        adapter.add(LibrarySeatFragment.class, getString(R.string.library_seat));
        adapter.add(BorrowBookFragment.class, getString(R.string.borrow_book));
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
            View layout = inflater.inflate(R.layout.dialog_login, null);
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
            alert.setPositiveButton("登录", (dialogInterface, i) -> {
                SharedPreferences.Editor editor = lib_pref.edit();
                String usnm = usernameInput.getText().toString();
                String pwd = passwordInput.getText().toString();
                editor.putString("username", usnm);
                editor.putString("password", pwd);
                editor.apply();

                viewPager.getAdapter().notifyDataSetChanged();

                for (LibraryLoginListener listener : listeners) {
                    listener.logined(usnm, pwd);
                }

                dialogInterface.dismiss();
            });
            alert.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
            alert.show();
            return true;
        }
        return false;
    }
}
