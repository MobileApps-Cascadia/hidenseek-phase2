package com.cascadia.hidenseek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends Activity {
	private TextView count_down_text = null;
	private MyCountDownTimer countdowntimer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.splash_screen, null);
		setContentView(view);
		count_down_text = (TextView) findViewById(R.id.mTextField);
		countdowntimer = new MyCountDownTimer(60000, 1000);
		countdowntimer.start();
	}

	private void redirectToLogin() {
		Intent intentToGame = new Intent(this, Active.class);
		startActivity(intentToGame);
		finish();
	}

	public void startCountDown(View view) {
		if (!countdowntimer.isRunning())
			countdowntimer.startCountDown();
		// can also directly countdowntimer.start()
	}

	public class MyCountDownTimer extends CountDownTimer {
		private long starttime;
		private boolean isrunning = false;

		public MyCountDownTimer(long startTime, long interval) {

			super(startTime, interval);
			this.starttime = startTime;
		}

		public void startCountDown() {
			isrunning = true;
			count_down_text.setText("" + starttime / 1000);
			Log.d("TAG", " starttime/1000:" + starttime / 1000);
			start();
		}

		@Override
		public void onFinish() {
			redirectToLogin();
			isrunning = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			count_down_text.setText("" + millisUntilFinished / 1000);
		}

		public boolean isRunning() {
			return isrunning;
		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
}

