package com.lidong.speech;


import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by LiDong on 2017/4/17.
 */

public class JsonParser {

    /*
    public static List<Msg> parseIatResult(String json) {
        List<Msg> msgList=new ArrayList<>();
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            String textString= (String) joResult.get("text");
            Log.i("JsonParser",textString);

            Msg question=new Msg(textString,Msg.TYPE_SENT);
            msgList.add(question);
            if (joResult.has("webPage")){
                JSONObject jsonObjectAnswer= (JSONObject) joResult.get("webPage");
                String answerUrl= (String) jsonObjectAnswer.get("url");
                Msg answer=new Msg(answerUrl,Msg.TYPE_RECEIVED_WEB);
                msgList.add(answer);
                Log.i("JsonParser",answerUrl);
            }
            else {
                JSONObject jsonObjectAnswer=joResult.getJSONObject("answer");
                String answerString= (String) jsonObjectAnswer.get("text");
                Msg answer = new Msg(answerString, Msg.TYPE_RECEIVED);
                msgList.add(answer);
                Log.i("JsonParser",answerString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("【置信度】" + obj.getInt("sc"));
                    ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

    public static String parseLocalGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("\n");
                }
            }
            ret.append("【置信度】" + joResult.optInt("sc"));

        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }
*/
    public static List<Msg> dealResult(String result){
        List<Msg> msgList=new ArrayList<>();
        try{
            JSONTokener jsonTokener=new JSONTokener(result);
            JSONObject jsonObject=new JSONObject(jsonTokener);
            //获取提问的问题
            String question= (String) jsonObject.get("text");
            Msg msgQuestion=new Msg(question,Msg.TYPE_SENT);
            Log.i("LiDong->>>question",question);
            msgList.add(msgQuestion);

            //获取给出的 operation和 service
            String operation= (String) jsonObject.get("operation");
            String service= (String) jsonObject.get("service");

            //根据不同的 operation 和 service 加载添加不同的信息
            if(operation.equals("ANSWER")){
                JSONObject jsonAnswer=jsonObject.getJSONObject("answer");
                String answer= (String) jsonAnswer.get("text");
                Log.i("LiDong->>>answer",answer);
                Msg msgAnswer=new Msg(answer,Msg.TYPE_RECEIVED);
                msgList.add(msgAnswer);
            }
            else if(operation.equals("QUERY") && service.equals("weather")){
                JSONObject jsonWebPage=jsonObject.getJSONObject("webPage");
                String webUrl=jsonWebPage.getString("url");
                Log.i("LiDong->>>webUrl",webUrl);
                Msg msgWebUrl=new Msg(webUrl,Msg.TYPE_RECEIVED_WEB);
                msgList.add(msgWebUrl);
            }
            else if(operation.equals("PLAY") && service.equals("music")){
                JSONObject jsonWebPage=jsonObject.getJSONObject("webPage");
                String webUrl=jsonWebPage.getString("url");
                Log.i("LiDong->>>webUrl",webUrl);
                Msg msgWebUrl=new Msg(webUrl,Msg.TYPE_RECEIVED_WEB);
                msgList.add(msgWebUrl);
            }
            else if(operation.equals("LAUNCH") && service.equals("app")){
                JSONObject semantic=jsonObject.getJSONObject("semantic");
                JSONObject slots=semantic.getJSONObject("slots");
                String appName= (String) slots.get("name");
                Log.i("LiDong->>>openApp",appName);
                Msg msgOpenApp=new Msg(appName,Msg.TYPE_RECEIVED_OPENAPP);
                msgList.add(msgOpenApp);
            }
            else if(operation.equals("CALL") && service.equals("telephone")){
                JSONObject semantic=jsonObject.getJSONObject("semantic");
                JSONObject slots=semantic.getJSONObject("slots");
                String callName= (String) slots.get("name");
                Log.i("LiDong->>>make call",callName);
                Msg msgMakeCall=new Msg(callName,Msg.TYPE_RECEIVED_CALL);
                msgList.add(msgMakeCall);
            }
            else if(operation.equals("SEND") && service.equals("message")){
                JSONObject semantic=jsonObject.getJSONObject("semantic");
                JSONObject slots=semantic.getJSONObject("slots");
                String smsName= (String) slots.get("name");
                String smsContent= (String) slots.get("content");
                Msg msgSendMessage=new Msg(smsName+"&"+smsContent,Msg.TYPE_RECEIVED_MESSAGE);
                msgList.add(msgSendMessage);
            }
            else if(operation.equals("CREATE") && service.equals("schedule")){
                JSONObject semantic=jsonObject.getJSONObject("semantic");
                JSONObject slots=semantic.getJSONObject("slots");
                String remindContent= (String) slots.get("content");
                JSONObject datetime=slots.getJSONObject("datetime");
                String remindTime= (String) datetime.get("time");
                String remindDate= (String) datetime.get("date");

                Msg schedule=new Msg(remindContent+"&"+remindTime+"&"+remindDate,Msg.TYPE_RECEIVED_CALENDAR);
                msgList.add(schedule);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return msgList;
    }

}
