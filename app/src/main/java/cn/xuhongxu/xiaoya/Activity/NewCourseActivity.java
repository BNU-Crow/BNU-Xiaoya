package cn.xuhongxu.xiaoya.Activity;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import cn.xuhongxu.Assist.Department;
import cn.xuhongxu.Assist.EducationLevel;
import cn.xuhongxu.Assist.Speciality;
import cn.xuhongxu.Assist.TableCourse;
import cn.xuhongxu.xiaoya.Fragment.DepartmentFragment;
import cn.xuhongxu.xiaoya.Fragment.EducationLevelFragment;
import cn.xuhongxu.xiaoya.Fragment.GradeFragment;
import cn.xuhongxu.xiaoya.Fragment.SpecialityFragment;
import cn.xuhongxu.xiaoya.Fragment.TableCourseFragment;
import cn.xuhongxu.xiaoya.Helper.TimetableHelper;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;
import cn.xuhongxu.xiaoya.YaApplication;

public class NewCourseActivity extends AppCompatActivity implements
        TableCourseFragment.OnListFragmentInteractionListener,
        SpecialityFragment.OnListFragmentInteractionListener,
        DepartmentFragment.OnListFragmentInteractionListener,
        EducationLevelFragment.OnListFragmentInteractionListener,
        GradeFragment.OnListFragmentInteractionListener {

    private FragmentManager fragmentManager;
    String level, grade, dept, speciality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.flContent, new EducationLevelFragment())
                .commit();

    }

    @Override
    public void onListFragmentInteraction(EducationLevel item) {
        level = item.getCode();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, new GradeFragment())
                .commit();
    }

    @Override
    public void onListFragmentInteraction(String item) {
        grade = item;
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, new DepartmentFragment())
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Department item) {
        dept = item.getCode();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, SpecialityFragment.newInstance(grade, level, dept))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Speciality item) {
        speciality = item.getCode();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.flContent, TableCourseFragment.newInstance(grade, level, dept, speciality))
                .commit();
    }

    @Override
    public void onListFragmentInteraction(TableCourse item) {
        TimetableHelper helper = new TimetableHelper(this);


        helper.parseTable(helper.calcWeek());
        for (int i = 1; i <= helper.getWeekCount(); ++i) {
            ArrayList<TimeTableView.Rectangle> curRects = helper.parseCourse(item, i);
            ArrayList<TimeTableView.Rectangle> rects = helper.parseTable(i);
            for (TimeTableView.Rectangle rect : rects) {
                for (TimeTableView.Rectangle curRect : curRects) {
                    if (curRect.day == rect.day && curRect.start <= rect.end && curRect.end >= rect.start) {
                        // 交叉
                        Snackbar.make(findViewById(R.id.flContent), R.string.has_class, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        }

        helper.getTableCourses().add(item);
        helper.saveCourses();
        setResult(RESULT_OK);
        finish();
    }
}
