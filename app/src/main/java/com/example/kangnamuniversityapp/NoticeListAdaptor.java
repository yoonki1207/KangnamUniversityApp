package com.example.kangnamuniversityapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;

public class NoticeListAdaptor extends RecyclerView.Adapter<NoticeListAdaptor.MyViewHolder> {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ArticleInfo> articles;
    // RecyclerView.ViewHolder를 상속해서 말 그래돌 뷰 홀더를 만든다.
    // 이곳에는 리스트 각 목록에 해당하는 요소들 (title, date, views 등)을 묶는다.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView titleView, timeView, typeView, viewsView;
        public MyViewHolder(View v) {
            super(v);
            titleView = v.findViewById(R.id.text_view_title);
            timeView = v.findViewById(R.id.text_view_time);
            typeView = v.findViewById(R.id.text_view_type);
            viewsView =v.findViewById(R.id.text_view_views);
        }
    }

    public NoticeListAdaptor(Context context, ArrayList<ArticleInfo> data){
        mContext = context;
        articles = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override  //  onCreateViewHolder는 ViewHolder가 만들어질때마다 생성되고 이는 ViewHolder를 반환해야 한다.
    public NoticeListAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View v = (View) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notice_content_simple, viewGroup, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override  //  항목을 설정set 하는 곳. 바인딩.  뷰 홀더와 포지션이 매개변수로 전달된다.
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        ArticleInfo article = articles.get(position);
        if("필독".equals(article.getNumber())){
            myViewHolder.titleView.setTypeface(null, Typeface.BOLD);

        }else{
            myViewHolder.titleView.setTypeface(null, Typeface.NORMAL);
        }
        myViewHolder.titleView.setText(article.getTitle());
        myViewHolder.timeView.setText(article.getTime());
        myViewHolder.typeView.setText(article.getType());
        myViewHolder.viewsView.setText(article.getViews());

    }

    @Override
    public int getItemCount() {
        if(articles==null)return 0;
        return articles.size();
    }

}
