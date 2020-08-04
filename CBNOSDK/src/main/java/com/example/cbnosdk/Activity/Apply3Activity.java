package com.example.cbnosdk.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cbnosdk.R;
import com.example.cbnosdk.utiles.SpUtils;
import com.example.cbnosdk.base.BaseApply3Activity;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.org.bjca.signet.component.core.activity.SignetCoreApi;
import cn.org.bjca.signet.component.core.bean.results.SignImageResult;
import cn.org.bjca.signet.component.core.callback.SetSignImageCallBack;
import cn.org.bjca.signet.component.core.enums.SetSignImgType;

public class Apply3Activity extends BaseApply3Activity {

    private QMUITipDialog tipDialog;



    TextView tv_apply3_name;
    TextView tv_apply3_name1;
    TextView tv_apply3_name2;
    TextView tv_apply3_bank;
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
    TextView sign_date;
    RelativeLayout re_sign;

    LinearLayout lv_apply3_record;
    //这是录像成功后的界面
    RelativeLayout rv_apply3_record;
    ImageView im_apply_record;
    TextView tv_apply3_re_record;
    TextView tv_apply3_record_date;
    //初始化按钮
    Button sbtn_apply3_next;

    QMUITopBarLayout mTopBar;


    private Intent intent;
    private String name = null,
            bank = null,
            videoPath = null,
            imagePath = null,
            phone = null,
            idcard = null;
    private Context mContext = Apply3Activity.this;
    private Bitmap handWritingBitmap = null;
    private String src = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply3);
        //initTopBar();
        initView();
        initBtn();
        //第一步，检测是否有证
        if (idcard != null) {
            showProgressDialog(mContext, "请稍后。。。");
            getNativeUserList(mContext, name, idcard, phone);
            //第二步，添加证书
        } else {
            getTipDialog(3, "请检查上一步是否有问题");
            delayCloseTip();
            finish();
        }

//        initView();

        //从别的页面跳转回来不会调用onCreate，只会调用onRestart、onStart、onResume
    }

    private void initView() {
        tv_apply3_name=findViewById(R.id.tv_apply3_name);
        tv_apply3_name1=findViewById(R.id.tv_apply3_name1);
        tv_apply3_name2=findViewById(R.id.tv_apply3_name2);
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
        mTopBar=findViewById(R.id.topbar_apply3);



        name = getIntent().getStringExtra("name");
        bank = SpUtils.getInstance(this).getString("bank", null);
        idcard = getIntent().getStringExtra("idcard");
        phone = getIntent().getStringExtra("phone");

        tv_apply3_name.setText(name);
        tv_apply3_name1.setText(name);
        tv_apply3_name2.setText(name);
        tv_apply3_bank.setText(bank);
        //取录像信息
        src = getIntent().getStringExtra("base64str");
        imagePath = getIntent().getStringExtra("imagepath");
        videoPath = getIntent().getStringExtra("videopath");
        if (imagePath != null) {
            getRecord();
        }
        if (src != null) {
            handWritingBitmap = base64ToBitmap(src);
            getAuto();
        }

    }

    @SuppressLint("ResourceAsColor")
    private void initTopBar() {
        mTopBar.setBackgroundAlpha(255);
        mTopBar.addLeftImageButton(R.drawable.back, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        //设置标题名
        mTopBar.setTitle("赋强公证申请");
    }

    //按钮判断
    @Override
    protected void onResume() {
        super.onResume();
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
                handWriting(mContext);
            }
        });
        //点击录像
        lv_apply3_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, ShipingongzhenActivity.class);
                intent.putExtra("basestr", src);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("idcard", idcard);
                startActivity(intent);
            }
        });

        tv_apply3_reauto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reAuto();
            }
        });

        tv_apply3_re_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPath=SpUtils.getInstance(mContext).getString("videopath",null);
                imagePath=SpUtils.getInstance(mContext).getString("imagepath",null);
                File video=new File(videoPath);
                File image=new File(imagePath);
                deleteFile(video);
                deleteFile(image);
                reRecord();
            }
        });

        im_apply_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, VideoActivity.class);
                startActivity(intent);
            }
        });
    }


    //如果正常录像调用此方法
    private void getRecord() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());

        iv_apply3_yes1.setVisibility(View.VISIBLE);
        rv_apply3_record.setVisibility(View.VISIBLE);
        lv_apply3_record.setVisibility(View.INVISIBLE);
        tv_apply3_record_date.setText(simpleDateFormat.format(date));
        im_apply_record.setImageURI(getImageContentUri(mContext, new File(imagePath)));
    }

    //重新录像
    private void reRecord() {
        iv_apply3_yes1.setVisibility(View.INVISIBLE);
        rv_apply3_record.setVisibility(View.INVISIBLE);
        lv_apply3_record.setVisibility(View.VISIBLE);
        intent = new Intent(mContext, ShipingongzhenActivity.class);
        intent.putExtra("basestr", src);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("idcard", idcard);
        startActivity(intent);
    }

    //手写签名
    public void handWriting(Context con) {
        SignetCoreApi.useCoreFunc(
                new SetSignImageCallBack(con, msspId, SetSignImgType.SET_HANDWRITING) {

                    @Override
                    public void onSetSignImageResult(SignImageResult setSignImageResult) {
                        src = setSignImageResult.getSignImageSrc();
//                        Log.d("shouxie", src);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        tv_apply3_auto_date.setText(simpleDateFormat.format(date));
        iv_apply3_yes.setVisibility(View.VISIBLE);
        rv_apply3_auto.setVisibility(View.VISIBLE);
        lv_apply3_auto.setVisibility(View.INVISIBLE);
        im_apply_auto.setImageBitmap(handWritingBitmap);
        //设置签名
//        sign_image.setImageBitmap(handWritingBitmap);
        //暂时这么写
        sign_date.setText(simpleDateFormat.format(date));
    }

    private void reAuto() {
        iv_apply3_yes.setVisibility(View.INVISIBLE);
        rv_apply3_auto.setVisibility(View.INVISIBLE);
        lv_apply3_auto.setVisibility(View.VISIBLE);
        handWriting(mContext);
    }

    public QMUITipDialog getTipDialog(int type, String str) {
        tipDialog = new QMUITipDialog.Builder(mContext)
                .setIconType(type)
                .setTipWord(str)
                .create();
        return tipDialog;
    }

    //1.5s后关闭tipDIalog
    public void delayCloseTip() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //要延时的程序
                if (tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }
            }
        }, 1500); //8000为毫秒单位
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //要延时的程序
//                if (tipDialog.isShowing()){
//                    tipDialog.dismiss();
//                }
//
//            }
//        },1500);
    }
}