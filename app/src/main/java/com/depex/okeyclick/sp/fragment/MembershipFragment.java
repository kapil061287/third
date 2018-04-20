package com.depex.okeyclick.sp.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.appscreens.PaymentActivity;
import com.depex.okeyclick.sp.modal.Membership;
import com.depex.okeyclick.sp.view.CheckboxPlanSelect;
import com.depex.okeyclick.sp.view.OkeyClickTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MembershipFragment extends Fragment implements View.OnClickListener {


    private static final int PAYMENT_REQUEST_CODE = 1;
    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.other_details)
    LinearLayout otherDetails;
    private Context context;

    @BindView(R.id.membership_upgrade_btn)
    Button memberShipupdateBtn;

    @BindView(R.id.plan_name)
    TextView planName;
    Membership membership;

    @BindView(R.id.number_of_services)
    TextView numOfServices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_membership_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle=getArguments();
        String json=bundle.getString("json");
        membership= Membership.fromJson(json);
        planName.setText(membership.getPlanName()+" Plans");
        description.setText(membership.getDescription());
        memberShipupdateBtn.setOnClickListener(this);
        if(membership.getPlanName().equalsIgnoreCase("basic")){

        }else {
            memberShipupdateBtn.setText("Upgrade To "+membership.getPlanName());
        }
        String des="";
        if(membership.getDescription().length()>=40){
            des=membership.getDescription().substring(0, 40)+"...read more";

        }else {
            des=membership.getDescription();

        }
        description.setText(des);

        initOtherDetails(membership);

        return view;
    }

    private void initOtherDetails(Membership membership) {
        CheckboxPlanSelect planSelect=new CheckboxPlanSelect(context);
        //View view=textView.getView(otherDetails, "You can select "+membership.getNoOfServices()+" Services.");
        numOfServices.setText("You can select "+membership.getNoOfServices()+" Services.");
       // View view=planSelect.getView(otherDetails, "One month amount $"+)
    }

    public static MembershipFragment getInstance(Membership membership){
        String json=membership.toJson();
        Bundle bundle=new Bundle();
        bundle.putString("json", json);
        MembershipFragment fragment=new MembershipFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onClick(View view) {
        startPaymentActivity(membership);
    }

    private void startPaymentActivity(Membership membership) {
        Intent intent=new Intent(context, PaymentActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("json", membership.toJson());
        intent.putExtras(bundle);
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
