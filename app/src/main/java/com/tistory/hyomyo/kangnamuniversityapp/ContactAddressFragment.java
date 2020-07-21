package com.tistory.hyomyo.kangnamuniversityapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

// 교내 전화번호만
public class ContactAddressFragment extends Fragment {

    private Button searchButton;
    private EditText searchValue;
    private OnFragmentInteraction onFragmentInteraction;
    private final String SEARCH_URL = "https://web.kangnam.ac.kr/menu/7a5545db676e4a6f2d3995ceed5e7c4d.do";
    private ArrayList<AddressInfo> addressInfos;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  OnFragmentInteraction){
            onFragmentInteraction = (OnFragmentInteraction)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_address, container, false);
        searchValue = rootView.findViewById(R.id.search_value);
        searchButton = rootView.findViewById(R.id.search_button);
        addressInfos = new ArrayList<>();
        searchButton.setOnClickListener(v -> {
            String str = searchValue.getText().toString().trim();
            try {
                str = URLEncoder.encode(str,"UTF-8");
                SetWebView(SEARCH_URL+"?searchValue3="+str);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        });
        return rootView;
    }

    private void SetWebView(final String url){
        new Thread(() -> {
            try {
                Document document = WebRequestBuilder.create()
                        .url(url)
                        .method(WebRequestBuilder.METHOD_GET)
                        .userAgent(WebRequestBuilder.USER_AGENT_PC)
                        .useCookie(true)
                        .build();
                GetData(document.html());

                Bundle bundle = new Bundle();
                for(int i = 0 ; i < addressInfos.size() ; i++)
                    bundle.putSerializable("results"+i, addressInfos.get(i));
                bundle.putSerializable("result",addressInfos.size());
                this.setArguments(bundle);
                onFragmentInteraction.hideFragment(R.layout.fragment_contact_address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void GetData(String html){
        Document doc = Jsoup.parse(html);
        Element tbody = doc.select("div[class=tbody]").first();
        Elements uls = tbody.getElementsByTag("ul");
        for (Element ul: uls) {
            AddressInfo addressInfo = new AddressInfo();
            Elements lis = ul.getElementsByTag("li");
            addressInfo.setBelong(lis.get(0).text().replaceFirst("소속", ""));
            addressInfo.setName(lis.get(1).text().replaceFirst("성명",""));
            addressInfo.setPhoneNumber(lis.get(2).text().replaceFirst("전화번호",""));
            addressInfo.setLocate(lis.get(3).text().replaceFirst("위치",""));
            addressInfo.setEmail(lis.get(4).text().replaceFirst("이메일",""));
            addressInfos.add(addressInfo);
        }
    }
}
