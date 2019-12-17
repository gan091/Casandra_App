package com.android.gan091.gramaudenar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        viewPager = findViewById(R.id.vpTutorial);
        int[] mImageIds = new int[] {
                R.drawable.t01,
                R.drawable.t02,
                R.drawable.t03,
                R.drawable.t04,
                R.drawable.t05,
                R.drawable.t06,
                R.drawable.t07,
                R.drawable.t08,
                R.drawable.t09,
                R.drawable.t10
        };

        ImageAdapter imageAdapter = new ImageAdapter(this, mImageIds);
        viewPager.setAdapter(imageAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.left_nav:
                viewPager.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.right_nav:
                viewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
            case R.id.close_nav:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }
}
