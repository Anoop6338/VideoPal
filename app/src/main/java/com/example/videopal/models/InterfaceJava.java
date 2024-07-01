package com.example.videopal.models;

import android.webkit.JavascriptInterface;

import com.example.videopal.activities.CallActivity;

public class InterfaceJava {
    CallActivity callActivity;

    public InterfaceJava(CallActivity callActivity){
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();

    }
}
