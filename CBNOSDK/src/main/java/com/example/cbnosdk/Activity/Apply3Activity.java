package com.example.cbnosdk.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cbnosdk.R;
import com.example.cbnosdk.base.BaseApply3Activity;
//import com.qmuiteam.qmui.widget.QMUITopBarLayout;
//import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.org.bjca.signet.component.core.activity.SignetCoreApi;
import cn.org.bjca.signet.component.core.bean.results.SignImageResult;
import cn.org.bjca.signet.component.core.callback.SetSignImageCallBack;
import cn.org.bjca.signet.component.core.enums.SetSignImgType;

public class Apply3Activity extends BaseApply3Activity {

//    private QMUITipDialog tipDialog;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;



    TextView tv_apply3_name;
    TextView tv_apply3_name1;
    TextView tv_apply3_name2;
    TextView tv_apply3_bank;
    TextView tv_apply3_file_name1;
    TextView tv_apply3_file_no;
    TextView tv_apply3_money;
    LinearLayout lv_apply3;
    //初始化下面两个界面
    LinearLayout lv_apply3_auto;
    RelativeLayout rv_apply3_auto;
    ImageView iv_apply3_yes;
    ImageView im_apply_auto;
    TextView tv_apply3_auto_date;
    TextView tv_apply3_reauto;
    ImageView iv_apply3_yes1;
    //生成pdf的组件
    ImageView sign_image;

    RelativeLayout re_sign;

    LinearLayout lv_apply3_record;
    //这是录像成功后的界面
    RelativeLayout rv_apply3_record;
    ImageView im_apply_record;
    TextView tv_apply3_re_record;
    TextView tv_apply3_record_date;
    //初始化按钮
    Button sbtn_apply3_next;

//    QMUITopBarLayout mTopBar;


    private Intent intent;
    private String name = null,
            bank = null,
            phone = null,
            idcard = null;

    private String token;
    private String loanName,loanMoney,loanCode;

/*    private String name = "王文哲",
            bank = "建设银行",
            videoPath = null,
            imagePath = null,
            cbno_phone = "13205401086",
            idcard = "370284199803310014";*/

    private Context mContext = Apply3Activity.this;
    private Bitmap handWritingBitmap = null;
    private String src = null;
    private String autoDate= null,recordDate= null;
    Bundle putBundle=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply3);
        //initTopBar();

        getBundle = getIntent().getExtras();


        initView();
        initBtn();

        //第一步，检测是否有证
        if (idcard != null) {
            showProgressDialog(mContext, "请稍后。。。");
            getNativeUserList(mContext, name, idcard, phone);
            //第二步，添加证书
        } else {
            Toast.makeText(mContext,"请检查上一步输入是否有误",Toast.LENGTH_SHORT).show();
            finish();
        }

//        initView();

        //从别的页面跳转回来不会调用onCreate，只会调用onRestart、onStart、onResume
    }

    private void initView() {
        tv_apply3_name=findViewById(R.id.tv_apply3_name);
        tv_apply3_name1=findViewById(R.id.tv_apply3_name1);
        tv_apply3_name2=findViewById(R.id.tv_apply3_name2);
        tv_apply3_file_name1=findViewById(R.id.tv_apply3_file_name1);
        tv_apply3_file_no=findViewById(R.id.tv_apply3_file_no);
        tv_apply3_money=findViewById(R.id.tv_apply3_money);
        tv_apply3_bank=findViewById(R.id.tv_apply3_bank);
        lv_apply3=findViewById(R.id.lv_apply3);
        lv_apply3_auto=findViewById(R.id.lv_apply3_auto);
        rv_apply3_auto=findViewById(R.id.rv_apply3_auto);
        iv_apply3_yes=findViewById(R.id.iv_apply3_yes);
        im_apply_auto=findViewById(R.id.im_apply_auto);
        tv_apply3_auto_date=findViewById(R.id.tv_apply3_auto_date);
        tv_apply3_reauto=findViewById(R.id.tv_apply3_reauto);
        iv_apply3_yes1=findViewById(R.id.iv_apply3_yes1);
        sign_image=findViewById(R.id.sign_image);
        sign_date=findViewById(R.id.sign_date);
        re_sign=findViewById(R.id.re_sign);
        lv_apply3_record=findViewById(R.id.lv_apply3_record);
        rv_apply3_record=findViewById(R.id.rv_apply3_record);
        im_apply_record=findViewById(R.id.im_apply_record);
        tv_apply3_re_record=findViewById(R.id.tv_apply3_re_record);
        tv_apply3_record_date=findViewById(R.id.tv_apply3_record_date);
        sbtn_apply3_next=findViewById(R.id.sbtn_apply3_next);
        //mTopBar=findViewById(R.id.topbar_apply3);


        if (getBundle.containsKey("name")){
            name=getBundle.getString("name");
            bank=getBundle.getString("bank");
            token=getBundle.getString("token");
            idcard=getBundle.getString("idcard");
            phone=getBundle.getString("cbno_phone");
            caseId=getBundle.getString("caseid");
            loanCode=getBundle.getString("loancode");
            loanName=getBundle.getString("loanname");
            loanMoney=getBundle.getString("loanmoney");
            autoDate=getBundle.getString("autodate");
            recordDate=getBundle.getString("recorddate");
        }


            //取录像信息
        if (getBundle.containsKey("imagepath")){
            src = getBundle.getString("base64str");
            imagePath = getBundle.getString("imagepath");
            videoPath = getBundle.getString("videopath");
        }




//        name = getIntent().getStringExtra("name");
//        bank = SpUtils.getInstance(this).getString("bank", null);
//        idcard = getIntent().getStringExtra("idcard");
//        cbno_phone = getIntent().getStringExtra("cbno_phone");

        tv_apply3_name.setText(name);
        tv_apply3_name1.setText(name);
        tv_apply3_name2.setText(name);
        tv_apply3_bank.setText(bank);
        tv_apply3_file_name1.setText(loanName);
        tv_apply3_file_no.setText(loanCode);
        tv_apply3_money.setText(loanMoney);


        if (imagePath != null) {
            getRecord();
        }
        if (src != null) {
            handWritingBitmap = base64ToBitmap(src);
            getAuto();
        }

    }

//    @SuppressLint("ResourceAsColor")
//    private void initTopBar() {
//        mTopBar.setBackgroundAlpha(255);
//        mTopBar.addLeftImageButton(R.drawable.cbno_back, R.id.topbar_right_change_button)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        finish();
//                    }
//                });
//        //设置标题名
//        mTopBar.setTitle("赋强公证申请");
//    }

    //按钮判断
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        //若二者都有即可进行下一步
        if (imagePath != null && src != null) {
            sbtn_apply3_next.setEnabled(true);
        } else {
            sbtn_apply3_next.setEnabled(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initBtn() {

        sbtn_apply3_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog(mContext, "加载中");
                re_sign.setVisibility(View.VISIBLE);
                setupPdf(lv_apply3);
                re_sign.setVisibility(View.INVISIBLE);
                //延时
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //要延时的程序
                        uploadPdf(mContext);
                    }
                }, 2000); //8000为毫秒单位
            }
        });
        lv_apply3_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoDate=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
                handWriting(mContext);
            }
        });
        //点击录像
        lv_apply3_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordDate=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
                jumpToRecord();
            }
        });

        tv_apply3_reauto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoDate=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
                reAuto();
            }
        });

        tv_apply3_re_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePath = getBundle.getString("imagepath");
                videoPath = getBundle.getString("videopath");
                deleteFile(new File(videoPath));
                deleteFile(new File(imagePath));
                recordDate=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
                reRecord();
            }
        });

        im_apply_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, CBNOVideoActivity.class);
                intent.putExtra("path",videoPath);
                startActivity(intent);
            }
        });
    }


    //如果正常录像调用此方法
    private void getRecord() {

        iv_apply3_yes1.setVisibility(View.VISIBLE);
        rv_apply3_record.setVisibility(View.VISIBLE);
        lv_apply3_record.setVisibility(View.INVISIBLE);
        tv_apply3_record_date.setText(recordDate);
        im_apply_record.setImageURI(getImageContentUri(mContext, new File(imagePath)));
    }

    //重新录像
    private void reRecord() {
        iv_apply3_yes1.setVisibility(View.INVISIBLE);
        rv_apply3_record.setVisibility(View.INVISIBLE);
        lv_apply3_record.setVisibility(View.VISIBLE);
        jumpToRecord();
    }

    private void jumpToRecord(){
        intent = new Intent(mContext, ShipingongzhenActivity.class);
        putBundle.putString("base64str",src);
        putBundle.putString("name",name);
        putBundle.putString("bank",bank);
        putBundle.putString("cbno_phone",phone);
        putBundle.putString("idcard",idcard);
        putBundle.putString("token",token);
        putBundle.putString("caseid",caseId);
        putBundle.putString("loancode", loanCode);
        putBundle.putString("loanname", loanName);
        putBundle.putString("loanmoney", loanMoney);
        putBundle.putString("autodate", autoDate);
        putBundle.putString("recorddate", recordDate);
        intent.putExtras(putBundle);
        startActivity(intent);
    }

    //手写签名
    public void handWriting(Context con) {
        SignetCoreApi.useCoreFunc(
                new SetSignImageCallBack(con, msspId, SetSignImgType.SET_HANDWRITING) {

                    @Override
                    public void onSetSignImageResult(SignImageResult setSignImageResult) {
                        src = setSignImageResult.getSignImageSrc();
//                        Log.d("cbno_shouxie", src);
//                        Log.d("手写", setSignImageResult.getErrMsg());
//                        Log.d("手写", setSignImageResult.getErrCode());
                        handWritingBitmap = base64ToBitmap(src);
                        if (setSignImageResult.getErrCode() != "0x11000001") {
                            getAuto();
                        }
                    }

                });
    }

    private void getAuto() {


        tv_apply3_auto_date.setText(autoDate);
        iv_apply3_yes.setVisibility(View.VISIBLE);
        rv_apply3_auto.setVisibility(View.VISIBLE);
        lv_apply3_auto.setVisibility(View.INVISIBLE);
        im_apply_auto.setImageBitmap(handWritingBitmap);
        //设置签名
//        sign_image.setImageBitmap(handWritingBitmap);
        //暂时这么写
        //生成pdf的时间
        //sign_date.setText(date);
    }

    private void reAuto() {
        iv_apply3_yes.setVisibility(View.INVISIBLE);
        rv_apply3_auto.setVisibility(View.INVISIBLE);
        lv_apply3_auto.setVisibility(View.VISIBLE);
        handWriting(mContext);
    }


    //    public QMUITipDialog getTipDialog(int type, String str) {
//        tipDialog = new QMUITipDialog.Builder(mContext)
//                .setIconType(type)
//                .setTipWord(str)
//                .create();
//        return tipDialog;
//    }
//
//    //1.5s后关闭tipDIalog
//    public void delayCloseTip() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //要延时的程序
//                if (tipDialog.isShowing()) {
//                    tipDialog.dismiss();
//                }
//            }
//        }, 1500); //8000为毫秒单位
//
//
//
////        Timer timer = new Timer();
////        timer.schedule(new TimerTask() {
////            @Override
////            public void run() {
////                //要延时的程序
////                if (tipDialog.isShowing()){
////                    tipDialog.dismiss();
////                }
////
////            }
////        },1500);
//    }
    //获取摄像头权限
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(MainActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}