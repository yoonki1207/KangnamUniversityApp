package com.example.kangnamuniversityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class NoticeViewActivity extends AppCompatActivity {
    WebView noticeWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_view);
        noticeWebView = findViewById(R.id.notice_web_view);

        Intent intent = getIntent();
        ArticleInfo articleInfo = (ArticleInfo) intent.getSerializableExtra("article");
        noticeWebView.loadUrl(articleInfo.getHref());
        //noticeWebView.loadUrl("file:///android_asset/www/index.html");
    }
}
