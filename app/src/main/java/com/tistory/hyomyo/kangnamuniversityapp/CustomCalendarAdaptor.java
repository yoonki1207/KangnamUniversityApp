package com.tistory.hyomyo.kangnamuniversityapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class CustomCalendarAdaptor extends RecyclerView.Adapter<CustomCalendarAdaptor.CustomCalendarViewHolder> {

    CalendarData calendarData;
    Context context;
    private int itemWidth = 0;
    private String[] current;

    public CustomCalendarAdaptor(Context context, CalendarData calendarData, String[] current){
        this.calendarData = calendarData;
        this.context = context;
        this.current = current;
    }

    @NonNull
    @Override
    public CustomCalendarAdaptor.CustomCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_simple, parent, false);
        return new CustomCalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomCalendarViewHolder holder, int position) {
        String dateString = calendarData.getDateString(position);
        holder.date.setText(dateString);

        int textColor;
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            textColor = Color.WHITE;
        }else{
            textColor = Color.BLACK;
        }
        if((position+7)%7==0 )
            holder.date.setTextColor(Color.RED);
        else if((position+7)%7==6 )
            holder.date.setTextColor(Color.BLUE);
        else
            holder.date.setTextColor(textColor);
        try{
            if(Integer.parseInt(dateString) == Integer.parseInt(current[2])){
                holder.date.setTypeface(null, Typeface.BOLD);
            }
        }catch(Exception e){

        }
        if(!dateString.trim().equals("")){
            for(int i = 0 ; i < 10; i++){
                if(itemWidth==0)
                    itemWidth = holder.linearLayout.getMeasuredWidthAndState();
                try{
                    DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    int dp = (int)dm.density;
                    holder.textView[i].setText(calendarData.getScheduleContentByIndex(position, i));
                    //holder.textView[i].setVisibility(View.VISIBLE);
                    int con = calendarData.getCondition(position, 0);
                    Log.d("어댑터","날짜 : "+dateString+" 그리고 con : "+con);
                    switch (con){
                        case DateData.First:
                            LinearLayout.LayoutParams llpf = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llpf.setMargins(dp, 0,0, dp);
                            holder.textView[i].setPadding(0,0,-100,0);
                            holder.textView[i].setLayoutParams(llpf);
                            break;
                        case DateData.Continuous:
                            Log.d("컨틴뉴어스값", itemWidth+" [] "+(calendarData.getContinuous(position, i))+" dp:"+dp);
                            holder.textView[i].setPadding(-itemWidth*calendarData.getContinuous(position, i)+dp, 0,-30,0);
                            LinearLayout.LayoutParams llpc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llpc.setMargins(0, 0, 0, dp);
                            holder.textView[i].setLayoutParams(llpc);
                            break;
                        case DateData.Last:

                            LinearLayout.LayoutParams llpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llpl.setMargins(0, 0, dp, dp);
                            holder.textView[i].setLayoutParams(llpl);
                            holder.textView[i].setPadding(-itemWidth*calendarData.getContinuous(position, i)+dp, 0,-30,0);
                            break;
                        case DateData.Only:
                            LinearLayout.LayoutParams llpo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llpo.setMargins(dp, 0, dp, dp);
                            holder.textView[i].setLayoutParams(llpo);
                            holder.textView[i].setPadding(0,0,-100,0);
                            break;
                    }
                }catch(Exception e){
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return calendarData.getNum();
    }

    public static class CustomCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView[] textView = new TextView[3];
        LinearLayout linearLayout;
        public CustomCalendarViewHolder(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.calender_date);
            linearLayout = v.findViewById(R.id.calendar_linear_layout);
            textView[0] = v.findViewById(R.id.schedule_1);
            textView[1] = v.findViewById(R.id.schedule_2);
            textView[2] = v.findViewById(R.id.schedule_3);

        }
    }
}
