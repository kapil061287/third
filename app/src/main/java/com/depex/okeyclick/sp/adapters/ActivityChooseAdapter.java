package com.depex.okeyclick.sp.adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.modal.Service;
import com.depex.okeyclick.sp.modal.SubService;
import com.depex.okeyclick.sp.view.RenderChooseSubServiceView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ActivityChooseAdapter extends BaseAdapter {

    private Context context;
    private List<Service> services;
    private Set<String> categoryIdSet=new HashSet<>();
    private Set<String> subCategoryIdSet=new HashSet<>();

    public ActivityChooseAdapter(Context context, List<Service> services){
        this.context=context;
        this.services=services;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int i) {
        return services.get(i);
    }

    @Override
    public long getItemId(int i) {
        return services.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1=LayoutInflater.from(context).inflate(R.layout.content_choos_activity_recy, viewGroup, false);
        TextView textView=view1.findViewById(R.id.service_text_view);
        LinearLayout layout=view1.findViewById(R.id.parent_linear_layout);
        Service service=services.get(i);

        if(service.getSubServices()!=null)
        textView.setText(service.getServiceName());
        if(service.getSubServices()!=null) {
            for (final SubService subService : service.getSubServices()) {
                RenderChooseSubServiceView renderChooseSubServiceView = new RenderChooseSubServiceView(context, R.layout.check_box_render_for_choose_activity);
                View view2 = renderChooseSubServiceView.getView(layout, subService);


                layout.addView(view2);

                final CheckBox checkBox= (CheckBox) view2;

                if(subCategoryIdSet.contains(subService.getId()))
                    checkBox.setChecked(true);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if(b){
                            String id=subService.getId();
                            String serviceId=subService.getServiceId();
                            subCategoryIdSet.add(id);
                            categoryIdSet.add(serviceId);
                            Log.i("subCategorySet", subCategoryIdSet.toString()+", "+categoryIdSet.toString());
                        }else {
                            subCategoryIdSet.remove(subService.getId());

                            Log.i("subCategorySet", subCategoryIdSet.toString()+", "+categoryIdSet.toString());
                        }
                    }
                });
            }
        }
        return view1;
    }

}