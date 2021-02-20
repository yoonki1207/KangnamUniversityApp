package com.tistory.hyomyo.kangnamuniversityapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;


public class SettingFragment extends Fragment { // 알람 프레그먼트지만 설정으로 바꾸자 그냥

//    private Button buttonNotification;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        createNotificationChannel();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.windows_2)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        buttonNotification = rootView.findViewById(R.id.button_notification);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // button set onColick Listener
//        buttonNotification.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                // 알림창 띄우기
//                notificationManager.notify(100, builder.build());
//            }
//        });

        return rootView;
    }
    // https://m.blog.naver.com/edisondl/221468737546
    /*
    버전
    알림 설정
    개발자 블로그
    개발자 정보
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
