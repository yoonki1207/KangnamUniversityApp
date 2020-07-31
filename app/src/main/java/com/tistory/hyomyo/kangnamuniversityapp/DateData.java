package com.tistory.hyomyo.kangnamuniversityapp;

import android.util.Log;

import java.util.Comparator;

public class DateData implements Comparable<DateData> {

    private String date;
    private String schedule;

    public static final int First = 0;
    public static final int Continuous = 1;
    public static final int Last = 2;
    public static final int Only = 3;

    public DateData(String date, String schedule){
        this.date = date;
        setDate(this.date);
        this.schedule = schedule;
    }

    public String getDate() {
        return date;
    }

    public int getFirstDate(){
        Log.d("aaa",date);
        try{
            return Integer.parseInt(date.split("~")[0].split("\\.")[1]);
        }catch (Exception ee){
            return Integer.parseInt(date.split("\\.")[1]);
        }
    }

    public int getLastDate(){
        try{
            return Integer.parseInt(date.split("~")[1].split("\\.")[1]);
        }catch (Exception ee){
            return Integer.parseInt(date.split("\\.")[1]);

        }
    }

    public void setDate(String date) {
        String[] _tmp = new String[2];
        _tmp[0] = "(";
        _tmp[1] = ")";
        String d = date.replaceAll("월", "")
                .replaceAll("화", "")
                .replaceAll("수", "")
                .replaceAll("목", "")
                .replaceAll("금", "")
                .replaceAll("토", "")
                .replaceAll("일", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "").trim();
        if(d.contains("~") && !d.substring(0,2).equals(d.substring(6,8))){
            d = d.substring(6,8)+".01"+d.substring(5);
        } //07.29~07.30
        this.date = d;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getPeriod(){
        try{
            String[] _ds = date.split("~");
            Log.d("퍼리오드", _ds.length+": "+_ds[0]+", "+_ds[1]);
            int period = Integer.parseInt(_ds[0].split("\\.")[1]) - Integer.parseInt(_ds[1].split("\\.")[1]);
            Log.d("퍼리오드", "정상종료");
            return period;
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * get state for today
     * @param today
     *
     */
    public int getState(String today){
        Log.d("StartGetState",", Call getPeriod()");
        if(getPeriod()==0)
            return Only;
        String[] _ds = date.split("~");
        int f = Integer.parseInt(_ds[0].split("\\.")[1]);
        int e = Integer.parseInt(_ds[1].split("\\.")[1]);
        int n = Integer.parseInt(today.split("\\.")[1]);
        Log.d("로그로그", "f:"+f+", e:"+e+", n:"+n);
        if(n==f)
            return First;
        else if (n==e)
            return Last;
        else
            return Continuous;
    }

    @Override
    public int compareTo(DateData o) {
        return this.getPeriod() - o.getPeriod();
    }

    public static Comparator<DateData> periodComparator = new Comparator<DateData>() {
        @Override
        public int compare(DateData o1, DateData o2) {
            return o1.getPeriod() - o2.getPeriod();
        }
    };
}
