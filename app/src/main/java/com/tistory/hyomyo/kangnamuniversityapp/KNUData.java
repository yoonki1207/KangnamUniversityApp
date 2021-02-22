package com.tistory.hyomyo.kangnamuniversityapp;

public class KNUData {

    private static KNUData _instance;
    private boolean isLogin;
    private String userId;
    private String userName;

    private KNUData(){
        isLogin = false;
    }

    public static KNUData getInstance(){
        if(_instance == null)
            _instance = new KNUData();
        return _instance;
    }

    public void setLogin(boolean isLogin){
        this.isLogin = isLogin;
    }
    public boolean isLogin(){
        return this.isLogin;
    }

    public final String getUserId(){
        return this.userId;
    }
    public final void setUserId(String userId){
        this.userId = userId;
    }
    public final String getUserName(){
        return this.userName;
    }
    public final void setUserName(String userName) {
        this.userName = userName;
    }
}
