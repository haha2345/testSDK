package com.example.cbnocamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.util.HashMap;

//import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;


public class BaseActivity extends AppCompatActivity {
    //加载框
    private static ProgressDialog progressDialog;
//    public QMUITipDialog tipDialog;

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
    protected static void showProgressDialog(Context mContext, String text) {
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
    protected static Boolean dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                return true;//取消成功
            }
        }
        return false;//已经取消过了，不需要取消
    }
//    public QMUITipDialog getTipDialog(Context con, int type, String str) {
//        tipDialog = new QMUITipDialog.Builder(con)
//                .setIconType(type)
//                .setTipWord(str)
//                .create();
//        return tipDialog;
//    }
//    //1.5s后关闭tipDIalog
//    public void delayCloseTip(){
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //要延时的程序
//                tipDialog.dismiss();
//            }
//        },1500);
//    }

//    public void breaker(Context mContext){
//        clear(mContext);
//        Intent intent=new Intent(mContext, LoginActivity.class);
//        //调到页面，关闭之前所有页面
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//    }


    private void showToast(final Context con, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(con,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteFile(File file){
        if (file.exists()){
            file.delete();
        }else{
            Log.d("删除", "deletePdf: 删除失败");
        }
    }

    protected static void breaker(Context mContext){
        Toast.makeText(mContext,"token已失效",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(mContext, MyActivity.class);
        //调到页面，关闭之前所有页面
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
