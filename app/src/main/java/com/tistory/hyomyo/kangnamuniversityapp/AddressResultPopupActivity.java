package com.tistory.hyomyo.kangnamuniversityapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AddressResultPopupActivity extends Activity {

    private Button phone;
    private Button email;
    private Button btnClose;
    private ClipboardManager clipboardManager;
    private AddressInfo ad;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.address_result_popup);

        phone = findViewById(R.id.copy_address_phone_number);
        email = findViewById(R.id.copy_address_email);
        btnClose = findViewById(R.id.address_result_close);

        phone.setOnClickListener(v->{
            phoneSelect();
        });
        email.setOnClickListener(v -> {
            emailSelect();
        });
        btnClose.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        ad = (AddressInfo) intent.getSerializableExtra("data");
        Log.d("정보", ad.getName());

        phone.setText(phone.getText()+": "+ad.getPhoneNumber());
        email.setText(email.getText()+": "+ad.getEmail());
    }

    public void phoneSelect(){
        Log.d("셀렉트","폰 셀렉트 ");
        clipboardManager.setPrimaryClip(ClipData.newPlainText("PhoneNumHYOMYO", ad.getPhoneNumber()));
        Toast.makeText(this, "전화번호를 복사했습니다.", Toast.LENGTH_SHORT).show();
    }

    public  void emailSelect(){
        Log.d("셀렉트","이멜 셀렉트 ");
        clipboardManager.setPrimaryClip(ClipData.newPlainText("EmailHYOMYO", ad.getEmail()));
        Toast.makeText(this, "이메일을 복사했습니다.", Toast.LENGTH_SHORT).show();
    }


}
