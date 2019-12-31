package com.example.udptest;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Tool {

    public Tool(Context context){
        sharedPreferences = context.getSharedPreferences("DATA",0);
    }

    SharedPreferences sharedPreferences;

    public void saveData(String key, String value){
        String ret = sharedPreferences.getString(key,"");
        if(!ret.equals(value)){
            sharedPreferences.edit().putString(key,value).apply();
        }

    }

    public String getData(String key){
        String value = sharedPreferences.getString(key,"");
        return value;
    }

    public void setNotification(Context context, String title, String content){
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        int defaults = 0;
        defaults |= Notification.DEFAULT_SOUND;

        builder.setSmallIcon(R.drawable.mwnlpairicon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo("3")
                .setAutoCancel(true)
                .setDefaults(defaults)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(2 , builder.build());

    }

    public void addID(String title, String id){
        int e = 0;
        String s = sharedPreferences.getString(title,"");
        String[] tokens = s.split(";");
        for (String token:tokens){
            if(token.length() > 0) { //紀錄裡已有配對
                if(id.equals(token)) { e++; //配對完成 }
                }else{ //無配對
                    s =  s + id + ";";
                    saveData(title,s);
                    e++;
                }
            }
            if (e == 0){ // 這個沒配對過
                s =  s + id + ";";
                saveData(title,s);
            }
        }

    }
}
