package com.vkyoungcn.smartdevices.yomemory.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vkyoungcn.smartdevices.yomemory.R;

import static com.vkyoungcn.smartdevices.yomemory.fragments.OnGeneralDfgInteraction.LEARNING_AND_CREATE_ORDER;
import static com.vkyoungcn.smartdevices.yomemory.fragments.OnGeneralDfgInteraction.LEARNING_AND_CREATE_RANDOM;


/*
* 本DFG对应普通学习模式下的手动结束情形：
* （首先显示还剩余多少时间，可以返回查看）
* 需要传入：总量、未完成量。（若只传未完成量，则无法分辨是否是“一个也没写”的极端情况）
* ①提示总量、未完成量，提示会拆分；（or全部完成、or全部未完成（隐藏确认键））
* ②有未正确的，提示将记录错误一次；
* ③如果没有上述情况任一，则提示正确（？），也可返回查看。
*
* 确然后的跳转操作还是由Activity负责，所以不需要持有gid。
* 三种不同学习模式的手动结束DFG使用同一布局文件。
* */
public class HandyFinishLgDiaFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "HandyFinishLgDiaFragment";

    private int totalAmount;
    private int emptyAmount;
    private int wrongAmount;
    private int restSeconds;

    private TextView tvWrongInfo;
    private TextView tvEmptyInfo;
    private TextView tvRestTimeInfo;
    private TextView btn_Confirm;

    private OnGeneralDfgInteraction mListener;

    public HandyFinishLgDiaFragment() {
        // Required empty public constructor
    }

    public static HandyFinishLgDiaFragment newInstance(int totalAmount,int emptyAmount, int wrongAmount, int restSeconds) {
        HandyFinishLgDiaFragment fragment = new HandyFinishLgDiaFragment();
        Bundle args = new Bundle();
        args.putInt("TOTAL_AMOUNT",totalAmount);
        args.putInt("WRONG_AMOUNT",wrongAmount);
        args.putInt("EMPTY_AMOUNT",emptyAmount);
        args.putInt("REST_SECONDS",restSeconds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.totalAmount = getArguments().getInt("TOTAL_AMOUNT");
            this.wrongAmount = getArguments().getInt("WRONG_AMOUNT");
            this.emptyAmount = getArguments().getInt("EMPTY_AMOUNT");
            this.restSeconds = getArguments().getInt("REST_SECONDS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dfg_learning_handy_finish, container, false);
        rootView.findViewById(R.id.tvBtn_back_dfgLHFinish).setOnClickListener(this);
        rootView.findViewById(R.id.tvBtn_giveUp_dfgLHFinish).setOnClickListener(this);
        btn_Confirm = rootView.findViewById(R.id.tvBtn_confirm_dfgLHFinish);
        btn_Confirm.setOnClickListener(this);
        TextView space = rootView.findViewById(R.id.space_64);

        tvWrongInfo = rootView.findViewById(R.id.tv_wrong_dfgLHFinish);
        tvEmptyInfo = rootView.findViewById(R.id.tv_empty_dfgLHFinish);
        tvRestTimeInfo = rootView.findViewById(R.id.tv_restTime_dfgLHFinish);


        if(wrongAmount==0 ){
            tvWrongInfo.setText(getResources().getString(R.string.wrong_amount_equals_zero));
            //如果下方finishAmount==0,则应对本Tv进行覆盖。
        }else {
            tvWrongInfo.setText(String.format(getResources().getString(R.string.wrong_amount_some),wrongAmount));
        }

        if(emptyAmount == totalAmount){
            //一个没写
            tvEmptyInfo.setText(getResources().getString(R.string.finish_amount_equals_zero_LG));
            tvWrongInfo.setVisibility(View.GONE);//一个没写当然没错误，但也绝不是“全对”。不予显示。
            space.setVisibility(View.VISIBLE);//替代性地，在时间tv下方，按钮组上方显示一个大空间以撑开整体
            btn_Confirm.setVisibility(View.GONE);//此时不建组，确认键无意义。不予显示。
        }else {
                tvEmptyInfo.setText(String.format(getResources().getString(R.string.finish_amount_some_LG), totalAmount,emptyAmount));
                //总量%1$d个，未完成的%2$d个单词会被拆分到新分组
        }

        tvRestTimeInfo.setText(String.format(getResources().getString(R.string.rest_min_and_sec),restSeconds/60,restSeconds%60));

        return rootView;



    }

    @Override
    public void onClick(View v) {
        //不论点击的是确认还是取消或其他按键，直接调用Activity中实现的监听方法，
        // 将view的id传给调用方处理。
        switch (v.getId()){
            case R.id.tvBtn_confirm_dfgLHFinish:
                // 由LearningActivity根据其持有的状态列表具体判断拆分等操作。在此不判断不传递。
                mListener.onButtonClickingDfgInteraction(OnGeneralDfgInteraction.LEARNING_FINISH_DFG_CONFIRM,null);
                dismiss();

                break;
            case R.id.tvBtn_back_dfgLHFinish:
                //返回查看，继续计时
                mListener.onButtonClickingDfgInteraction(OnGeneralDfgInteraction.LEARNING_FINISH_DFG_BACK,null);
                dismiss();

                break;

            case R.id.tvBtn_giveUp_dfgLHFinish:
                //放弃，就当什么都没发生。通知Act，直接返回分组的Rv列表页。
                mListener.onButtonClickingDfgInteraction(OnGeneralDfgInteraction.LEARNING_FINISH_DFG_GIVE_UP,null);
                dismiss();

                break;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGeneralDfgInteraction) {
            mListener = (OnGeneralDfgInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGeneralDfgInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}