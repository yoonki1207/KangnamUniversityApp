package com.example.kangnamuniversityapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    private OnFragmentInteraction onFragmentInteraction;
    ArrayList<ArticleInfo> noticeData = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteraction) {
            onFragmentInteraction = (OnFragmentInteraction) context;
        }else{
            Log.d("인터페이스 미구현","인터페이스 미구현");
        }
    }

    @Nullable
    @Override
    public void onStart() {
        Log.d("FRAGMENT","onStart() called");
        super.onStart();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";
        //group (container) 뷰는 부모 뷰라고 이해하면 될듯. 특정 틀에 맞춰짐
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.notice_swipe_refresh_layout_home);

        //리사이클러 뷰 설정
        recyclerView = rootView.findViewById(R.id.notice_article_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 설정
        mAdapter = new NoticeListAdaptor(getContext(), noticeData);
        recyclerView.setAdapter(mAdapter);

        if(getArguments()!=null){
            ArrayList<ArticleInfo> articleInfos = getArguments().getParcelableArrayList("articles");
            noticeData = articleInfos;
            mAdapter.notifyDataSetChanged();
        }else{
            loadNoticeArticle(url);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//새로고침 listener
            @Override
            public void onRefresh() {//새로고침 하면
                loadNoticeArticleList(url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }
    private void loadNoticeArticle(final String url) {
        //글 불러오기
        if(noticeData.size()==0){
            loadNoticeArticleList(url);
        }else{
            mAdapter.notifyDataSetChanged();
        }

    }

    private void loadNoticeArticleList(final String url) {
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
//                    Log.e("Dsds", "count: " + listArticle.size());
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

                        newArticle.setNumber(num).setType(type).setTitle(title).setAuthor(author).setTime(time).setFile(file).setViews(views).setHref(href);
                        list.add(newArticle);
                    }
                    //안하면 NullPointerException나더라. 왜그런지는 잘 모르겠지만 아마 getActivicty()가 null을 참조하고있지 않았을까...
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noticeData.clear();
                                mAdapter.notifyDataSetChanged();
                                noticeData.addAll(list);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (NullPointerException e){
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener( // notice에 아이템 클릭되면
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        String title, href, views;
                        ArticleInfo article = noticeData.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("article",article);
                        bundle.putParcelableArrayList("articles", (ArrayList<? extends Parcelable>) noticeData);
                        setArguments(bundle);
                        Log.d("ITEM","아이템이 클릭됨.");
                        onFragmentInteraction.hideFragment();
                    }
                }
        );
    }

}
