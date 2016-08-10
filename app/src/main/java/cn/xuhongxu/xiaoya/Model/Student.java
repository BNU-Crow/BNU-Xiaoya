package cn.xuhongxu.xiaoya.Model;

import java.util.Date;

import cn.bmob.v3.BmobUser;

/**
 * Created by xuhon on 2016/8/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class Student extends BmobUser{
    // 年级 nj
    private String grade;
    // 专业代码 zydm
    private String specialityCode;
    // 学年 xn
    private String academicYear;
    // 学期 xq_m
    private String schoolTerm;
    // bdtime 报到时间
    private String registrationTime = "";
    // mz 民族
    private String nationality = "";
    // lqzy 录取专业
    private String speciality = "";
    // sydw 毕业中学
    private String middleSchool = "";
    // bjmc 班级名称
    private String className = "";
    // yhxh 学号
    private String studentId = "";
    // whcd 文化程度
    private String cultureStandard = "";
    // zymc 志愿名称
    private String collegeWill = "";
    // xz 学制
    private String schoolSystem = "";
    // sfzjh 身份证件号
    private String idNumber = "";
    // pycc 培养层次
    private String educationLevel = "";
    // xm 姓名
    private String name = "";
    // gkksh 高考考生号
    private String gaokaoID = "";
    // xh 学生号
    private String number = "";
    // zpid 照片号
    private String avatarID = "";
    // yxb 院系
    private String college = "";
    // xb 性别
    private String gender = "";
    // txdz 通讯地址
    private String address = "";
    // xmpy 姓名拼音
    private String pinyin = "";
    // rxnj 入学年级
    private String registrationGrade = "";
    // csrq 出生日期
    private Date birthday;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(String specialityCode) {
        this.specialityCode = specialityCode;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSchoolTerm() {
        return schoolTerm;
    }

    public void setSchoolTerm(String schoolTerm) {
        this.schoolTerm = schoolTerm;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getMiddleSchool() {
        return middleSchool;
    }

    public void setMiddleSchool(String middleSchool) {
        this.middleSchool = middleSchool;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String id) {
        this.studentId = id;
    }

    public String getCultureStandard() {
        return cultureStandard;
    }

    public void setCultureStandard(String cultureStandard) {
        this.cultureStandard = cultureStandard;
    }

    public String getCollegeWill() {
        return collegeWill;
    }

    public void setCollegeWill(String collegeWill) {
        this.collegeWill = collegeWill;
    }

    public String getSchoolSystem() {
        return schoolSystem;
    }

    public void setSchoolSystem(String schoolSystem) {
        this.schoolSystem = schoolSystem;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGaokaoID() {
        return gaokaoID;
    }

    public void setGaokaoID(String gaokaoID) {
        this.gaokaoID = gaokaoID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(String avatarID) {
        this.avatarID = avatarID;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getRegistrationGrade() {
        return registrationGrade;
    }

    public void setRegistrationGrade(String registrationGrade) {
        this.registrationGrade = registrationGrade;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
