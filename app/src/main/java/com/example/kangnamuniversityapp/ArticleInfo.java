package com.example.kangnamuniversityapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ArticleInfo implements Serializable, Parcelable {
    private String number, type, title, file, author, time, views, href;
    public ArticleInfo(){};
    public ArticleInfo(String number, String type, String title, String file, String author, String time, String views){
        this.setNumber(number).setType(type).setTitle(title).setFile(file).setAuthor(author).setTime(time).setViews(views);
    }

    protected ArticleInfo(Parcel in) {
        number = in.readString();
        type = in.readString();
        title = in.readString();
        file = in.readString();
        author = in.readString();
        time = in.readString();
        views = in.readString();
        href = in.readString();
    }

    public static final Creator<ArticleInfo> CREATOR = new Creator<ArticleInfo>() {
        @Override
        public ArticleInfo createFromParcel(Parcel in) {
            return new ArticleInfo(in);
        }

        @Override
        public ArticleInfo[] newArray(int size) {
            return new ArticleInfo[size];
        }
    };

    public ArticleInfo setNumber(String number){
        this.number = number;
        return this;
    }
    public ArticleInfo setType(String type){
        this.type = type;
        return this;
    }
    public ArticleInfo setTitle(String title){
        this.title = title;
        return this;
    }
    public ArticleInfo setFile(String file){
        this.file = file; // "첨부파일" , "-" 중 하나
        return this;
    }
    public ArticleInfo setAuthor(String author){
        this.author = author;
        return this;
    }
    public ArticleInfo setTime(String time){
        this.time = time;
        return this;
    }
    public ArticleInfo setViews(String views){
        this.views = views;
        return this;
    }
    public ArticleInfo setHref(String href) {
        this.href = href;
        return this;
    }

    public String getNumber(){
        return this.number;
    }
    public String getType(){
        return this.type;
    }
    public String getTitle(){
        return this.title;
    }
    public String getFile(){
        return this.file;
    }
    public String getAuthor(){
        return this.author;
    }
    public String getTime(){
        return this.time;
    }
    public String getViews(){
        return this.views;
    }
    public String getHref() {
        return href;
    }

    public boolean hasFile(){
        if("첨부파일".equals(this.getFile()))
            return true;
        else
            return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(file);
        dest.writeString(author);
        dest.writeString(time);
        dest.writeString(views);
        dest.writeString(href);
    }
}
