package cn.xuhongxu.xiaoya;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import cn.xuhongxu.Assist.EvaluationCourse;
import cn.xuhongxu.Assist.EvaluationItem;
import cn.xuhongxu.Assist.ExamArrangement;
import cn.xuhongxu.Assist.ExamRound;
import cn.xuhongxu.Assist.ExamScore;
import cn.xuhongxu.Assist.PlanChildCourse;
import cn.xuhongxu.Assist.PlanCourse;
import cn.xuhongxu.Assist.SchoolworkAssist;
import cn.xuhongxu.Assist.SelectionResult;
import cn.xuhongxu.Assist.StudentDetails;
import cn.xuhongxu.Assist.StudentInfo;
import cn.xuhongxu.xiaoya.Activity.ErrorActivity;
import cn.xuhongxu.xiaoya.Model.Student;

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
    public Student student;
    private ArrayList<PlanCourse> planCourses;
    private ArrayList<PlanChildCourse> planChildCourses;
    private ArrayList<SelectionResult> selectionResults;

    @Override
    public void onCreate() {
        super.onCreate();

        evaluationItemList = new ArrayList<>();
        evaluationCourses = new ArrayList<>();
        examRounds = new ArrayList<>();
        examArrangement = new ArrayList<>();
        examScores = new ArrayList<>();

        planCourses = new ArrayList<>();
        planChildCourses = new ArrayList<>();
        selectionResults = new ArrayList<>();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace();
        Intent intent = new Intent (getApplicationContext(),ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String stackTraceString = "";
        for(StackTraceElement trace : e.getStackTrace()) {
            stackTraceString += "\n" + trace.toString();
        }
        stackTraceString += "\n\nCause:\n" + e.getCause().getMessage() + "\n";
        for(StackTraceElement trace : e.getCause().getStackTrace()) {
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
}
