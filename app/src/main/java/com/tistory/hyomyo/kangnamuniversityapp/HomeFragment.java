package com.tistory.hyomyo.kangnamuniversityapp;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tistory.hyomyo.kangnamuniversityapp.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment{
    private OnFragmentInteraction onFragmentInteraction;
    ArrayList<ArticleInfo> noticeData = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Toolbar toolbar;
    private String globalUrl = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteraction) {
            onFragmentInteraction = (OnFragmentInteraction) context;
        }else{
            Log.d("인터페이스 미구현","인터페이스 미구현");
        }
    }

    @Override
    public void onStart() {
        Log.d("FRAGMENT","onStart() called");
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // group (container) 뷰는 부모 뷰라고 이해하면 될듯. 특정 틀에 맞춰짐
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.notice_swipe_refresh_layout_home);

        // 리사이클러 뷰 설정
        recyclerView = rootView.findViewById(R.id.notice_article_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 설정
        mAdapter = new NoticeListAdaptor(getContext(), noticeData);
        recyclerView.setAdapter(mAdapter);

        // Linear 레이아웃 메니저
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        if(getArguments()!=null){
            ArrayList<ArticleInfo> articleInfos = getArguments().getParcelableArrayList("articles");
            noticeData = articleInfos;
            mAdapter.notifyDataSetChanged();
        }else{
            loadNoticeArticle(globalUrl);
        }

        // 새로고침 하면 (scroll up = 아래로 당기기)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//새로고침 listener
            @Override
            public void onRefresh() {
                noticeData.clear();
                mAdapter.notifyDataSetChanged();
                loadNoticeArticleList(globalUrl, 1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener( // notice에 아이템 클릭되면 callback. 웹뷰 띄우기
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
                        onFragmentInteraction.hideFragment(R.layout.fragment_home);
                    }
                }
        );
    }

    private void loadNoticeArticle(final String url) {
        //글 불러오기
        if(noticeData.size()==0){
            loadNoticeArticleList(url, 1);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

//    private void loadNoticeArticleList(final String url, int page){
//        String args = "?paginationInfo.currentPageNo="+page+"&searchMenuSeq=0&searchType=&searchValue=";
//        loadNoticeArticleList(url+args);
//        // https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do?paginationInfo.currentPageNo=1&searchMenuSeq=0&searchType=&searchValue=
//        // https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do
//    }

    private void loadNoticeArticleList(final String _url, final int page) {      //강남대 메인 공지사항 로딩하는 쓰레드
        final String url = _url+"?paginationInfo.currentPageNo="+page+"&searchMenuSeq=0&searchType=&searchValue=";
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
                        if(page!=1 && num.equals("필독")){
                            Log.d("PASS", "필독 PASS"+page);
                            continue;
                        }
                        type = listContent.get(1).text();
                        title = listContent.get(2).text();
                        file = listContent.get(3).text();
                        author = listContent.get(4).text();
                        time = listContent.get(5).text();
                        views =listContent.get(6).text();
                        hrefTmp = listContent.get(2).selectFirst("a").attr("data-params");
                        //this.url이랑 조금 다름
                        StringBuilder tmp = new StringBuilder("https://web.kangnam.ac.kr/menu/board/info/f19069e6134f8f8aa7f689a4a675e66f.do?");
                        try{
                            Pattern ptn = Pattern.compile("([a-z]|[A-Z]|[0-9])+");
                            Matcher matcher;
                            matcher = ptn.matcher(hrefTmp);
                            int i=0;
                            while(matcher.find()){
                                if(i==0){
                                    tmp.append(matcher.group()).append("=");
                                }else if(i==1){
                                    tmp.append(matcher.group()).append("&");
                                }else if(i==2){
                                    tmp.append(matcher.group()).append("=");
                                }else if(i==3){
                                    tmp.append(matcher.group()).append("&");
                                }else if(i==4){
                                    tmp.append(matcher.group()).append("=");
                                }else{
                                    tmp.append(matcher.group());
                                }
                                i++;
                            }
                        } catch(Exception ignored) {

                        }
                        final String href = tmp.toString();

                        newArticle.setNumber(num).setType(type).setTitle(title).setAuthor(author).setTime(time).setFile(file).setViews(views).setHref(href);
                        //Log.d("Href_",href);
                        list.add(newArticle);
                    }
                    //안하면 NullPointerException나더라. 왜그런지는 잘 모르겠지만 아마 getActivicty()가 null을 참조하고있지 않았을까...
                    try{
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //noticeData.clear();
                                //mAdapter.notifyDataSetChanged();
                                noticeData.addAll(list);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (NullPointerException ignored){
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }



    public void loadNextDataFromApi(int offset){
        loadNoticeArticleList(globalUrl, offset+1);
    }
}
