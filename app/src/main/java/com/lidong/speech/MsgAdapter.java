package com.lidong.speech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import assistant.lidong.com.personalassistant.R;


/**
 * Created by LiDong on 2017/4/17.
 */

public class MsgAdapter extends ArrayAdapter<Msg>{
    private int viewId;
    MsgAdapter(Context context, int viewId, List<Msg> objects){
        super(context,viewId,objects);
        this.viewId=viewId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(viewId,null);
            viewHolder=new ViewHolder();
            viewHolder.leftLayout=(LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout=(LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg=(TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg=(TextView) view.findViewById(R.id.right_msg);
            viewHolder.leftWebView= (WebView) view.findViewById(R.id.left_Webview);
            view.setTag(viewHolder);
        }
        else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        if(msg.getType()==Msg.TYPE_RECEIVED  || msg.getType()==Msg.TYPE_RECEIVED_OPENAPP){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftWebView.setVisibility(View.INVISIBLE);
            viewHolder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType()==Msg.TYPE_SENT){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
        }
        else if(msg.getType()== Msg.TYPE_RECEIVED_WEB){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.INVISIBLE);
            viewHolder.leftMsg.setVisibility(View.INVISIBLE);
            viewHolder.leftWebView.getSettings().setJavaScriptEnabled(true);
            viewHolder.leftWebView.loadUrl(msg.getContent());
        }
        return view;
    }

    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        WebView leftWebView;
    }

}
