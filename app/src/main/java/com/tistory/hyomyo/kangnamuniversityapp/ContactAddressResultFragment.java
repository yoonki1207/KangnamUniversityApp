package com.tistory.hyomyo.kangnamuniversityapp;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;

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
    }
}
