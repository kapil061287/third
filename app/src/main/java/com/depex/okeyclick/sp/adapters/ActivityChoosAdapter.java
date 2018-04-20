package com.depex.okeyclick.sp.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.ChooseAdapterModal;
import com.depex.okeyclick.sp.modal.Service;
import com.depex.okeyclick.sp.modal.SubService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityChoosAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Service> services;
    Set<ChooseAdapterModal> adapterModals=new HashSet<>();
    Set<Integer> expandGroupId=new HashSet<>();

    public ActivityChoosAdapter( Context context, List<Service> services, Set<ChooseAdapterModal> adapterModals){
        this.services=services;
        this.context=context;
        if(adapterModals!=null){
            this.adapterModals.addAll(adapterModals);
           for(ChooseAdapterModal modal : this.adapterModals){
               Log.i("adapterModal", modal.toString());
           }

        }
    }

    public Set<ChooseAdapterModal> getAdapterModals() {
        return adapterModals;
    }

    public void setAdapterModals(Set<ChooseAdapterModal> adapterModals) {
        this.adapterModals = adapterModals;
    }

    @Override
    public int getGroupCount() {
        return services.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(services.get(i).getSubServices()!=null)
        return services.get(i).getSubServices().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int i) {
        return services.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        Service service=services.get(i);
        if(service.getSubServices()!=null) {
            return services.get(i).getSubServices().get(i1);
        }else
            return null;
    }

    @Override
    public long getGroupId(int i) {
        return services.get(i).hashCode();
    }

    @Override
    public long getChildId(int i, int i1) {
        Service service=services.get(i);
        if(service.getSubServices()!=null) {
            return services.get(i).getSubServices().get(i1).hashCode();
        }else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View view1= LayoutInflater.from(context).inflate(R.layout.content_choos_activity_recy, viewGroup, false);
        Service service=services.get(i);
        String serviceName=service.getServiceName();
        TextView textView=view1.findViewById(R.id.service_text_view);
        Drawable drawable=context.getResources().getDrawable(R.drawable.ic_expand_less_black_24dp);
        if(expandGroupId.contains(i))
        textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_less_black_24dp, 0);
        else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more_black_24dp, 0);
        }
        textView.setText(serviceName);
        return view1;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final SubService subService=services.get(i).getSubServices().get(i1);
        final Service service=services.get(i);
        View view2=LayoutInflater.from(context).inflate(R.layout.check_box_render_for_choose_activity, viewGroup, false);
        CheckBox checkBox=view2.findViewById(R.id.checkbox_choose_activity);
        checkBox.setText(subService.getServiceName());
        final ChooseAdapterModal modal=new ChooseAdapterModal();
        modal.setServiceId(subService.getServiceId());
        modal.setSubServiceId(subService.getId());
        modal.setSubServiceName(subService.getServiceName());
        modal.setServiceName(service.getServiceName());
        if(adapterModals.contains(modal)){
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    adapterModals.add(modal);
                    Log.i("viewModalIn", adapterModals.toString());
                }else {
                            adapterModals.remove(modal);
                    Log.i("viewModalIn", adapterModals.toString());
                }
            }
        });
        return view2;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        expandGroupId.add(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        expandGroupId.remove(groupPosition);
    }
}