package com.daon.admin_onorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daon.admin_onorder.model.PrintOrderModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView paymentBtn;
    ImageView orderBtn;
    ImageView serviceBtn;
    ImageView menuBtn;
    SharedPreferences pref;
    ImageView tableBtn;

    ImageView bottom_home;
    ImageView bottom_service;
    ImageView bottom_order;
    ImageView bottom_payment;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    AdminApplication app = new AdminApplication();
    String time;

    TextView printer_status1;
    TextView printer_status2;
    TextView printer_status3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        printer_status1 = findViewById(R.id.printer_status_1);
        printer_status2 = findViewById(R.id.printer_status_2);
        printer_status3 = findViewById(R.id.printer_status_3);

        BackThread thread = new BackThread();  // ??????????????? ??????
        thread.setDaemon(true);  // ?????????????????? ?????? ?????????
        //thread.start();

        bottom_order = findViewById(R.id.bottom_menu3);
        bottom_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        bottom_service = findViewById(R.id.bottom_menu2);
        bottom_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        bottom_payment = findViewById(R.id.bottom_menu4);
        bottom_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "????????? ?????????.", Toast.LENGTH_SHORT).show();

            }
        });
        menuBtn = findViewById(R.id.menu3);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
//                startActivity(intent);
//                finish();
                Toast.makeText(MainActivity.this, "????????? ?????????.", Toast.LENGTH_SHORT).show();

            }
        });

        serviceBtn = findViewById(R.id.menu1);
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        paymentBtn = findViewById(R.id.menu4);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
//                startActivity(intent);
//                finish();
                Toast.makeText(MainActivity.this, "????????? ?????????.", Toast.LENGTH_SHORT).show();
            }
        });

        orderBtn = findViewById(R.id.menu2);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        tableBtn = findViewById(R.id.menu5);
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "????????? ?????????.", Toast.LENGTH_SHORT).show();
            }
        });
        initFirebase();
    }
    public void initFirebase(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

        time = format2.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename", "")).child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    PrintOrderModel printOrderModel = item.getValue(PrintOrderModel.class);
                    try {
                        if (printOrderModel.getPrintStatus().equals("x")) {
                            if(print(printOrderModel)==true) {
                                printOrderModel.setPrintStatus("o");
                                FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename", "")).child(time).child(item.getKey()).setValue(printOrderModel);
                            }
                            /*
                                }else {

                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
                                    mp.start();
                                }
                                */
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("service").child(pref.getString("storename", "")).child(time).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    PrintOrderModel printOrderModel = item.getValue(PrintOrderModel.class);
                    if (printOrderModel.getPrintStatus().equals("x")) {
                        try {
                                print(printOrderModel);
                                printOrderModel.setPrintStatus("o");
                                FirebaseDatabase.getInstance().getReference().child("service").child(pref.getString("storename", "")).child(time).child(item.getKey()).setValue(printOrderModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean print(PrintOrderModel printOrderModel) throws InterruptedException {
        Log.d("daon_test = ", printOrderModel.getOrder());
        String[] orderArr = printOrderModel.getOrder().split("###");
        Log.d("daon_test", orderArr[0]);

        String order = printOrderModel.getOrder();
        order = order.replace("###", "\n\n");
        order = order.replace("##", "");
        order = order.replace("??? ?????????", "");
        order = order.replace(" ?????????", "");
        order = order.replace("?????????", "");
        String order1 = "";
        String order2 = "";
        if (printOrderModel.getTable().contains("??????")){
            order = printOrderModel.getTable();
        }
        for (int i = 0; i < orderArr.length; i++){
            if (orderArr[i].contains("?????????") || orderArr[i].contains("?????????") || orderArr[i].contains("?????????") || orderArr[i].contains("?????????") ||
                    orderArr[i].contains("?????????") || orderArr[i].contains("?????????") || orderArr[i].contains("??????")){
                if (!orderArr[i].equals("\n\n")) {

                    order2 = order2 + orderArr[i];
                    order2 = order2.replace("###", "\n\n");
                    order2 = order2.replace("##", "");
                    order2 = order2.replace("???", "???\n");
                    order2 = order2.replace("??? ?????????", "");
                    order2 = order2.replace(" ?????????", "");
                    order2 = order2.replace("?????????", "");
                }

            }
            if (orderArr[i].contains("??????") || orderArr[i].contains("?????????") || orderArr[i].contains("?????????") || orderArr[i].contains("??????")){
                Log.d("daon_test", "aaaaaa"+orderArr[i]+"bbb");
                if (!orderArr[i].equals("\n\n")) {
                    order1 = order1 + orderArr[i];
                    order1 = order1.replace("###", "\n\n");
                    order1 = order1.replace("##", "");
                    order1 = order1.replace("???", "???\n");
                    order1 = order1.replace("??? ?????????", "");
                    order1 = order1.replace(" ?????????", "");
                    order1 = order1.replace("?????????", "");
                }
            }
        }
        if(app.IsConnected1()==false)
        {
            Sam4sPrint sam4sPrint1 = app.getPrinter();
            try {
                sam4sPrint1.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.100", 9100);
                Thread.sleep(300);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
            app.setPrinter(sam4sPrint1);
        }
        if(app.IsConnected2()==false)
        {
            Sam4sPrint sam4sPrint2 = app.getPrinter2();
            try {
                sam4sPrint2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.101", 9100);
                Thread.sleep(300);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
            app.setPrinter2(sam4sPrint2);
        }
        if(app.IsConnected3()==false)
        {
            Sam4sPrint sam4sPrint3 = app.getPrinter3();
            try {
                sam4sPrint3.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.102", 9100);
                Thread.sleep(300);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
            app.setPrinter3(sam4sPrint3);
        }

        Sam4sPrint sam4sPrint = app.getPrinter();
        Sam4sPrint sam4sPrint2 = app.getPrinter2();
        Sam4sPrint sam4sPrint3 = app.getPrinter3();

        try {
            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        Sam4sBuilder builder2 = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        Sam4sBuilder builder3 = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            String type = "(??????)";
            if (printOrderModel.getOrdertype().equals("cash")){
                type = "(??????)";
            }
            if (printOrderModel.getTable().contains("??????")){
                type = "";
            }
            // 1??? ?????????
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(1);
            builder.addTextSize(2,2);
            builder.addText("[?????????]");
            builder.addFeedLine(2);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[?????????] ");
            builder.addText(printOrderModel.getTable()+" "+type);
            builder.addFeedLine(1);
            builder.addText("==========================================");
//            builder.addFeedLine(2);
// body
            builder.addTextSize(2,2);
            builder.addTextPosition(0);
            builder.addTextBold(true);

//            builder.addText("??? ??? ??? ???");
            builder.addTextPosition(400);
            builder.addTextBold(false);
            builder.addFeedLine(2);
            builder.addTextSize(1,1);
            builder.addText("------------------------------------------");
            builder.addTextSize(2,2);
            builder.addText(order);
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addTextSize(1,1);
// footer
            builder.addText("==========================================");
            builder.addFeedLine(1);
            builder.addText("[????????????]");
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(2);
            builder.addCut(Sam4sBuilder.CUT_FEED);

            //2??? ?????????
            builder2.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder2.addFeedLine(1);
            builder2.addTextSize(2,2);
            builder2.addText("[?????????]");
            builder2.addFeedLine(2);
            builder2.addTextSize(1,1);
            builder2.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder2.addText("[?????????] ");
            builder2.addText(printOrderModel.getTable()+" "+type);
            builder2.addFeedLine(1);
            builder2.addText("==========================================");
            builder2.addFeedLine(2);
// body
            builder2.addTextSize(2,2);
            builder2.addTextPosition(0);
            builder2.addTextBold(true);

//            builder2.addText("??? ??? ??? ???");
            builder2.addTextPosition(400);
            builder2.addTextBold(false);
            builder2.addFeedLine(2);
            builder2.addTextSize(1,1);
            builder2.addText("------------------------------------------");
            builder2.addTextSize(2,2);
            builder2.addText(order1);
            builder2.addFeedLine(1);
            builder2.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder2.addTextSize(1,1);
// footer
            builder2.addText("==========================================");
            builder2.addFeedLine(1);
            builder2.addText("[????????????]");
            builder2.addText(printOrderModel.getTime());
            builder2.addFeedLine(2);
            builder2.addCut(Sam4sBuilder.CUT_FEED);


            //3??? ?????????
            builder3.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder3.addFeedLine(1);
            builder3.addTextSize(2,2);
            builder3.addText("[?????????]");
            builder3.addFeedLine(2);
            builder3.addTextSize(1,1);
            builder3.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder3.addText("[?????????] ");
            builder3.addText(printOrderModel.getTable()+" "+type);
            builder3.addFeedLine(1);
            builder3.addText("==========================================");
            builder3.addFeedLine(2);
// body
            builder3.addTextSize(2,2);
            builder3.addTextPosition(0);
            builder3.addTextBold(true);

//            builder3.addText("??? ??? ??? ???");
            builder3.addTextPosition(400);
            builder3.addTextBold(false);
            builder3.addFeedLine(2);
            builder3.addTextSize(1,1);
            builder3.addText("------------------------------------------");
            builder3.addTextSize(2,2);
            builder3.addText(order2);
            builder3.addFeedLine(1);
            builder3.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder3.addTextSize(1,1);
// footer
            builder3.addText("==========================================");
            builder3.addFeedLine(1);
            builder3.addText("[????????????]");
            builder3.addText(printOrderModel.getTime());
            builder3.addFeedLine(2);
            builder3.addCut(Sam4sBuilder.CUT_FEED);

            /////
//            sam4sPrint.sendData(builder);

            if (printOrderModel.getTable().contains("??????") || printOrderModel.getTable().contains("??????")) {
                sam4sPrint.sendData(builder);
//                sam4sPrint2.sendData(builder);

                if (!order1.equals("")) {
                    sam4sPrint2.sendData(builder2);
                }
                if (!order2.equals("")) {
                    sam4sPrint3.sendData(builder3);
                }


                if (printOrderModel.getOrdertype().equals("card")) {
                    print2(printOrderModel);
                }
            }else {
                sam4sPrint.sendData(builder);
//                sam4sPrint2.sendData(builder);
//                sam4sPrint3.sendData(builder);

            }

            Thread.sleep(300);
            sam4sPrint.closePrinter();
            sam4sPrint2.closePrinter();
            sam4sPrint3.closePrinter();

            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    class BackThread extends Thread{  // Thread ??? ???????????? ??????????????? ??????
        @Override
        public void run() {
            while (true) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());
                SimpleDateFormat format = new SimpleDateFormat("hh-mm-ss",  Locale.getDefault());
                String status_1 = "";
                //String status_2 = "";
                //String status_3 = "";
//                time = format2.format(calendar.getTime());
                String time2 = format.format(calendar.getTime());

                SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());
                String time_ = format3.format(calendar.getTime());
                Log.d("daon_test", "time1 = "+time);
                Log.d("daon_test", "time1 = "+time_);
                if (time != null) {
                    if (!time.equals(time_)) {
                        time = time_;
                        Log.d("daon_test", "time = " + time);
                        Log.d("daon_test", "time = " + time_);
                        initFirebase();
                    }
                }
                try {
                    status_1 = app.getPrinter().getPrinterStatus()+"::"+app.getPrinter().IsConnected(Sam4sPrint.DEVTYPE_ETHERNET) + "::"+time2;
                    //status_2 = app.getPrinter2().getPrinterStatus()+"::"+app.getPrinter2().IsConnected(Sam4sPrint.DEVTYPE_ETHERNET) + "::"+time2;
                    //status_3 = app.getPrinter3().getPrinterStatus()+"::"+app.getPrinter3().IsConnected(Sam4sPrint.DEVTYPE_ETHERNET) + "::"+time2;

                    printer_status1.setText(status_1);
                    //printer_status2.setText(status_2);
                    //printer_status3.setText(status_3);
                } catch (Exception e) {
                    status_1 ="??????";
                    printer_status1.setText(status_1);
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);   // 1000ms, ??? 1??? ????????? ??????????????? ??????
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void print2(PrintOrderModel printOrderModel) throws InterruptedException {

        Sam4sPrint sam4sPrint = app.getPrinter();
        //Sam4sPrint sam4sPrint2 = app.getPrinter2();
        //Sam4sPrint sam4sPrint3 = app.getPrinter3();
        String[] orderArr = printOrderModel.getOrder().split("###");

        String order = printOrderModel.getOrder();
        order = order.replace("###", "\n\n");
        order = order.replace("##", "");
        try {
            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            // top
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(2);
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText("????????????");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[?????????]");
            builder.addFeedLine(1);
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(1);
            builder.addText("?????????????????????");
            builder.addFeedLine(1);
            builder.addText("????????? \t");
            builder.addText("555-03-01946 \t");
            builder.addText("Tel : 064-725-1200");
            builder.addFeedLine(1);
            builder.addText("????????????????????? ????????? ????????? 226 2???");
            builder.addFeedLine(1);
            // body
            builder.addText("------------------------------------------");
            builder.addFeedLine(2);
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            builder.addText(order);

//            for (int i = 0; i < orderArr.length; i++) {
//                String arrOrder = orderArr[i];
//                String[] subOrder = arrOrder.split("###");
//                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
//                builder.addText(subOrder[0]);
//                builder.addText(subOrder[1]);
//                builder.addFeedLine(1);
//                builder.addTextAlign(Sam4sBuilder.ALIGN_RIGHT);
//                builder.addText(subOrder[2]);
//                builder.addFeedLine(2);
//            }
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            // footer
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("IC??????");
            builder.addTextPosition(120);
            builder.addText("???  ??? : ");
            //builder.addTextPosition(400);
            int a = (Integer.parseInt(printOrderModel.getPrice()))/10;
            builder.addText(myFormatter.format(a*9)+"???");
            builder.addFeedLine(1);
            builder.addText("DDC?????????");
            builder.addTextPosition(120);
            builder.addText("????????? : ");
            builder.addText(myFormatter.format(a*1)+"???");
            builder.addFeedLine(1);
            builder.addTextPosition(120);
            builder.addText("???  ??? : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(printOrderModel.getPrice()))+"???");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("??????No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(printOrderModel.getAuthnum());
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addText("???????????? : ");
            builder.addText(printOrderModel.getNotice());
            builder.addFeedLine(1);
            builder.addText("??????????????? : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("?????????????????? : ");
            builder.addText(printOrderModel.getVantr());
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("???????????????.");
            builder.addCut(Sam4sBuilder.CUT_FEED);
            //sam4sPrint.sendData(builder);
            sam4sPrint.sendData(builder);
            //sam4sPrint.closePrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }
}