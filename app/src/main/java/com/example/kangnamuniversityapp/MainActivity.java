package com.example.kangnamuniversityapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<ArticleInfo> noticeData = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";
//        final String url2 = "https://m.cafe.naver.com/ArticleListAjax.nhn?search.clubid=27842958&search.menuid=1&search.page=1";

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        swipeRefreshLayout = findViewById(R.id.notice_swipe_refresh_layout);
        loadNoticeArticle(url);
        recyclerView = findViewById(R.id.notice_article_recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 명시
        mAdapter = new NoticeListAdaptor(this, noticeData);
        recyclerView.setAdapter(mAdapter);

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        transaction.replace(R.id.frame_layout, homeFragment).commitNowAllowingStateLoss();

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener( // notice에 아이템 클릭되면
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("click", position + "");
                        String title, href, views;
                        Intent intent = new Intent(getApplicationContext(), NoticeViewActivity.class);
                        ArticleInfo article = noticeData.get(position);
                        intent.putExtra("article", article);
                        startActivity(intent);
                    }
                }
        );

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);//하단 네비게이션 선택 리스너
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//새로고침 listener
            @Override
            public void onRefresh() {//새로고침 하면
                loadNoticeArticle(url);
                swipeRefreshLayout.setRefreshing(false);//이거때문에 그런가 laodUkkikki를 다 확인하고 해야하나
            }
        });


    }
    private void loadNoticeArticle(final String url) {
        new Thread(new Runnable() {
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
                    final ArrayList<ArticleInfo> list = new ArrayList<>();
                    for (Element article : listArticle) {
                        ArticleInfo newArticle = new ArticleInfo();
                        final String title, author, num, time, type, file, views, hrefTmp;
                        Elements listContent = article.select("li");

                        num = listContent.get(0).text();
                        type = listContent.get(1).text();
                        title = listContent.get(2).text();
                        file = listContent.get(3).text();
                        author = listContent.get(4).text();
                        time = listContent.get(5).text();
                        views =listContent.get(6).text();
                        hrefTmp = listContent.get(2).selectFirst("a").attr("data-params");
                        String tmp = "https://web.kangnam.ac.kr/menu/board/info/f19069e6134f8f8aa7f689a4a675e66f.do?";
                        try{
                            String text = hrefTmp;
                            Pattern ptn = Pattern.compile("([a-z]|[A-Z]|[0-9])+");
                            Matcher matcher = ptn.matcher(text);
                            int i=0;
                            while(matcher.find()){
                                if(i==0){
                                    tmp+=matcher.group()+"=";
                                }else if(i==1){
                                    tmp+=matcher.group()+"&";
                                }else if(i==2){
                                    tmp+=matcher.group()+"=";
                                }else if(i==3){
                                    tmp+=matcher.group()+"&";
                                }else if(i==4){
                                    tmp+=matcher.group()+"=";
                                }else{
                                    tmp+=matcher.group();
                                }
                                i++;
                            }
                        } catch(Exception e) {

                        }
                        final String href = tmp;
                        Log.d("HREF",href);

                        newArticle.setNumber(num).setType(type).setTitle(title).setAuthor(author).setTime(time).setFile(file).setViews(views).setHref(href);
                        list.add(newArticle);
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noticeData.clear();
                            mAdapter.notifyDataSetChanged();
                            noticeData.addAll(list);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    } //
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_notice:
                            //openFragment(HomeFragment.newInstance("", ""));
                            Log.d("NAVIGATION","HOME");
                            return true;
                        case R.id.navigation_major_notice:
                            //openFragment(MajorFragment.newInstance("", ""));
                            Log.d("NAVIGATION","MAJOR");
                            return true;
                        case R.id.navigation_professor_information:
                            //openFragment(NotificationFragment.newInstance("", ""));
                            Log.d("NAVIGATION","PROFESSOR");
                            return true;
                    }
                    return false;
                }

            };
    private void loadUkkikki(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("loadUkkikki", "Thread is running");
                try {
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();//Jsoup.connect(url).get();
                    Elements listArticle = document.select("li[class=board_box]");
                    final ArrayList<ArticleInfo> list = new ArrayList<>();
                    for (Element article : listArticle) {
                        ArticleInfo newArticle = new ArticleInfo();
                        final String title, author, time, num;
                        Element listContent = article.selectFirst("strong[class=tit]");
                        Log.d("우끼ㅣ끼끼ㅣ끼끼", listContent.text());

                        title = listContent.text();
                        author = article.selectFirst("span[class=ellip]").text();
                        time = article.selectFirst("span[class=time]").text();
                        num = article.selectFirst("span[class=no]").text();

                        newArticle.setAuthor(author).setFile("").setNumber(num).setTime(time).setTitle(title).setType(num).setViews("");
                        list.add(newArticle);
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noticeData.clear();
                            mAdapter.notifyDataSetChanged();
                            noticeData.addAll(list);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
