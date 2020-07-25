package com.tistory.hyomyo.kangnamuniversityapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static org.jsoup.Jsoup.parse;


public class CalendarFragment extends Fragment {

    private String currentYearHtml;
    private String currentDate;
    private String[] date;
    private String currentYear;
    private String currentMonth;
    private HashMap<String, String> mapYear = new HashMap<String,String>();
    private WebView calenderView;
    private ProgressBar progressBar;
    private EditText yearText;
    private Button leftBtn;
    private Button rightBtn;
    private String css = ".cal_list{list-style:none;display:flex!important;flex-direction:column;align-content:space-around}.cal_month{display:block;text-align:center;font-size:20px;margin-bottom:.5em;margin-top: 1.5em}.calendal{display:flex;flex-direction:column;align-content:center}caption{display:none}table{margin:.5em 0}.SUN{color:red}.SAT{color:#00f}";
    private final String BASE_URL = "https://web.kangnam.ac.kr/menu/02be162adc07170ec7ee034097d627e9.do?";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calender_notice, container, false);
        calenderView = rootView.findViewById(R.id.calender_web_view);
        progressBar = rootView.findViewById(R.id.progressBar);
        yearText = rootView.findViewById(R.id.year_text);
        rightBtn = rootView.findViewById(R.id.calender_right);
        leftBtn = rootView.findViewById(R.id.calender_left);
        calenderView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url){
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        long now = System.currentTimeMillis();
        Date _date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        currentDate = simpleDateFormat.format(_date);
        date = currentDate.split("-");

        currentYear = date[0];
        currentMonth = date[1];
        // 텍스트
        yearText.setText(date[0]);
        yearText.setTextColor(Color.BLACK);
        yearText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String year = s.toString().trim();
                if(year.length()>=4){
                    int nYear;
                    try{
                        nYear = Integer.parseInt(year);
                    }catch (NumberFormatException e){
                        nYear = Integer.parseInt(date[0]);
                    }
                    if(nYear>=1900 && nYear<=Integer.parseInt(date[0])+20 && nYear!=Integer.parseInt(date[0])){
                        //가능
                        currentYear = date[0] = nYear+"";
                        date[1] = "01";
                        loadHtml(BASE_URL);
                    }else{
                        // out of years
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        leftBtn.setOnClickListener(v -> {
            int n = Integer.parseInt(date[1]);
            if(n==1){
                n = 12;
                date[1] = String.format("%02d",n);
                date[0] = (Integer.parseInt(date[0])-1)+"";
                yearText.setText(date[0]);
            }else
                date[1] = String.format("%02d",--n);

            loadHtml(BASE_URL);
        });
        rightBtn.setOnClickListener(v -> {
            int n = Integer.parseInt(date[1]);
            if(n==12){
                n = 1;date[1] = String.format("%02d",n);
                date[0] = (Integer.parseInt(date[0])+1)+"";
                yearText.setText(date[0]);
            }else
                date[1] = String.format("%02d",++n);
            loadHtml(BASE_URL);
        });

        loadHtml(BASE_URL);
        return rootView;
    }

    // year와 month가 적용된 올바른 url을 주면 date[]를 기반으로 데이터를 가져옴
    private void loadHtml(final String url){
        try{

            if(!mapYear.containsKey(date[0])){
                new Thread(() -> {
                    try {
                        // 인터넷 접속
                        final String _url =  url+"year="+date[0]+"&month="+date[1]+"&tab=1";
                        Document document = WebRequestBuilder.create()
                                .url(_url)
                                .method(WebRequestBuilder.METHOD_GET)
                                .userAgent(WebRequestBuilder.USER_AGENT_PC)
                                .useCookie(true)
                                .build();
                        Elements elements = document.select("div[class=contents]");
                        Element element = elements.first();
                        currentYearHtml = element.html();
                        mapYear.put(date[0], currentYearHtml);

                        assert currentYearHtml != null;
                        Document doc = parse(currentYearHtml);
                        String monthId = "calendar"+date[0]+date[1];
                        String monthHtml = getMonthHtmlById(doc, monthId);
                        loadData(getSortedHtml(monthHtml));
                    }catch (IOException ignored){
                    }
                }).start();
            }else{
                new Thread(()->{
                    currentYearHtml = mapYear.get(date[0]);
                    assert currentYearHtml != null;
                    Document doc = parse(currentYearHtml);
                    String monthId = "calendar"+date[0]+date[1];
                    loadData(getSortedHtml(getMonthHtmlById(doc, monthId)));
                }).start();
            }
        }catch(Exception ignored){

        }
    }

    private void loadData(String html){
        try{
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                calenderView.getSettings().setBuiltInZoomControls(true);//줌 가능
        //                            noticeWebView.getSettings().setSupportZoom(true);//줌 UI
                //calenderView.clearFormData();
                calenderView.loadData(html,"text/html","UFT-8");
                });
            }catch(NullPointerException ignored) {
        }
    }

    private String getSortedHtml(final String body){
        String html = "<!doctype html><html><head><style>"+css+"</style></head><body>"+body+"</body></html>";
        return html;
    }

    private String getMonthHtmlById(Document doc, String id){
        Element element = doc.getElementById(id.trim());
        return element.html();
    }

}
