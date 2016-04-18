package cn.xuhongxu.xiaoya;

import android.app.Application;

import cn.xuhongxu.Assist.SchoolworkAssist;

/**
 * Created by xuhongxu on 16/4/12.
 *
 * YaApplication
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class YaApplication extends Application {

    private SchoolworkAssist assist;

    public SchoolworkAssist getAssist() {
        return assist;
    }

    public void setAssist(SchoolworkAssist assist) {
        this.assist = assist;
    }
}
