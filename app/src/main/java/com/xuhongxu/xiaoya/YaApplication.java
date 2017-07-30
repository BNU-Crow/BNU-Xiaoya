package com.xuhongxu.xiaoya;

import android.app.Application;
import android.content.Intent;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;

import java.io.IOException;
import java.util.ArrayList;

import com.xuhongxu.Assist.CancelCourse;
import com.xuhongxu.Assist.ElectiveCourse;
import com.xuhongxu.Assist.EvaluationCourse;
import com.xuhongxu.Assist.EvaluationItem;
import com.xuhongxu.Assist.ExamArrangement;
import com.xuhongxu.Assist.ExamRound;
import com.xuhongxu.Assist.ExamScore;
import com.xuhongxu.Assist.NeedLoginException;
import com.xuhongxu.Assist.PlanChildCourse;
import com.xuhongxu.Assist.PlanCourse;
import com.xuhongxu.Assist.SchoolworkAssist;
import com.xuhongxu.Assist.SelectionResult;
import com.xuhongxu.Assist.StudentDetails;
import com.xuhongxu.Assist.StudentInfo;
import com.xuhongxu.xiaoya.Activity.ErrorActivity;
import com.xuhongxu.xiaoya.Service.QueryScoreService;

/**
 * Created by xuhongxu on 16/4/12.
 * <p/>
 * YaApplication
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class YaApplication extends Application {

    private SchoolworkAssist assist;
    private ArrayList<EvaluationItem> evaluationItemList;
    private ArrayList<EvaluationCourse> evaluationCourses;
    private ArrayList<ExamRound> examRounds;
    private ArrayList<ExamArrangement> examArrangement;
    private ArrayList<ExamScore> examScores;
    private StudentDetails studentDetails;
    private StudentInfo studentInfo;
    private ArrayList<PlanCourse> planCourses;
    private ArrayList<PlanChildCourse> planChildCourses;
    private ArrayList<SelectionResult> selectionResults;
    private ArrayList<ElectiveCourse> electiveCourses;
    private ArrayList<CancelCourse> cancelCourses;

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this, "vXdeiDEvPWNif2dvtCVc7Q1N-9Nh9j0Va", "CVlURpsG9thauLU2xUwnbuFi");
        AVAnalytics.enableCrashReport(this, true);

        evaluationItemList = new ArrayList<>();
        evaluationCourses = new ArrayList<>();
        examRounds = new ArrayList<>();
        examArrangement = new ArrayList<>();
        examScores = new ArrayList<>();

        planCourses = new ArrayList<>();
        planChildCourses = new ArrayList<>();
        selectionResults = new ArrayList<>();
        electiveCourses = new ArrayList<>();
        cancelCourses = new ArrayList<>();


        //            e.printStackTrace();
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

    }

    public void clearAll() {
        studentInfo = null;
        studentDetails = null;

        evaluationItemList = new ArrayList<>();
        evaluationCourses = new ArrayList<>();
        examRounds = new ArrayList<>();
        examArrangement = new ArrayList<>();
        examScores = new ArrayList<>();

        planCourses = new ArrayList<>();
        planChildCourses = new ArrayList<>();
        selectionResults = new ArrayList<>();
        electiveCourses = new ArrayList<>();
        cancelCourses = new ArrayList<>();
    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        // e.printStackTrace();
        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String stackTraceString = "";
        for (StackTraceElement trace : e.getStackTrace()) {
            stackTraceString += "\n" + trace.toString();
        }
        stackTraceString += "\n\nCause:\n" + e.getCause().getMessage() + "\n";
        for (StackTraceElement trace : e.getCause().getStackTrace()) {
            stackTraceString += "\n" + trace.toString();
        }
        intent.putExtra("Error", e.getMessage() + "\n" + stackTraceString);
        startActivity(intent);
        System.exit(1);
    }

    public SchoolworkAssist getAssist() {
        return assist;
    }

    public void setAssist(SchoolworkAssist assist) {
        this.assist = assist;
    }

    public ArrayList<EvaluationItem> getEvaluationItemList() {
        return evaluationItemList;
    }

    public void setEvaluationItemList(ArrayList<EvaluationItem> evaluationItemList) {
        this.evaluationItemList = evaluationItemList;
    }

    public ArrayList<EvaluationCourse> getEvaluationCourses() {
        return evaluationCourses;
    }

    public void setEvaluationCourses(ArrayList<EvaluationCourse> evaluationCourses) {
        this.evaluationCourses = evaluationCourses;
    }

    public ArrayList<ExamRound> getExamRounds() {
        return examRounds;
    }

    public void setExamRounds(ArrayList<ExamRound> examRounds) {
        this.examRounds = examRounds;
    }

    public ArrayList<ExamArrangement> getExamArrangement() {
        return examArrangement;
    }

    public void setExamArrangement(ArrayList<ExamArrangement> examArrangement) {
        this.examArrangement = examArrangement;
    }

    public ArrayList<ExamScore> getExamScores() {
        return examScores;
    }

    public void setExamScores(ArrayList<ExamScore> examScores) {
        this.examScores = examScores;
    }

    public StudentDetails getStudentDetails() {
        return studentDetails;
    }

    public void setStudentDetails(StudentDetails studentDetails) {
        this.studentDetails = studentDetails;
    }

    public StudentInfo getStudentInfo() {
        if (studentInfo == null && assist != null) {
            try {
                studentInfo = assist.getStudentInfo();
            } catch (IOException | NeedLoginException e) {
                e.printStackTrace();
            }
        }
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
    }

    public ArrayList<PlanCourse> getPlanCourses() {
        return planCourses;
    }

    public void setPlanCourses(ArrayList<PlanCourse> planCourses) {
        this.planCourses = planCourses;
    }

    public ArrayList<PlanChildCourse> getPlanChildCourses() {
        return planChildCourses;
    }

    public void setPlanChildCourses(ArrayList<PlanChildCourse> planChildCourses) {
        this.planChildCourses = planChildCourses;
    }

    public ArrayList<SelectionResult> getSelectionResults() {
        return selectionResults;
    }

    public void setSelectionResults(ArrayList<SelectionResult> selectionResults) {
        this.selectionResults = selectionResults;
    }

    public ArrayList<ElectiveCourse> getElectiveCourses() {
        return electiveCourses;
    }

    public void setElectiveCourses(ArrayList<ElectiveCourse> electiveCourses) {
        this.electiveCourses = electiveCourses;
    }

    public ArrayList<CancelCourse> getCancelCourses() {
        return cancelCourses;
    }

    public void setCancelCourses(ArrayList<CancelCourse> cancelCourses) {
        this.cancelCourses = cancelCourses;
    }
}

