package com.tistory.hyomyo.kangnamuniversityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Objects;
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
    private NoticeViewFragment noticeViewFragment;
    private CalendarFragment calendarFragment;
    private ContactAddressFragment contactAddressFragment;
    private ContactAddressResultFragment contactAddressResultFragment;
    private SettingFragment settingFragment;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private Button loginBtn;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntroActivity.intoActivity.finish();

        // setting the current theme (aftger activity restart)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        noticeViewFragment = new NoticeViewFragment();
        calendarFragment = new CalendarFragment();
        settingFragment = new SettingFragment();
        contactAddressFragment = new ContactAddressFragment();
        contactAddressResultFragment = new ContactAddressResultFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.main_toolbar);



        //좌측 Drawer(서랍) 코드
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        NavigationView navigationView;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            drawerLayout.closeDrawers();

            int id = item.getItemId();

            switch(id){
                case R.id.time_table:
                    Log.d("Nav Log", "time table");
                    break;
                case R.id.map:
                    Log.d("Nav Log", "map");
                    break;
                case R.id.setting:
                    Log.d("Nav Log", "setting");
                    break;
                default:
                    return false;
            }
            return true;
        });

        // 로그인 버튼
        loginBtn = navigationView.getHeaderView(0).findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(v -> {
            Log.d("Touch","버튼클릭됨");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });


        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);//하단 네비게이션 선택 리스너

        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                final String TAG = "Firebase TAG";
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = Objects.requireNonNull(task.getResult()).getToken();

                    // Log and toast
                    databaseReference.child("token").child("id").push().setValue(token);
                }
            });
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_appbar_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.appbar_action_theme_toggle:
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.this.getClass()));
                break;
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        if(noticeViewFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().remove(noticeViewFragment).commitAllowingStateLoss();
            showFragment(R.layout.fragment_home);
        }else if(contactAddressResultFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().remove(contactAddressResultFragment).commitAllowingStateLoss();
            showFragment(R.layout.fragment_contact_address);
        }
        else{
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

    // 바텀 네비게이션 셀렉티드 리스너
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
                            transaction.replace(R.id.frame_layout, calendarFragment).commitAllowingStateLoss();
                            Log.d("NAVIGATION", "MAJOR");
                            return true;
                        case R.id.navigation_professor_information:
                            //openFragment(NotificationFragment.newInstance("", ""));
                            transaction.replace(R.id.frame_layout, contactAddressFragment).commitAllowingStateLoss();
                            Log.d("NAVIGATION", "PROFESSOR");
                            return true;
                        case R.id.navigation_notification:
                            transaction.replace(R.id.frame_layout, settingFragment).commitAllowingStateLoss();
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public void hideFragment(int layout) { // notice fragment 숨기기
        switch(layout){
            case R.layout.fragment_home:
                hideNoticeFragment(); // hide fragment
                // get bundle (get article info data)

                noticeViewFragment.setArguments(homeFragment.getArguments());
                // replace fragment by bundle data
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, noticeViewFragment).commitAllowingStateLoss();
                break;
            case R.layout.fragment_contact_address:
                // hide fragment
                if(contactAddressFragment!=null)
                    getSupportFragmentManager().beginTransaction().hide(contactAddressFragment).commitAllowingStateLoss();
                assert contactAddressFragment != null;
                contactAddressResultFragment.setArguments(contactAddressFragment.getArguments());
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, contactAddressResultFragment).commitAllowingStateLoss();

                break;
        }
    }

    @Override
    public void showFragment(int layout) { // notice fragment 숨기기
        switch(layout){
            case R.layout.fragment_home:
                showNoticeFragment();
                Log.d("프레그먼트","SHOW");
                break;
            case R.layout.fragment_contact_address:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, contactAddressFragment).commitAllowingStateLoss();
                break;
        }
    }

}
