package com.tistory.hyomyo.kangnamuniversityapp;

import android.app.DownloadManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivityTag";

    private final String LOGIN_URL = "https://m.kangnam.ac.kr/knusmart/c/c001.do?";

    private String login_param;
    private TextInputEditText textId;
    private TextInputEditText textPassword;
    private Button sendBtn;
    private RequestQueue queue;
    private String rawCookies;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textId = findViewById(R.id.login_uid);
        textPassword = findViewById(R.id.login_password);
        sendBtn = findViewById(R.id.send_btn);

        rawCookies = "";
        login_param = "";

        queue = Volley.newRequestQueue(this);

        sendBtn.setOnClickListener(view -> {
            if(queue!=null)
                queue.cancelAll(TAG);
            login_param="user_id="+textId.getText()
                    +"&"
                    +"user_pwd="+textPassword.getText();
            queue.add(requestLogin());
        });
    }

    public StringRequest requestLogin(){
        return new StringRequest(StringRequest.Method.GET, LOGIN_URL+login_param, response -> {
            Log.d("RESPONSE", response);
            if(jsonIsValidAccount(response)){
                KNUData.getInstance().setLogin(true);
                if(queue!=null)
                    queue.cancelAll(TAG);
                MainActivity.mainActivity.recreate();
                KNUData.getInstance().setUserId(textId.getText().toString());
                this.finish();
            }else{
                Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("Error", "String Request status Code: "+error.networkResponse.statusCode+"");
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null
                        || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<>();
                }
                CookieApp.get().addSessionCookie(headers);
                return headers;
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = response.headers.get("Set-Cookie");
                if(str != null && !str.trim().equals("")){
                    Log.d("문자열",str+"\n"+response.statusCode);
                    try{
                        str = str.substring(str.indexOf("mast_name_e="), str.indexOf(";", str.indexOf("mast_name_e=")) < 0 ? str.length() : str.indexOf(";", str.indexOf("mast_name_e=")));
                        str = str.replaceAll("mast_name_e=", "").replaceAll(";", "").trim();
                        try {
                            byte[] decode = Base64.getDecoder().decode(str);
                            str = new String(decode, StandardCharsets.UTF_8);
                            Log.d("이름", str);

                            str = URLDecoder.decode(str, "UTF-8");

                            Log.d("이름", str);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        KNUData.getInstance().setUserName(str);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                CookieApp.get().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queue!=null)
            queue.cancelAll(TAG);
    }

    public void logLargeString(String str){
        if(str.length() > 3000){
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        }else{
            Log.i(TAG, str);
        }
    }

    private boolean jsonIsValidAccount(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.getString("result");
            return result.equals("success");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
