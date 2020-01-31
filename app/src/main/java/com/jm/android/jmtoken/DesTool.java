package com.jm.android.jmtoken;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DesTool
 * @Description TODO
 * @Author ximingren
 * @Date 2020/1/29 23:48
 */
public class DesTool {
    static {
        System.loadLibrary("DesTool");
    }

    private static native String signToken(String str, String str2, String str3, String str4);


    public static native byte[] decrypt(byte[] bytes, byte[] bytes1, int i);

    public static String decrypt_now(String data, String key) {
        byte[] cipherText = Base64.decode(data.getBytes(), 0);
        new String(cipherText);
        byte[] plainText = decrypt(cipherText, key.getBytes(), 0);
        return plainText == null ? "" : new String(plainText);
    }

    public static String signToken(Map<String, String> params, long currentUnixTime, String token, String appName) {
        Map<String, String> reqParams = new HashMap<>();
        for (Map.Entry<String, String> _stringStringEntry : params.entrySet()) {
            String _key = _stringStringEntry.getKey();
            if (!_key.contentEquals("uid") && !_key.contentEquals("antifraud_tid") && !_key.contentEquals("antifraud_sign") && !_key.contentEquals("antifraud_ts")) {
                reqParams.put(_key, _stringStringEntry.getValue());
            }
        }
        Object[] keys = reqParams.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        boolean isFirstKey = true;
        for (Object key : keys) {
            if (!isFirstKey) {
                sb.append("|");
            }
            if (!TextUtils.isEmpty(reqParams.get(key))) {
                sb.append(reqParams.get(key));
            }
            isFirstKey = false;
        }
        String paramValues = sb.toString();
        System.out.println("paramValues:" + paramValues);
        try {
            Log.d("debugToken", " currentUnixTime=" + currentUnixTime + " paramValues=" + paramValues + " token=" + token);
            return signToken(currentUnixTime + "", paramValues, token, "micro_video");
        } catch (Exception _e) {
            _e.printStackTrace();
            return null;
        }
    }

}
