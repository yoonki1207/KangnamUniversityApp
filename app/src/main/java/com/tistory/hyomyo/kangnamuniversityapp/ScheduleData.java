package com.tistory.hyomyo.kangnamuniversityapp;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleData {
    String scheduleDate;
    String schedule;
    public ScheduleData(String scheduleDate, String schedule){
        this.scheduleDate = scheduleDate;
        this.schedule = schedule;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }
}
