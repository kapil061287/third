package com.depex.okeyclick.sp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.depex.okeyclick.sp.R;

/**
 * Created by we on 1/31/2018.
 */

public class PublicProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_public_profile_fragment, container, false);

        Toolbar toolbar=getActivity().getWindow().getDecorView().findViewById(R.id.toolbar);
        toolbar.setTitle("PUBLIC PROFILE");

        return view;
    }
}
