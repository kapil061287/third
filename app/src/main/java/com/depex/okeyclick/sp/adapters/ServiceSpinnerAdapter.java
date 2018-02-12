package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.Service;

import java.util.List;

/**
 * Created by we on 1/15/2018.
 */

public class ServiceSpinnerAdapter extends BaseAdapter{

    List<Service> services;
    Context context;
    public ServiceSpinnerAdapter(Context context,List<Service> services ){
        this.services=services;
        this.context=context;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return services.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.service_spinner_layout, parent, false);
            ServiceViewHolder holder=new ServiceViewHolder();
            TextView textView=convertView.findViewById(R.id.service_spinner_txt);
            Service service=services.get(position);
            String servicename=service.getServiceName();
            textView.setText(servicename);
            holder.setTextView(textView);
            convertView.setTag(holder);
        }else {
                ServiceViewHolder holder= (ServiceViewHolder) convertView.getTag();
                holder.getTextView().setText(services.get(position).getServiceName());
        }
        return convertView;
    }

    private class ServiceViewHolder{
        TextView textView;
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if(position==0){
            return false;
        }else
            return true;
    }
}