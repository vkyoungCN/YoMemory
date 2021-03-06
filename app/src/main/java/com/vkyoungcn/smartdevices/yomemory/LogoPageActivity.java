package com.vkyoungcn.smartdevices.yomemory;

import android.animation.AnimatorSet;
import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vkyoungcn.smartdevices.yomemory.customUI.HorizontalProgressBar;
import com.vkyoungcn.smartdevices.yomemory.sqlite.YoMemoryDbHelper;

import java.lang.ref.WeakReference;
/*
 * 作者：杨胜 @中国海洋大学
 * 别名：杨镇时
 * author：Victor Young@ Ocean University of China
 * email: yangsheng@ouc.edu.cn
 * 2018.08.01
 * */
public class LogoPageActivity extends AppCompatActivity implements Constants {
//* 程序第一个页面，过渡性页面（结束后不可返回）
//* 功能：①欢迎（Logo动画）；②首次运行时向DB填充预置数据；
    private static final String TAG = "LogoPageActivity";
    private Handler handler = new LogoPageActivity.FirstActivityHandler(this);//通过其发送消息。
    public static final int MESSAGE_DB_POPULATED = 5001;
    public static final int MESSAGE_NEW_PERCENTAGE_NUMBER = 5002;

    private TextView logoStrCn;
    private HorizontalProgressBar hpb_progress;//需要传入百分比的分子数值。
    private LinearLayout llt_firstRun;
//    private ImageView imv_du_1;
//    private ImageView imv_du_2;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_page);

        logoStrCn = findViewById(R.id.logo_cn);
        hpb_progress = findViewById(R.id.hpb_LPA);
        llt_firstRun = findViewById(R.id.llt_forFirstRun_LPA);
//        imv_du_1 = findViewById(R.id.imv_du_1);
//        imv_du_2 = findViewById(R.id.imv_du_2);


        SharedPreferences sharedPreferences=getSharedPreferences(SP_STR_TITLE_YO_MEMORY, MODE_PRIVATE);
        boolean isFirstLaunch=sharedPreferences.getBoolean(STR_IS_FIRST_LAUNCH, true);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(isFirstLaunch){
            //第一次运行：开启新线程执行DB填充操作然，同时提示。
            //完成后跳转下一页
            llt_firstRun.setVisibility(View.VISIBLE);
            new Thread(new PopTheDatabaseRunnable()).start();

            editor.putBoolean(STR_IS_FIRST_LAUNCH, false);
            editor.apply();
        }else{
            //执行动画效果，在动画执行完后（监听器）跳转下一Activity。
            startAnimator();
        }
    }

    public class PopTheDatabaseRunnable implements Runnable{
        @Override
        public void run() {
            YoMemoryDbHelper memoryDbHelper = YoMemoryDbHelper.getInstance(context);//应该是在获取DB-Helper后直接触发数据填充吧。

            Message message = new Message();
            message.what = MESSAGE_DB_POPULATED;
//            message.obj = missions;

            handler.sendMessage(message);
        }

    }


    final static class FirstActivityHandler extends Handler {
        private final WeakReference<LogoPageActivity> activityWeakReference;

        private FirstActivityHandler(LogoPageActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LogoPageActivity LogoPageActivity = activityWeakReference.get();
            if(LogoPageActivity!=null){
                LogoPageActivity.handleMessage(msg);
            }
        }
    }

    void handleMessage(Message message){
        switch (message.what){
            case MESSAGE_DB_POPULATED:
//                missions = (ArrayList<Mission>) message.obj;

                Intent intent= new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case MESSAGE_NEW_PERCENTAGE_NUMBER:
                hpb_progress.setNewPercentage(message.arg1);
        }
    }

//    动画
    private void startAnimator(){
        AnimatorSet animationSet = new AnimatorSet();

        ValueAnimator CenterLogoAnimator = ValueAnimator.ofFloat(0,80,90,100);

//        CenterLogoAnimator.setDuration(1200);
        CenterLogoAnimator.addUpdateListener(new LogoDisAnimatorListener());
        CenterLogoAnimator.setInterpolator(new LinearInterpolator());
        CenterLogoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                Intent intent= new Intent(LogoPageActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });

        /*ValueAnimator Du_Animator_1 = ValueAnimator.ofFloat(0,100,100,100);
//        Du_Animator_1.setDuration(1200);
        Du_Animator_1.addUpdateListener(new DuDuAnimatorListener_1());
        Du_Animator_1.setInterpolator(new LinearInterpolator());

        ValueAnimator Du_Animator_2 = ValueAnimator.ofFloat(0,0,0,100,100);
//        Du_Animator_2.setDuration(1200);
        Du_Animator_2.addUpdateListener(new DuDuAnimatorListener_2());
        Du_Animator_2.setInterpolator(new LinearInterpolator());*/

        animationSet.setDuration(1200);
        animationSet.playTogether(CenterLogoAnimator);
        animationSet.start();

    }

    private class LogoDisAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        public void onAnimationUpdate(ValueAnimator valueanimator) {
            float value = (Float)valueanimator.getAnimatedValue();
            logoStrCn.setAlpha(value/100);
        }
    }
   /* private class DuDuAnimatorListener_1 implements ValueAnimator.AnimatorUpdateListener {

        public void onAnimationUpdate(ValueAnimator valueanimator) {
            float value = (Float)valueanimator.getAnimatedValue();
            imv_du_1.setAlpha(value/100);
        }
    }
   private class DuDuAnimatorListener_2 implements ValueAnimator.AnimatorUpdateListener {

        public void onAnimationUpdate(ValueAnimator valueanimator) {
            float value = (Float)valueanimator.getAnimatedValue();
            imv_du_2.setAlpha(value/100);
        }
    }*/

    /*
    * 此方法在DB的填充逻辑中调用，用于根据填充的进度修改进度条的显示
    * 【但是注意，由于DB的填充逻辑是在新线程中发起的，因而不能直接在此对hpb控件设置，
    * 否则程序崩溃，提示大致为“新线程触碰了UI线程，不应这样做”】
    * */
    public void setNewPercentNum(int percentageNum){
//        Log.i(TAG, "setNewPercentNum:"+percentageNum);
        Message msgForNum = new Message();
        msgForNum.what = MESSAGE_NEW_PERCENTAGE_NUMBER;
        msgForNum.arg1 = percentageNum;
        handler.sendMessage(msgForNum);

    }

}
