package com.tistory.hyomyo.kangnamuniversityapp;

import java.io.Serializable;

public class AddressInfo implements Serializable {
    private String belong;
    private String name;
    private String locate;
    private String phoneNumber;
    private String email;

    public AddressInfo(){
        this.belong = "null";
        this.name = "null";
        this.locate = "null";
        this.phoneNumber = "null";
        this.email = "null";
    }


    public void setBelong(String belong) {
        this.belong = belong;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLocate() {
        return locate;
    }

    public String getName() {
        return name;
    }

    public String getBelong() {
        return belong;
    }

}
