package com.tistory.hyomyo.kangnamuniversityapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class ContactAddressResultFragment extends Fragment {

    private ArrayList<AddressInfo> addressInfos;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context rootContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.rootContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("프레그먼트","onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_address_result, container, false);

        Bundle bundle = this.getArguments();
        addressInfos = new ArrayList<>();
        try{
            assert bundle != null;
            int infoSize = (int) bundle.getSerializable("result");
            Log.d("프렉",infoSize+"");
            for(int i = 0 ; i < infoSize ; i++)
                addressInfos.add((AddressInfo)bundle.getSerializable("results"+i));
        }catch(NullPointerException e){
            Log.d("번들","Error");
            e.printStackTrace();
        }
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout_search_address);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView = rootView.findViewById(R.id.recycler_view_search_address);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        assert container != null;
        mAdapter = new AddressResultAdapter(addressInfos, this.getContext(), rootView);
        recyclerView.setAdapter(mAdapter);


        Toast.makeText(getContext(),"검색 결과 출력",Toast.LENGTH_SHORT).show();

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d("프렉","onStart() called");
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView1, position, v) -> {
            Intent intent = new Intent(rootContext, AddressResultPopupActivity.class);
            AddressInfo ad = addressInfos.get(position);
            intent.putExtra("data", ad);
            startActivityForResult(intent, 1);
            Toast.makeText(rootContext, ad.getName()+"님의 전화번호로 연결합니다", Toast.LENGTH_SHORT);
        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener((recyclerView1, position, v) -> {
            AddressInfo ad = addressInfos.get(position);
            String _phoneNumber = ad.getPhoneNumber().replaceAll("-", "")
                    .replaceAll("–","")
                    .replaceAll(" ","")
                    .trim();
            if(_phoneNumber.equals("")){
                final Snackbar snackbar = Snackbar.make(Objects.requireNonNull(this.getView()), "전화를 걸 수 없습니다.", Snackbar.LENGTH_LONG);
                snackbar.setAction("확인", v1 -> {
                    snackbar.dismiss();
                });
                snackbar.show();
            }else{
                Uri number = Uri.parse("tel:"+_phoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                try{
                    rootContext.startActivity(callIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return true;
        });

    }
}
