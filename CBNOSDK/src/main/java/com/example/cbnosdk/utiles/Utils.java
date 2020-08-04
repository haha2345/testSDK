package com.example.cbnosdk.utiles;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Utils extends AppCompatActivity {

    //解析json
    public JsonObject getJson(String jsonString){
        JsonObject responseBodyJSONObject =
                (JsonObject) new JsonParser()
                .parse(jsonString);
        return responseBodyJSONObject;
    }
    // 实现在子线程中显示Toast
    public void showToastInThread(final Context context, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

}
