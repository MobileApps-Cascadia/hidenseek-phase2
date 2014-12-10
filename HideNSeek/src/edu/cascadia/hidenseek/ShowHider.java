package edu.cascadia.hidenseek;

import android.os.CountDownTimer;
import edu.cascadia.hidenseek.Player.Role;
import edu.cascadia.hidenseek.network.PutRoleRequest;

public class ShowHider extends CountDownTimer {
	private boolean isrunning = false;
	Player isSeeker;

	public ShowHider(long startTime, long interval) {
			super(startTime, interval);
		
	}
	public ShowHider(long StartTime,long interval, Player p){
		super(StartTime, interval);
		isSeeker=p;
	}
	

	

	public void startCountDown1() {
		isrunning = true;
		isSeeker.SetRole(Role.Supervisor);
	    PutRoleRequest pp = new PutRoleRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
								
		};
		pp.DoRequest(isSeeker);
		start();
	}
	
	@Override
	public void onFinish() {
		isrunning = true;
		isSeeker.SetRole(Role.Seeker);
	    PutRoleRequest pp = new PutRoleRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
								
		};
		pp.DoRequest(isSeeker);
		start();
		isrunning = false;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		
	}

	public boolean isRunning() {
		return isrunning;
	}
}
