package com.example.udptest;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Tool tool = new Tool(this);
        tool.saveData("Token",token);
        System.out.println("FCM"+ "Token:"+token);
    }
}
