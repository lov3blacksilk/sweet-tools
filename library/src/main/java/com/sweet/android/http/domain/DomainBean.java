package com.sweet.android.http.domain;

import com.sweet.android.http.beans.BaseBean;
import com.sweet.android.util.Log;
import org.json.JSONObject;

import android.text.TextUtils;



/**
 * 域名解析对像
 * @author fengzihua
 *
 */
public class DomainBean implements BaseBean {
    /**
     * 域名
     */
    public String domain;
    
    /**
     * 根据json解析出域名
     * @param respond
     * @return
     */
    public static DomainBean analysisRespond(String respond) {
        if (TextUtils.isEmpty(respond))  {
            return null;
        }
        try {
            JSONObject jo = new JSONObject(respond);
            if(jo.has("domain")) {
                DomainBean bean = new DomainBean();
                bean.domain = jo.getString("domain");
                return bean;
            }
        } catch (Exception e) {
            Log.w("[DomainEngine] analysisRespond e: " + e.getMessage());
        }
        return null;
    }
}