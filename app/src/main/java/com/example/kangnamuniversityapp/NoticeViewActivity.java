package com.example.kangnamuniversityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NoticeViewActivity extends AppCompatActivity {
    WebView noticeWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_view);
        //표시할 웹뷰 설정
        noticeWebView = findViewById(R.id.notice_web_view);
        //Intent로 데이터 가져오기
        Intent intent = getIntent();
        ArticleInfo articleInfo = (ArticleInfo) intent.getSerializableExtra("article");

        final String url = articleInfo.getHref();
        noticeWebView.loadUrl(articleInfo.getHref());

        //noticeWebView.loadUrl("file:///android_asset/www/index.html");
        /*
        https://web.kangnam.ac.kr/common/plugin/syworks.design.library/syworks.design.base.syworks.min.css
        div[class=tbody]
        ul[index=1]
        div[class=inner_txt]
        div
        이 태그가 본문 태그
        css는 위에거 적용

        web js 기능?
        css적용
        확대 기능 적용
        우클릭 기능 적용
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d("Thread","Thread is Running");
                try {
                    final String html;
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();
                    Elements elements = document.select("div[class=tbody]");Log.d("HTML","0");
                    Elements elements2 = elements.select("ul");Log.d("HTML","1");
                    Element element = elements2.get(1);Log.d("HTML","2");
                    Element element2 = element.selectFirst("div[class=inner_txt]");Log.d("HTML","3");

                    html = element2.html();Log.d("HTML","4");
                    boolean valid = Jsoup.isValid(html, Whitelist.basic());
                    if(valid){
                        Log.d("HTML","valid.");
                    }else{
                        Log.d("HTML",html);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
