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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        BackThread thread = new BackThread();  // 작업스레드 생성
        thread.setDaemon(true);  // 메인스레드와 종료 동기화
        thread.start();

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
                Toast.makeText(MainActivity.this, "준비중 입니다.", Toast.LENGTH_SHORT).show();

            }
        });
        menuBtn = findViewById(R.id.menu3);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
//                startActivity(intent);
//                finish();
                Toast.makeText(MainActivity.this, "준비중 입니다.", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(MainActivity.this, "준비중 입니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "준비중 입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

        Sam4sPrint sam4sPrint = app.getPrinter();
        Sam4sPrint sam4sPrint2 = app.getPrinter2();
        Sam4sPrint sam4sPrint3 = app.getPrinter3();

        time = format2.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename", "")).child(time).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    PrintOrderModel printOrderModel = item.getValue(PrintOrderModel.class);
                    Log.d("daon_test", "print = " + item.getKey());
                    try {

                            if (printOrderModel.getPrintStatus().equals("x")) {
//                                if (sam4sPrint.getPrinterStatus() != null && sam4sPrint2.getPrinterStatus() != null && sam4sPrint3.getPrinterStatus() != null) {
                                    print(printOrderModel);
                                    printOrderModel.setPrintStatus("o");
                                    FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename", "")).child(time).child(item.getKey()).setValue(printOrderModel);
//                                }else {
//
//                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
//                                    mp.start();
//                                }
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
                            if (sam4sPrint.getPrinterStatus() != null && sam4sPrint2.getPrinterStatus() != null && sam4sPrint3.getPrinterStatus() != null) {

                                print(printOrderModel);
                                printOrderModel.setPrintStatus("o");
                                FirebaseDatabase.getInstance().getReference().child("service").child(pref.getString("storename", "")).child(time).child(item.getKey()).setValue(printOrderModel);
                            }else{
                                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
                                mp.start();
                            }
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

    public void print(PrintOrderModel printOrderModel){
        Log.d("daon_test = ", printOrderModel.getOrder());
        String[] orderArr = printOrderModel.getOrder().split("###");
        Log.d("daon_test", orderArr[0]);

        String order = printOrderModel.getOrder();
        order = order.replace("###", "\n\n");
        order = order.replace("##", "");
        order = order.replace("해 주세요", "");
        order = order.replace(" 주세요", "");
        order = order.replace("주세요", "");
        String order1 = "";
        String order2 = "";
        if (printOrderModel.getTable().contains("호출")){
            order = printOrderModel.getTable();
        }
        for (int i = 0; i < orderArr.length; i++){
            if (orderArr[i].contains("돈까스") || orderArr[i].contains("볶음밥") || orderArr[i].contains("군만두") || orderArr[i].contains("탕수육") ||
                    orderArr[i].contains("수제비") || orderArr[i].contains("짬뽕밥") || orderArr[i].contains("차돌")){
                if (!orderArr[i].equals("\n\n")) {

                    order2 = order2 + orderArr[i];
                    order2 = order2.replace("###", "\n\n");
                    order2 = order2.replace("##", "");
                    order2 = order2.replace("개", "개\n");
                    order2 = order2.replace("해 주세요", "");
                    order2 = order2.replace(" 주세요", "");
                    order2 = order2.replace("주세요", "");
                }

            }
            if (orderArr[i].contains("짬뽕") || orderArr[i].contains("짜장면") || orderArr[i].contains("짬짜면") || orderArr[i].contains("밀면")){
                Log.d("daon_test", "aaaaaa"+orderArr[i]+"bbb");
                if (!orderArr[i].equals("\n\n")) {
                    order1 = order1 + orderArr[i];
                    order1 = order1.replace("###", "\n\n");
                    order1 = order1.replace("##", "");
                    order1 = order1.replace("개", "개\n");
                    order1 = order1.replace("해 주세요", "");
                    order1 = order1.replace(" 주세요", "");
                    order1 = order1.replace("주세요", "");
                }
            }
        }

        Sam4sPrint sam4sPrint = app.getPrinter();
        Sam4sPrint sam4sPrint2 = app.getPrinter2();
        Sam4sPrint sam4sPrint3 = app.getPrinter3();
        Log.d("daon_test0", order);
        Log.d("daon_test1", order1);
        Log.d("daon_test2", order2);
        try {
            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        Sam4sBuilder builder2 = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        Sam4sBuilder builder3 = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            String type = "(카드)";
            if (printOrderModel.getOrdertype().equals("cash")){
                type = "(현금)";
            }
            if (printOrderModel.getTable().contains("호출")){
                type = "";
            }
            // 1번 프린터
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(1);
            builder.addTextSize(2,2);
            builder.addText("[주문서]");
            builder.addFeedLine(2);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[테이블] ");
            builder.addText(printOrderModel.getTable()+" "+type);
            builder.addFeedLine(1);
            builder.addText("==========================================");
//            builder.addFeedLine(2);
// body
            builder.addTextSize(2,2);
            builder.addTextPosition(0);
            builder.addTextBold(true);

//            builder.addText("주 문 내 역");
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
            builder.addText("[주문시간]");
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(2);
            builder.addCut(Sam4sBuilder.CUT_FEED);


            //2번 프린터
            builder2.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder2.addFeedLine(1);
            builder2.addTextSize(2,2);
            builder2.addText("[주문서]");
            builder2.addFeedLine(2);
            builder2.addTextSize(1,1);
            builder2.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder2.addText("[테이블] ");
            builder2.addText(printOrderModel.getTable()+" "+type);
            builder2.addFeedLine(1);
            builder2.addText("==========================================");
            builder2.addFeedLine(2);
// body
            builder2.addTextSize(2,2);
            builder2.addTextPosition(0);
            builder2.addTextBold(true);

//            builder2.addText("주 문 내 역");
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
            builder2.addText("[주문시간]");
            builder2.addText(printOrderModel.getTime());
            builder2.addFeedLine(2);
            builder2.addCut(Sam4sBuilder.CUT_FEED);


            //3번 프린터
            builder3.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder3.addFeedLine(1);
            builder3.addTextSize(2,2);
            builder3.addText("[주문서]");
            builder3.addFeedLine(2);
            builder3.addTextSize(1,1);
            builder3.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder3.addText("[테이블] ");
            builder3.addText(printOrderModel.getTable()+" "+type);
            builder3.addFeedLine(1);
            builder3.addText("==========================================");
            builder3.addFeedLine(2);
// body
            builder3.addTextSize(2,2);
            builder3.addTextPosition(0);
            builder3.addTextBold(true);

//            builder3.addText("주 문 내 역");
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
            builder3.addText("[주문시간]");
            builder3.addText(printOrderModel.getTime());
            builder3.addFeedLine(2);
            builder3.addCut(Sam4sBuilder.CUT_FEED);

            /////
//            sam4sPrint.sendData(builder);

            if (printOrderModel.getTable().contains("주문") || printOrderModel.getTable().contains("포장")) {
                sam4sPrint.sendData(builder);
                Log.d("daon_test0", order);
                Log.d("daon_test1", order1);
                Log.d("daon_test2", order2);
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
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class BackThread extends Thread{  // Thread 를 상속받은 작업스레드 생성
        @Override
        public void run() {
            while (true) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

                time = format2.format(calendar.getTime());
                Log.d("daon_test", "time = "+time);
                try {
                    Thread.sleep(60000);   // 1000ms, 즉 1초 단위로 작업스레드 실행
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void print2(PrintOrderModel printOrderModel){

        Sam4sPrint sam4sPrint = app.getPrinter();
        Sam4sPrint sam4sPrint2 = app.getPrinter2();
        Sam4sPrint sam4sPrint3 = app.getPrinter3();
        String[] orderArr = printOrderModel.getOrder().split("###");
        Log.d("daon_test", orderArr[0]);

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
            builder.addText("신용매출");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[고객용]");
            builder.addFeedLine(1);
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(1);
            builder.addText("돈짬제주시청점");
            builder.addFeedLine(1);
            builder.addText("김정화 \t");
            builder.addText("555-03-01946 \t");
            builder.addText("Tel : 064-725-1200");
            builder.addFeedLine(1);
            builder.addText("제주특별자치도 제주시 중앙로 226 2층");
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
            builder.addText("IC승인");
            builder.addTextPosition(120);
            builder.addText("금  액 : ");
            //builder.addTextPosition(400);
            int a = (Integer.parseInt(printOrderModel.getPrice()))/10;
            builder.addText(myFormatter.format(a*9)+"원");
            builder.addFeedLine(1);
            builder.addText("DDC매출표");
            builder.addTextPosition(120);
            builder.addText("부가세 : ");
            builder.addText(myFormatter.format(a*1)+"원");
            builder.addFeedLine(1);
            builder.addTextPosition(120);
            builder.addText("합  계 : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(printOrderModel.getPrice()))+"원");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("승인No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(printOrderModel.getAuthnum());
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addText("매입사명 : ");
            builder.addText(printOrderModel.getNotice());
            builder.addFeedLine(1);
            builder.addText("가맹점번호 : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("거래일련번호 : ");
            builder.addText(printOrderModel.getVantr());
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("감사합니다.");
            builder.addCut(Sam4sBuilder.CUT_FEED);
            //sam4sPrint.sendData(builder);
            sam4sPrint.sendData(builder);
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