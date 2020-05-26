package com.example.udptest;


import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class FirebaseSender {

    public static void pushFCMNotification(String userDeviceIdKey, String title, String body , int status ) throws Exception {
        String authKey = BuildConfig.NOTIFICATION_AUTHOR_KEY;   //Please add you FCM AUTH key here
        String FMCurl = "https://fcm.googleapis.com/fcm/send";

        URL url = new URL(FMCurl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization","key="+authKey);
        conn.setRequestProperty("Content-Type","application/json");

        JSONObject json = new JSONObject();
        json.put("to",userDeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", title);
        info.put("body", body);
        info.put("sound","default" );
        info.put("vibrate","true");
        json.put("data", info);
        if(status == 0) {
            json.put("notification", info);
        }
        json.put("priority","high");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(json.toString());
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + json);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }
}
