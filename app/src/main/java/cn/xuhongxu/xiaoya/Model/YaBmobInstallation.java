package cn.xuhongxu.xiaoya.Model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class YaBmobInstallation extends BmobInstallation{

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
