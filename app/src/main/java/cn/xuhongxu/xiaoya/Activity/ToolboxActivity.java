package cn.xuhongxu.xiaoya.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
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

import cn.xuhongxu.xiaoya.Adapter.ViewPagerFragmentAdapter;
import cn.xuhongxu.xiaoya.Fragment.BorrowBookFragment;
import cn.xuhongxu.xiaoya.Fragment.ClassroomFragment;
import cn.xuhongxu.xiaoya.Fragment.GatewayFragment;
import cn.xuhongxu.xiaoya.Fragment.LibraryFragment;
import cn.xuhongxu.xiaoya.R;

public class ToolboxActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

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
}
