package com.jm.android.jmtoken;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.video.R;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @ClassName RequestMain
 * @Description TODO
 * @Author ximingren
 * @Date 2020/1/30 10:34
 */
public class RequestMain {
    private JSONArray jsonArray;
    public void getPage() {
        String url = "http://www.baidu.com/";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    System.out.println("run:" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Call getToken(String device_id, String client_v, String platform, String platform_v) {
        String url = "http://api.shuabaola.cn/passport/get_anti_fraud_token";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("device-id", device_id)
                .addHeader("client-v", client_v)
                .addHeader("platform", platform)
                .addHeader("platform-v", platform_v)
                .build();
        final Call call = okHttpClient.newCall(request);
        return call;
    }

    public Call getVideo(Map<String, String> params) {
        String url = "http://api.shuabaola.cn/user_center/user_list_short_video";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("client_v", params.get("client_v"))
                .add("device_id", params.get("device_id"))
                .add("platform", params.get("platform"))
                .add("antifraud_tid", params.get("antifraud_tid"))
                .add("antifraud_ts", params.get("antifraud_ts"))
                .add("lastScore", params.get("lastScore"))
                .add("antifraud_sign", params.get("antifraud_sign"))
                .add("size", params.get("size"))
                .add("uid", params.get("uid"))
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("device-id", params.get("device_id"))
                .addHeader("client-v", params.get("client_v"))
                .addHeader("platform", params.get("platform"))
                .post(requestBody)
                .build();
        final Call call = okHttpClient.newCall(request);
        return call;
    }

    public Call getUserList(Map<String, String> params) {
        String url = "https://api.shuabaola.cn/s_e/search_list";
        System.out.println(params);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("client_v", params.get("client_v"))
                .add("device_id", params.get("device_id"))
                .add("platform", params.get("platform"))
                .add("antifraud_tid", params.get("antifraud_tid"))
                .add("antifraud_ts", params.get("antifraud_ts"))
                .add("lastScore", params.get("lastScore"))
                .add("antifraud_sign", params.get("antifraud_sign"))
                .add("size", params.get("size"))
                .add("key_word", params.get("key_word"))
                .add("page", params.get("page"))
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("device-id", params.get("device_id"))
                .addHeader("client-v", params.get("client_v"))
                .addHeader("platform", params.get("platform"))
                .post(requestBody)
                .build();
        final Call call = okHttpClient.newCall(request);

        return call;
    }

    public void downVideo(final String description, String video_url) {
        File file = new File("/storage/emulated/0/刷宝待上传视频/");
        if (!file.exists()) {
            file.mkdirs();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(video_url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File("/storage/emulated/0/刷宝待上传视频/" + description + ".mp4"));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(description, "视频下载成功");
            }
        });
    }
}
