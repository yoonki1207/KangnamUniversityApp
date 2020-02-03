package com.example.kangnamuniversityapp;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    ArrayList<ArticleInfo> noticeData;
    final ArrayList<ArticleInfo> noticeDataTmp = new ArrayList<ArticleInfo>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";




        Thread noticeArticleParsingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();//Jsoup.connect(url).get();

                    Elements listArticle = document.selectFirst("div[class=tbody]").select("ul");
                    Log.e("Dsds", "count: " + listArticle.size());

                    for (Element article : listArticle) {
                        String title, author, num, time, type, file, views;
                        Elements listContent = article.select("li");


                        num = listContent.get(0).text();
                        type = listContent.get(1).text();
                        title = listContent.get(2).text();
                        author = listContent.get(4).text();
                        time = listContent.get(5).text();
                        file = "none";
                        views = "none";

                        noticeDataTmp.add(new ArticleInfo(num, type, title, file, author, time, views));
                        //널오류났었음^
                        Log.d("num : ", "["+num+"]");
                        Log.d("type : ", type);
                        Log.d("title : ", title);
                        Log.d("author : ", author);
                        Log.d("time : ", time);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                    }
                }
        );
        noticeArticleParsingThread.start();

        try {
            noticeArticleParsingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //this.InitializeNoticeArticleData();
        recyclerView = (RecyclerView)findViewById(R.id.notice_article_recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 명시
        mAdapter = new NoticeListAdaptor(this, noticeDataTmp);
        recyclerView.setAdapter(mAdapter);

    }

    public void InitializeNoticeArticleData() {
        noticeData = new ArrayList<ArticleInfo>();

        noticeData.add(new ArticleInfo("1336", "장학", "국가장학금신청", "첨부파일", "장학복지팀", "20.01.30", "1054"));
        noticeData.add(new ArticleInfo("1337", "장학", "국가장학금신청2", "첨부파일", "장학복지팀", "20.01.31", "941"));
    }

}
