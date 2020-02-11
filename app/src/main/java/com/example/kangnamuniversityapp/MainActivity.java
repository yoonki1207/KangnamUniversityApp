package com.example.kangnamuniversityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
/*
https://www.flaticon.com/kr/packs/essential-set-2
<div>아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com</a></div>
Place the attribution on the credits/description page of the application.
 */

/*
https://web.kangnam.ac.kr/common/plugin/syworks.design.library/syworks.design.base.syworks.min.css
div[class=tbody]
ul[index=1]
div[class=inner_txt]
div
이 태그가 본문 태그
css는 위에거 적용

 */
public class MainActivity extends AppCompatActivity implements OnFragmentInteraction{

    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private HomeFragment homeFragment;
    private MajorNoticeFragment majorNoticeFragment;
    private NoticeViewFragment noticeViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";

        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        majorNoticeFragment = new MajorNoticeFragment();
        noticeViewFragment = new NoticeViewFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

//        actionBar.setDisplayShowTitleEnabled(false);


        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);//하단 네비게이션 선택 리스너
    }
    @Override
    public void onBackPressed(){
        if(noticeViewFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().remove(noticeViewFragment).commitAllowingStateLoss();
            showFragment();
        }else{
            super.onBackPressed();
        }
    }
    public void hideNoticeFragment(){// hide Home Fragment
        if(homeFragment!=null){
            getSupportFragmentManager().beginTransaction().hide(homeFragment).commitAllowingStateLoss();
        }
    }
    public void showNoticeFragment(){// show Home Fragment
        if(homeFragment!=null){
            Bundle getBundle = homeFragment.getArguments();
            Bundle setBundle = new Bundle();
            if(getBundle!=null){
                ArrayList<ArticleInfo> articles = getBundle.getParcelableArrayList("articles");
                setBundle.putParcelableArrayList("articles",articles);
                homeFragment.setArguments(setBundle);
            }else{

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
        }
    }
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    transaction = fragmentManager.beginTransaction();
//                    transaction.addToBackStack(null);
                    switch (item.getItemId()) {
                        case R.id.navigation_notice:
                            transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();
                            Log.d("NAVIGATION", "HOME");
                            return true;
                        case R.id.navigation_major_notice:
                            transaction.replace(R.id.frame_layout, majorNoticeFragment).commitAllowingStateLoss();
                            Log.d("NAVIGATION", "MAJOR");
                            return true;
                        case R.id.navigation_professor_information:
                            //openFragment(NotificationFragment.newInstance("", ""));
                            Log.d("NAVIGATION", "PROFESSOR");
                            return true;
                        case R.id.navigation_notification:

                            return true;
                    }
                    return false;
                }
            };

    @Override
    public void hideFragment() {
        hideNoticeFragment();Log.d("프레그먼트","HIDE");
        Bundle bundle = homeFragment.getArguments();
        ArticleInfo article = (ArticleInfo) bundle.getSerializable("article");
        bundle.putSerializable("article",article);
        noticeViewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,noticeViewFragment).commitAllowingStateLoss();

    }

    @Override
    public void showFragment() {
        showNoticeFragment();
        Log.d("프레그먼트","SHOW");
    }
}
