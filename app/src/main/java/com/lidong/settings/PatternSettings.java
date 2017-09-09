package com.lidong.settings;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import assistant.lidong.com.personalassistant.R;

public class PatternSettings extends Activity implements LockPatternView.OnPatternChangeListener{

    private String TAG="MainActivity";
    private  String firstUserPattern,secondeUserPattern;
    private TextView mLockPatternHint;
    private LockPatternView mLockPatternView;
    private Button btnPatternGoon,btnPatternOk,btnPatternCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_settings);

        mLockPatternHint = (TextView) findViewById(R.id.lock_pattern_hint);
        mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
        mLockPatternView.setOnPatternChangeListener(this);

        firstUserPattern=null;

        initView();
    }
    private void initView(){
        btnPatternCancel= (Button) findViewById(R.id.btnPatternCancel);
        btnPatternGoon= (Button) findViewById(R.id.btnPatternGoon);
        btnPatternOk= (Button) findViewById(R.id.btnPatternOk);
        btnPatternOk.setOnClickListener(btnClicked);
        btnPatternGoon.setOnClickListener(btnClicked);
        btnPatternOk.setOnClickListener(btnClicked);
    }
    View.OnClickListener btnClicked=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnPatternCancel:
                    finish();
                    break;
                case R.id.btnPatternGoon:
                    btnPatternGoon.setVisibility(View.INVISIBLE);
                    btnPatternOk.setVisibility(View.VISIBLE);
                    mLockPatternView.clearPattern();
                    mLockPatternView.invalidate();
                    break;
                case R.id.btnPatternOk:
                    Log.i(TAG,firstUserPattern+ "  "+secondeUserPattern);
                    if(firstUserPattern.equals(secondeUserPattern)){
                        MyKey.patternLock=true;
                        MyKey.patternPassword=firstUserPattern;
                        Toast.makeText(PatternSettings.this,"图案绘制成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        mLockPatternHint.setText("两次图案不相符");
                    }
                    break;
            }
        }
    };

    @Override
    public void onPatternChange(String patternPassword) {
        if (patternPassword == null) {
            mLockPatternHint.setText("至少5个点");
        } else {
            mLockPatternHint.setText(patternPassword);
            makeBtnClickable(patternPassword);
        }
    }

    @Override
    public void onPatternStarted(boolean isStarted) {
        if (isStarted) {
            mLockPatternHint.setText("请绘制图案");
        }
    }
    private void makeBtnClickable(String patternPassword){
        if(btnPatternGoon.getVisibility()==View.VISIBLE){
            firstUserPattern=patternPassword;
            btnPatternGoon.setClickable(true);
            Log.i(TAG,"btnPatternGoon clickadle");
        }
        if(btnPatternOk.getVisibility()==View.VISIBLE){
            secondeUserPattern=patternPassword;
            btnPatternOk.setClickable(true);
            Log.i(TAG,"btnPatternOk clickadle");
        }
    }
}
