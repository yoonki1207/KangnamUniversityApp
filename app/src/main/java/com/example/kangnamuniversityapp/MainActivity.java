package com.example.kangnamuniversityapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

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
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private HomeFragment homeFragment;
    private MajorNoticeFragment majorNoticeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
        final String url = "https://web.kangnam.ac.kr/menu/f19069e6134f8f8aa7f689a4a675e66f.do";

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        majorNoticeFragment = new MajorNoticeFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);//하단 네비게이션 선택 리스너
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    transaction = fragmentManager.beginTransaction();
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
                    }
                    return false;
                }
            };
}
