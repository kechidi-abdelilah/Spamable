package com.example.smsspamdetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.smsspamdetection.ml.Lstm3;

import org.greenrobot.eventbus.EventBus;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    ArrayList<Float> f = null;

    public static final String SMS_BUNDLE = "pdus";
    String smsBody;
    String address;
    long date;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();
                date = smsMessage.getTimestampMillis();
            }

            try {
                f = Preprocess.nlp(context,smsBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteBuffer byteBuffer= ByteBuffer.allocateDirect(79*4);
            byteBuffer.order(ByteOrder.nativeOrder());

            // filling the buffer with the words from the message after tokenizing
            for(int i =0;i<f.size();i++){
                byteBuffer.putFloat(f.get(i));
            }
            try {
                Lstm3 model = Lstm3.newInstance(context);

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 79}, DataType.FLOAT32);
                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                Lstm3.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                // Releases model resources if no longer used.
                model.close();
                System.out.println("--------------------------------------------");
                System.out.println(outputFeature0.getFloatArray()[0]);
                System.out.println("--------------------------------------------");
                MainActivity inst = MainActivity.instance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();
                String[] arrOfStr = smsDate.split(" ");



                if(outputFeature0.getFloatArray()[0]<=0.5){
                    inst.addData(true,new Message(smsBody, address,arrOfStr[1]+" "+arrOfStr[2]));
                }else {
                    inst.addData(false,new Message(smsBody, address,arrOfStr[1]+" "+arrOfStr[2]));
                }

            } catch (IOException e) {
                // TODO Handle the exception
            }
        }
    }
}
