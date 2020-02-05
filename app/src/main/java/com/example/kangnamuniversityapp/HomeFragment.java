package com.example.kangnamuniversityapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    ArrayList<ArticleInfo> noticeData = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";
        //group (container) 뷰는 부모 뷰라고 이해하면 될듯. 특정 틀에 맞춰짐
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.notice_swipe_refresh_layout);

        //리사이클러 뷰 설정
        recyclerView = rootView.findViewById(R.id.notice_article_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 설정
        mAdapter = new NoticeListAdaptor(getContext(), noticeData);
        recyclerView.setAdapter(mAdapter);


        loadNoticeArticle(url);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener( // notice에 아이템 클릭되면
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("click", position + "");
                        String title, href, views;
                        Intent intent = new Intent(getActivity(), NoticeViewActivity.class);
                        ArticleInfo article = noticeData.get(position);
                        intent.putExtra("article", article);
                        startActivity(intent);
                    }
                }
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//새로고침 listener
            @Override
            public void onRefresh() {//새로고침 하면
                loadNoticeArticle(url);
                swipeRefreshLayout.setRefreshing(false);//이거때문에 그런가 laodUkkikki를 다 확인하고 해야하나
            }
        });

        return rootView;
    }

    private void loadNoticeArticle(final String url) {
        //글 불러오기
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
                        //this.url이랑 조금 다름
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

                    getActivity().runOnUiThread(new Runnable() {
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
