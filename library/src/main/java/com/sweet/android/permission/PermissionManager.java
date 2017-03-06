package com.sweet.android.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.UserHandle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.sweet.android.util.LeUtil;
import com.sweet.android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingding on 28/04/16.
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";

    private static final String[] mDangerousPermissonArray = PermissionConfig.DANGEROUS_PERMISSION_ARRAY;

    private Activity mContext;

    //private LeNeverPermissionRequestDialog leNeverPermissionRequestDialog = null;

    public PermissionManager(Activity context){
        mContext = context;
    }

    /**
     * 检测权限
     * @param dangerous
     * @return 被拒绝的权限
     */
    private String[] checkDeniedPermission(String[] dangerous){
        Log.d(TAG, "checkDeniedPermission...");
        List<String> mDeniedPermissions = new ArrayList<String>();
        for (String permisson : dangerous){
            if (ContextCompat.checkSelfPermission(mContext, permisson) == PackageManager.PERMISSION_DENIED){
                mDeniedPermissions.add(permisson);
            }
        }
        return LeUtil.listToArray(String.class, mDeniedPermissions);
    }

    /**
     * 是否所有的dangerous权限都被授权
     * 判断所有的权限
     * @return
     */
    public boolean isAllPermissionGranted(){
        String[] mDenitedPermissions = checkDeniedPermission(mDangerousPermissonArray);
        if (mDenitedPermissions.length > 0){
            Log.d(TAG, "isAllPermissionGranted: " + false);
            return false;
        }
        Log.d(TAG, "isAllPermissionGranted: " + true);
        return true;
    }

    /**
     * 是否有权限被勾选不再询问
     * 注: 只要有一个权限被勾选【不再询问】，就弹【去设置】权限框
     * @param dangerous
     * @return false:有权限被勾选不再询问　true: 没有权限被勾选不再询问
     */
    private boolean shouldShowRequestPermissionRationale(String[] dangerous){
        Log.d(TAG, "shouldShowRequestPermissionRationale...");
        for (String permisson : dangerous){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, permisson)){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param requestCode
     * @return true: 所有权限都被授权　false:有部分权限没被授权
     */
    public boolean checkPermisson(int requestCode){
        Log.d(TAG, "checkPermisson...　requestCode: " + requestCode);
        String[] mDeniedPermissions = checkDeniedPermission(mDangerousPermissonArray);
        //　有被拒绝的权限
        if (mDeniedPermissions.length > 0){
            if (shouldShowRequestPermissionRationale(mDeniedPermissions)){
                //用户设置过禁止（非始终禁止）
                ActivityCompat.requestPermissions(mContext, mDeniedPermissions, requestCode);
            } else {
                //用户设置了“始终禁止”或“权限初始化状态”即用户未设置过。
                if (isPermissionInit(mDeniedPermissions)){
                    ActivityCompat.requestPermissions(mContext, mDeniedPermissions, requestCode);
                } else {
                    showLeNeverPermissionRequestDialog(mDeniedPermissions);
                }
            }
            return false;
        } else {
            //用户设置未允许
            Log.i(TAG,"RuntimePermission checkSelfPermission --> PERMISSION_GRANTED");
        }
        return true;
    }

    /**
     * 当应用权限被拒绝时给出弹框
     */
    public void showLeNeverPermissionRequestDialog(String[] mDeniedPermissions){
        Log.d(TAG, "showLeNeverPermissionRequestDialog...");
        List<PermissionInfo> list = getPermisson(mDeniedPermissions);
        /*try {
            leNeverPermissionRequestDialog = new LeNeverPermissionRequestDialog(mContext,list, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick... cancel");
                    leNeverPermissionRequestDialog.disappear();
                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        leNeverPermissionRequestDialog.appear();*/
    }

    /**
     * 获取权限
     * @return
     */
    private List<PermissionInfo> getPermisson(String[] permissons){
        PackageManager pm = mContext.getPackageManager();
        List<PermissionInfo> list = new ArrayList<PermissionInfo>();
        for (String permission : permissons){
            PermissionInfo permissionInfo = null;
            try {
                permissionInfo = pm.getPermissionInfo(permission, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (permissionInfo != null){
                list.add(permissionInfo);
            }
        }
        return list;
    }

    /**
     * 是不是权限初始化状态
     * @param dangerous
     * @return
     */
    private boolean isPermissionInit(String[] dangerous){
        Log.d(TAG, "isPermissionInit...");
        PackageManager pm = mContext.getPackageManager();
        String pkgName = mContext.getPackageName();
        /*UserHandle userHandle = new UserHandle(UserHandle.myUserId());
        for (String permission : dangerous){
            int permissionFlag = pm.getPermissionFlags(permission, pkgName, userHandle);
            if(permissionFlag == 0){
                return true;
            }
        }*/
        return false;
    }
}
