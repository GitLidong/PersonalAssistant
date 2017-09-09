package com.lidong.note;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import assistant.lidong.com.personalassistant.R;

/**
 * Created by LiDong on 2017/4/18.
 */

public class NoteActivity extends AppCompatActivity{

    private Button btnGotoWrite,btnGotoCamer,btnGotoVideo;
    private ListView listNote;
    private Intent i;
    private NoteAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase dbReader;
    Cursor cursor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
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
    private void initView(){
        btnGotoWrite= (Button) findViewById(R.id.btnGotoWrite);
        btnGotoCamer= (Button) findViewById(R.id.btnGotoCamer);
        btnGotoVideo= (Button) findViewById(R.id.btnGotoVideo);
        btnGotoWrite.setOnClickListener(btnClicked);
        btnGotoCamer.setOnClickListener(btnClicked);
        btnGotoVideo.setOnClickListener(btnClicked);

        listNote= (ListView) findViewById(R.id.listNote);
        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent i=new Intent(NoteActivity.this,SelectAty.class);
                i.putExtra(NotesDB.ID,cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT,cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME,cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                i.putExtra(NotesDB.PATH,cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                i.putExtra(NotesDB.VIDEO,cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                startActivity(i);
            }
        });

        notesDB=new NotesDB(this);
        dbReader=notesDB.getReadableDatabase();
    }
    View.OnClickListener btnClicked=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            i=new Intent(NoteActivity.this,AddContent.class);
            switch (view.getId()){
                case R.id.btnGotoWrite:
                    i.putExtra("flag","1");
                    startActivity(i);
                    break;
                case R.id.btnGotoCamer:
                    i.putExtra("flag","2");
                    startActivity(i);
                    break;
                case R.id.btnGotoVideo:
                    i.putExtra("flag","3");
                    startActivity(i);
                    break;
            }
        }
    };
    public void selectDB(){
        cursor=dbReader.query(NotesDB.TABLE_NAME,null,null,null,null,null,null);
        adapter=new NoteAdapter(this,cursor);
        listNote.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
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
}
