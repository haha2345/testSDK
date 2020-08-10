package com.example.cbnosdk.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cbnosdk.R;

public class ShipingongzhenActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;
    private Button btn;
//    private String src,name,phone,idcard,token;
    private ImageView iv;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipingongzhen);
        bundle=getIntent().getExtras();
//        src=bundle.getString("basestr");
//        name=bundle.getString("name");
//        idcard=bundle.getString("idcard");
//        phone=bundle.getString("phone");
//        token=bundle.getString("token");

        btn=findViewById(R.id.btn_shipingongzheng);
        iv=findViewById(R.id.iv_aa);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShipingongzhenActivity.this, MyRecordActivity.class);
//                Bundle bundle1=new Bundle();
//                bundle1.putString("token",token);
//                bundle1.putString("basesrc",src);
//                bundle1.putString("name",name);
//                bundle1.putString("idcard",idcard);
//                bundle1.putString("phone",phone);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }



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
                    Toast.makeText(ShipingongzhenActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShipingongzhenActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}