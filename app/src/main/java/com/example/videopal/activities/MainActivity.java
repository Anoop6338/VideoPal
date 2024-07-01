package com.example.videopal.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.videopal.R;
import com.example.videopal.databinding.ActivityMainBinding;
import com.example.videopal.models.User;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity {
    CircularImageView profilePicture;
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins = 0;
    String profileUrl;


    String[] permissions = new String[]{        // this string defines all the permissions we need
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        profilePicture = findViewById(R.id.profilePicture);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        FirebaseUser currentUser = auth.getCurrentUser();

        database.getReference().child("profiles")
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            coins = user.getCoins();
                            binding.coins.setText("You have: " + coins);

                            Glide.with(MainActivity.this)
                                    .load(currentUser.getPhotoUrl())
                                    .into(binding.profilePicture);

//                            Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);        // this code passes the image to the connecting activity
//                            intent.putExtra("imageUri", user.getProfile());     //this code retirves the image from the database to the intent
//                            startActivity(intent);
                            profileUrl = user.getProfile();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "coins not updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionGranted()) { // if permissions granted then proceed forward
                    if (coins > 50) {
//                    Toast.makeText(MainActivity.this, "Call finding...", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);        // this code passes the image to the connecting activity
                        intent.putExtra("imageUri", profileUrl);     //this code retirves the image from the database to the intent
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
                    }
                }
                else {  // if not given permission ask again
                    askPermission();
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void askPermission(){   // code to ask permissions
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }
    private boolean isPermissionGranted(){      // code which checks whether all permissions are given or not
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}