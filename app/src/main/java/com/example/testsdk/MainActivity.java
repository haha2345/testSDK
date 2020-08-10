package com.example.testsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cbnosdk.Activity.Apply3Activity;
import com.example.cbnosdk.CBNOApi;


public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;


    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        Button btn=findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Apply3Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "王文哲");
                bundle.putString("idcard", "370284199803310014");
                bundle.putString("token", "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleToiOiIxNjFlYTk5MS1mNmE0LTQ5MjYtYTI0NS01ZThmZWY0ZjE4YTgifQ.kzSmF58J2-TKvqG5GAhQ51XGFPNCJFnwkek4w4zqqg8tzxaKKnGPxyO5AQq5CmbdBwQI0saT5mu-scj0GyS1qg");
                bundle.putString("phone", "13205401086");
                bundle.putString("bank", "bank");
                bundle.putString("caseid", "400");
                intent.putExtras(bundle);
                startActivity(intent);
//                CBNOApi.beginApiService(MainActivity.this);
//                cbnoApi.start(MainActivity.this);
//                new CBNOApi().start(MainActivity.this);

//                CBNOApi.beginApiService(MainActivity.this);

            }
        });
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
                    Toast.makeText(MainActivity.this, "camera permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "[WARN] camera permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}