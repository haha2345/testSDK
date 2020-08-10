package com.example.testsdk;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.cbnosdk.constant.netConstant;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.util.JsonMap;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CBNO {


    private String keyId="5ff73aa181364844b29a0bddab5ff8c6"
            ,phoneNum="13205401086"
            ,authTime
            ,SecretKey="c9560b00-4e43-49dc-852c-341084deeb4c"
            ,authinfo
            ,token;

    private String TAG="aa";

    public void start(Context con){
        authTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        authinfo=getSHA256(keyId+phoneNum+authTime+SecretKey);
        getToken(con);

        Log.d(TAG, "start: "+keyId+phoneNum+authTime+SecretKey);
        Log.d(TAG, "start: "+authinfo);
    }



    private void getToken(final Context mContext){

        String jsonStr = "{\n" +
                "    \"keyId\": \""+keyId+"\",\n" +
                "    \"phoneNum\": \""+phoneNum+"\",\n" +
                "    \"authTime\": \""+authTime+"\",\n" +
                "    \"authInfo\": \""+authinfo+"\"\n" +
                "}";

        HttpRequest.build(mContext, "http://i305k85088.qicp.vip:36422/notarize/mobile/authLogin")
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap jsonMap, Exception e) {
                        Log.d(TAG, "onResponse: "+jsonMap.toString());
                        if (e!=null){
                            if (jsonMap.getString("code").equals("200")){
                                token=jsonMap.getString("token");
                                Log.d("token获取成功", "onResponse: "+authinfo);
                                Log.d("token获取成功", "onResponse: "+token);
                                Toast.makeText(mContext,token,Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(mContext,jsonMap.getString("msg"),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.e(TAG, "onResponse: ",e );
                            Toast.makeText(mContext,jsonMap.getString("msg"),Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .doPost();
    }

    //获得sha256
    private String getSHA256(String str){
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
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}