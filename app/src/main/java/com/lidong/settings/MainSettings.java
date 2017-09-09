package com.lidong.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidong.mail.SendEmail;

import assistant.lidong.com.personalassistant.R;

public class MainSettings extends AppCompatActivity {

    private Button btnUserSettings;
    private Button button3,button4;
    private Button button,button2;
    private EditText editText3;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);
        initView();
    }

    private void initView() {
        btnUserSettings= (Button) findViewById(R.id.radioButton2);
        btnUserSettings.setOnClickListener(btnClicked);

        button3= (Button) findViewById(R.id.button3);
        button3.setOnClickListener(btnClicked);

        button4= (Button) findViewById(R.id.button4);
        button4.setOnClickListener(btnClicked);

        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(btnClicked);

        button2= (Button) findViewById(R.id.button2);
        button2.setOnClickListener(btnClicked);


    }
    View.OnClickListener btnClicked =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.radioButton2:
                    Intent intent=new Intent(MainSettings.this, PatternSettings.class);
                    startActivity(intent);
                    break;
                case R.id.button3:
                    AboutDialog();
                    break;
                case R.id.button:
                    SendSug();
                    break;
                case R.id.button4:
                    UpdateInfo();
                    break;
                case R.id.button2:
                    Tuijian();
                    break;
            }

        }
    };
    private void AboutDialog(){
        final AlertDialog.Builder dialog= new AlertDialog.Builder(MainSettings.this);
        dialog.setTitle("关于"+"\n"+"个人助理：大连理工大学-李栋");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
    private void SendSug(){
        final AlertDialog.Builder dialog= new AlertDialog.Builder(MainSettings.this);
        dialog.setTitle("反馈建议");
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.layout,null);
        editText3= (EditText) view.findViewById(R.id.editText3);
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                content=editText3.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendEmail.send("952307596@qq.com",content);
                    }
                }).start();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
    private void UpdateInfo(){
        final AlertDialog.Builder dialog= new AlertDialog.Builder(MainSettings.this);
        dialog.setTitle("修改信息");
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.update_info,null);
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }

    private void Tuijian(){
        final AlertDialog.Builder dialog= new AlertDialog.Builder(MainSettings.this);
        dialog.setTitle("推荐方式");
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.tuijian_layout,null);
        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
}
