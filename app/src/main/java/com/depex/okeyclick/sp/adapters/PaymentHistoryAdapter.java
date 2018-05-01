package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.depex.okeyclick.sp.GlideApp;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.PaymentHistoryModal;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {

    private Context context;
    private List<PaymentHistoryModal> paymentHistoryModals;

    public PaymentHistoryAdapter(Context context, List<PaymentHistoryModal> paymentHistoryModals){
        this.context=context;
        this.paymentHistoryModals=paymentHistoryModals;
    }

    @NonNull
    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.content_payment_history_rec_layout, parent, false);
        return new PaymentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryViewHolder holder, int position) {
        PaymentHistoryModal paymentHistoryModal=paymentHistoryModals.get(position);
        String url=paymentHistoryModal.getCategoryIcon();
        GlideApp.with(context).load(url).into(holder.subCategoryImage);
        holder.spName.setText(paymentHistoryModal.getSpName());
        holder.serviceName.setText(paymentHistoryModal.getCategory());
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM, yyyy h:m a");
        String time=dateFormat.format(paymentHistoryModal.getTaskDate());
        holder.totalPrice.setText(context.getString(R.string.uro)+paymentHistoryModal.getTotal());
        holder.serviceTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return paymentHistoryModals.size();
    }

    class PaymentHistoryViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.subcategory_icon)
        ImageView subCategoryImage;

        @BindView(R.id.sp_name)
        TextView spName;

        @BindView(R.id.service_name)
        TextView serviceName;

        @BindView(R.id.service_time)
        TextView serviceTime;

        @BindView(R.id.total_price)
        TextView totalPrice;

        public PaymentHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
