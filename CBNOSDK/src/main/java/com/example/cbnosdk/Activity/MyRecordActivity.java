package com.example.cbnosdk.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cbnosdk.CBNOApi;
import com.example.cbnosdk.R;
import com.example.cbnosdk.base.BaseMyCameraActivity;
import com.example.cbnosdk.constant.netConstant;
import com.example.cbnosdk.utiles.SpUtils;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.util.JsonMap;

import java.io.File;

import static com.example.cbnosdk.utiles.DataCleanManager.clear;

public class MyRecordActivity extends BaseMyCameraActivity {

    String name, phone, idcard,token,caseId;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        bundle=getIntent().getExtras();

        token=bundle.getString("token");
        caseId=bundle.getString("caseid");

        Log.d("录像初始化", "onCreate: "+token+caseId);
        onCreateActivity();
        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 1280;
        cameraHeight = 720;
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
                showProgressDialog(MyRecordActivity.this, "请稍后");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //要延时的程序
                        uploadVideo();
                    }
                }, 2000); //8000为毫秒单位
                //dismissProgressDialog();

//                Intent intent=new Intent(getBaseContext(), Apply3Activity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("imagepath",imagePath);
//                intent.putExtra("base64str",src);
//                startActivity(intent);
                Log.d("测试", "aaa");

            }
        });


    }
    //拦截器
    public void breaker(Context mContext) {
        clear(mContext);
        Intent intent = new Intent(mContext, Apply2Activity.class);
        //调到页面，关闭之前所有页面
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    public void uploadVideo() {
//        token = SpUtils.getInstance(this).getString("token", null);
//        caseId = SpUtils.getInstance(this).getString("caseId", null);
//        filepath = SpUtils.getInstance(this).getString("videopath", null);
        videoFile = new File(filepath);

        if (videoFile.exists()) {
            Log.d("判断文件是否存在", "uploadVideo: 文件存在");
            if (!IsFileInUse(filepath)) {
                HttpRequest.build(MyRecordActivity.this, netConstant.getUploadNotarizeVideo())
                        .setMediaType(baseokhttp3.MediaType.parse("video/mpeg4"))
                        .addHeaders("Authorization", "Bearer " + token)
                        .addHeaders("Content-Type", "multipart/form-data")
                        .addParameter("notarizeVideoFile", videoFile)
                        .addParameter("caseId", caseId)
                        .setJsonResponseListener(new JsonResponseListener() {
                            @Override
                            public void onResponse(JsonMap main, Exception error) {
//                                if (error!=null){
//                                    Log.e("上传","连接失败",error);
//                                    dismissProgressDialog();
//                                    Toast.makeText(CameraActivity.this,"连接失败,请重试",Toast.LENGTH_SHORT).show();
//                                }else {
                                if (main.getString("code").equals("200")) {
                                    //上传成功 上传传参
                                    Intent intent = new Intent(getBaseContext(), Apply3Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("name", name);
//                                    bundle.putString("idcard", idcard);
//                                    bundle.putString("phone", phone);
                                    bundle.putString("videopath", filepath);
                                    bundle.putString("imagepath", imagePath);
//                                    bundle.putString("base64str", src);
//                                    bundle.putString("token",token);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    dismissProgressDialog();
                                } else if (main.getString("code").equals("401")) {
                                    dismissProgressDialog();
                                    breaker(MyRecordActivity.this);
                                } else {
                                    Log.e("上传", main.getString("msg"));
                                    Log.e("上传", main.getString("code"));
                                    dismissProgressDialog();
                                    Toast.makeText(MyRecordActivity.this, main.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            }
//                            }
                        })
                        .doPost();
            } else {
                //文件正在被操作
                Log.d("a", "uploadVideo: 文件正在被操作");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        //do something
                        uploadVideo();
                    }
                }, 2000);    //延时1s执行
            }

        } else {
            Log.d("判断文件是否存在", "uploadVideo: 文件不存在");
            Toast.makeText(MyRecordActivity.this, "录制失败，请重新录制", Toast.LENGTH_SHORT).show();

            tv_camera_timer.setText("00:00");
            uploadBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setImageResource(R.drawable.error);
            recordBtn.setEnabled(true);
        }


    }

    private void deleteVideo(File file) {
        if (file.exists()) {
            file.delete();
        } else {
            Log.d("删除", "deletePdf: 删除失败");
        }
    }
}