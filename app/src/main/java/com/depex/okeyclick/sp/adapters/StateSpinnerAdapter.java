package com.depex.okeyclick.sp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.Country;
import com.depex.okeyclick.sp.modal.State;

import java.util.List;

/**
 * Created by we on 1/11/2018.
 */

public class StateSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<State> states;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public StateSpinnerAdapter(Context context, List<State> states){
        this.states=states;
        this.context=context;
    }

    @Override
    public int getCount() {
        return states.size();
    }

    @Override
    public Object getItem(int position) {
        return states.get(position);
    }

    @Override
    public long getItemId(int position) {
        return states.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView!=null){
            SignupViewHolder signupViewHolder= (SignupViewHolder) convertView.getTag();
            signupViewHolder.getTextView().setText(states.get(position).getStateName());
        }else {
            convertView= LayoutInflater.from(context).inflate(R.layout.address_spinner_layout,parent, false);
            TextView textView=convertView.findViewById(R.id.address_spinner_txt);
            textView.setText(states.get(position).getStateName());
            SignupViewHolder signupViewHolder=new SignupViewHolder();
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