package com.example.smsspamdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class add extends AppCompatActivity {

    private EditText phoneNumber, myMessage;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ImageButton home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(0,0);
            }
        });

        ImageButton about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.example.smsspamdetection.AboutUs.class));
                overridePendingTransition(0,0);
            }
        });

        send = findViewById(R.id.send);
        phoneNumber = findViewById(R.id.phone_number);
        myMessage = findViewById(R.id.my_message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(add.this,"fdsf",Toast.LENGTH_SHORT).show();
                        sendSMS();
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                    }
                }
            }
        });
    }

    private void sendSMS(){
        String Number = phoneNumber.getText().toString().trim();
        String sms = myMessage.getText().toString().trim();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Number,null,sms,null,null);
            Toast.makeText(this, "Message is sent to "+ phoneNumber.getText().toString() ,Toast.LENGTH_SHORT).show();
            phoneNumber.setText("");
            myMessage.setText("");
            phoneNumber.clearFocus();
            myMessage.clearFocus();

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            phoneNumber.setText("");
            myMessage.setText("");
            phoneNumber.clearFocus();
            myMessage.clearFocus();
        }
    }
}