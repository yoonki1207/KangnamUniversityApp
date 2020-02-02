package com.example.kangnamuniversityapp;

public class ArticleInfo {
    private String number, type, title, file, author, time, views;
    public ArticleInfo(String number, String type, String title, String file, String author, String time, String views){
        this.setNumber(number).setType(type).setTitle(title).setFile(file).setAuthor(author).setTime(time).setViews(views);
    }
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
        this.file = file;
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
}
