package com.depex.okeyclick.sp.appscreens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.depex.okeyclick.sp.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentConfirmActivity extends AppCompatActivity {

    @BindView(R.id.state_progress)
    StateProgressBar stateProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);
        ButterKnife.bind(this);
        String[]arr=new String[]{"Service\nAccepted", "On the \nWay", "Arrived to \nLocation", "Start the \nJob", "Finish \nJob"};
        stateProgressBar.setStateDescriptionData(arr);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
    }
}
