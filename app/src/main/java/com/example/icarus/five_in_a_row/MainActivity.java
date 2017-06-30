package com.example.icarus.five_in_a_row;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MainActivity extends AppCompatActivity {

    private Button btn1,btn2;
    private BmobUtils bmobUtils =new BmobUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBmob();

        requestPermission();

        initViews();


    }

    private void initBmob() {
        Bmob.initialize(MainActivity.this, "3480a490b932f40256204b2142d74ea5");
// 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
// 启动推送服务
        BmobPush.startWork(this);
        GameInfo.myId = (int)(Math.random()*1000)+"";
        bmobUtils.initPlayer(MainActivity.this);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE},0);
        }
    }

    private void initViews() {
        btn1 = (Button) findViewById(R.id.main_btn_1);
        btn2 = (Button) findViewById(R.id.main_btn_2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameInfo.isPalyWithAi = true;
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        final EditText editText = new EditText(this);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup p = (ViewGroup) editText.getParent();
                if (p!=null){
                    p.removeView(editText);
                }
                editText.setText("");
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入房间号码").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String roomNum = editText.getText().toString();
                        bmobUtils.searchRoom(roomNum);
                        Intent intent = new Intent(MainActivity.this,GameActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        initBmob();
    }
}
