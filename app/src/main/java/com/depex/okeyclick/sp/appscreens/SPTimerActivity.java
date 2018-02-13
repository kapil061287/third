package com.depex.okeyclick.sp.appscreens;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.depex.okeyclick.sp.R;
import com.google.gson.TypeAdapterFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SPTimerActivity extends AppCompatActivity {

    SharedPreferences preferences;
    @BindView(R.id.timer_text)
    TextView timer_text;

    @BindView(R.id.progress_icno_image)
    ImageView timer_image;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_timer);

        ButterKnife.bind(this);

        preferences=getSharedPreferences("service_pref_user", MODE_PRIVATE);
        Typeface typeface= ResourcesCompat.getFont(this, R.font.digital);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPTimerActivity.super.onBackPressed();
            }
        });
        timer_text.setTypeface(typeface);
    }
}