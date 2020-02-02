package com.example.udptest;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Tool {

    public Tool(Context context){
        sharedPreferences = context.getSharedPreferences("DATA",0);
    }

    SharedPreferences sharedPreferences;

    public void saveData(String key, String value){
        sharedPreferences.edit().putString(key,value).apply();
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

    public void addID(String id){

        for(int i = 0 ; i < 4 ; i++){
            String key = "Pair"+i;
            String s = sharedPreferences.getString(key ,"");
            if(s == ""){
                saveData( key , id);
                Log.d("save token "+ i  ,id);
                break;
            }
        }
    }
}
