package com.jm.android.jmtoken;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jm.video.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {
    private RequestMain requestMain = new RequestMain();
    private String device_id = "9fd3f6d6f3c72fed";
    private String client_v = "1.908";
    private String platform = "Android";
    private String platform_v = "5.1.1";
    private final AntiFraudTokenRsp antiFraudTokenRsp = new AntiFraudTokenRsp();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            LinearLayout ll = findViewById(R.id.ll);
            for (Object item : (JSONArray) msg.obj) {
                TextView tv = getTextView();
                JSONObject i = (JSONObject) item;
                String showText = "uid:" + i.getString("uid") + "  nickname:" + i.getString("nickname") + "\nfans_count:" + i.getString("fans_count") + "   praise_count:" + i.getString("praise_count");
                tv.setText(showText);
                tv.setTextSize(18);
                ll.addView(tv);
            }
        }
    };

    public TextView getTextView() {
        TextView tv = new TextView(this);
        return tv;
    }
    public void parseToken() {
        Call call = requestMain.getToken(device_id, client_v, platform, platform_v);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                antiFraudTokenRsp.token = JSON.parseObject(response.body().string()).get("data").toString();
                antiFraudTokenRsp.onParsed();
                System.out.println(antiFraudTokenRsp.tokenBean.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parseToken();
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }

    public void parseSign(View view) {
        String plainText = DesTool.decrypt_now(antiFraudTokenRsp.tokenBean.token, antiFraudTokenRsp.tokenBean.anti_device_id);
        long lastTime = System.currentTimeMillis() / 1000;
        long newTime = System.currentTimeMillis() / 1000;
        long l = antiFraudTokenRsp.tokenBean.time;
        long currentUnixTime = newTime - lastTime + l;
        Map<String, String> params = new HashMap<>();
        EditText editText = findViewById(R.id.etUid);
        String uid = editText.getText().toString();
        params.put("uid", uid);
        params.put("size", "10");
        params.put("lastScore", "");
        params.put("client_v", client_v);
        params.put("platform", platform);
        params.put("device_id", device_id);
        String signToken = DesTool.signToken(params, currentUnixTime, plainText, "");
        String tk_id = String.valueOf(antiFraudTokenRsp.tokenBean.tk_id);
        params.put("antifraud_sign", signToken);
        params.put("antifraud_ts", String.valueOf(currentUnixTime));
        params.put("antifraud_tid", tk_id);

        Call call = requestMain.getVideo(params);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                JSONArray items = ((JSONObject) jsonObject.get("data")).getJSONArray("item_list");
                for (Object item : items) {
                    JSONObject i = (JSONObject) item;
                    String description = i.getString("description");
                    String video_url = i.getString("video_url");
                    requestMain.downVideo(description, video_url);
                }
            }
        });
    }

    public void searchUserList(View view) {
        Map<String, String> param = new HashMap<>();
        int page = 1;
        int size = 20;
        String lastScore = "";
        EditText editText = findViewById(R.id.etName);
        String keyWord = editText.getText().toString();
        param.put("key_word", keyWord + "");
        param.put("page", page + "");
        param.put("size", size + "");
        param.put("lastScore", lastScore);
        param.put("client_v", client_v);
        param.put("platform", platform);
        param.put("device_id", device_id);
        long lastTime = System.currentTimeMillis() / 1000;
        long newTime = System.currentTimeMillis() / 1000;
        long l = antiFraudTokenRsp.tokenBean.time;
        long currentUnixTime = newTime - lastTime + l;
        String plainText = DesTool.decrypt_now(antiFraudTokenRsp.tokenBean.token, antiFraudTokenRsp.tokenBean.anti_device_id);
        String signToken = DesTool.signToken(param, currentUnixTime, plainText, "");
        String tk_id = String.valueOf(antiFraudTokenRsp.tokenBean.tk_id);
        param.put("antifraud_sign", signToken);
        param.put("antifraud_ts", String.valueOf(currentUnixTime));
        param.put("antifraud_tid", tk_id);
        Call call = requestMain.getUserList(param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONArray jsonArray = ((JSONObject) JSON.parseObject(response.body().string()).get("data")).getJSONArray("list");
                SetViewText setViewText = new SetViewText(jsonArray);
                setViewText.start();

            }
        });

    }

    class SetViewText extends Thread {
        private JSONArray jsonArray;

        public SetViewText(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        @Override
        public void run() {
            Message msg = new Message();
            msg.obj = jsonArray;
            mHandler.sendMessage(msg);
        }
    }
}

