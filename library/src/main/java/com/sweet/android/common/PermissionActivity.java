package com.sweet.android.common;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.sweet.android.permission.PermissionManager;
import com.sweet.android.util.Log;

/**
 * Created by dingding on 05/04/16.
 *
 */
public class PermissionActivity extends Activity{
    public static final String TAG = "PermissionActivity";

    private PermissionManager mPermissionManager;

    /**
     * 是否监听账户变动, 默认false
     * 子类可以覆盖
     * @return
     */
    protected boolean monitorAccountChanges(){
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionManager = new PermissionManager(this);
        addMonitor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeMonitor();
    }

    /**
     * 增加状态监听
     */
    private void addMonitor(){
    }

    /**
     * 移除状态监听
     */
    private void removeMonitor(){

    }

    /*---------------------------------permission start--------------------------------------------*/

    /**
     * 检测dangerous权限
     * @param requestCode
     */
    protected boolean checkDangerousPermisson(int requestCode){
        Log.d(TAG, "checkDangerousPermisson... requestCode: " + requestCode);
        boolean isAllPermissionGranted = mPermissionManager.checkPermisson(requestCode);
        Log.d(TAG, "isAllPermissionGranted..." + isAllPermissionGranted);
        if (isAllPermissionGranted){
            allPermissionGranted(requestCode);
        }
        return isAllPermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "requestCode: "+requestCode+",permissions size: " + permissions.length + ",grantResults size: " + grantResults.length);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0){
            if (isAllPermissionGranted(grantResults)) {
                Log.d(TAG, "all dangerous permission granted");
                allPermissionGranted(requestCode);
            } else {
                Log.d(TAG, "permission denied, boo!");
                //mPermissionManager.showLeNeverPermissionRequestDialog(checkDeniedPermission(permissions, grantResults));
            }
        } else {
            Log.d(TAG, "request is cancelled");
        }
    }

    /**
     * 授权回调
     * @param requestCode
     */
    protected void allPermissionGranted(int requestCode){
        Log.d(TAG, "allPermissionGranted... requestCode: " + requestCode);
    }

    /**
     * 是否所有的dangerous权限都被授权
     * @param grantResults　从授权回调中判断
     * @return
     */
    private boolean isAllPermissionGranted(int[] grantResults){
        for (Integer grantResult : grantResults){
            if (grantResult == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    /**
     * 是否所有的dangerous权限都被授权
     * 判断所有的权限
     * @return
     */
    protected boolean isAllPermissionGranted(){
        return mPermissionManager.isAllPermissionGranted();
    }

    /*---------------------------------permission end--------------------------------------------*/
}
