package com.example.cbnosdk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.cbnosdk.R;
//import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class CBNOVideoActivity extends AppCompatActivity {

//    QMUITopBarLayout mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbno_video);
        VideoView videoView = (VideoView)findViewById(R.id.videoView);
//        mTopBar=findViewById(R.id.topbar_video);

        //加载指定的视频文件
        //String path = SpUtils.getInstance(this).getString("videopath",null);
        
        String path=getIntent().getStringExtra("path");
        videoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();
//        initTopBar();

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
//        mTopBar.setTitle("查看视频");
//    }
}