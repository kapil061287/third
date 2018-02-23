package com.depex.okeyclick.sp.view;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.Spinner;

import com.depex.okeyclick.sp.adapters.SubServiceMultiSpinnerAdapter;
import com.depex.okeyclick.sp.modal.SubService;

import java.util.ArrayList;

/**
 * Created by we on 1/16/2018.
 */

public class MultiChoiceSpinner extends android.support.v7.widget.AppCompatSpinner {

    SubServiceMultiSpinnerAdapter adapter;
    public MultiChoiceSpinner(Context context, SubServiceMultiSpinnerAdapter adapter) {
        super(context);
        this.adapter=adapter;
    }

}
