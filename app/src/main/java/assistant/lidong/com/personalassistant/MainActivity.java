package assistant.lidong.com.personalassistant;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.iflytek.cloud.SpeechConstant;

import com.iflytek.cloud.SpeechUtility;
import com.lidong.note.NoteActivity;
import com.lidong.settings.MainSettings;
import com.lidong.speech.ChatWhitHer;

public class MainActivity extends AppCompatActivity {

    private Button btnGotoSpeech;
    private Button btnGotoNote;
    private Button btnExit;
    private Button btnGotoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSpeech();
        initView();
    }

    private void initSpeech() {
        // 请勿在 “ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility( this, SpeechConstant.APPID + "=58ec6254" );
    }

    private void initView() {
        btnGotoSpeech= (Button) findViewById(R.id.btnGotoSpeech);
        btnGotoSpeech.setOnClickListener(btnClicked);

        btnGotoNote= (Button) findViewById(R.id.btnGotoNote);
        btnGotoNote.setOnClickListener(btnClicked);

        btnExit= (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(btnClicked);

        btnGotoSettings= (Button) findViewById(R.id.btnGotoSettings);
        btnGotoSettings.setOnClickListener(btnClicked);
    }

    View.OnClickListener btnClicked=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnGotoSpeech:
                    Intent intent=new Intent(MainActivity.this, ChatWhitHer.class);
                    startActivity(intent);
                    break;
                case R.id.btnGotoNote:
                    Intent intentNote=new Intent(MainActivity.this, NoteActivity.class);
                    startActivity(intentNote);
                    break;
                case R.id.btnExit:
                    exitDialog();
                    break;
                case R.id.btnGotoSettings:
                    Intent intent1=new Intent(MainActivity.this, MainSettings.class);
                    startActivity(intent1);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*
        这样设计的目的是类似于微信启动，启动以后按back返回后，再次打开应用是不再加载启动界面
        而是直接打开主界面
        当应用进程被小会后，再次打开应用才会再次加载启动界面
         */
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_toolbar_item4:
                exitDialog();
                break;
            case R.id.main_toolbar_item1:
                break;
            case R.id.main_toolbar_item2:
                break;
            case R.id.main_toolbar_item3:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exitDialog(){
        final AlertDialog.Builder dialog= new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("即将退出");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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
