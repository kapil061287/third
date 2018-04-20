package com.depex.okeyclick.sp.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.depex.okeyclick.sp.R;


public class CheckboxPlanSelect extends ViewRender<String> {

    public CheckboxPlanSelect(Context context ) {

        super(context, R.layout.content_check_box_plan_select);
    }

    @Override
    public void bindView(View v, String data) {
        CheckBox checkBox= (CheckBox) v;
        checkBox.setText(data);
    }
}
