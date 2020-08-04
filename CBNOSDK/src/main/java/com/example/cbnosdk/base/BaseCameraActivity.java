package com.example.cbnosdk.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;

import com.example.cbnosdk.R;
import com.example.cbnosdk.utiles.SpUtils;
import com.example.cbnosdk.utiles.Timeutils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;


public class BaseCameraActivity extends AppCompatActivity {
    public Handler handler=new Handler();
    public Runnable runnable=new Runnable() {
        @Override
        public void run() {
            cameraRecorder.stop();

            //暂停计时
            timer.stopTimer();
            recordBtn.setImageResource(R.drawable.record);
            recordBtn.setEnabled(false);
            cancelBtn.setImageResource(R.drawable.chongxinrecord);
            cancelBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            flag=true;
            handler.postDelayed(this,40000);
        }
    };

    private SampleGLView sampleGLView;
    public static CameraRecorder cameraRecorder=null;
    public String filepath;
    public ImageButton recordBtn,cancelBtn,uploadBtn;
    public TextView tv_camera_timer;
    protected LensFacing lensFacing = LensFacing.FRONT;
    public String token,caseId;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 720;
    private AlertDialog filterDialog;
    private boolean toggleClick = false;
    //检测是否录像
    public boolean flag=true;
    public QMUITipDialog tipDialog;

    public String imagePath,src;

    public File videoFile;

    //调用计时器
    public Timeutils timer;
    public ProgressDialog progressDialog;
    Timer time = new Timer();

//    //检测时间 定时关闭
//    private Handler handler= new Handler();
//    private Runnable runnable = new Runnable(){
//        public void run(){
//            String time=tv_camera_timer.getText().toString();
//            if (time.equals("00:20")){
//                //结束录像
//                cameraRecorder.stop();
//                //暂停计时
//                timer.stopTimer();
//                recordBtn.setImageResource(R.drawable.record);
//                recordBtn.setEnabled(false);
//                cancelBtn.setImageResource(R.drawable.chongxinrecord);
//                cancelBtn.setVisibility(View.VISIBLE);
//                uploadBtn.setVisibility(View.VISIBLE);
//                flag=true;
//                handler.removeCallbacks(runnable);
//            }
//            handler.postDelayed(this,1000);//定时时间
//        }
//    };

    protected void onCreateActivity() {
        tv_camera_timer=findViewById(R.id.tv_recorder_time);
        //初始化计时器
        timer=new Timeutils(tv_camera_timer);
        src=getIntent().getStringExtra("basesrc");

        cancelBtn=findViewById(R.id.ibtn_cancle);
        uploadBtn=findViewById(R.id.ibtn_upload);
        recordBtn = findViewById(R.id.ibtn_record);



        //点击录像
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    //自动获取视频路径
                    filepath = getVideoFilePath();
                    Log.d("视频路径",filepath);
                    //开始计时
                    timer.startTimer();

                    SpUtils.getInstance(getParent()).setString("videopath",filepath);
                    cameraRecorder.start(filepath);
                    recordBtn.setImageResource(R.drawable.luwan);
                    //关闭取消按钮
                    cancelBtn.setVisibility(View.INVISIBLE);
                    //在录像开始时获取图片
                    captureBitmap(new BitmapReadyCallbacks() {
                        @Override
                        public void onBitmapReady(final Bitmap bitmap) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    //保存图片路径
                                    imagePath = getImageFilePath();
                                    Log.d("图片路径",imagePath);
                                    SpUtils.getInstance(getParent()).setString("imagepath",imagePath);
                                    saveAsPngImage(bitmap, imagePath);
                                    exportPngToGallery(getApplicationContext(), imagePath);
                                }
                            });

                        }
                    });
                    //延时执行操作，在主线程中工作


                    handler.postDelayed(runnable,40000);
//                    handler.postDelayed(new Runnable(){
//                        public void run() {
//                            //execute the task
//
//                        }
//                    }, 40500);
//                    time.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            //要延时的程序
//                            Looper.prepare();
//                            cameraRecorder.stop();
//
//                            //暂停计时
//                            timer.stopTimer();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    recordBtn.setImageResource(R.drawable.record);
//                                    recordBtn.setEnabled(false);
//                                    cancelBtn.setImageResource(R.drawable.chongxinrecord);
//                                    cancelBtn.setVisibility(View.VISIBLE);
//                                    uploadBtn.setVisibility(View.VISIBLE);
//                                    flag=true;
//                                }
//                            });
//
//                        }
//                    },10000);

//                    handler.removeCallbacks(runnable);
//                    handler.postDelayed(runnable,1000);
                    flag=false;
                } else {
                    handler.removeCallbacks(runnable);
                    //结束录像
                    cameraRecorder.stop();
                    //暂停计时
                    timer.stopTimer();
                    recordBtn.setImageResource(R.drawable.record);
                    recordBtn.setEnabled(false);
                    cancelBtn.setImageResource(R.drawable.chongxinrecord);
                    cancelBtn.setVisibility(View.VISIBLE);
                    uploadBtn.setVisibility(View.VISIBLE);
                    flag=true;
                }
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_camera_timer.getText().equals("00:00")){
                    finish();
                }else {
                    tv_camera_timer.setText("00:00");
                    uploadBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setImageResource(R.drawable.error);
                    recordBtn.setEnabled(true);
                }
            }
        });




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        releaseCamera();
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (cameraRecorder != null) {
            cameraRecorder.stop();
            cameraRecorder.release();
            cameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.wrap_view)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout frameLayout = findViewById(R.id.wrap_view);
                frameLayout.removeAllViews();
                sampleGLView = null;
                sampleGLView = new SampleGLView(getApplicationContext());

                sampleGLView.setTouchListener(new SampleGLView.TouchListener() {
                    @Override
                    public void onTouch(MotionEvent event, int width, int height) {
                        if (cameraRecorder == null) return;
                        cameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
                    }
                });
                frameLayout.addView(sampleGLView);
            }
        });
    }


    public void setUpCamera() {
        setUpCameraView();

        cameraRecorder = new CameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean flashSupport) {

                    }

                    @Override
                    public void onRecordComplete() {
                        exportMp4ToGallery(getApplicationContext(), filepath);
                    }

                    @Override
                    public void onRecordStart() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("CameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setUpCamera();
                                }
                            });
                        }
                        toggleClick = false;
                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();


    }



    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                EGL10 egl = (EGL10) EGLContext.getEGL();
                GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                final Bitmap snapshotBitmap = createBitmapFromGLSurface(sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
                    }
                });
            }
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd_HHmm").format(new Date()) + "video.mp4";
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "cameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
    public static boolean IsFileInUse(String fileName) {
        File file = new File(fileName);
        if (file.renameTo(file)) {
            return false;
//            System.out.println("文件未bai被操作du");
        } else {
            return true;
//            System.out.println("文件正在zhi被dao操作");
        }
    }
}
