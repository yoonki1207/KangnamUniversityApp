package com.tistory.hyomyo.kangnamuniversityapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarScheduleAdaptor extends  RecyclerView.Adapter<CalendarScheduleAdaptor.CSAdaptor>{

    ArrayList<ScheduleData> arrayList;

    public CalendarScheduleAdaptor(ArrayList<ScheduleData> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CSAdaptor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new CSAdaptor(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CSAdaptor v, int position) {
        ScheduleData s = arrayList.get(position);
        String d = s.getScheduleDate();
        String c = s.getSchedule();
        v.scheduleDate.setText(d!=null?d:"null");
        v.scheduleContent.setText(c!=null?c:"null");
    }

    @Override
    public int getItemCount() {
        if(arrayList==null)
            return 0;
        return arrayList.size();
    }

    public static class CSAdaptor extends RecyclerView.ViewHolder {
        TextView scheduleDate;
        TextView scheduleContent;
        public CSAdaptor(@NonNull View v) {
            super(v);
            scheduleDate = v.findViewById(R.id.schedule_date);
            scheduleContent = v.findViewById(R.id.schedule_content);
        }
    }
}
