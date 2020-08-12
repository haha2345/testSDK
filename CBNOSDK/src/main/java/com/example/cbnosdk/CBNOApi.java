package com.example.cbnosdk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.cbnosdk.Activity.Apply3Activity;
import com.example.cbnosdk.base.BaseActivity;
import com.example.cbnosdk.constant.netConstant;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.util.JsonMap;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.org.bjca.identifycore.callback.IdentifyCallBack;
import cn.org.bjca.identifycore.enums.CtidActionType;
import cn.org.bjca.identifycore.enums.CtidModelEnum;
import cn.org.bjca.identifycore.impl.BJCAIdentifyAPI;
import cn.org.bjca.identifycore.params.BJCAAuthModel;
import cn.org.bjca.identifycore.params.CtidReturnParams;

import static android.content.ContentValues.TAG;

public class CBNOApi extends BaseActivity {

    private ProgressDialog progressDialog;
    private static CtidReturnParams ctidReturnParams;
    private static String keyId = "5ff73aa181364844b29a0bddab5ff8c6"
            , phoneNum
            , authTime
            , SecretKey = "c9560b00-4e43-49dc-852c-341084deeb4c"
            , authinfo
            , token
            , caseId
            , caseJson;//= "13205401086"
    private static Intent intent;
//    private static String coid = "2"
//            , loanCode = "12412"
//            , loanName = "wih"
//            , loanUserName = "fasf"
//            , loanUserMobile = phoneNum
//            , loanUserIdnum = "123456789123456789"
//            , loanMoney = "1111"
//            , loanRatio = "123"
//            , loanFromTo = "xzv"
//            , loanTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


    private static String coid
            , loanCode
            , loanName
            , loanUserName
            , loanUserMobile = phoneNum
            , loanUserIdnum
            , loanMoney
            , loanRatio
            , loanFromTo
            , loanTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    private static String bank;

    private static String name, idcard;

    public static void beginApiService(Context con,CBNOEntity entity) {
        phoneNum=entity.getPhoneNum();
        coid=entity.getCoId();
        loanCode=entity.getLoanCode();
        loanName=entity.getLoanName();
        loanUserName=entity.getLoanUserName();
        loanUserIdnum=entity.getLoanUserIdnum();
        loanMoney=entity.getLoanMoney();
        loanRatio=entity.getLoanRatio();
        loanFromTo=entity.getLoanFromTo();
        loanUserMobile=phoneNum;

        authTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        authinfo = getSHA256(keyId + phoneNum + authTime + SecretKey);
        showProgressDialog(con, "请稍等");
        getToken(con,entity);


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);//超时时间60秒


    }


    //获得token
    private static void getToken(Context mContext,CBNOEntity entity) {
       /* Map<String, Object> reqMap = new HashMap();
        reqMap.put("keyId", keyId);
        reqMap.put("phoneNum", phoneNum);
        reqMap.put("authTime", authTime);
        reqMap.put("authInfo", authinfo);
        String reqJson = JSON.toJSONString(reqMap);*/
//        String jsonStr = "{\n" +
//                "    \"keyId\": \""+keyId+"\",\n" +
//                "    \"phoneNum\": \""+phoneNum+"\",\n" +
//                "    \"authTime\": \""+authTime+"\",\n" +
//                "    \"authInfo\": \""+authinfo+"\"\n" +
//                "}";

        phoneNum=entity.getPhoneNum();
        String jsonStr = "{\n" +
                "    \"keyId\": \"" + keyId + "\",\n" +
                "    \"phoneNum\": \"" + phoneNum + "\",\n" +
                "    \"authTime\": \"" + authTime + "\",\n" +
                "    \"authInfo\": \"" + authinfo + "\"\n" +
                "}";

        HttpRequest.build(mContext, netConstant.getAuthLogin())
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap jsonMap, Exception e) {
                        Log.d(TAG, "onResponse: " + jsonMap.toString());
                        if (jsonMap.getString("code").equals("200")) {
                            token = jsonMap.getString("token");
                            Log.d("token获取成功", "onResponse: " + authinfo);
                            Log.d("token",  token);
                            addContract(mContext,entity);
//                            Toast.makeText(mContext, token, Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, "开始请求", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, jsonMap.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .doPost();
    }


    //添加合同信息，取得caseid
    private static void addContract(Context mContext,CBNOEntity entity) {
//        Map<String, Object> reqMap = new HashMap();
//        reqMap.put("coId", coid);
//        reqMap.put("loanCode", loanCode);
//        reqMap.put("loanName", loanName);
//        reqMap.put("loanUserName", loanUserName);
//        reqMap.put("loanUserMobile", loanUserMobile);
//        reqMap.put("loanUserIdnum", loanUserIdnum);
//        reqMap.put("loanMoney", loanMoney);
//        reqMap.put("loanRatio", loanRatio);
//        reqMap.put("loanFromTo", loanFromTo);
//        reqMap.put("loanTime", loanTime);
//        String reqJson = JSON.toJSONString(reqMap);
        phoneNum=entity.getPhoneNum();
        coid=entity.getCoId();
        loanCode=entity.getLoanCode();
        loanName=entity.getLoanName();
        loanUserName=entity.getLoanUserName();
        loanUserIdnum=entity.getLoanUserIdnum();
        loanMoney=entity.getLoanMoney();
        loanRatio=entity.getLoanRatio();
        loanFromTo=entity.getLoanFromTo();
        loanUserMobile=phoneNum;

        String jsonStr="{\n" +
                "    \"coId\": \""+coid+"\",\n" +
                "    \"loanCode\": \""+loanCode+"\",\n" +
                "    \"loanName\": \""+loanName+"\",\n" +
                "    \"loanUserName\": \""+loanUserName+"\",\n" +
                "    \"loanUserMobile\": \""+loanUserMobile+"\",\n" +
                "    \"loanUserIdnum\": \""+loanUserIdnum+"\",\n" +
                "    \"loanMoney\": \""+loanMoney+"\",\n" +
                "    \"loanRatio\": \""+loanRatio+"\",\n" +
                "    \"loanFromTo\": \""+loanFromTo+"\",\n" +
                "    \"loanTime\": \""+loanTime+"\"\n" +
                "}";

        HttpRequest.build(mContext, netConstant.getAddContract())
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap jsonMap, Exception e) {
                        if (jsonMap.getString("code").equals("200")) {
                            //获取case信息，可能之后要用
                            caseJson = jsonMap.getJsonMap("data").toString();
                            caseId = jsonMap.getJsonMap("data").getString("id");
                            bank=jsonMap.getJsonMap("data").getString("coName");
                            Log.d(TAG, "onResponse: "+bank);
//                            Toast.makeText(mContext, caseId, Toast.LENGTH_SHORT).show();
                            Log.d("caseid", caseId);
//                            dismissProgressDialog();
                            test2nd(mContext);
                        } else {
                            Toast.makeText(mContext, jsonMap.getString("msg"), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: 失败"+jsonMap.getString("msg")+jsonMap.getString("code"));
                        }

                    }
                })
                .doPost();

    }

    //初始化 获取authinfo
    private static String test2nd(Context mContext) {
        ctidReturnParams = BJCAIdentifyAPI.initialCtidIdentify(mContext, CtidModelEnum.MODEL_0X12, CtidActionType.AUTH_ACTION);
        authinfo = ctidReturnParams.getValue();
        Log.d("status", ctidReturnParams.getStatus());
        Log.d("msg", ctidReturnParams.getMessage());
        Log.d("auth", authinfo);
        postAuthinfo(authinfo,mContext);
        return authinfo;
    }


    //提交authorinfo
    private static void postAuthinfo(final String authinfo, final Context mContext) {
        HttpRequest.build(mContext, netConstant.getApplyURL())
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter("{\"authInfo\":\"" + authinfo + "\"}")
                .setJsonResponseListener(new JsonResponseListener() {

                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        if (main.getString("code").equals("200")) {
                            final JsonMap result = main.getJsonMap("data");
                            String str = result.getString("authResultInfo");
                            Log.d("没错", result.getString(str));
                            dismissProgressDialog();
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
                                                model0x12(mContext, val);
                                            } else {
                                                Toast.makeText(mContext, "操作有误，请重新操作", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        //在吊起之前执行加载
                                        @Override
                                        public void onPreExecute() {
                                            showProgressDialog(mContext, "请稍后");
                                        }
                                    });
                            //testIdentify(str);

                        } else if (main.getString("code").equals("401")) {
                            breaker(mContext);
                        } else {

                            dismissProgressDialog();
//                                getTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL, );
//                                delayCloseTip();
                            Toast.makeText(mContext, main.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .doPost();

    }

    //四项实人接口
    //传递身份证号，人名，手机号
    private static void model0x12(Context mContext, String value) {

//        String jsonStr="{\n" +
//                "    \"caseId\":\""+caseId+"\",\n" +
//                "    \"authInfo\":\""+value+"\",\n" +
//                "    \"personMobile\":\""+username+"\"\n" +
//                "}";
        //test
        String jsonStr = "{\n" +
                "    \"caseId\":\"" + caseId + "\",\n" +
                "    \"authInfo\":\"" + value + "\",\n" +
                "    \"personMobile\":\"" + phoneNum + "\"\n" +
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
                                //SpUtils.getInstance(mContext).setString("name", name);
                                //SpUtils.getInstance(mContext).setString("idcard", idcard);

                                dismissProgressDialog();
                                //认证成功直接跳转
                                jumpToApply3(mContext);
                            } else if (main.getString("code").equals("401")) {
                                breaker(mContext);
                            } else {
                                dismissProgressDialog();
                                final String msg = main.getString("msg");
//                                getTipDialog(3, msg).show();
//                                delayCloseTip();

                            }
                        } else {

                            dismissProgressDialog();
//                            getTipDialog(3, "连接失败").show();
//                            delayCloseTip();
                        }
                    }

                })
                .doPost();
    }

    //跳转到第3步
    private static void jumpToApply3(Context mContext){
        intent = new Intent(mContext, Apply3Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("idcard", idcard);
        bundle.putString("token", token);
        bundle.putString("cbno_phone", phoneNum);
        bundle.putString("bank", bank);
        bundle.putString("caseid", caseId);
        bundle.putString("loancode", loanCode);
        bundle.putString("loanname", loanName);
        bundle.putString("loanmoney", "¥ "+loanMoney);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    //获得sha256
    private static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    //上一步用到的方法
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
