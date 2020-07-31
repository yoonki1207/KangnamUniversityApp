package com.tistory.hyomyo.kangnamuniversityapp;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class CalendarData {
    private HashMap<String, String> schedule; // format "Stirng:"12.25(월)~12.25(월)", String:"christmas"" or 07.27(월)
    private ArrayList<String> day = new ArrayList<>();
    private int days[] = new int[42];
    private int startDay;
    private int lastDay;
    private int currentMonth;
    private int currentYear;
    private ArrayList<ArrayList<DateData>> calendar = new ArrayList<>(32);

    public CalendarData(int year, int month){
        this.currentMonth = month;
        this.currentYear = year;
        schedule = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, 1);
        startDay = cal.get(Calendar.DAY_OF_WEEK);
        lastDay = cal.getActualMaximum(Calendar.DATE);
        int d = startDay;
        calendar.ensureCapacity(32);
        Log.d("캘랜더 사이즈",calendar.size()+"");
        for(int i = 0 ; i < 32 ; i++){
            calendar.add(i, new ArrayList<>());
        }
        Log.d("캘랜더 사이즈",calendar.size()+"");
        for(int i = 0 ; i < 42 ; i++)
            days[i] = 0;
        for(d = startDay-1 ; d < lastDay + startDay -1 ; d++)
            days[d] = d-startDay+2;
    }

    private void setting(){
        Calendar cal = Calendar.getInstance();

    }
    /*
    * add schedule at calendar according date
    *
    * */
    public void addData(String date, String schedule){
        DateData newDate = new DateData(date, schedule);
        int start = newDate.getFirstDate();
        int end = newDate.getLastDate();
        for(int i = start ; i <=end ; i++){
            calendar.get(i).add(newDate);
            Collections.sort(calendar.get(i), DateData.periodComparator);
        }
    }

    public CalendarData(String year, String month){
        this(Integer.parseInt(year), Integer.parseInt(month));
    }

    public int getDate(int position){
        return days[position];
    }

    public int getCondition(int position, int index){
        String s = String.format("%02d",getDate(position));
        int cond = calendar.get(getDate(position)).get(index).getState("00."+s);
        return cond;
    }

    public String getScheduleContentByIndex(int position, int index) throws IndexOutOfBoundsException{
        return calendar.get(getDate(position)).get(index).getSchedule();
    }

    public int getNumSchedule(int position){
        int d = getDate(position);
        int ret = calendar.get(d).size();
        return ret;
    }

    public int getContinuous(int position, int index){
        int t = calendar.get(getDate(position)).get(index).getFirstDate();
        Log.d("로그_","getDate"+getDate(position)+", "+t+" = "+(getDate(position) - t));
        return getDate(position) - t;
    }

    /**
     *
     * @param dayFormat example: "05.06~05.07"
     * @return 0 means only day, 1 means 2days
     */
    public int getDateDifference(String dayFormat){
        return (new DateData(dayFormat, "")).getPeriod();
    }

    public String getDateString(int position){
        int ret = getDate(position);
        if(ret==0)
            return "";
        else
            return (new Integer(ret)).toString();
    }

    public int getNum(){
        return 42;
    }
}
