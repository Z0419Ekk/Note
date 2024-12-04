package com.example.note;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {
    private ImageView welcomeImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        welcomeImg = (ImageView) findViewById(R.id.wrap);
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
        anima.setDuration(2000);// 设置简单动画的显示时间
        welcomeImg.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());


        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip();
            }
        });
    }
    private class AnimationImpl implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            welcomeImg.setBackgroundResource(R.drawable.notebg);//这个是你开发的APP开机的图片，yw是图片名字，对应下面要谈到的图片所在位置，这个是关键要注意，往往很多人出错就在这里
        }
        @Override
        public void onAnimationEnd(Animation animation) {
            skip(); // 动画结束后跳转到别的页面
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
    private void skip() {
            startActivity(new Intent(this, LoginActivity.class));//动画开屏后返回APP主界面
            finish(); //结束动画Activity进程


    }
}