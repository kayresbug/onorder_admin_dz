package com.daon.admin_onorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daon.admin_onorder.model.PrintOrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.sam4s.io.OnConnectListener;
import com.sam4s.io.ethernet.SocketInfo;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sFinder;
import com.sam4s.printer.Sam4sPrint;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;
    Handler handler = new Handler();
    String[] deviceList = null;
    Sam4sFinder ef = new Sam4sFinder();
    final static int DISCOVERY_INTERVAL = 500;
    EditText edit_id;
    EditText edit_pass;
    AdminApplication app;
    RelativeLayout loginBtn;
    String fcm_id;
    String TAG = "daon_test";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Sam4sPrint printer = new Sam4sPrint();
    Sam4sPrint printer2 = new Sam4sPrint();
    Sam4sPrint printer3 = new Sam4sPrint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("storename", "돈짬");
        editor.putString("storecode", "ddz12");
        editor.commit();
        app = new AdminApplication();
        edit_id = findViewById(R.id.edit_id);
        edit_pass = findViewById(R.id.edit_pw);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        fcm_id = token;
                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        try {
            try {
                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.20.191", 9100);
                printer.resetPrinter();
                printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.20.193", 6001);
                printer2.resetPrinter();
                printer3.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.102", 9100);
                printer3.resetPrinter();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("daon", "print error = "+e.getMessage());
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)){
                try {
//                    Log.d("daon", "print error = "+printer.getPrinterStatus());
//                    Log.d("daon", "print error2 = "+printer2.getPrinterStatus());
//                    Log.d("daon", "print error3 = "+printer3.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    app.setPrinter(printer);
                    app.setPrinter2(printer2);
//                    app.setPrinter3(printer3);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginBtn = findViewById(R.id.loginactivity_btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    setFcm(fcm_id);
//                    print2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFcm(String fcm_id) throws Exception {

        if (printer.getPrinterStatus() != null && printer2.getPrinterStatus() != null && printer3.getPrinterStatus() != null) {
//        if (printer != null){
            String str_id = "ddz12";
            String str_pass = "1234";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://15.164.232.164:5000/")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

            interfaceApi.setFcm(str_id, str_pass, fcm_id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (String.valueOf(response.body().get("StatusCode")).equals("200")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("daon", "fail = " + t.getMessage());
                }
            });
        }else{
            if (printer.getPrinterStatus() == null){
                Toast.makeText(LoginActivity.this, "1번 프린터 이상", Toast.LENGTH_SHORT).show();

            }else if (printer2.getPrinterStatus() == null){
                Toast.makeText(LoginActivity.this, "2번 프린터 이상", Toast.LENGTH_SHORT).show();

            }else if (printer3.getPrinterStatus() == null){
                Toast.makeText(LoginActivity.this, "3번 프린터 이상", Toast.LENGTH_SHORT).show();

            }
            setPrinter();
        }
    }
    public void setPrinter(){
        app = new AdminApplication();
        try {
            try {
                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.37", 9100);
                printer.resetPrinter();
                printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.33", 9100);
                printer2.resetPrinter();
                printer3.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.26", 9100);
                printer3.resetPrinter();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("daon", "print error = "+e.getMessage());
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)){
                try {
                    Log.d("daon", "print error1 = "+printer2.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                app.setPrinter(printer);
                app.setPrinter2(printer2);
                app.setPrinter3(printer3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print2(){

        Sam4sPrint sam4sPrint = app.getPrinter();
        Sam4sPrint sam4sPrint2 = app.getPrinter2();
        Sam4sPrint sam4sPrint3 = app.getPrinter3();
        try {
//            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            // top
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(2);
            builder.addTextSize(1,1);
            builder.addText("[신규-주방주문서]-주문서");
            builder.addFeedLine(2);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[테이블] 기본-");
            builder.addText("16");
            builder.addFeedLine(1);
            builder.addText("[주문번호] ");
            builder.addText("0066-0001");
            builder.addFeedLine(1);
            builder.addText("==========================================");
            builder.addFeedLine(2);
// body
            builder.addTextSize(2,1);
            builder.addTextPosition(0);
            builder.addTextBold(true);
            builder.addText("메 뉴 명");
            builder.addTextPosition(400);
            builder.addTextBold(false);
            builder.addText("수량");
            builder.addFeedLine(2);
            builder.addTextSize(1,1);
            builder.addText("------------------------------------------");
            builder.addTextSize(1,1);
            builder.addFeedLine(1);
            builder.addText("돈가스짬뽕");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_RIGHT);
            builder.addText("1");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("돈가스따로줘요용");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_RIGHT);
            builder.addText("1");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addTextSize(1,1);
// footer
            builder.addText("------------------------------------------");
            builder.addFeedLine(2);
            builder.addText("[주방메모]");
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addText("==========================================");
            builder.addFeedLine(1);
            builder.addText("POS: ");
            builder.addText("order");
            builder.addText("   ");
            builder.addText("[주문시간]");
            builder.addText("2021-04-13 22:14:79");
            builder.addFeedLine(2);
            builder.addCut(Sam4sBuilder.CUT_FEED);
            sam4sPrint.sendData(builder);
//            sam4sPrint.closePrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}