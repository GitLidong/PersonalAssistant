package assistant.lidong.com.personalassistant;

import android.content.Intent;
import android.os.Handler;

import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;



public class StartActivity extends AppCompatActivity {

    String [] showList={"魅力软件","升级无限","编译现在","规划未来"};
    TextView startText;
    Handler handler;
    int textPoint=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        this.setTitle("欢迎使用个人助理");

        startText= (TextView) findViewById(R.id.startText);
        startText.setText(showList[0]+"\n\n");

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

         handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    startText.append(showList[textPoint-1]+"\n\n");
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new DealTextThread()).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
                StartActivity.this.finish();
            }
        },3000);
    }

    class DealTextThread extends Thread{
        @Override
        public void run() {
            while (textPoint<4){
                try {
                    Thread.sleep(700);
                    Message msg=new Message();
                    msg.what=1;
                    handler.sendMessage(msg);
                    textPoint++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
