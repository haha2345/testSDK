package com.example.cbnosdk.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cbnosdk.Activity.Apply2Activity;
import com.example.cbnosdk.constant.netConstant;
import com.example.cbnosdk.utiles.SpUtils;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.JsonResponseListener;
import com.kongzue.baseokhttp.util.JsonMap;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.org.bjca.signet.component.core.activity.SignetCoreApi;
import cn.org.bjca.signet.component.core.activity.SignetToolApi;
import cn.org.bjca.signet.component.core.bean.results.FindBackUserResult;
import cn.org.bjca.signet.component.core.bean.results.GetUserListResult;
import cn.org.bjca.signet.component.core.bean.results.RegisterResult;
import cn.org.bjca.signet.component.core.bean.results.SignDataResult;
import cn.org.bjca.signet.component.core.callback.FindBackUserCallBack;
import cn.org.bjca.signet.component.core.callback.RegisterCallBack;
import cn.org.bjca.signet.component.core.callback.SignDataCallBack;
import cn.org.bjca.signet.component.core.enums.IdCardType;
import cn.org.bjca.signet.component.core.enums.RegisterType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.cbnosdk.utiles.DataCleanManager.clear;


public class BaseApply3Activity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private PdfDocument doc;
    private PdfDocument.PageInfo pageInfo;
    private PdfDocument.Page page;
    public String msspId, activeCode, signId, fileName, fileSize, filePath, fileHashSha1, statusInfo;
    private String token, caseId, userId;
    private File uploadFile;
    private String caseCode, date, bank;
    public QMUITipDialog tipDialog;
    //加载框
    private ProgressDialog progressDialog;

    //拦截器
    public void breaker(Context mContext){
        clear(mContext);
        Intent intent=new Intent(mContext, Apply2Activity.class);
        //调到页面，关闭之前所有页面
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    //生成pdf 内容+签字
    public void setupPdf(LinearLayout lv_apply3) {
        doc = new PdfDocument();
        pageInfo = new PdfDocument.PageInfo.Builder((int) (lv_apply3.getWidth()*0.35f), (int) (lv_apply3.getHeight()*0.35f), 1)
                .create();
        page = doc.startPage(pageInfo);
        Canvas canvas=page.getCanvas();
        canvas.scale(0.35f,0.35f);
        lv_apply3.draw(canvas);
        doc.finishPage(page);

//        //签字
//        //往pdf添加组件
//        pageInfo = new PdfDocument.PageInfo.Builder(layout.getWidth()/2, layout.getHeight()/2, 1).create();
//        page = doc.startPage(pageInfo);
//        canvas=page.getCanvas();
//        canvas.scale(0.5f,0.5f);
//        layout.draw(canvas);
//        doc.finishPage(page);
        //设置路径
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try {
            String pdfPath=file.getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "ad.pdf";
            SpUtils.getInstance(getParent()).setString("pdfpath",pdfPath);
            uploadFile = new File(pdfPath);
            doc.writeTo(new FileOutputStream(uploadFile));
            //应该弹一个对话框
            Log.d("生成pdf", "成功");

        } catch (IOException e) {
            Log.d("生成pdf", "失败");
            e.printStackTrace();
        }
        doc.close();
    }

    //路径转uri
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
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

    //本地查找是否有证(13.获取本地用户列表）
    public void getNativeUserList(Context con, String name, String idcard, String phone) {//name为上一页面传进来的姓名
        GetUserListResult listResult = SignetToolApi.getUserList(getBaseContext());
        Log.d("证件数", listResult.getUserListMap().size() + "");
        if (listResult.getUserListMap().containsValue(name)) {
            Log.d("本地用户", listResult.getUserListMap().toString());
            msspId = getKey(listResult.getUserListMap(), name);
            Log.d("获得mssid", msspId);
            dismissProgressDialog();
            //如果有证，直接签名
        } else {
            //本地没证，api获取状态
            getUserState(con, name, idcard, phone);
        }
    }


    //api获取用户状态
    public void getUserState(final Context con, final String name, final String idcard, final String phone) {
        token = SpUtils.getInstance(this).getString("token", null);
        String jsonStr = "{\n" +
                "    \"idCardType\":\"SF\",\n" +
                "    \"idCard\":\"" + idcard + "\"}";
        HttpRequest.build(getBaseContext(), netConstant.getUserInfoandStateURL())
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + token)
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        Log.d("aa", main.toString());
                        if (main.getString("code").equals("200")) {
                            JsonMap data = main.getJsonMap("data");
                            if (data.getString("keyID").equals("null")) {
                                //如果keyid不存在，说明该用户没有注册，调用注册sdk
                                Log.d("getUserState", "sda");
                                getNewRegiterInfo(con, name, idcard, phone);
                            } else {
                                //keyid存在，调用重新注册sdk
                                dismissProgressDialog();
                                Log.d("getUserState", "找回密码");
                                findRegister(con, name, idcard);

                            }
                        } else if (main.getString("code").equals("401")){
                            breaker(con);
                        }else {
                            getTipDialog(con,3,main.getString("msg")).show();
                            delayCloseTip();
                            Log.d("获取用户状态", "失败");

                        }
                    }
                })
                .doPost();
    }


    //调用注册SDK
    private void newRegister(final Context con, final String code) {
        SignetCoreApi.useCoreFunc(new RegisterCallBack(con, code, RegisterType.COORDINATE) {
            @Override
            public void onRegisterResult(RegisterResult registerResult) {

                if (registerResult.getErrCode().equals("0x00000000")){
                    msspId=registerResult.getMsspID();
                    Log.d("msspID", registerResult.getMsspID());
                }else {
                    getTipDialog(con,3,registerResult.getErrCode()+registerResult.getErrMsg()).show();
                    delayCloseTip();
                    newRegister(con,code);
                }

            }
        });
    }

    //找回证书SDK
    private void findRegister(final Context con, final String name, final String idCard) {
        SignetCoreApi.useCoreFunc(new FindBackUserCallBack(con, name, idCard, IdCardType.SF) {
            @Override
            public void onFindBackResult(FindBackUserResult findBackUserResult) {

                if (findBackUserResult.getErrCode().equals("0x00000000")){
                    msspId=findBackUserResult.getMsspID();
                    Log.d("msspID", findBackUserResult.toString());
                }else {
                    getTipDialog(con,3,findBackUserResult.getErrCode()+findBackUserResult.getErrMsg()).show();
                    delayCloseTip();
                    findRegister(con,name,idCard);
                }
            }
        });
    }

    //调用ca添加用户接口 api 并且调用注册
    private void getNewRegiterInfo(final Context con, String name, String idcard, String phone) {

        String jsonStr = "{\n" +
                "    \"name\":\"" + name + "\",\n" +
                "    \"idType\":\"SF\",\n" +
                "    \"idCardNum\":\"" + idcard + "\",\n" +
                "    \"mobile\":\"" + phone + "\"\n" +
                "}";
        HttpRequest.build(getBaseContext(), netConstant.getAddTrustUserV2URL())
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "Bearer " + token)
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        Log.e("getNewRegiterInfo", main.toString());
                        if (main.getString("code").equals("200")) {
                            JsonMap data = main.getJsonMap("data");
                            msspId = data.getString("msspId");
                            activeCode = data.getString("userQrCode");

                            dismissProgressDialog();
                            //调用注册
                            newRegister(con, activeCode);

                            Log.d("activeCode", activeCode);
                        } else if (main.getString("code").equals("401")){
                            dismissProgressDialog();
                            breaker(con);
                        }else {
                            dismissProgressDialog();
                            getTipDialog(con,3,main.getString("msg")).show();
                            delayCloseTip();
                            Log.d("添加用户接口调用", "失败");
                        }
                    }
                })
                .doPost();

    }



    //base64转bitmap
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

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


    //上传完删除文件
    //上传pdf 异步执行会与下一步冲突，所以把签署pdf的方法放在此函数中
    public void uploadPdf(final Context con) {
        token = SpUtils.getInstance(this).getString("token", null);
        HttpRequest.build(con, netConstant.getCloudSealUploadDocWithKeyIDURL())
                .setMediaType(baseokhttp3.MediaType.parse("application/pdf"))
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "multipart/form-data")
                .addParameter("notifyLetterFile", uploadFile)
                .addParameter("msspId", msspId)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        if (error != null) {
                            dismissProgressDialog();
                            Log.d("上传", "连接失败", error);
                            getTipDialog(con,3,"连接失败").show();


                            delayCloseTip();
                        } else {
                            if (main.getString("code").equals("200")) {
                                //上传成功
                                JsonMap jsonMap = main.getJsonMap("data");
                                signId = jsonMap.getString("signId");
                                Log.d("上传", signId);

                                dismissProgressDialog();
                                //签署pdf
                                signPdf(con);
                            }else if (main.getString("code").equals("401")){
                                dismissProgressDialog();

                                breaker(con);
                            } else {
                                Log.e("上传", main.getString("msg"));
                                Log.e("上传", main.getString("code"));
                                Toast.makeText(con,main.getString("msg"),Toast.LENGTH_SHORT).show();
                                dismissProgressDialog();

                            }
                        }
                    }
                })
                .doPost();

    }

    //电子签章
    public void signPdf(final Context con) {
        SignetCoreApi.useCoreFunc(new SignDataCallBack(con, msspId, signId) {
            @Override
            public void onSignDataResult(SignDataResult result) {
                if (result.getErrCode().equals("0x00000000")){
                    Log.d("sign", result.getSignDataJobId());
                    Log.d("sign", result.getErrMsg());
                    Log.d("sign", result.getErrCode());

                    //Log.d("sign",result.getSignDataInfos().toString());
                    showProgressDialog(con,"请稍后");
                    getSeal(con);
                }else {
                    Toast.makeText(con,"有错误，请重新输入",Toast.LENGTH_SHORT).show();
                    signPdf(con);

                }

            }
        });

    }

    //获取数字签名结果
    public void getSeal(final Context con) {

        String json = "{\n" +
                "    \"signId\":\"" + signId + "\"\n" +
                "}";
        if (signId != null) {
            token = SpUtils.getInstance(this).getString("token", null);
            HttpRequest.build(con, netConstant.getCloudSealCommitSignURL())
                    .addHeaders("Authorization", "Bearer " + token)
                    .addHeaders("Content-Type", "application/json")
                    .setJsonParameter(json)
                    .setJsonResponseListener(new JsonResponseListener() {
                        @Override
                        public void onResponse(JsonMap main, Exception error) {
                            if (error != null) {
                                dismissProgressDialog();
                                getTipDialog(con,3,"连接失败").show();
                                delayCloseTip();
                                Log.d("请求结果", "连接失败", error);
                            } else {
                                if (main.getString("code").equals("200")) {
                                    //上传成功
                                    JsonMap jsonMap = main.getJsonMap("data");
                                    fileHashSha1 = jsonMap.getString("fileHashSha1");
                                    fileName = jsonMap.getString("fileName");
                                    statusInfo = jsonMap.getString("statusInfo");
                                    fileSize = jsonMap.getString("fileSize");
                                    filePath = jsonMap.getString("filePath");
                                    Log.d("请求结果", fileHashSha1);
                                    Log.d("请求结果", fileName);
                                    Log.d("请求结果", statusInfo);
                                    Log.d("请求结果", fileSize);
                                    Log.d("请求结果", filePath);
                                    uploadNotifyLetter(con);

                                } else if (main.getString("code").equals("401")){
                                    dismissProgressDialog();
                                    breaker(con);
                                }else {
                                    dismissProgressDialog();
                                    getTipDialog(con,3,main.getString("msg")).show();
                                    delayCloseTip();
                                    Log.e("请求结果", main.getString("msg"));
                                    Log.e("请求结果", main.getString("code"));

                                }
                            }
                        }
                    })
                    .doPost();
        }
    }

    //上传告知函
    private void uploadNotifyLetter(final Context con) {
        token = SpUtils.getInstance(this).getString("token", null);
        caseId = SpUtils.getInstance(this).getString("caseId", null);
        File video=new File(SpUtils.getInstance(this).getString("videopath", null));
        String jsonStr = "{\n" +
                "    \"fileHashSha1\":\"" + fileHashSha1 + "\",\n" +
                "    \"fileSize\":\"" + fileSize + "\",\n" +
                "    \"filePath\":\"" + filePath + "\",\n" +
                "    \"caseId\":\"" + caseId + "\"\n" +
                "}";
        HttpRequest.build(con, netConstant.getUploadNotifyLetterURL())
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/json")
                .setJsonParameter(jsonStr)
                .setJsonResponseListener(new JsonResponseListener() {
                    @Override
                    public void onResponse(JsonMap main, Exception error) {
                        if (error != null) {
                            Log.d("上传告知函", "连接失败", error);
                            getTipDialog(con,3,"连接失败").show();
                            delayCloseTip();
                            dismissProgressDialog();

                        } else {
                            if (main.getString("code").equals("200")) {
                                dismissProgressDialog();
                                //上传成功
                                JsonMap jsonMap = main.getJsonMap("data");
                                //取参传到下一页
                                caseCode = jsonMap.getString("caseCode");
                                date = jsonMap.getString("applyTime");
                                bank = jsonMap.getString("coName");
                                Intent intent = new Intent(con, Apply2Activity.class);
                                intent.putExtra("filename", fileName);
                                intent.putExtra("date", date);
                                intent.putExtra("bank", bank);
                                startActivity(intent);
                                finish();
                                Log.d("上传告知函", jsonMap.toString());
                            } else if (main.getString("code").equals("401")){
                                dismissProgressDialog();
                                breaker(con);
                            }else {
                                getTipDialog(con,3,main.getString("msg")).show();
                                delayCloseTip();
                                Log.e("上传告知函", main.getString("msg"));
                                Log.e("上传告知函", main.getString("code"));
                                dismissProgressDialog();
                            }
                        }
                    }
                })
                .doPost();

    }

//    public void uploadVideo(Context con,LinearLayout layout){
//        token= SpUtils.getInstance(this).getString("token",null);
//        caseId=SpUtils.getInstance(this).getString("caseId",null);
//        String filepath=SpUtils.getInstance(this).getString("videopath",null);
//        File videoFile=new File(filepath);
//        if (videoFile.exists()){
//            if (!IsFileInUse(filepath)){
//                Log.d(TAG, "uploadVideo: 文件未操作");
//                HttpRequest.build(con, netConstant.getUploadNotarizeVideo())
//                        .setMediaType(MediaType.parse("video/mpeg4"))
//                        .addHeaders("Authorization","Bearer "+token)
//                        .addHeaders("Content-Type","multipart/form-data")
//                        .addParameter("notarizeVideoFile",videoFile)
//                        .addParameter("caseId",caseId)
//                        .setJsonResponseListener(new JsonResponseListener() {
//                            @Override
//                            public void onResponse(JsonMap main, Exception error) {
//                                if (error!=null){
//                                    Log.e("上传","连接失败",error);
//                                    dismissProgressDialog();
//                                    Toast.makeText(con,"连接失败,请重试",Toast.LENGTH_SHORT).show();
//                                }else {
//                                    if (main.getString("code").equals("200")){
//                                        //上传成功
//                                        Log.d(TAG, "onResponse: 上传成功");
//                                        setupPdf(layout);
//                                    } else if (main.getString("code").equals("401")){
//
//                                        breaker(con);
//
//                                    }else {
//                                        Log.e("上传",main.getString("msg"));
//                                        Log.e("上传",main.getString("code"));
//
//                                        dismissProgressDialog();
//                                        Toast.makeText(con,main.getString("msg"),Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            }
//                        })
//                        .doPost();
//            }else {
//                //文件正在被操作
//                Log.d(TAG, "uploadVideo: 文件正在被操作");
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        //do something
//                        uploadVideo(con,layout);
//                    }
//                }, 2000);    //延时1s执行
//            }
//
//        }else {
//
//            Toast.makeText(con,"录制失败，请重新录制",Toast.LENGTH_SHORT).show();
//
//
//        }
//
//
//    }

//    //下载告知函
//    private void Download(final Context con) {
//        DownloadUtil.get().download(netConstant.getDownloadCaseFile(),
//                Environment.getExternalStorageDirectory().getAbsolutePath(),
//                fileName,
//                new DownloadUtil.OnDownloadListener() {
//                    @Override
//                    public void onDownloadSuccess(File file) {
//
//                    }
//
//                    @Override
//                    public void onDownloading(int progress) {
//                        showProgressDialog(con, "下载中，请稍后" + progress + "%");
//                    }
//
//                    @Override
//                    public void onDownloadFailed(Exception e) {
//
//                    }
//                });
//    }

    public void testDownload() {
        token = SpUtils.getInstance(this).getString("token", null);
        caseId = SpUtils.getInstance(this).getString("caseId", null);
        userId = SpUtils.getInstance(this).getString("userId", null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + token)
                        .url(netConstant.getDownloadCaseFile() + "?userId=21&caseId=196&fileType=300010")
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    byte[] bytes = response.body().bytes();
                    saveBytesToFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "111.pdf", bytes);
                    dismissProgressDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //存文件
    public static void saveBytesToFile(String filePath, byte[] data) {
        File file = new File(filePath);
        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            outStream.write(data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void showExitDialog() {
        new AlertDialog.Builder(getBaseContext())
                .setTitle("成功")
                .setMessage("好")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
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
                if (tipDialog.isShowing()){
                    tipDialog.dismiss();
                }

            }
        },1500);
    }
    private void showMessagePositiveDialog(final Context con) {
        new QMUIDialog.MessageDialogBuilder(con)
                .setMessage("签章成功，点击确定跳转到下一页")
                .setSkinManager(QMUISkinManager.defaultInstance(con))
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
//成功跳转
                        Intent intent = new Intent(con, Apply2Activity.class);
                        intent.putExtra("filename", fileName);
                        intent.putExtra("date", date);
                        intent.putExtra("bank", bank);
                        startActivity(intent);

                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }
    private void deletePdf(File file){
        if (file.exists()){
            file.delete();
        }else{
            Log.d(TAG, "deletePdf: 删除失败");
        }
    }
    public void deleteFile(File file){
        if (file.exists()){
            file.delete();
        }else{
            Log.d("删除", "deletePdf: 删除失败");
        }
    }


}



