package com.example.canteen_attempt4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class qrReader extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    @Override
    public void handleResult(Result result) {   //called when qr code is scanned and data is retrieved
        if(result!=null){
            Vibrator vibrate=(Vibrator) getSystemService(VIBRATOR_SERVICE);     //get vibration service from android system
            vibrate.vibrate(60);
            //the below statement is for API 26 and higher. Above statement on the other hand for API 25. So both are included. To change minimum sdk version, goto File->ProjectStructure->DefaultConfig->minSdk
            vibrate.vibrate(VibrationEffect.createOneShot(60,100)); //vibrate device when qr code data is retrieved
            startActivity(new Intent(this,ConfirmCustomer.class).putExtra("ID",result.getText()));
        }
    }
    ZXingScannerView scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        scanner=new ZXingScannerView(this);
        scanner=findViewById(R.id.qr_scan);
        scanner.setAutoFocus(true);
        scanner.setScaleX(2);
        scanner.setScaleY(2);
    }

    @Override
    protected void onResume() {     //here camera is started
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1888);
            }
        }
        scanner.startCamera();
        scanner.setResultHandler(this);
    }

    @Override
    protected void onPause() {      //when activity is paused, camera stops
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void onBackPressed() {   //when user presses back key, intent to pending orders activity
        finish();
        startActivity(new Intent(this,PendingOrders.class));
    }
}


