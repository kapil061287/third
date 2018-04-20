package com.depex.okeyclick.sp.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.depex.okeyclick.sp.modal.Service;
import com.depex.okeyclick.sp.modal.SubService;


public class RenderChooseSubServiceView extends ViewRender<SubService>  {


    public RenderChooseSubServiceView(Context context, int layoutRes) {
        super(context, layoutRes);
    }

    @Override
    public void bindView(View v, SubService data) {
                if(v instanceof CheckBox){
                    CheckBox checkBox= (CheckBox) v;
                    checkBox.setText(data.getServiceName());
                }
    }
}
