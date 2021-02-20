package com.tistory.hyomyo.kangnamuniversityapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.Objects;

public class CookieApp extends Application {
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "JSESSIONID";

    private static CookieApp _instance;
    private RequestQueue _requestQueue;
    private SharedPreferences _preferences;

    public static CookieApp get(){
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _requestQueue = Volley.newRequestQueue(this);
    }

    public RequestQueue getRequestQueue(){
        return _requestQueue;
    }

    /**
     * Check the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers){
        Log.d("CheckSessionCookie", headers.toString());
        if(headers.containsKey(SET_COOKIE_KEY)){
            String cookie = headers.get(SET_COOKIE_KEY);
            if(cookie.length() > 0){
                Log.d("Cut Cookie", cookie);
                SharedPreferences.Editor prefEditor = _preferences.edit();
                String[] splitCookie = cookie.split(";");
                for(int i = 0 ; i < splitCookie.length ; i++){
                    String[] splitSessionId = splitCookie[i].split("=");
                    try{
                        prefEditor.putString(splitSessionId[0], splitSessionId[1]);
                    }catch(ArrayIndexOutOfBoundsException e){

                    }
                }
                prefEditor.commit();
            }
        }
    }
    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers){
        Log.d("Preference", _preferences.getAll().toString());/*
        String sessionId = _preferences.getString(SESSION_COOKIE, "");
        if(sessionId.length() > 0){
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if(headers.containsKey(COOKIE_KEY)){
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }*/
        String str = new String();
        Map<String, ?> pref = _preferences.getAll();
        StringBuilder builder = new StringBuilder();
        for(String key : pref.keySet()){
            builder.append(key+"="+(String)pref.get(key)+"; ");
        }
        if(headers.containsKey(COOKIE_KEY)){
            builder.append(headers.get(COOKIE_KEY));
            str = builder.toString();
        }else{
            str = builder.toString();
            if(str.length() > 2)
                str = str.substring(0, str.length()-2);
        }
        headers.put(COOKIE_KEY, str);
        Log.d("Final_addSessionCookie", headers.toString());
    }


    /*
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     *//*
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY) && Objects.requireNonNull(headers.get(SET_COOKIE_KEY)).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                SharedPreferences.Editor prefEditor = _preferences.edit();
                prefEditor.putString(SESSION_COOKIE, cookie);
                prefEditor.commit();
            }
        }
    }*/

    /*
     * Adds session cookie to headers if exists.
     * @param headers
     *//*
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = _preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }*/

}
