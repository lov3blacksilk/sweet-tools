
package com.sweet.android.http.domain;


import android.content.Context;
import android.text.TextUtils;
//import com.letv.leui.util.LeDomainManager;
import com.sweet.android.util.Log;

import java.util.Map;

/**
 * 从服务器获取下发域名
 * 
 * @since 2016.4.19
 * @author fengzihua
 */
public class DomainEngine {

    private static final String CUSTOM = "custom";

    private static DomainEngine mHelper;
    private Context mAppContext;
    private final byte[] mLock = new byte[0];
    // 新的域名管理类
    //private LeDomainManager manager;
    // 监听变化接口
    //private LeDomainManager.CallbackWrapper mDomainResponseListener;
    // 标记请求状态
    private boolean onRequestFinish;
    // 临时存储
    private String mTempFetchDomain;
    
    public static DomainEngine getInstance() {
        if (mHelper == null) {
            synchronized (DomainEngine.class) {
                if (mHelper == null)
                    mHelper = new DomainEngine();
            }
        }
        return mHelper;
    }

    private DomainEngine() {
    }
    
    /**
     * 格式化url, 添加https或是http
     * @param domain
     * @return
     */
    private String formatUrl(String domain) {
        if(domain.startsWith("https") || domain.startsWith("http")) {
            return domain;
        } 
        return domain = "https://" + domain;
    }
    
    /**
     * 同步读取domain值
     * 
     * @return
     */
    public final String readDomainSync(String nowHost) {
        if (!TextUtils.isEmpty(nowHost)) {
            return nowHost;
        }
        synchronized (mLock) {
            if (TextUtils.isEmpty(nowHost)) {
                fetchDomainLocked();
            }
        }
        return mTempFetchDomain;
    }

    /**
     * 初次进入时，从perference中读取
     * 
     * @param context
     * 
     * @parm 当前host
     *
     */
    public void init(Context context) {
        mAppContext = context;
        //manager = new LeDomainManager(context.getContentResolver());
        initSettings();
        /*mDomainResponseListener = new LeDomainManager.CallbackWrapper() {
            *//**
             * 请求正常返回
             *//*
            @Override
            public void onSuccess(final Map<String, String> result) {
                onRequestFinish = true;
                try {
                    String domain = result.get(CUSTOM);
                    Log.d("[DomainEngine] mDomainResponseListener.onSuccess, domain: " + domain);
                    writeDomainSync(domain);
                } catch (Exception e) {
                    Log.d("[DomainEngine] mDomainResponseListener.onSuccess, e: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // 通知等待线程
                    synchronized (mLock) {
                        mLock.notifyAll();
                    }
                }
            }

            *//**
             * 请求出错回调
             *//*
            @Override
            public void onFailure(int code, Exception e){
                super.onFailure(code, e);
                // 该imei后台没有配置分组或者网络原因
                Log.d("[DomainEngine] mDomainResponseListener.onFailure, code: " + code + ", errorMsg: " + e.getMessage());
                onRequestFinish = true;
                // 通知请求错误
                synchronized (mLock) {
                    mLock.notifyAll();
                }
            }
        };*/
    }
    
    /**
     * 从调置中读取
     */
    private void initSettings() {
        // 为了安全起见，每次都再次获取
       /* synchronized (mLock) {
            String domain = StoreHelper.getBusinessDomain(mAppContext);
            if (!TextUtils.isEmpty(BusinessConstant.HOST)) {
                BusinessConstant.HOST = formatUrl(domain);
            }
        }*/
    }
    
    /**
     * 从服务器获取到的域名写入disk和内存中
     * 
     * @param domain
     * 
     * @return
     */
    private final boolean writeDomainSync(String domain) {
        if (TextUtils.isEmpty(domain)) return false;
        
        // {"domain":"lock.scloud.letv.com"}

        // 添加https或是http
        domain = formatUrl(domain);
        Log.d("[DomainEngine] writeDomainSync, host: " + domain);
        synchronized (mLock) {
            // 为了安全起见，不用读取或是写入
            //StoreHelper.storeBusinessDomain(mAppContext, domain);
            //BusinessConstant.HOST = domain;
            // 临时存储
            mTempFetchDomain = domain;
        }
        return true;
    }
    
    /**
     * 从服务器上获取域名 check 域名，如果域名为空则去服务器上去取，如果不为空则什么也不做
     */
    private final void fetchDomainLocked() {
        // 判断域名是否为空，为空则去取域名
        if (!TextUtils.isEmpty(mTempFetchDomain))
            return;

        try{
            onRequestFinish = false;
            //String imei = PhoneDeviceInfo.getInstance().getImei();
            //Log.d("[DomainEngine] fetchDomainLocked imei is: " + imei);
            //根据分组获取域名
            //manager.getDomainByGroup(imei, mDomainResponseListener, CUSTOM);
        }catch(Exception e){//有可能抛出IMEI为空或不正确的异常
            e.printStackTrace();
            //mDomainResponseListener.onFailure(-1, e);
        }

        // 如果结果已以在此线程的回调方法中返回，不用wait
        if(onRequestFinish) return;
        // 等待结果返回
        synchronized (mLock) {
            try{
                mLock.wait(8000);
            } catch(InterruptedException e) {
                Log.d("[DomainEngine] fetchDomainLocked, e: " + e.getMessage());
            }
        }
    }
}