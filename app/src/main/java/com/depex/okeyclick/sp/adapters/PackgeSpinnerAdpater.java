package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.Package;

import java.util.List;

/**
 * Created by we on 1/16/2018.
 */

public class PackgeSpinnerAdpater extends BaseAdapter {


    Context context;
    List<Package> packages;
    public PackgeSpinnerAdpater(Context context, List<Package> packages){
        this.context=context;
        this.packages=packages;
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public Object getItem(int position) {
        return packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return packages.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.service_spinner_layout, parent, false);
            PackageViewHolder viewHolder=new PackageViewHolder();
            TextView textView=convertView.findViewById(R.id.service_spinner_txt);
            textView.setText(packages.get(position).getPackageName());
            viewHolder.setTextView(textView);
            convertView.setTag(viewHolder);

        }else {
            PackageViewHolder packageViewHolder= (PackageViewHolder) convertView.getTag();
            packageViewHolder.getTextView().setText(packages.get(position).getPackageName());
        }

        return convertView;
    }

    private class PackageViewHolder{
        TextView textView;
        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}
