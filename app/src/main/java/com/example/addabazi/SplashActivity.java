package com.example.addabazi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.addabazi.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    private ImageView iv_splash;
    private TextView tv_splash;
    private Animation splashAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        iv_splash = findViewById(R.id.iv_splash_ID);
        tv_splash = findViewById(R.id.tv_splash_ID);

        splashAnimation = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        splashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        iv_splash.startAnimation(splashAnimation);
        tv_splash.startAnimation(splashAnimation);

    }
}