package cn.xuhongxu.xiaoya.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.scottyab.aescrypt.AESCrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.xuhongxu.Assist.ExamRound;
import cn.xuhongxu.Assist.LoginException;
import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.Assist.SchoolworkAssist;
import cn.xuhongxu.Assist.StudentDetails;
import cn.xuhongxu.Assist.StudentInfo;
import cn.xuhongxu.xiaoya.Fragment.EvaluationCourseFragment;
import cn.xuhongxu.xiaoya.Fragment.EvaluationFragment;
import cn.xuhongxu.xiaoya.Fragment.ExamArrangementFragment;
import cn.xuhongxu.xiaoya.Fragment.ExamRoundFragment;
import cn.xuhongxu.xiaoya.Fragment.HomeFragment;
import cn.xuhongxu.xiaoya.Fragment.ScoreFragment;
import cn.xuhongxu.xiaoya.Model.Student;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ExamRoundFragment.OnFragmentInteractionListener,
        ExamArrangementFragment.OnListFragmentInteractionListener,
        EvaluationFragment.OnFragmentInteractionListener,
        EvaluationCourseFragment.OnListFragmentInteractionListener,
        ScoreFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();
    YaApplication app;

    ImageView avatarView;
    TextView usernameView;
    TextView userIDView;

    FragmentManager fragmentManager;
    Fragment fragment;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    int currentFragmentId = -1;
    int fragmentId;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, "b9ae7e8f85b4a31144bd382619290008");
        BmobInstallation.getCurrentInstallation().save();
        BmobPush.startWork(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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

    public void showBackToolbarArrow(boolean enabled) {

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
        switch (id) {
            case R.id.action_settings:
                dumpFragment();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void changeFragment(int id) {

        Class fragmentClass = null;
        fragment = null;

        int titleId = R.string.app_name;

        if (id == R.id.nav_home) {
            fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_select) {
            // TODO: 选课


        } else if (id == R.id.nav_test) {
            // 考试
            titleId = R.string.Test;
            fragmentClass = ExamRoundFragment.class;
        } else if (id == R.id.nav_score) {
            // TODO: 成绩
            titleId = R.string.Score;
            fragmentClass = ScoreFragment.class;
        } else if (id == R.id.nav_evaluate) {
            // 评教
            titleId = R.string.Evaluate;
            fragmentClass = EvaluationFragment.class;
        } else if (id == R.id.nav_logout) {
            reLogin(true, true);
        }

        setTitle(titleId);

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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        if (logout) {
            intent.putExtra(LoginActivity.MESSAGE_LOGOUT, true);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onReLogin(boolean back) {
        reLogin(back);
    }

    @Override
    public void onExamRoundSelected(int pos) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, ExamArrangementFragment.newInstance(pos))
                .commitAllowingStateLoss();
    }

    @Override
    public void onEvaluateCourse(int pos) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, EvaluationCourseFragment.newInstance(pos))
                .commitAllowingStateLoss();
    }

    public void dumpFragment() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            fragmentManager.dump("", null, new PrintWriter(outputStream, true), null);
            final String s = new String(outputStream.toByteArray(), "UTF-8");
            Log.i("HI", s);
        } catch (Exception e) {
            Log.e("HI", e.getLocalizedMessage());
        }
    }

    private class StudentDetailsTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            View view = findViewById(android.R.id.content);
            assert view != null;
            try {
                if (app.getStudentDetails() == null) {
                    app.setStudentDetails(app.getAssist().getStudentDetails());
                    return 0;
                }
            } catch (NeedLoginException needLogin) {
                return R.string.login_timeout;
            } catch (IOException e) {
                return R.string.network_error;
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                loadAvatar();
                StudentDetails sd = app.getStudentDetails();
                usernameView.setText(sd.getName());
                userIDView.setText(sd.getId());

                Student student = new Student();
                student.setRegistrationTime(sd.getRegistrationTime());
                student.setNationality(sd.getNationality());
                student.setSpeciality(sd.getSpeciality());
                student.setEmail(sd.getEmail());
                student.setMobilePhoneNumber(sd.getMobile());
                student.setMiddleSchool(sd.getMiddleSchool());
                student.setClassName(sd.getClassName());
                student.setStudentId(sd.getId());
                student.setCultureStandard(sd.getCultureStandard());
                student.setCollegeWill(sd.getCollegeWill());
                student.setSchoolSystem(sd.getSchoolSystem());
                student.setIdNumber(sd.getIdNumber());
                student.setEducationLevel(sd.getEducationLevel());
                student.setName(sd.getName());
                student.setGaokaoID(sd.getGaokaoID());
                student.setNumber(sd.getNumber());
                student.setAvatarID(sd.getAvatarID());
                student.setCollege(sd.getCollege());
                student.setGender(sd.getGender());
                student.setAddress(sd.getAddress());
                student.setPinyin(sd.getPinyin());
                student.setRegistrationGrade(sd.getRegistrationGrade());
                student.setBirthday(sd.getBirthday());
                student.update(app.student.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            Log.e(TAG, "done: update user info error", e);
                        }
                    }
                });

            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                reLogin();
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
        String avatarURL = "http://zyfw.bnu.edu.cn/share/showphoto.jsp?zpid=" + app.getStudentDetails().getAvatarID();
        new LoadAvatarTask().execute(avatarURL, getString(R.string.avatar_desp));
    }

    @Override
    public void onStart() {
        super.onStart();

        // 判断用户存在
        BmobQuery<Student> query = new BmobQuery<>();
        query.addWhereEqualTo("username", app.getAssist().getUsername());
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        // 用户不存在
                        app.student = new Student();
                        app.student.setUsername(app.getAssist().getUsername());
                        StudentInfo si = app.getStudentInfo();
                        try {
                            app.student.setPassword(AESCrypt.encrypt(si.getId(), getString(R.string.encryptedMsg)));
                        } catch (GeneralSecurityException e1) {
                            Log.e(TAG, "done: encrypting error", e1);
                            Toast.makeText(getApplicationContext(), R.string.encrypt_error, Toast.LENGTH_LONG).show();
                        }
                        app.student.setStudentId(si.getId());
                        app.student.setGrade(si.getGrade());
                        app.student.setSpeciality(si.getSpeciality());
                        app.student.setSpecialityCode(si.getSpecialityCode());
                        app.student.setAcademicYear(si.getAcademicYear());
                        app.student.setSchoolTerm(si.getSchoolTerm());
                        app.student.signUp(new SaveListener<Student>() {
                            @Override
                            public void done(Student student, BmobException e) {
                                if (e == null) {
                                    // 获取用户详细信息
                                    getStudentDetails();
                                } else {
                                    Log.e(TAG, "done: signup error", e);
                                }
                            }
                        });
                    } else {
                        try {
                            BmobUser.loginByAccount(app.getAssist().getUsername(),
                                    AESCrypt.encrypt(
                                            app.getStudentInfo().getId(),
                                            getString(R.string.encryptedMsg)),
                                    new LogInListener<Student>() {
                                        @Override
                                        public void done(Student student, BmobException e) {
                                            if (e == null) {
                                                app.student = student;
                                                // 获取用户详细信息
                                                getStudentDetails();
                                            } else {
                                                Log.e(TAG, "done: login error", e);
                                                reLogin(true);
                                            }
                                        }
                                    });
                        } catch (GeneralSecurityException e1) {
                            Log.e(TAG, "done: encrypting error", e1);
                            Toast.makeText(getApplicationContext(), R.string.encrypt_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
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
}
