package com.example.videopal.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videopal.R;
import com.example.videopal.models.InterfaceJava;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {
    WebView webView;
    String uniqueId ="";
    String username ="";
    String friendsUsername ="";

    boolean isPeerConnected = false;
    boolean isAudio =true;
    boolean isVideo =true;
    String createdBy;
    boolean pageExit= false;
    DatabaseReference databaseRef;
    FirebaseAuth auth;

    ImageView end_btn, mic_btn, video_btn;
//    WebView controlsView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call);

        webView = findViewById(R.id.webView);
        mic_btn = findViewById(R.id.mic_btn);
        end_btn = findViewById(R.id.end_btn);
        video_btn = findViewById(R.id.video_btn);
//        controlsView = findViewById(R.id.webView);




        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

        if (incoming != null && incoming.equalsIgnoreCase(friendsUsername)) {
            friendsUsername = incoming;
        }
        setUpWebView();


        mic_btn.setOnClickListener(new View.OnClickListener() {     // for mic button
            @Override
            public void onClick(View view) {
                isAudio =! isAudio;     //this sets false -> true and true -> false
                callJavaScriptFunction("javascript:toggleAudio(\""+isAudio+"\") ");
                if(isAudio){
                    mic_btn.setImageResource(R.drawable.btn_unmute_normal);
                }
                else{
                    mic_btn.setImageResource(R.drawable.btn_mute_normal);

                }
            }
        });


        video_btn.setOnClickListener(new View.OnClickListener() { // for video button
            @Override
            public void onClick(View view) {
                isVideo =! isVideo;     //this sets false -> true and true -> false
                callJavaScriptFunction("javascript:toggleVideo(\""+isVideo+"\") ");
                if(isVideo){
                    video_btn.setImageResource(R.drawable.btn_video_normal);
                }
                else{
                    video_btn.setImageResource(R.drawable.btn_video_muted);

                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setUpWebView(){
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new InterfaceJava(this),"Android");

        // load video call
        loadVideoCall();
    }

    public  void loadVideoCall(){
        String filePath ="file:android_asset/call.html";
        webView.loadUrl(filePath);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                initializePeer();
            }
        });
    }
    void initializePeer(){
        uniqueId = getUniqueId();

        callJavaScriptFunction("javascript:init(\""+ uniqueId+ "\") ");

        if(createdBy != null && createdBy.equalsIgnoreCase(username)){
            databaseRef.child(username).child("connId").setValue(uniqueId);
            databaseRef.child(username).child("isAvailable").setValue(true);

//            controlsView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);

        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendsUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue()!=null){
                                        //send call request
                                        sendCallRequest();

                                    };
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            },2000);
        }
    }

    public void onPeerConnected(){
        isPeerConnected = true;
    }

    void sendCallRequest(){
        if(!isPeerConnected) {
            Toast.makeText(this, "No internet connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }
            //listen connection id
            listenConnId();
    }

    void listenConnId(){
        
        databaseRef.child(friendsUsername)
                .child("connId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()==null){
                            return;
                        }
                        webView.setVisibility(View.VISIBLE);
                        String connId = snapshot.getValue(String.class);
                        callJavaScriptFunction("javascript:startCall(\""+connId+"\") ");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void callJavaScriptFunction(String function){   // function which helps us to run  any javascript function
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(function, null);
            }
        });
    }

    String getUniqueId(){
        return UUID.randomUUID().toString();    // gives a random unique id everytime which is generated once and never matches
    }
}