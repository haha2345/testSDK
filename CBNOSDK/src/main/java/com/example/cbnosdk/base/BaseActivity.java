package com.example.cbnosdk.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.cbnosdk.Activity.Apply2Activity;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.cbnosdk.utiles.DataCleanManager.clear;


public class BaseActivity extends AppCompatActivity {
    //加载框
    private ProgressDialog progressDialog;
    public QMUITipDialog tipDialog;

    //获取map中的key
    public String getKey(HashMap<String, String> map, String value) {
        String key = null;
        //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
        for (String getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                key = getKey;
            }
        }
        return key;
        //这个key肯定是最后一个满足该条件的key.
    }
    //显示加载框
    public void showProgressDialog(Context mContext, String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(text);    //设置内容
        progressDialog.setCancelable(false);//点击屏幕和按返回键都不能取消加载框
        progressDialog.show();

        //设置超时自动消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dismissProgressDialog()) {

                }
            }
        }, 60000);//超时时间60秒
    }

    //取消加载框
    public Boolean dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                return true;//取消成功
            }
        }
        return false;//已经取消过了，不需要取消
    }
    public QMUITipDialog getTipDialog(Context con, int type, String str) {
        tipDialog = new QMUITipDialog.Builder(con)
                .setIconType(type)
                .setTipWord(str)
                .create();
        return tipDialog;
    }
    //1.5s后关闭tipDIalog
    public void delayCloseTip(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //要延时的程序
                tipDialog.dismiss();
            }
        },1500);
    }

//    public void breaker(Context mContext){
//        clear(mContext);
//        Intent intent=new Intent(mContext, LoginActivity.class);
//        //调到页面，关闭之前所有页面
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//    }


    public void showToast(final Context con, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(con,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteFile(File file){
        if (file.exists()){
            file.delete();
        }else{
            Log.d("删除", "deletePdf: 删除失败");
        }
    }

    public void breaker(Context mContext){
        clear(mContext);
        Intent intent=new Intent(mContext, Apply2Activity.class);
        //调到页面，关闭之前所有页面
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
