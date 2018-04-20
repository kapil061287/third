package com.depex.okeyclick.sp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.adapters.ServiceHistoryAdapter;
import com.depex.okeyclick.sp.constants.UtilsMethods;
import com.depex.okeyclick.sp.listener.ServiceHistoryItemClickListener;
import com.depex.okeyclick.sp.modal.ServiceHistory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceHistoryCompleteFragment extends Fragment implements ServiceHistoryItemClickListener {
    @BindView(R.id.service_history_complete_fragment_rec)
    RecyclerView serviceHistoryCompleteRec;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_service_history_complete_fragment,  container, false);
        ButterKnife.bind(this, view);
        Bundle bundle=getArguments();
        String json=bundle.getString("json");
        Log.i("responseData", "Service History Complete : "+json);
        Gson gson=new GsonBuilder().setDateFormat(getString(R.string.date_time_format_from_web)).create();
        ServiceHistory[]arr=gson.fromJson(json, ServiceHistory[].class);
        List<ServiceHistory> list= new ArrayList<>(Arrays.asList(arr));
        List<ServiceHistory> list1=new ArrayList<>();
        for(ServiceHistory serviceHistory : list ){
            if(serviceHistory.getStatus().equalsIgnoreCase("8")||serviceHistory.getStatus().equalsIgnoreCase("7")){
                list1.add(serviceHistory);
            }
        }
        ServiceHistoryAdapter adapter=new ServiceHistoryAdapter(context, list1, this);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        serviceHistoryCompleteRec.setLayoutManager(manager);
        serviceHistoryCompleteRec.setAdapter(adapter);

        return view;
    }

    public static ServiceHistoryCompleteFragment newInstance(String json){
        ServiceHistoryCompleteFragment fragment=new ServiceHistoryCompleteFragment();
        Bundle bundle=new Bundle();
        bundle.putString("json", json);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onServiceHistoryItemClickListener(ServiceHistory serviceHistory) {
        UtilsMethods.viewTaskProcess(context, serviceHistory);
    }
}
