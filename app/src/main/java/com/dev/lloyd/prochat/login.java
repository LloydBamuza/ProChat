package com.dev.lloyd.prochat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.sendbird.android.SendBird;

import static com.dev.lloyd.prochat.fragments.fragActiveChat.startMessageReceiver;

public class login extends AppCompatActivity {
    public static final int READ_WRITE_INTERNET = 808;


    private final static String API_ID = "C1986EC7-1C74-44B9-92FE-3EE2705872DA";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get permissions
        requestPermissions();


        //verify that all permissions were granted
        final boolean[] allPermissionsGranted = {true};

        new ActivityCompat.OnRequestPermissionsResultCallback()
        {
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
            {

                for(int i : grantResults)
                {
                    if(i == PackageManager.PERMISSION_DENIED)
                    {
                        allPermissionsGranted[0] = false;
                        break;
                    }
                    allPermissionsGranted[0] = true;
                }
            }
        };

        final boolean[] exitApp = {false};

        while(allPermissionsGranted[0] == false && !exitApp[0])
        {
            //show dialog requesting permisions until user exits
            AlertDialog alertDialog = new AlertDialog.Builder(login.this).create();
            alertDialog.setTitle("All permissions required. Retry?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    requestPermissions();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    exitApp[0] = true;
                }
            });

            alertDialog.show();
        }


        //initialize app using SendBird API_ID
        SendBird.init(API_ID,login.this);

        //login
        findViewById(R.id.btnLogIn).setOnClickListener(e->
        {
            String userId = ((EditText) findViewById(R.id.edtUserId)).getText().toString();

            SendBird.connect(userId, (user,error)->
            {
                if(error == null)
                {
                    Toast.makeText(login.this,"Connection Established",Toast.LENGTH_LONG).show();
                    TheUser.username = userId;
                    //open chat activity
                    Intent intent = new Intent(login.this,ActivityChat.class);
                    startMessageReceiver();

                    startActivity(intent);

                }
                else
                    Toast.makeText(login.this,"Connection Unsuccessful",Toast.LENGTH_LONG).show();
            });
        });

    }

    void requestPermissions()
    {
        ActivityCompat.requestPermissions(login.this,new String[]{Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                READ_WRITE_INTERNET);
    }
}
