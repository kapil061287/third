package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.modal.Membership;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pay_btn)
    Button payBtn;
    @BindView(R.id.payment_card)
    CardMultilineWidget cardMultilineWidget;
    SharedPreferences preferences;
    Membership membership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_color));
        preferences=getSharedPreferences(Utils.SITE_PREF, MODE_PRIVATE);
        payBtn.setOnClickListener(this);
        String json=getIntent().getExtras().getString("json");
        membership=Membership.fromJson(json);
    }

    private void paymentProcess() {
            Card card=cardMultilineWidget.getCard();
            card.setName(preferences.getString("fullname", "0"));
            if(card.validateCard()){
                Stripe stripe=new Stripe(this, "pk_test_qdtBZkSgzZkD36xIS8ASlKcm");
                stripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Log.e("responseDataError", "Payment Error : "+error.toString());
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Log.i("tokenGen", token.getId());
                        String tokenStr=token.getId();
                        MyTask asyn=new MyTask();
                        asyn.execute(tokenStr);
                    }
                });
            }
    }



    @Override
    public void onClick(View view) {
        paymentProcess();
    }

    class MyTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            com.stripe.Stripe.apiKey="sk_test_dYNtoGXjfL5Le0gABqZSvmzB";
            Map<String, Object> params = new HashMap<>();
            String amountStr=membership.getOneMonthAmount();
            int amount= (int) (Float.parseFloat(amountStr)*100);

            params.put("amount", amount /*amoutn in cent*/);
            params.put("currency", "usd");

            params.put("source", strings[0]);
            Map<String,  Object> invoiceParams=new HashMap<>();
            invoiceParams.put("amount_due", 0);
            invoiceParams.put("amount_paid", amount);

            try {
                Charge charge = Charge.create(params);
                Log.i("responseDataCharge", charge.toJson());
                return charge.getPaid();
            } catch (AuthenticationException e) {
                Log.e("responseDataError","AuthenticationException" +e.toString());
            } catch (InvalidRequestException e) {
                Log.e("responseDataError","InvalidRequestException" +e.toString());
            } catch (APIConnectionException e) {
                Log.e("responseDataError","APIConnectionException" +e.toString());
            } catch (CardException e) {
                Log.e("responseDataError","CardException" +e.toString());
            } catch (APIException e) {
                Log.e("responseDataError","APIException" +e.toString());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
                   /* Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("paid", aBoolean);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();*/
        }
    }
}
