package com.xuhongxu.xiaoya.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SignUpCallback;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.xuhongxu.Assist.NeedLoginException;
import com.xuhongxu.Assist.SchoolworkAssist;
import com.xuhongxu.Assist.StudentDetails;
import com.xuhongxu.xiaoya.Fragment.CancelCourseFragment;
import com.xuhongxu.xiaoya.Fragment.ElectiveCourseFragment;
import com.xuhongxu.xiaoya.Fragment.ExamArrangementFragment;
import com.xuhongxu.xiaoya.Fragment.ExamRoundFragment;
import com.xuhongxu.xiaoya.Fragment.HomeFragment;
import com.xuhongxu.xiaoya.Fragment.ScoreFragment;
import com.xuhongxu.xiaoya.Fragment.SelectResultFragment;
import com.xuhongxu.xiaoya.Fragment.PlanChildCourseFragment;
import com.xuhongxu.xiaoya.Fragment.PlanCourseFragment;
import com.xuhongxu.xiaoya.Fragment.SelectCourseFragment;
import com.xuhongxu.xiaoya.R;
import com.xuhongxu.xiaoya.YaApplication;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        SelectCourseFragment.OnFragmentInteractionListener,
        PlanCourseFragment.OnFragmentInteractionListener,
        PlanChildCourseFragment.OnListFragmentInteractionListener,
        ExamRoundFragment.OnFragmentInteractionListener,
        ExamArrangementFragment.OnListFragmentInteractionListener,
        SelectResultFragment.OnFragmentInteractionListener,
        ElectiveCourseFragment.OnFragmentInteractionListener,
        CancelCourseFragment.OnFragmentInteractionListener,
        ScoreFragment.OnFragmentInteractionListener {

    boolean registered = false;
    private static final String TAG = MainActivity.class.getName();
    YaApplication app;

    private ImageView avatarView;
    private TextView usernameView;
    private TextView userIDView;

    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private int currentFragmentId = -1;
    private int fragmentId;
    private boolean isBack = false;


    private TabLayout tabLayout;

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVAnalytics.trackAppOpened(getIntent());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert mDrawer != null;
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBack) {
                    onBackPressed();
                }
            }
        });
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                isBack = fragmentManager.getBackStackEntryCount() > 0;
                showBackToolbarArrow(isBack);
            }
        });

        View navHeader = navigationView.getHeaderView(0);

        // 全局应用程序
        app = (YaApplication) getApplication();
        // 获取Avatar
        avatarView = (ImageView) navHeader.findViewById(R.id.imageAvatar);
        usernameView = (TextView) navHeader.findViewById(R.id.textUsername);
        userIDView = (TextView) navHeader.findViewById(R.id.textUserID);

        if (savedInstanceState == null) {
            changeFragment(R.id.nav_home);
        }

    }

    private void showBackToolbarArrow(boolean enabled) {

        ValueAnimator valueAnimator;

        if (enabled) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (getSupportActionBar() != null) {
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
                }
            });
        } else {
            valueAnimator = ValueAnimator.ofFloat(1, 0);
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        }

        // Play the hamburger icon/back arrow animation based on the ValueAnimator.ofFloat.
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animationOffset = (float) animation.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawer, animationOffset);
            }
        });
        valueAnimator.setDuration(400);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_feedback:
                builder.setTitle(R.string.action_feedback);
                builder.setMessage(R.string.feedback_message);
                builder.setPositiveButton(R.string.action_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("QQGroup", "334180755");
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            case R.id.action_about:
                builder.setTitle(R.string.action_about);
                builder.setMessage(R.string.about);
                builder.setPositiveButton(R.string.contact_me, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse("http://xuhongxu.com/");
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(int id) {

        Class fragmentClass = null;
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_select) {
            // 选课
            fragmentClass = SelectCourseFragment.class;
        } else if (id == R.id.nav_test) {
            // 考试
            fragmentClass = ExamRoundFragment.class;
        } else if (id == R.id.nav_score) {
            // 成绩
            fragmentClass = ScoreFragment.class;
        } /* else if (id == R.id.nav_evaluate) {
            // 评教
            boolean show = false;
            if (app.getAssist().getUsername().contains("20141121"))
                show = true;

            for (String u : getResources().getStringArray(R.array.evaluation_users)) {
                if (app.getAssist().getUsername().contains(u)) {
                    show = true;
                    break;
                }
            }

            if (show)
                fragmentClass = EvaluationFragment.class;
        } */ else if (id == R.id.nav_logout) {
            reLogin(true, true);
            return;
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return;
        }

        if (fragmentClass != null) {

            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(fragmentId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            if (currentFragmentId != id) {
                currentFragmentId = id;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fragmentId = fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.flContent, fragment)
                        .commitAllowingStateLoss();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        changeFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                app.getAssist().login();
            } catch (Exception e) {
                return e.getMessage();
            }

            return getString(R.string.succeed_login);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                result = "网络错误";
            }
            if (!getString(R.string.succeed_login).contentEquals(result)) {
                // 登录失败
                reLogin(true);
            }
        }
    }

    private void reLogin() {
        reLogin(false, false);
    }

    private void reLogin(boolean back) {
        reLogin(back, false);
    }

    private void reLogin(boolean back, boolean logout) {
        if (!back && app.getAssist() != null) {
            new LoginTask().execute();
            return;
        }
        AVUser.logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        if (logout) {
            intent.putExtra(LoginActivity.MESSAGE_LOGOUT, true);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onReLogin(boolean back) {
        // reLogin(back);
    }

    @Override
    public void onPlanCourseSelected(int pos) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, PlanChildCourseFragment.newInstance(pos))
                .commit();
    }

    @Override
    public void onExamRoundSelected(int pos) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, ExamArrangementFragment.newInstance(pos))
                .commit();
    }


    private class StudentDetailsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            View view = findViewById(android.R.id.content);
            assert view != null;
            try {
                app.setStudentDetails(app.getAssist().getStudentDetails());
                return null;
            } catch (NeedLoginException needLogin) {
                return getString(R.string.login_timeout);
            } catch (IOException e) {
                return getString(R.string.network_error);
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                loadAvatar();
                final StudentDetails sd = app.getStudentDetails();
                usernameView.setText(sd.getName());
                userIDView.setText(sd.getId());

                if (registered) return;

                PushService.subscribe(getApplicationContext(), "College-" + sd.getCollege(), MainActivity.class);
                PushService.subscribe(getApplicationContext(), "Grade-" + sd.getRegistrationGrade(), MainActivity.class);

                AVUser user = new AVUser();// 新建 AVUser 对象实例
                user.setUsername(sd.getId());// 设置用户名
                user.setPassword(sd.getNumber() + sd.getGaokaoID());// 设置密码
                user.setEmail(sd.getEmail());// 设置邮箱
                // user.setMobilePhoneNumber(sd.getMobile());
                user.put("RegistrationTime", sd.getRegistrationTime());
                user.put("Nationality", sd.getNationality());
                user.put("AdmitSpeciality", sd.getSpeciality());
                user.put("MiddleSchool", sd.getMiddleSchool());
                user.put("ClassName", sd.getClassName());
                user.put("CultureStandard", sd.getCultureStandard());
                user.put("CollegeWill", sd.getCollegeWill());
                user.put("SchoolSystem", sd.getSchoolSystem());
                user.put("EducationLevel", sd.getEducationLevel());
                user.put("Name", sd.getName());
                user.put("Number", sd.getNumber());
                user.put("AvatarID", sd.getAvatarID());
                user.put("College", sd.getCollege());
                user.put("Gender", sd.getGender());
                user.put("Address", sd.getAddress());
                user.put("Pinyin", sd.getPinyin());
                user.put("mobile", sd.getMobile());
                user.put("RegistrationGrade", sd.getRegistrationGrade());
                user.put("Birthday", sd.getBirthday());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null || e.getCode() == AVException.USERNAME_TAKEN || e.getCode() == AVException.EMAIL_TAKEN || e.getCode() == AVException.USER_MOBILE_PHONENUMBER_TAKEN) {
                            // 注册成功
                            registered = true;
                            AVUser.logInInBackground(sd.getId(), sd.getNumber() + sd.getGaokaoID(), new LogInCallback<AVUser>() {
                                @Override
                                public void done(AVUser avUser, AVException e) {
                                    AVInstallation.getCurrentInstallation().put("user_id", avUser);
                                }
                            });
                        } else {
                            Log.e(getClass().getName(), e.getMessage());
                        }
                    }
                });

            } else if (result.equals(getString(R.string.login_timeout)) || result.equals(getString(R.string.network_error))) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                reLogin(true);
            }
        }
    }

    private void getStudentDetails() {
        new StudentDetailsTask().execute();
    }

    private class LoadAvatarTask extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                URLConnection conn = new URL(params[0]).openConnection();
                conn.setDoOutput(true);
                conn.setRequestProperty(SchoolworkAssist.HEADER_REFERER, SchoolworkAssist.REFERER);
                for (Map.Entry<String, String> cookie : app.getAssist().getCookies().entrySet()) {
                    conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
                }
                return Drawable.createFromStream(conn.getInputStream(), params[1]);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (result != null) {
                assert avatarView != null;
                avatarView.setImageDrawable(result);
            }
        }

    }

    private void loadAvatar() {
        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (pf.getBoolean("show_avatar", true)) {
            String avatarURL = "http://zyfw.bnu.edu.cn/share/showphoto.jsp?zpid=" + app.getStudentDetails().getAvatarID();
            new LoadAvatarTask().execute(avatarURL, getString(R.string.avatar_desp));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (app.getAssist() == null) {
            reLogin(true);
        } else {
            getStudentDetails();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("assist", app.getAssist());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (app.getAssist() == null) {
            app.setAssist((SchoolworkAssist) savedInstanceState.getParcelable("assist"));
            reLogin();
        }
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
