package com.example.memonote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.io.*;

/**
 * Created by 박민주 on 2016-06-18.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(4000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
