package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.City;

import java.util.List;

/**
 * Created by we on 1/11/2018.
 */

public class CitySpinnerAdapter  extends BaseAdapter {


    List<City> cities;
    Context context;

    public CitySpinnerAdapter(Context context, List<City> cities){
        this.cities=cities;
        this.context=context;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cities.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView!=null){
            SignupViewHolder signupViewHolder= (SignupViewHolder) convertView.getTag();
            signupViewHolder.getTextView().setText(cities.get(position).getCityName());
        }else
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.address_spinner_layout, parent, false);
            TextView textView=convertView.findViewById(R.id.address_spinner_txt);
            textView.setText(cities.get(position).getCityName());
            SignupViewHolder signupViewHolder=new SignupViewHolder();
            signupViewHolder.setTextView(textView);
            convertView.setTag(signupViewHolder);
        }
        /*if(position==0){
            SignupViewHolder valueHolder= (SignupViewHolder) convertView.getTag();
            valueHolder.getTextView().setTextColor(Color.parseColor("#ffaaaaaa"));
        }*/
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if(position==0){
            return false;
        }
        return true;
    }
}
