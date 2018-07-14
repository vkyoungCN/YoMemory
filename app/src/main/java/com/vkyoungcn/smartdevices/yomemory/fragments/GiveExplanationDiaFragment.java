package com.vkyoungcn.smartdevices.yomemory.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vkyoungcn.smartdevices.yomemory.R;
import com.vkyoungcn.smartdevices.yomemory.models.RVGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("all")
public class GiveExplanationDiaFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "GiveExplanationDiaFragment";
    private OnGeneralDfgInteraction mListener;

    private TextView tvConfirm;

    public GiveExplanationDiaFragment() {
        // Required empty public constructor
    }

    public static GiveExplanationDiaFragment newInstance() {
        GiveExplanationDiaFragment fragment = new GiveExplanationDiaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.dfg_explanation, container, false);

        tvConfirm = (TextView) rootView.findViewById(R.id.btn_confirm_explanationGel);
        tvConfirm.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm_explanationGel:
                this.dismiss();
                break;
        }
    }
}