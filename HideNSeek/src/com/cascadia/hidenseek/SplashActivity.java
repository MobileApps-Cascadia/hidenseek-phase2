package com.cascadia.hidenseek;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.splash_screen, null);
        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
        aa.setDuration(10000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectToLogin();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

		
	}

    private void redirectToLogin() {
        Intent intentToActive = new Intent(this, Active.class);
        startActivity(intentToActive);
        finish();
    }
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	

}
