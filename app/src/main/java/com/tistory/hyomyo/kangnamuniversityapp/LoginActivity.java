package com.tistory.hyomyo.kangnamuniversityapp;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivityTag";

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

        //CookieApp cookieApp = new CookieApp();

        queue = Volley.newRequestQueue(this);
        String url = "https://knusso.kangnam.ac.kr/sso/pmi-sso-login-uid-password.jsp";
//        String url = "https://knusso.kangnam.ac.kr/login.jsp";


        // string request login
        final StringRequest stringRequest1 = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ResponseLogin", "html code here...");
        }, error1 -> {
            error1.printStackTrace();
            if(error1.networkResponse.statusCode == 302){
                Log.d("ResponseLoginError", error1.networkResponse.headers.toString());
                Log.d("로그", error1.networkResponse.allHeaders.toString());
                CookieApp.get().checkSessionCookie(error1.networkResponse.headers);
                requestUrl("https://web.kangnam.ac.kr/sso/index.jsp", "", "TAG", 0);
            } else if (error1.networkResponse.statusCode == 500) {
                Log.d("ERROR", "password or something is wrong");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // 나중에 예외처리 구현 권장
                params.put("gid","gid_web");
                params.put("returl", "https://web.kangnam.ac.kr/sso/index.jsp");
                params.put("uid", textId.getText().toString().trim());
                params.put("password", textPassword.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null
                        || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<String, String>();
                }
                CookieApp.get().addSessionCookie(headers);
                Log.d("Request", headers.toString());
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                CookieApp.get().checkSessionCookie(response.headers);
                Log.d("ParseNetworkResponse", response.headers.toString());
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest1.setTag(TAG);


        // string request to ajax (set login)
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://knusso.kangnam.ac.kr/setLoginCookie.jsp", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                queue.add(stringRequest1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 302){
                    Log.d("ErrorResponse",error.networkResponse.allHeaders.toString());

                }else if(error.networkResponse.statusCode == 500){
                    Log.e("WrongPassword","Wrong password Error or Something is wrong: 500");
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",textId.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null
                || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<String, String>();
                }
                CookieApp.get().addSessionCookie(headers);
                Log.d("Request1", headers.toString());

                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                CookieApp.get().checkSessionCookie(response.headers);
                Log.d("ResponseLog", response.headers.toString());
                return super.parseNetworkResponse(response);
            }
        };

        stringRequest.setTag(TAG);

        sendBtn.setOnClickListener(view -> {
            queue.add(stringRequest);
        });

    }

    private void requestUrl(final String url, final String Cookie, String tag, int i){
        Log.d("요청유알엘", url);
        final StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("응답하라", url);
        }, error -> {
            Log.d("URL", url);
            Log.d("Cookie", Cookie);
            Log.d("ERROR", error.networkResponse.headers.toString()+"");
            if(error.networkResponse.statusCode == 302){
                final String url2 = error.networkResponse.headers.get("Location");
                Log.d("Next Location", url2);
                Log.d("This Response Headers", error.networkResponse.headers.toString());
                if(!url2.trim().equals("") && !url2.trim().equals("https://knusso.kangnam.ac.kr/login.jsp")){
                    CookieApp.get().checkSessionCookie(error.networkResponse.headers);
                    requestUrl(url2, "", tag, i+1);
                }
            }else{
                Log.d("Other Error Code", error.networkResponse.statusCode+"");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if(headers == null
                || headers.equals(Collections.emptyMap())){
                    headers = new HashMap<String, String>();
                }

                CookieApp.get().addSessionCookie(headers);
                Log.d("Request", headers.toString());
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response == null) {
                    Log.d("응답됨", "null");
                    super.parseNetworkResponse(response);
                }
                CookieApp.get().checkSessionCookie(response.headers);
                Log.d("ResponseLog", response.headers.toString());
                return super.parseNetworkResponse(response);
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queue!=null){
            queue.cancelAll(TAG);
            queue.cancelAll("TAG");
            queue.cancelAll("TAG0");
            queue.cancelAll("TAG1");
            queue.cancelAll("TAG2");
            queue.cancelAll("TAG3");
            queue.cancelAll("TTT");
        }
    }

    public void logLargeString(String str){
        if(str.length() > 3000){
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        }else{
            Log.i(TAG, str);
        }
    }
}
