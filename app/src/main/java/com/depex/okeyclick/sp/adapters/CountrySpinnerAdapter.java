package com.depex.okeyclick.sp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.Country;

import java.util.ArrayList;
import java.util.List;

public class CountrySpinnerAdapter extends BaseAdapter {



    private  List<Country> countries;
    private Context context;

    public CountrySpinnerAdapter(List<Country> countries, Context context){
        this.countries=countries;
        this.context = context;
    }


    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countries.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView!=null){
            SignupViewHolder signupViewHolder = (SignupViewHolder) convertView.getTag();
                signupViewHolder.textView.setText(countries.get(position).getName());
        }else {
            convertView= LayoutInflater.from(context).inflate(R.layout.address_spinner_layout,parent, false);
            TextView textView=convertView.findViewById(R.id.address_spinner_txt);
            textView.setText(countries.get(position).getName());
            SignupViewHolder signupViewHolder =new SignupViewHolder();
            signupViewHolder.setTextView(textView);
            convertView.setTag(signupViewHolder);
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


}
