package com.test.expirydatetracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class List extends Fragment {
    DBhelper dBhelper;
    Handler handler;
    Runnable runnable;
    public String strdate;
    public TextView timer,empty;


    public List(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recycle);
        View a = inflater.inflate(R.layout.p_row,container,false);
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        empty = v.findViewById(R.id.emptyTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dBhelper = new DBhelper(getContext());
        ArrayList<ProductModel> arr = dBhelper.fetchdata();
        RecycleProductAdapter adapter = new RecycleProductAdapter(getContext(),arr,dBhelper);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RecycleProductAdapter adapter = new RecycleProductAdapter(getContext(),arr,dBhelper);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if(arr.isEmpty()){
                            recyclerView.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            empty.setVisibility(View.GONE);
                        }
                    }
                }, 2000);            }
        });
        if(arr.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
        return v;
    }

}