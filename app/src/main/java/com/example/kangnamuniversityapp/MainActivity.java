package com.example.kangnamuniversityapp;

import android.content.Context;
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

    ArrayList<ArticleInfo> noticeData = new ArrayList<ArticleInfo>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";
        final String url2 = "https://m.cafe.naver.com/ArticleListAjax.nhn?search.clubid=27842958&search.menuid=1&search.page=1";

        swipeRefreshLayout = findViewById(R.id.notice_swipe_refresh_layout);
        final Thread noticeArticleParsingThread = loadUkkikki(url2);//url주소에서 article 긁어오는 쓰레드 생성
        final Thread thread = loadUkkikki(url2);
        //thread.start();

        recyclerView = (RecyclerView)findViewById(R.id.notice_article_recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 명시
        mAdapter = new NoticeListAdaptor(this, noticeData);
        recyclerView.setAdapter(mAdapter);



        noticeArticleParsingThread.start();// 쓰레드 시작
//        try {
//            noticeArticleParsingThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener( // notice에 아이템 클릭되면
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                    }
                }
        );



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//새로고침 listener
            @Override
            public void onRefresh() {//새로고침 하면
                loadUkkikki(url2).start();
                swipeRefreshLayout.setRefreshing(false);//이거때문에 그런가 laodUkkikki를 다 확인하고 해야하나
//                recyclerView.setAdapter(mAdapter);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        });

    }

    public void InitializeNoticeArticleData() {// sample data
        noticeData = new ArrayList<ArticleInfo>();

        noticeData.add(new ArticleInfo("1336", "장학", "국가장학금신청", "첨부파일", "장학복지팀", "20.01.30", "1054"));
        noticeData.add(new ArticleInfo("1337", "장학", "국가장학금신청2", "첨부파일", "장학복지팀", "20.01.31", "941"));
    }

    private Thread loadNoticeArticle(final String url){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();//Jsoup.connect(url).get();

                    Elements listArticle = document.selectFirst("div[class=tbody]").select("ul");// html tag 전부 찾아서 Elments로 만듦.
                    Log.e("Dsds", "count: " + listArticle.size());
                    //noticeData.clear();
                    for (Element article : listArticle) {
                        final String title, author, num, time, type, file, views;
                        Elements listContent = article.select("li");

                        num = listContent.get(0).text();
                        type = listContent.get(1).text();
                        title = listContent.get(2).text();
                        author = listContent.get(4).text();
                        time = listContent.get(5).text();
                        file = "none";
                        views = "none";
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                noticeData.clear();
//                                mAdapter.notifyDataSetChanged();
//                                noticeData.add(new ArticleInfo("num", "type", title, "file", author, time, "views"));
//                            }
//                        });
                        noticeData.add(new ArticleInfo("num", "type", title, "file", author, time, "views"));
                        //널오류났었음^
//                        Log.d("num : ", "["+num+"]");
//                        Log.d("type : ", type);
//                        Log.d("title : ", title);
//                        Log.d("author : ", author);
//                        Log.d("time : ", time);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    } //
    private Thread loadUkkikki(final String url){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("loadUkkikki","Thread is running");
                noticeData.clear();//클리어
                try {
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();//Jsoup.connect(url).get();
                    Elements listArticle = document.select("li[class=board_box]");
//                    if(mAdapter!=null){
//                        mAdapter.notifyDataSetChanged(); // 안드로이드 버그 데이터셋 바뀜 명시
//                    }
                    for(Element article : listArticle){
                        final String title, author, time,num;
                        Element listContent = article.selectFirst("strong[class=tit]");
                        Log.d("우끼ㅣ끼끼ㅣ끼끼",listContent.text());
                        title = listContent.text();
                        author = article.selectFirst("span[class=ellip]").text();
                        time = article.selectFirst("span[class=time]").text();
                        num = article.selectFirst("span[class=no]").text();
//                        noticeData.add(new ArticleInfo("num", num, title, "file", author, time, "views"));
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                mAdapter.notifyDataSetChanged();
                                noticeData.add(new ArticleInfo("num", "type", title, "file", author, time, "views"));
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
