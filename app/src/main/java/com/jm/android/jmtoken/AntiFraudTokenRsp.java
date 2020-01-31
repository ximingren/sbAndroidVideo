package com.jm.android.jmtoken;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @ClassName AntiFraudTokenRsp
 * @Description TODO
 * @Author ximingren
 * @Date 2020/1/30 11:04
 */
public class AntiFraudTokenRsp {
    public String token;
    public TokenBean tokenBean;

    public void onParsed() {
        this.tokenBean = (TokenBean)JSON.parseObject(this.token, TokenBean.class);
    }

    public static class TokenBean implements Serializable {
        @JSONField(name = "anti_device_id")
        public String anti_device_id;
        @JSONField(name = "expire")
        public int expire;
        @JSONField(name = "expire_time")
        public long expire_time;
        @JSONField(name = "time")
        public long time;
        @JSONField(name = "tk_id")
        public long tk_id;
        @JSONField(name = "token")
        public String token;

        public String toString() {
            return "TokenBean{tk_id=" + this.tk_id + ", token='" + this.token + '\'' + ", expire_time=" + this.expire_time + ", expire=" + this.expire + ", time=" + this.time + ", anti_device_id='" + this.anti_device_id + '\'' + '}';
        }
    }

    public String toString() {
        return "AntiFraudTokenRsp{token=" + this.token + '}';
    }


}
