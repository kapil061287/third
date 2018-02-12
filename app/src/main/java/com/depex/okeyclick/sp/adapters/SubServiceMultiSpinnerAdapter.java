package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.SubService;

import java.util.List;

public class SubServiceMultiSpinnerAdapter extends BaseAdapter{

    Context context;
    List<SubService> subCategories;
    public SubServiceMultiSpinnerAdapter(Context context, List<SubService> subCategories){
        this.subCategories=subCategories;
        this.context=context;
    }


    @Override
    public int getCount() {
        return subCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return subCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return subCategories.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.sub_category_spinner_layout, parent, false);
            SubCategoryViewHolder viewHolder=new SubCategoryViewHolder();
            //TextView textView=convertView.findViewById(R.id.sub_service_spinner_txt);
            //textView.setText(subCategories.get(position).getServiceName());
            CheckBox checkBox=convertView.findViewById(R.id.sub_service_spinner_txt);
            checkBox.setText(subCategories.get(position).getServiceName());
            viewHolder.setCheckBox(checkBox);
            //viewHolder.setTextView(textView);
            convertView.setTag(viewHolder);
        }else {
            SubCategoryViewHolder viewHolder= (SubCategoryViewHolder) convertView.getTag();
            //viewHolder.getTextView().setText(subCategories.get(position).getServiceName());
            viewHolder.getCheckBox().setText(subCategories.get(position).getServiceName());
        }
        return convertView;
    }


    @Override
    public boolean isEnabled(int position) {
        if(position==0){
            return false;
        }
        return true;
    }



    private class SubCategoryViewHolder{
        CheckBox checkBox;


        TextView textView;


        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}