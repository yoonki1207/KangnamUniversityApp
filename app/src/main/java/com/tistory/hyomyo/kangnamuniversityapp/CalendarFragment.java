package com.tistory.hyomyo.kangnamuniversityapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static org.jsoup.Jsoup.parse;


public class CalendarFragment extends Fragment {
//
//    private String currentYearHtml;
//    private String currentDate;
//    private String[] date;
//    private String currentYear;
//    private String currentMonth;
//    private HashMap<String, String> mapYear = new HashMap<String,String>();
//    private String css = ".cal_list{list-style:none;display:flex!important;flex-direction:column;align-content:space-around}.cal_month{display:block;text-align:center;font-size:20px;margin-bottom:.5em;margin-top: 1.5em}.calendal{display:flex;flex-direction:column;align-content:center}caption{display:none}table{margin:.5em 0}.SUN{color:red}.SAT{color:#00f}";
    private final String BASE_URL = "https://web.kangnam.ac.kr/menu/02be162adc07170ec7ee034097d627e9.do?";

    private RecyclerView recyclerView;
    private RecyclerView scheduleRecycleView;
    private CustomCalendarAdaptor mAdapter;
    private CalendarScheduleAdaptor sAdapter;
    private ArrayList<ScheduleData> scheduleData;
    private Context rootContext;
    private CalendarData currentCalendarData;
    private TextView monthText;
    private TextView yearText;
    private TextView dateText;

    private Button leftMonth;
    private Button rightMonth;
    private ScrollView scrollView;
    String[] current;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        rootContext = context;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_calendar_layout, container, false);
        recyclerView = rootView.findViewById(R.id.custom_calendar_recycler_view);
        scheduleRecycleView = rootView.findViewById(R.id.schedule_recycler_view);
        //scrollView = rootView.findViewById(R.id.calendar_scroll_view);

        Date currentDate = new Date();
        SimpleDateFormat monthIdFormat = new SimpleDateFormat("yyyy-MM-dd");
        current = monthIdFormat.format(currentDate).split("-");

        currentCalendarData =  new CalendarData(current[0], current[1]);

        yearText = rootView.findViewById(R.id.calender_year);
        monthText = rootView.findViewById(R.id.calender_month);
        dateText = rootView.findViewById(R.id.calender_date);

        leftMonth = rootView.findViewById(R.id.leftMonthBtn);
        rightMonth = rootView.findViewById(R.id.rightMonthBtn);

        //scrollView.computeScroll();

        yearText.setText(current[0]);
        monthText.setText(current[1]+"월");
        dateText.setText(current[2]+"일");

//        scrollView.setLayoutTransition();

        mAdapter = new CustomCalendarAdaptor(getContext(), currentCalendarData, current);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(rootContext, 7){
            @Override
            public boolean canScrollVertically(){
                return false;
//                return true;
            }
        };
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        scheduleData = new ArrayList<>();
        sAdapter = new CalendarScheduleAdaptor(scheduleData);
        scheduleRecycleView.setAdapter(sAdapter);
        scheduleRecycleView.setHasFixedSize(false);
        scheduleRecycleView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        });

        //mAdapter.notifyDataSetChanged();
        Log.d("스케듈로그", Integer.parseInt(current[0])+" and "+Integer.parseInt(current[2]));
        getSchedule(Integer.parseInt(current[0]), Integer.parseInt(current[1]));

        leftMonth.setOnClickListener(v -> {
            int Y = Integer.parseInt(current[0]);
            int M = Integer.parseInt(current[1]);
            if(--M < 1){
                M = 12;
                --Y;
            }
            current[0] = String.format("%02d", Y);
            current[1] = String.format("%02d", M);
            yearText.setText(current[0]);
            monthText.setText(current[1]+"월");
            dateText.setText(current[2]+"일");
            getSchedule(Y, M);
        });

        rightMonth.setOnClickListener(v -> {
            int Y = Integer.parseInt(current[0]);
            int M = Integer.parseInt(current[1]);
            if(++M > 12){
                M = 1;
                ++Y;
            }
            current[0] = String.format("%02d", Y);
            current[1] = String.format("%02d", M);
            yearText.setText(current[0]);
            monthText.setText(current[1]+"월");
            dateText.setText(current[2]+"일");
            getSchedule(Y, M);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView1, position, v) -> {
            Log.d("캘린더", "아이템 클릭됨 : "+position);

        });
    }

    private void getSchedule(int year, int month){
        String _year = year+"";
        _year = _year.trim();
        String _month = String.format("%02d",month);
        String monthId = year+_month;
        monthId = monthId.trim();
        try {
            String final_year = _year;
            new Thread(()->{
                final String _url =  BASE_URL+"year="+ final_year +"&month="+_month+"&tab=1";
                Document document = null;
                try {
                    document = WebRequestBuilder.create()
                            .url(_url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();
                } catch (IOException e) {
                    e.printStackTrace();
                }try{
                    Element element = document.select("div[class=contents]").first();
                    Elements allMon = element.getElementsByClass("calendal_list");
                    Elements elementMonth = allMon.get(month-1).select("tr");
                    ArrayList<ScheduleData> tmpScheduleData = new ArrayList<>();
                    for(Element e : elementMonth){
                        //Log.d("로그", "날짜 : "+e.getAllElements().get(1).text()+"\n일정 : "+e.getAllElements().get(2).text());
                        String _date = e.getAllElements().get(1).text();
                        try{
                            Integer.parseInt(_date.substring(0,2));
                        }catch(Exception ign){
                            continue;
                        }
                        String _content = e.getAllElements().get(2).text();
                        tmpScheduleData.add(new ScheduleData(_date, _content));
                    }
                    Log.d("데이터1", tmpScheduleData.size()+"");
                    try{
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            scheduleData.clear();
                            currentCalendarData.reset(year, month);
                            scheduleData.addAll(tmpScheduleData);
                            for(ScheduleData s : tmpScheduleData){
                                currentCalendarData.addData(s.getScheduleDate(), s.getSchedule());
                                mAdapter.notifyDataSetChanged();
                            }
                            sAdapter.notifyDataSetChanged();
                            //scrollView.computeScroll();
                            Log.d("데이터", scheduleData.size()+"");

                        });
                    }catch (Exception e){

                    }
                }catch(Exception ee){

                }



            }).start();
        }catch (Exception ignored){

        }
    }
    public void setScrollViewHeightBasedByChilderen(){

    }

}
