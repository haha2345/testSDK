package com.example.cbnosdk.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cbnosdk.R;
import com.example.cbnosdk.utiles.SpUtils;
import com.example.cbnosdk.utiles.Utils;
import com.example.cbnosdk.base.BaseActivity;
import com.example.cbnosdk.constant.netConstant;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.util.JsonMap;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cn.org.bjca.identifycore.callback.IdentifyCallBack;
import cn.org.bjca.identifycore.enums.CtidActionType;
import cn.org.bjca.identifycore.enums.CtidModelEnum;
import cn.org.bjca.identifycore.impl.BJCAIdentifyAPI;
import cn.org.bjca.identifycore.params.BJCAAuthModel;
import cn.org.bjca.identifycore.params.CtidReturnParams;

public class Apply2Activity extends BaseActivity {

    EditText et_apply2_phone;
    EditText et_apply2_vcode;
    TextView tv_apply2_vcode;
    Button sbtn_apply2_verify;
    QMUITopBarLayout mTopBar;
    //判断是否成功
    private int flag=0;

    String caseId, userId, username, vcode, uuid, token, name, idcard;
    String TAG = "Apply2";
    //TEST
    private CtidReturnParams ctidReturnParams;
    private String authinfo;
    MyCountDownTimer2 myCountDownTimer;
    Utils utils = new Utils();
    Intent intent;
    Context mContext = Apply2Activity.this;
    private QMUITipDialog tipDialog;
    private QMUITipDialog qmuiTipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply2);

        initViews();
        myCountDownTimer = new MyCountDownTimer2(60000, 1000);
        qmuiTipDialog = new QMUITipDialog.Builder(mContext)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后")
                .create();
        initData();
        //initTopBar();
        initBtns();
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

    private void initViews(){
        et_apply2_phone=findViewById(R.id.et_apply2_phone);
        et_apply2_vcode=findViewById(R.id.et_apply2_vcode);
        tv_apply2_vcode=findViewById(R.id.tv_apply2_vcode);
        sbtn_apply2_verify=findViewById(R.id.sbtn_apply2_verify);
        mTopBar=findViewById(R.id.topbar_apply2);
    }

    private void initData() {
        caseId = SpUtils.getInstance(this).getString("caseId", null);
        token = SpUtils.getInstance(this).getString("token", null);
        token="eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleToiOiJkNmY1ODdlOC00MmMwLTQ5YTQtYjI3NC01NGE0YWYwM2M5ZGMifQ.J0-Kz4zcG5q08RA9i3VU8fFkNqI7iShr1TJyaf4Ltfa7GekNwgjj1B1CrCvBcpYsrLeDaN4SktnkqzTBQtcvtw";
    }

    private void initBtns() {
        username = et_apply2_phone.getText().toString();
        vcode = et_apply2_vcode.getText().toString();
        sbtn_apply2_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_apply2_phone.getText().toString();
                vcode = et_apply2_vcode.getText().toString();

                if (isTelphoneValid(username)){
                    if (vcode.length()==6){
                        showProgressDialog(mContext, "请稍后");
                        sbtn_apply2_verify.setEnabled(false);
                        checkVcode();
                    }else {
                        getTipDialog(3,"请检查验证码是否输入正确").show();
                        delayCloseTip();
                        //Toast.makeText(mContext,"请检查验证码是否输入正确",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    getTipDialog(3,"请检查手机号是否输入正确").show();
                    delayCloseTip();
                }
            }
        });


        tv_apply2_vcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_apply2_phone.getText().toString();
                if (username.isEmpty()) {
                    getTipDialog(QMUITipDialog.Builder.ICON_TYPE_INFO, "请输入用户名").show();
                    delayCloseTip();
                } else {
                    if (isTelphoneValid(username)) {
                        //测试qmui的提示框
                        //获取uuid和用户名
                        sbtn_apply2_verify.setEnabled(true);
                        qmuiTipDialog.show();
                        getUuid();
                        myCountDownTimer.start();
                        sbtn_apply2_verify.setEnabled(true);
                    } else {
                        getTipDialog(QMUITipDialog.Builder.ICON_TYPE_INFO, "请输入正确的手机号").show();
                        delayCloseTip();
                    }
                }

            }
        });
    }

    private void getUuid() {
        HttpRequest.GET(mContext, netConstant.getGetVcodeURL() + "?mobile=" + username, null, new JsonResponseListener() {
            @Override
            public void onResponse(JsonMap main, Exception error) {
                if (error == null) {
                    qmuiTipDialog.dismiss();
                    //先判断是否正常
                    if (main.getString("code").equals("200")) {
                        uuid = main.getString("uuid");
                        //
                        // utils.showToastInThread(mContext, "已发送验证码，注意查收" + uuid);
                    } else if (main.getString("code").equals("401")){
                        breaker(mContext);
                    }
                    else {
                        myCountDownTimer.onFinish();
                        utils.showToastInThread(mContext, "错误");
                    }
                } else {
                    qmuiTipDialog.dismiss();
                    Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkVcode() {
        HttpRequest.build(mContext, netConstant.getCheckSMSCodeURL())
                .addParameter("username", username)
                .addParameter("code", vcode)
                .addParameter("uuid", uuid)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {

                        if (error == null) {
                            Log.d(TAG, main.toString());
                            if (main.getString("code").equals("200")) {
                                Log.d(TAG, "核验正确");
                                dismissProgressDialog();
                                //showProgressDialog(mContext, "请稍后");
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        //要延时的程序
                                        postAuthinfo(test2nd());
                                    }
                                }, 1500);

                            }else if (main.getString("code").equals("401")){
                                breaker(mContext);
                            } else {
                                et_apply2_vcode.setText("");
                                myCountDownTimer.cancel();
                                myCountDownTimer.onFinish();
                                dismissProgressDialog();
                                Log.e(TAG, main.getString("msg"));
                                Toast.makeText(mContext, main.getString("msg"), Toast.LENGTH_SHORT).show();
                                getTipDialog(3, main.getString("msg")).show();
                                myCountDownTimer.onFinish();
                                delayCloseTip();
                                sbtn_apply2_verify.setEnabled(true);

                            }
                        } else {
                            et_apply2_vcode.setText("");
                            myCountDownTimer.cancel();
                            myCountDownTimer.onFinish();
                            dismissProgressDialog();
                            getTipDialog(3, "连接失败").show();
                            delayCloseTip();
                            sbtn_apply2_verify.setEnabled(true);
                        }
                    }
                })
                .doGet();
    }

    //初始化 获取authinfo
    private String test2nd() {
        ctidReturnParams = BJCAIdentifyAPI.initialCtidIdentify(mContext, CtidModelEnum.MODEL_0X12, CtidActionType.AUTH_ACTION);
        authinfo = ctidReturnParams.getValue();
        Log.d("status", ctidReturnParams.getStatus());
        Log.d("msg", ctidReturnParams.getMessage());
        Log.d("auth", authinfo);
        return authinfo;
    }

    //提交authorinfo
    private void postAuthinfo(final String authinfo) {
        HttpRequest.build(mContext, netConstant.getApplyURL())
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter("{\"authInfo\":\"" + authinfo + "\"}")
                .setJsonResponseListener(new JsonResponseListener() {

                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        if (error == null) {
                            if (main.getString("code").equals("200")) {
                                final JsonMap result = main.getJsonMap("data");
                                String str=result.getString("authResultInfo");
                                Log.d("没错", result.getString(str));
                                //dismissProgressDialog();
                                BJCAIdentifyAPI.actionCtidIdentify(mContext, str, CtidModelEnum.MODEL_0X12, CtidActionType.AUTH_ACTION
                                        , new BJCAAuthModel(), true, new IdentifyCallBack(mContext) {
                                            @Override
                                            public void onIdentifyCallBack(CtidReturnParams ctidReturnParams) {


                                                if (ctidReturnParams.getStatus().equals("0x0000")) {
                                                    final String val = ctidReturnParams.getValue();
                                                    Log.d("status", ctidReturnParams.getStatus());
                                                    Log.d("msg", ctidReturnParams.getMessage());
                                                    Log.d("value", val);
                                                    showProgressDialog(mContext, "请稍后");
                                                    //要延时的程序
                                                    model0x12(val);


                                                } else {
                                                    et_apply2_vcode.setText("");
                                                    myCountDownTimer.cancel();
                                                    myCountDownTimer.onFinish();
                                                    getTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL, ctidReturnParams.getMessage()).show();
                                                    delayCloseTip();
                                                    sbtn_apply2_verify.setEnabled(true);
                                                }

                                            }

                                            //在吊起之前执行加载
                                            @Override
                                            public void onPreExecute() {
                                                showProgressDialog(mContext,"请稍后");
                                            }
                                        });
                                //testIdentify(str);

                            }else if (main.getString("code").equals("401")){
                                breaker(mContext);
                            } else {
                                et_apply2_vcode.setText("");
                                myCountDownTimer.cancel();
                                myCountDownTimer.onFinish();
                                dismissProgressDialog();
                                getTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL, main.getString("msg"));
                                delayCloseTip();
                                sbtn_apply2_verify.setEnabled(true);
                            }

                        } else {
                            et_apply2_vcode.setText("");
                            myCountDownTimer.cancel();
                            myCountDownTimer.onFinish();
                            dismissProgressDialog();
                            getTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL, "连接失败");
                            delayCloseTip();
                            sbtn_apply2_verify.setEnabled(true);
                        }
                    }
                })
                .doPost();

    }

    //测试识人接口
    private void testIdentify(final String value) {
        BJCAIdentifyAPI.actionCtidIdentify(mContext, value, CtidModelEnum.MODEL_0X12, CtidActionType.AUTH_ACTION
                , new BJCAAuthModel(), true, new IdentifyCallBack(mContext) {
                    @Override
                    public void onIdentifyCallBack(CtidReturnParams ctidReturnParams) {


                        if (ctidReturnParams.getStatus().equals("0x0000")) {
                            final String val = ctidReturnParams.getValue();
                            Log.d("status", ctidReturnParams.getStatus());
                            Log.d("msg", ctidReturnParams.getMessage());
                            Log.d("value", val);
                            showProgressDialog(mContext, "请稍后");
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    //要延时的程序
                                    model0x12(val);
                                }
                            }, 1500);
                            //传值

                        } else {
                            et_apply2_vcode.setText("");
                            myCountDownTimer.cancel();
                            myCountDownTimer.onFinish();
                            getTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL, ctidReturnParams.getMessage()).show();
                            delayCloseTip();
                            sbtn_apply2_verify.setEnabled(true);
                        }

                    }

                    @Override
                    public void onPreExecute() {

                    }
                });
    }

    //四项实人接口
    private void model0x12(String value) {
        caseId = SpUtils.getInstance(this).getString("caseId", null);

        username=et_apply2_phone.getText().toString();
//        String jsonStr="{\n" +
//                "    \"caseId\":\""+caseId+"\",\n" +
//                "    \"authInfo\":\""+value+"\",\n" +
//                "    \"personMobile\":\""+username+"\"\n" +
//                "}";
        //test
        String jsonStr = "{\n" +
                "    \"caseId\":\"" + caseId + "\",\n" +
                "    \"authInfo\":\"" + value + "\",\n" +
                "    \"personMobile\":\"" + username + "\"\n" +
                "}";
        HttpRequest.build(mContext, netConstant.getModel0x12URL())
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        if (error == null) {

                            Log.d("没错", main.toString());
                            if (main.getString("code").equals("200")) {
                                JsonMap result = main.getJsonMap("data");
                                Log.d(TAG, result.getString("name"));
                                //姓名
                                name = result.getString("name");
                                idcard = result.getString("idNumber");
                                //储存名字
                                SpUtils.getInstance(mContext).setString("name", name);
                                SpUtils.getInstance(mContext).setString("idcard", idcard);

                                dismissProgressDialog();
                                //认证成功直接跳转
                                intent = new Intent(mContext, Apply3Activity.class);
                                intent.putExtra("idcard", idcard);
                                intent.putExtra("name", name);
                                intent.putExtra("phone", username);
                                startActivity(intent);
                                finish();
                            } else if (main.getString("code").equals("401")){
                                breaker(mContext);
                            }else {
                                et_apply2_vcode.setText("");
                                myCountDownTimer.cancel();
                                myCountDownTimer.onFinish();
                                dismissProgressDialog();
                                final String msg = main.getString("msg");
                                getTipDialog(3, msg).show();
                                delayCloseTip();
                                sbtn_apply2_verify.setEnabled(true);
                            }
                        } else {
                            et_apply2_vcode.setText("");
                            myCountDownTimer.cancel();
                            myCountDownTimer.onFinish();
                            dismissProgressDialog();
                            getTipDialog(3, "连接失败").show();
                            delayCloseTip();
                            sbtn_apply2_verify.setEnabled(true);
                        }
                    }

                })
                .doPost();

    }

    // 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
    private boolean isTelphoneValid(String account) {
        if (account == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }

    //倒计时函数
    private class MyCountDownTimer2 extends CountDownTimer {

        public MyCountDownTimer2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tv_apply2_vcode.setClickable(false);
            tv_apply2_vcode.setText(l / 1000 + "秒后可再发送");
            if(flag==1){

            }

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            tv_apply2_vcode.setText("重新获取");
            //设置可点击
            tv_apply2_vcode.setClickable(true);
        }
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //要延时的程序
                if (tipDialog.isShowing()){
                    tipDialog.dismiss();

                }
            }
        }, 1500);
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setMessage("实人认证成功，点击确定进行下一步")
                .setSkinManager(QMUISkinManager.defaultInstance(mContext))
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
//成功跳转
                        intent = new Intent(mContext, Apply3Activity.class);
                        intent.putExtra("idcard", idcard);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", username);
                        startActivity(intent);
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }
}