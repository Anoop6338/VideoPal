package com.example.videopal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.videopal.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartPage extends AppCompatActivity {
    Button btn1;
    FirebaseAuth auth;

    @Override
   protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_start_page);

                FirebaseAuth auth = FirebaseAuth.getInstance();

                if(auth.getCurrentUser()!=null){
                    goToNextActivity();
                }

                findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNextActivity();
                    }
                });
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
        });
    }
    void goToNextActivity(){
        startActivity(new Intent(StartPage.this, LogIn.class));
        finish();
    }
}