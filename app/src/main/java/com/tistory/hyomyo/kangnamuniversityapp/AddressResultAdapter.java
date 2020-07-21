package com.tistory.hyomyo.kangnamuniversityapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AddressResultAdapter extends RecyclerView.Adapter<AddressResultAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<AddressInfo> addressInfos;
    private View rootView;
    private ClipboardManager clipboardManager;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView belong;
        TextView name;
        TextView locate;
        TextView email;
        Button callBtn;
        public MyViewHolder(@NonNull View v) {
            super(v);
            belong = v.findViewById(R.id.address_belong);
            name = v.findViewById(R.id.address_name);
            locate = v.findViewById(R.id.address_locate);
            email = v.findViewById(R.id.address_email);
            callBtn = v.findViewById(R.id.address_call_btn);
        }
    }

    public AddressResultAdapter(ArrayList<AddressInfo> addressInfos, Context parent, View v){
        this.addressInfos = new ArrayList<>();
        this.addressInfos.addAll(addressInfos);
        this.context = parent;
        this.rootView = v;
        clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @NonNull
    @Override
    public AddressResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_address_content_simple, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressResultAdapter.MyViewHolder holder, int position) {

        AddressInfo addressInfo = addressInfos.get(position);
        holder.belong.setText(addressInfo.getBelong());
        holder.name.setText(addressInfo.getName());
        holder.locate.setText(addressInfo.getLocate());
        holder.email.setText(addressInfo.getEmail());
        final String tel = addressInfo.getPhoneNumber().trim().replaceAll("-", "")
                .replaceAll("–","")
                .replaceAll(" ","")
                .trim();
        holder.callBtn.setOnClickListener(v -> {
            try{
                Log.d("전번",tel);
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(tel)));
                //clipboardManager.setPrimaryClip(ClipData.newPlainText(addressInfo.getName(),tel));
                final String _toastMsg =  "'"+addressInfo.getName()+"' : "+tel;
                Toast.makeText(context, _toastMsg, Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                final Snackbar snackbar = Snackbar.make(rootView, "전화를 걸 수 없습니다.", Snackbar.LENGTH_LONG);
                snackbar.setAction("확인", v1 -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(addressInfos==null)return 0;
        return addressInfos.size();
    }
}
