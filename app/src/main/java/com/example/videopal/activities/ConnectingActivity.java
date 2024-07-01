package com.example.videopal.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.videopal.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Objects;

public class ConnectingActivity extends AppCompatActivity {
    CircularImageView profilePicture;
    FirebaseDatabase database;
    FirebaseAuth auth;

    boolean isOkay = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connecting);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        profilePicture = findViewById(R.id.profilePicture);


        Intent intent = getIntent();            // this recieves the url of the image
        String imageUri = intent.getStringExtra("imageUri");

        if(imageUri!=null){
            Glide.with(this)        // this places the new image under old image
                    .load(Uri.parse(imageUri))
                    .into(profilePicture);
        }
        else{
            //do nothing
        }


        String username = auth.getUid();
        database.getReference().child("users")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            isOkay = true;

                            //room availbale
                            for(DataSnapshot childSnap : snapshot.getChildren()){
                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);

                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("status")
                                        .setValue(1);

                                Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);

                                intent.putExtra("username", username);

                                String incoming = childSnap.child("incoming").getValue(String.class);
                                intent.putExtra("incoming", incoming);

                                String createdBy = childSnap.child("createdBy").getValue(String.class);
                                intent.putExtra("createdBy", createdBy);

                                boolean isAvailable = childSnap.child("isAvailable").getValue(Boolean.class);
                                intent.putExtra("isAvailable", isAvailable);

                                startActivity(intent);
                            }
                        }
                        else{
                            //room not available
                            HashMap<String, Object> room = new HashMap<>();
                            room.put("incoming", username);
                            room.put("created by", username);
                            room.put("isAvailable", true);
                            room.put("status", 0);

                            database.getReference()
                                    .child("users")
                                    .child(username)
                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference()
                                                    .child("users")
                                                    .child(username).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.child("status").exists()){
                                                                if(snapshot.child("status").getValue(Integer.class)==1){

                                                                    if(isOkay == true){
                                                                        return;
                                                                    }

                                                                    isOkay = true;
                                                                    Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);

                                                                    intent.putExtra("username", username);

                                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                                    intent.putExtra("incoming", incoming);

                                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                                    intent.putExtra("createdBy", createdBy);

                                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);
                                                                    intent.putExtra("isAvailable", isAvailable);

                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}