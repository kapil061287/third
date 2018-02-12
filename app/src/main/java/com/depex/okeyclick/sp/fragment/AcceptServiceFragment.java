package com.depex.okeyclick.sp.fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.depex.okeyclick.sp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class AcceptServiceFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    CircularProgressBar progressBar;
    Context context;
    TextView updateTimeText;
    GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.content_accept_service_fragment, container, false);
        SupportMapFragment mapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_in_accept__service_frgment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mapFragment.getView().setElevation(2);
        }
        updateTimeText=view.findViewById(R.id.update_time_txt);
        Typeface typeface=Typeface.createFromAsset(context.getAssets(), "fonts/digital.ttf");
        updateTimeText.setTypeface(typeface);
        progressBar=view.findViewById(R.id.circularProgressBar);
        progressBar.setOnClickListener(this);
        progressBar.setProgress(0);
        MyTask myTask=new MyTask();
        myTask.execute(0);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Progress bar clicked ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    private class MyTask extends AsyncTask<Integer, Integer, String>{
        boolean isIncrease;
        int updateTimeValue=0;
        @Override
        protected String doInBackground(Integer... integers) {

            for (int i=integers[0];i<=120;i++){
                try {
                    Thread.sleep(250);
                    publishProgress(i);
                    Thread thread=Thread.currentThread();
                    Log.i("threadLog", thread.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Success";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if(values.length>0) {
                float progress_step=100.f/120.f;
                float current_progress=progress_step*values[0];
                ViewGroup.LayoutParams params=progressBar.getLayoutParams();
                ConstraintLayout.LayoutParams params1= (ConstraintLayout.LayoutParams) params;
                setProgress(current_progress);
                if(values[0]%4==0){
                    int updateTime=30-updateTimeValue;
                    if(updateTime>-1) {
                        if (updateTime < 10) {
                            updateTimeText.setText("00 : 0" + updateTime);
                        } else {
                            updateTimeText.setText("00 : " + updateTime);
                        }
                    }
                    updateTimeValue++;
                }
            }
        }
    }

    public void setProgress(float f){
        progressBar.setProgress(f);
    }
}