package com.xuhongxu.xiaoya.Permission;

/*
 * 权限监听器
 */
public interface PermissionListener {
    /**
     * 用户授权后调用
     */
    public void  onGranted();

    /**
     * 用户禁止后调用
     */
    public void  onDenied();

    /**是否显示阐述性说明
     * @param permissions 返回需要显示说明的权限数组
     */
    public void onShowRationale(String[] permissions);
}


