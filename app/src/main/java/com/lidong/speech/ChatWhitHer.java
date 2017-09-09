package com.lidong.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.List;


import assistant.lidong.com.personalassistant.R;

import static android.R.attr.delay;

/*
 * Created by LiDong on 2017/4/17.
 */

public class ChatWhitHer extends AppCompatActivity {

    private ListView listSpeechText;
    private ImageButton btnStartSpeech, btnSpeechTip;
    private MsgAdapter msgAdapter;
    private List<Msg> msgList;
    private List<PackageInfo> installedAppInfoList;
    public static List<AppInfo> appInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_with_her);
        setMyBack();
        initView();

    }

    private void setMyBack() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        listSpeechText = (ListView) findViewById(R.id.listSpeechText);
        btnStartSpeech = (ImageButton) findViewById(R.id.btnStartSpeech);
        btnSpeechTip = (ImageButton) findViewById(R.id.btnSpeechTip);
        btnStartSpeech.setOnClickListener(btnClicked);

        initInstalledAppInfo();

        msgList = new ArrayList<>();
        Msg msg = new Msg("Hello", Msg.TYPE_RECEIVED);
        msgList.add(msg);

        msgAdapter = new MsgAdapter(this, R.layout.chat_content, msgList);
        listSpeechText.setAdapter(msgAdapter);

        speekText(msg);
    }

    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startSpeechDialog();
        }
    };

    private void startSpeechDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, mInitListener);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解//结果
        mDialog.setParameter("asr_sch", "1");
        mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(mRecognizerDialogListener);
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                showTip("初始化失败");
            }
        }
    };
    RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            writerResultToMsgList(recognizerResult);
            Log.i("result info", result);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };

    private void showTip(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }

    private void writerResultToMsgList(RecognizerResult results) {
        List<Msg> tempList = JsonParser.dealResult(results.getResultString());
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getContent().equals("")) {
                msgList.add(new Msg("好像听不懂", Msg.TYPE_RECEIVED));
            }
            else if (tempList.get(i).getType() == Msg.TYPE_RECEIVED_OPENAPP) {
                String appName = tempList.get(i).getContent();
                if (indexOfThisApp(appName) != -1) {
                    msgList.add(new Msg("正在打开" + tempList.get(i).getContent(), Msg.TYPE_RECEIVED_OPENAPP));
                } else {
                    msgList.add(new Msg("未找到该应用", Msg.TYPE_RECEIVED_OPENAPP));
                }
            }
            else if (tempList.get(i).getType() == Msg.TYPE_RECEIVED_CALL) {
                if(getContactPhoneNumFromName(tempList.get(i).getContent()) != null){
                    msgList.add(new Msg("正在打电话给" + tempList.get(i).getContent(), Msg.TYPE_RECEIVED));
                }
                else {
                    msgList.add(new Msg("未找到联系人或联系人号码为空" , Msg.TYPE_RECEIVED));
                }

            }
            else if(tempList.get(i).getType() == Msg.TYPE_RECEIVED_MESSAGE){
                String [] myMessage=tempList.get(i).getContent().split("&");
                String msgName=myMessage[0];
                String msgContent=myMessage[1];
                String msgNum=getContactPhoneNumFromName(msgName);
                if(msgNum != null){
                    msgList.add(new Msg("正在发短信给" + msgName, Msg.TYPE_RECEIVED));
                }
                else {
                    msgList.add(new Msg("未找到联系人或联系人号码为空" , Msg.TYPE_RECEIVED));
                }
                Log.i("TEST",msgName+msgContent+msgNum);
            }
            else if(tempList.get(i).getType() == Msg.TYPE_RECEIVED_CALENDAR){

            }
            else {
                msgList.add(tempList.get(i));
            }
        }
        msgAdapter.notifyDataSetChanged();
        listSpeechText.smoothScrollByOffset(2);

        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getType() == Msg.TYPE_RECEIVED &&
                    tempList.get(i).getType() != Msg.TYPE_RECEIVED_WEB) {
                speekText(tempList.get(i));
            }
            else if (tempList.get(i).getType() == Msg.TYPE_RECEIVED_OPENAPP) {
                String appName = tempList.get(i).getContent();
                if (indexOfThisApp(appName) != -1) {
                    final String appPackageName = appInfoList.get(indexOfThisApp(appName)).getAppPackageName();
                    Log.i("LiDong->>ChatWithHer", appPackageName);
                    speekText(new Msg("正在打开" + appName, Msg.TYPE_RECEIVED_OPENAPP));
                    //将打开其他应用延迟1500ms处理
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doStartApplicationWithPackageName(appPackageName);
                        }
                    }, 1500);
                } else {
                    speekText(new Msg("未找到该应用", Msg.TYPE_RECEIVED_OPENAPP));
                }
            }
            else if (tempList.get(i).getType() == Msg.TYPE_RECEIVED_CALL) {
                String phoneName = tempList.get(i).getContent();
                final String phoneNum = getContactPhoneNumFromName(phoneName);
                if (phoneNum != null) {
                    speekText(new Msg("正在打电话给" + tempList.get(i).getContent(), Msg.TYPE_RECEIVED));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                            if (ActivityCompat.checkSelfPermission(ChatWhitHer.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);
                        }
                    }, 2000);

                }else{
                    speekText(new Msg("未找到联系人或联系人号码为空" , Msg.TYPE_RECEIVED));
                }
            }
            else if(tempList.get(i).getType() == Msg.TYPE_RECEIVED_MESSAGE){
                String [] myMessage=tempList.get(i).getContent().split("&");
                String msgName=myMessage[0];
                String msgContent=myMessage[1];
                String msgNum=getContactPhoneNumFromName(msgName);
                if(msgNum != null){
                   speekText(new Msg("正在发短信给" + msgName, Msg.TYPE_RECEIVED));
                }
                else {
                    speekText(new Msg("未找到联系人或联系人号码为空" , Msg.TYPE_RECEIVED));
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+msgNum));
                intent.putExtra("sms_body", msgContent);
                startActivity(intent);
            }
        }

    }
    /*
     * speekText用于将文字转换成语音并播放
     */
    private void speekText(Msg msg) {
        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer( this, null);
        //2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
        //设置发音人（更多在线发音人，用户可参见 附录 13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan" ); // 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50" );// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80" );// 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant. TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在 “./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant. TTS_AUDIO_PATH, "./sdcard/iflytek.pcm" );
        //3.开始合成
        mTts.startSpeaking( msg.getContent(), mySynthesizerListener) ;
    }
    private SynthesizerListener mySynthesizerListener=new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private void initInstalledAppInfo(){
        appInfoList=new ArrayList<>();
        PackageManager packageManager=getPackageManager();
        installedAppInfoList=packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo packageInfo:installedAppInfoList){
            String appPackageName=packageInfo.packageName;
            String appName= (String) packageInfo.applicationInfo.loadLabel(packageManager);
            AppInfo appInfo=new AppInfo(appPackageName,appName);
            appInfoList.add(appInfo);
        }
        for(AppInfo temp:appInfoList){
            Log.i("lidong->>>>>>>appInfo",temp.getAppPackageName()+"-------"+temp.getAppName());
        }
    }
    /*
     *   返回该appName在appinfolist中对应的index
     *   没有则返回-1
     */
    private int indexOfThisApp(String appName){
        for(AppInfo temp:appInfoList){
            if(temp.getAppName().equals(appName)){
                return appInfoList.indexOf(temp);
            }
        }
        return -1;
    }
    /*
        根据包名启动应用
     */
    private void doStartApplicationWithPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }
    /*
    根据姓名获取电话号码
     */
    private String getContactPhoneNumFromName(String name) {
        //使用ContentResolver查找联系人数据
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //遍历查询结果，找到所需号码
        while (cursor.moveToNext()) {
            //获取联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人的名字
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (name.equals(contactName)) {
                //使用ContentResolver查找联系人的电话号码
                Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    return phoneNumber;
                }
            }
        }
        return null;
    }
    /*
    添加行程
     */
    private void addCalendar(String time,String date,String content){
        String calanderURL = "content://com.android.calendar/calendars";
        String calanderEventURL = "content://com.android.calendar/events";
        String calanderRemiderURL = "content://com.android.calendar/reminders";


    }
}
