package com.example.kangnamuniversityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class NoticeViewFragment extends Fragment {
    private WebView noticeWebView;
    private String sortedHtml;
    private ArticleInfo article;
    private String url;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_web_view_notice,container,false);
        noticeWebView = rootView.findViewById(R.id.notice_web_view);
        Bundle bundle = getArguments();
        if(bundle!=null){
            Log.d("번들","번들번들");
            article = (ArticleInfo) getArguments().getSerializable("article");
            try{
                url = article.getHref();
                final String urlArg = url;
                loadSortedHtml(urlArg);
                Log.d("로드","완료");
            }catch(NullPointerException e){
                Log.d("로드","실패");
            }


        }Log.d("로드","후");

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(noticeWebView!=null){
            noticeWebView.setDownloadListener(new DownloadListener() {
                //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                //23이상일 경우 아래 코드로 해결
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    Log.d("DOWNLOAD","onDownloadStart is running");
                    try{
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setMimeType(mimeType);
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading file");
                        String fileName = contentDisposition.replace("inline filename=","");
                        fileName = fileName.replaceAll("\"","");
                        request.setTitle(fileName);
//                        request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimeType));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//                        URLUtil.guessFileName(url,contentDisposition,mimeType);
                        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getActivity().getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                    }catch(Exception e){
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Toast.makeText(getActivity().getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            } else {
                                Toast.makeText(getActivity().getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        110);
                            }
                        }
                    }
                }
            });
        }else{
            Log.d("NOTICEVIEW","is null.");
        }
    }

    public void loadSortedHtml(final String url){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final String html;
                    Document document = WebRequestBuilder.create()
                            .url(url)
                            .method(WebRequestBuilder.METHOD_GET)
                            .userAgent(WebRequestBuilder.USER_AGENT_PC)
                            .useCookie(true)
                            .build();
                    Elements elements = document.select("div[class=tbody]");
                    Elements elements2 = elements.select("ul");
                    Element element = elements2.get(1);
                    Element element2 = element.selectFirst("div");
//                    Log.d("HTML",element2.html());
                    html = element2.html();
//                    sortedHtml.replace("style=\"display: none;\"","");
                    sortedHtml = getSortedHtml(html, document);
                    Log.d("HTML",sortedHtml);
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noticeWebView.getSettings().setBuiltInZoomControls(true);//줌 가능
//                                noticeWebView.getSettings().setSupportZoom(true);//줌 UI
                                noticeWebView.loadData(sortedHtml,"text/html","UFT-8");
                                Log.d("HTML","로드 데이터");
//                            noticeWebView.loadUrl(sortedHtml);
                            }
                        });
                    }catch(NullPointerException e){

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getSortedHtml(String html, Document document) {
        String sortedHtml;
        if(article.hasFile()){
            Elements elements = document.select("div[class=tbody]");
            Elements elements2 = elements.select("ul");
            Element element = elements2.get(2); // 첨부파일이 있는 tag=ul 얻기
            //이거 지금 보류해놓고 나중에 디자인 할때 다시 건들어야함.
//            Elements e = element.select("a");
//            String herfBox="";
//            for(Element herf : e){
//                Log.d("첨부파일","\""+herf.text()+"\"");
//                Log.d("첨부파일 링크","\""+herf.attr("href")+"\"");//"https://web.kangnam.ac.kr/" + herf.attr("href");
//                herfBox += "<><>";
//            }
            //link 들어갈 시 첨부파일 다운받는 기능
            sortedHtml = "<!doctype html><html xmls=\"http://www.w3.org/1999/xhtml\" lang=\"ko\"><head></head><body>"+html+element.html()+"</body></html>";
            sortedHtml = sortedHtml.replace("href=\"/","href=\"https://web.kangnam.ac.kr/");
        }else{
            sortedHtml = "<!doctype html><html xmls=\"http://www.w3.org/1999/xhtml\" lang=\"ko\"><head></head><body>"+html+"</body></html>";
        }
        return sortedHtml;
    }

}