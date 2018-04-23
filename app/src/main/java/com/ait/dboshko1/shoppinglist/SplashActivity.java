package com.ait.dboshko1.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        final ImageView icon = findViewById(R.id.imgSplash);
        final Animation tvAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_fade_in_anim);
        final Animation imgAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_fade_in_anim);
        initAnims(tvAnim, imgAnim, icon);

        tvTitle.startAnimation(tvAnim);
    }

    @NonNull
    private void initAnims(final Animation tvAnim, final Animation imgAnim, final ImageView icon) {
        tvAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon.setVisibility(View.VISIBLE);
                icon.startAnimation(imgAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
