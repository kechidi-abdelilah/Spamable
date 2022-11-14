package com.example.smsspamdetection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.smsspamdetection.ml.Lstm3;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ArrayList<Float> f = null;

    private ArrayList<Message> smsList,spamList;
    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private RadioButton smsButton, spamButton;
    private static MainActivity inst;
    private RecyclerAdapter adapter;
    ArrayList<String> messages,senders,dates;
    boolean checked;

    public static MainActivity instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        adapter = new RecyclerAdapter(smsList);

        ImageButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),add.class));
                overridePendingTransition(0,0);
            }
        });

        ImageButton about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutUs.class));
                overridePendingTransition(0,0);
            }
        });

        smsButton = findViewById(R.id.smsButton);
        spamButton = findViewById(R.id.spamButton);
        radioGroup = findViewById(R.id.radioGroup);

        recyclerView = findViewById(R.id.myrecycler);
        smsList = new ArrayList<Message>();
        spamList = new ArrayList<Message>();

        smsButton.setChecked(true); checked=true;
        SharedPreferences sh = getSharedPreferences("MySharedPref",  MODE_APPEND);

        String s1 = sh.getString("first", "");

        if (s1.contentEquals("yes")){
            loadData(smsList,spamList,true);
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("first", "yes");
            myEdit.commit();
            setSMS();
        }

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("contnet://sms/inbox"),
                null,null,null,null);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.smsButton:
                        loadData(smsList,spamList,true);
                        checked = true;
                        //setAdapter(smsList);
                        break;
                    case R.id.spamButton:
                        loadData(smsList,spamList,false);
                        checked = false;
                        //setSPAM();
                       // setAdapter(spamList);
                        break;
                }
            }
        });

      }



    private void setAdapter(ArrayList<Message> l) {
        adapter = new RecyclerAdapter(l);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSMS(){
        int i=0;
        smsList.clear();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null,null,null);
        cursor.moveToFirst();
        do{
            i++;
            String txt = cursor.getString(12);
            String sender = cursor.getString(2);
            String date = cursor.getString(4);
            Long timestamp = Long.parseLong(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            Date finaldate = calendar.getTime();
            String smsDate = finaldate.toString();
            String[] arrOfStr = smsDate.split(" ");

            ByteBuffer byteBuffer= ByteBuffer.allocateDirect(79*4);
            byteBuffer.order(ByteOrder.nativeOrder());

                try {
                    f = Preprocess.nlp(this, txt);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for(int x=0;x<f.size();x++){
                    byteBuffer.putFloat(f.get(x));
                }
                try {
                    Lstm3 model = Lstm3.newInstance(this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 79}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Lstm3.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    if(outputFeature0.getFloatArray()[0]<=0.5){
                        smsList.add(new Message(txt, sender,arrOfStr[1]+" "+arrOfStr[2]));
                    }else {
                        spamList.add(new Message(txt, sender,arrOfStr[1]+" "+arrOfStr[2]));
                    }
                } catch (IOException e) {
                    // TODO Handle the exception
                }

        }while (cursor.moveToNext());
        saveData(smsList,spamList);
        setAdapter(smsList);
    }

    private void setSPAM(){
        setAdapter(spamList);
    }

    public void updateSmsList(Message m) {
        smsList.add(0,m);
        adapter.notifyDataSetChanged();
        //setAdapter();
        Toast.makeText(MainActivity.this, "updatelist", Toast.LENGTH_SHORT).show();

    }

    public void updateSpamList(Message m) {
        spamList.add(0,m);
        adapter.notifyDataSetChanged();
        //setAdapter();
        Toast.makeText(MainActivity.this, "updateSpamlist", Toast.LENGTH_SHORT).show();

    }

    public void loadData(ArrayList<Message> sms,ArrayList<Message> spam,boolean b){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String smsJson = sharedPreferences.getString("sms", null);
        String spamJson = sharedPreferences.getString("spam", null);


        Type type = new TypeToken<ArrayList<Message>>() {}.getType();


        sms = gson.fromJson(smsJson, type);
        spam = gson.fromJson(spamJson, type);

        // checking below if the array list is empty or not
        if (sms == null) {
            // if the array list is empty
            // creating a new array list.
            sms = new ArrayList<>();
        }
        if (spam == null) {
            // if the array list is empty
            // creating a new array list.
            spam = new ArrayList<>();
        }
        if(b){
            setAdapter(sms);
        }else {
            setAdapter(spam);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData(ArrayList<Message> sms, ArrayList<Message> spam) {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();


        String smsJson = gson.toJson(smsList);
        String spamJson = gson.toJson(spamList);



        editor.putString("sms", smsJson);
        editor.putString("spam", spamJson);



        editor.apply();

    }
    public void addData(boolean b, Message m){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        ArrayList<Message> sms;
        ArrayList<Message> spam;
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String smsJson = sharedPreferences.getString("sms", null);
        String spamJson = sharedPreferences.getString("spam", null);
        Type type = new TypeToken<ArrayList<Message>>() {}.getType();
        sms = gson.fromJson(smsJson, type);
        spam = gson.fromJson(spamJson, type);
        SharedPreferences sharedPreferences2= getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        if (b){
            sms.add(0,m) ;
            String smsJsonn = gson.toJson(sms);
            editor.putString("sms", smsJsonn);
            editor.apply();
            if(checked) {
                setAdapter(sms);
            }
        }else{
            spam.add(0,m);
            String spamJsonn = gson.toJson(spam);
            editor.putString("spam", spamJsonn);
            editor.apply();
            if(!checked) {
                setAdapter(spam);
            }
        }

    }

}

