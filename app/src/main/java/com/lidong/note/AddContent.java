package com.lidong.note;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import assistant.lidong.com.personalassistant.R;


/*
 * Created by LiDong on 2017/4/18.
 */

public class AddContent extends AppCompatActivity implements View.OnClickListener{

    private String val;
    private Button savebtn,deletebtn;
    private EditText ettext;
    private ImageView c_img;
    private VideoView v_video;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private File phoneFile,videoFile;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_content);
        val=getIntent().getStringExtra("flag");

        savebtn=(Button) findViewById(R.id.save);
        deletebtn=(Button) findViewById(R.id.delete);
        ettext=(EditText) findViewById(R.id.ettext);
        c_img=(ImageView) findViewById(R.id.c_img);
        v_video=(VideoView) findViewById(R.id.c_video);
        savebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);

        notesDB=new NotesDB(this);
        dbWriter=notesDB.getWritableDatabase();

        initView();
    }
    public void initView(){
        if(val.equals("1")){   //添加文字，只显示文本
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.GONE);
        }
        if(val.equals("2")){            //添加图片，显示图片和文本  Android7.0调用相机方法有所变化
            c_img.setVisibility(View.VISIBLE);
            v_video.setVisibility(View.GONE);
            Intent goImg=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            phoneFile=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+getTime()+".jpg");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            Log.i("currentapiVersion","currentapiVersion====>"+currentapiVersion);
            if (currentapiVersion<24){
                goImg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(phoneFile));
                startActivityForResult(goImg, 1);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, phoneFile.getAbsolutePath());
                Uri uri = getBaseContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                goImg.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(goImg, 1);
            }
        }
        if(val.equals("3")){   //添加视频，显示视频和文本
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.VISIBLE);

            Intent goVideo=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            videoFile=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+getTime()+".mp4");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            Log.i("currentapiVersion","currentapiVersion====>"+currentapiVersion);
            if (currentapiVersion<24){
                goVideo.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                startActivityForResult(goVideo, 2);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, videoFile.getAbsolutePath());
                Uri uri = getBaseContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                goVideo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(goVideo, 2);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Bitmap bitmap= BitmapFactory.decodeFile(phoneFile.getAbsolutePath());
            c_img.setImageBitmap(bitmap);
        }
        if(requestCode==2){
            v_video.setVideoURI(Uri.fromFile(videoFile));
            v_video.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                addDB();
                finish();
                break;
            case R.id.delete:
                finish();
                break;
        }
    }

    public void addDB(){
        ContentValues cv=new ContentValues();
        cv.put(NotesDB.CONTENT,ettext.getText().toString());
        cv.put(NotesDB.TIME,getTime());
        cv.put(NotesDB.PATH,phoneFile+"");
        cv.put(NotesDB.VIDEO,videoFile+"");
        dbWriter.insert(NotesDB.TABLE_NAME,null,cv);
    }
    public String getTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date();
        String str=format.format(date);
        return str;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
       // mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
