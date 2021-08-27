package com.example.aggos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class ControllerActivity extends AppCompatActivity {

    private BLEController bleController;
    private RemoteControl remoteControl;
    private Button dpad_fwd;

    private TextView binStatusTxt;

    public ConstraintLayout holderBinReady;
    public ConstraintLayout holderBinFull;

    private WebView cam;

    private boolean isLEDOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        dpad_fwd = findViewById(R.id.dpad_fwd);

        //CAMERA
        cam = findViewById(R.id.cam);
        WebSettings webSettings = cam.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        cam.setWebViewClient(new Callback());
        cam.loadUrl("http://192.168.43.94");

        holderBinReady = findViewById(R.id.holder_binStatReady);
        holderBinFull = findViewById(R.id.holder_binStatFull);

        binStatusTxt = findViewById(R.id.txt_binStatReady);

        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        bleController.setCharacteristicNotification(true);

        dpad_fwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLEDOn = !isLEDOn;
                remoteControl.switchLED(isLEDOn);
                Toast.makeText(ControllerActivity.this, "BtnFWD is clicked", Toast.LENGTH_SHORT).show();
                //log("LED switched " + (isLEDOn?"On":"Off"));

            }
        });

        //livedata
        renderUpdates();
    }

    private void renderUpdates(){
        MyViewModel myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        Observer<Integer> liveDataObserver=new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
               if(integer == 0)
               {
                   holderBinReady.setVisibility(View.VISIBLE);
                   holderBinFull.setVisibility(View.INVISIBLE);
               }
               else if (integer == 1)
               {
                   holderBinReady.setVisibility(View.INVISIBLE);
                     holderBinFull.setVisibility(View.VISIBLE);
               }
            }
        };
        myViewModel.getLiveData().observe(this,liveDataObserver);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}
