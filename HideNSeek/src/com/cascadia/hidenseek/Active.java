package com.cascadia.hidenseek;

import java.util.GregorianCalendar;

import com.cascadia.hidenseek.Player.Role;
import com.cascadia.hidenseek.Player.Status;
import com.cascadia.hidenseek.SplashActivity.MyCountDownTimer;
import com.cascadia.hidenseek.network.DeletePlayingRequest;
import com.cascadia.hidenseek.network.GetPlayerListRequest;
import com.cascadia.hidenseek.network.PutGpsRequest;
import com.cascadia.hidenseek.network.PutRoleRequest;
import com.cascadia.hidenseek.network.PutStatusRequest;
import com.cascadia.hidenseek.network.PutStopRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Build;

public class Active extends FragmentActivity {
	GoogleMap googleMap;
	Match match;
	Player player;
	boolean isActive;
	Status pend;
	Role playerRole;
	Player temp;
	String Timer;
	final Context context = this;
	boolean tagged = true;
	private ShowHider sh;
	Long showTime = (long) 30000;
	private int counter;
	private int numPlayers;

	// Used for periodic callback.
	private Handler h2 = new Handler();
	// Millisecond delay between callbacks
	private final int callbackDelay = 500;
	private final int callbackDelay1 = 15000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active);
		
		

		match = LoginManager.GetMatch();
		player = LoginManager.playerMe;
		isActive = true;
		
		if (match == null || player == null) {
			Dialog d = new Dialog(this);
			d.setTitle("Error: null match.");
			d.show();
			finish();

		}
		if(LoginManager.isHost){
			if(LoginManager.GetMatch().GetType()==Match.MatchType.HideNSeek)
			{
			Timer = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE)
					.getString("Seektime", null);
			this.scheduleAlarm();
			}
		}
		
		
			
		
		ActionBar ab = getActionBar();
		if (player.GetRole() != Player.Role.Seeker) {
			ab.hide();
		}

		// Show user's position on map
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapview)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.setMyLocationEnabled(true);
		googleMap
				.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location location) {
						LatLng point = new LatLng(location.getLatitude(),
								location.getLongitude());
						player.SetLocation(location);
						googleMap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(point, 17));
					}
				});
		// User clicked Leave Match button
		ImageButton btnLeave = (ImageButton) findViewById(R.id.btnLeaveGame);
		btnLeave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Active.this, Home.class);
				startActivity(intent);
			}
		});

		Runnable callback = new Runnable() {

			// This function gets called twice per second until the app is
			// stopped.
			@Override
			public void run() {

				// Do request and update values in match. No callback needed.
				GetPlayerListRequest gplRequest = new GetPlayerListRequest() {

					@Override
					protected void onException(Exception e) {
					}

					@Override
					protected void onComplete(Match match) {
						numPlayers = match.players.size();
						googleMap.clear();
						counter=0;
						for (final Player p : match.players) {
							pend = p.GetStatus();
							if(match.GetType()==Match.MatchType.HideNSeek)
							{
							if(p.GetStatus()==Player.Status.Found)
							{
								counter++;
								if(counter==numPlayers-1)
								{
									
									CheckForEndGame();
								}
								
							}
							}
							if (p.GetRole() == Player.Role.Seeker) {
								temp = p;
								
							}
							

							playerRole = p.GetRole();
							if (pend == Status.Spotted
									&& player.GetId() == p.GetId()) {
								if (tagged) {
									tagged = false;
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);

									// set title

									alertDialogBuilder.setTitle("Found You");
									alertDialogBuilder
											.setMessage(
													"The seeker just said he found you, is this correct?")
											.setCancelable(false)
											.setPositiveButton(
													"Yes",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															
															p.SetStatus(Player.Status.Found);
															PutStatusRequest pp = new PutStatusRequest() {

																@Override
																protected void onException(
																		Exception e) {
																	e.printStackTrace();
																}

															};
															
															pp.DoRequest(p);
															isActive=false;
															ShowSeeker();
															tagged = true;
															Intent intent = new Intent(
																	context,
																	Home.class);
															startActivity(intent);

														}
													})
											.setNegativeButton(
													"No",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															// if this button is
															// clicked, just
															// close
															// the dialog box
															// and do nothing
															p.SetStatus(Status.Hiding);
															PutStatusRequest pp = new PutStatusRequest() {

																@Override
																protected void onException(
																		Exception e) {
																	e.printStackTrace();
																}

															};
															pp.DoRequest(p);
															tagged = true;

														}
													});

									// create alert dialog
									AlertDialog alertDialog = alertDialogBuilder
											.create();

									// show it
									alertDialog.show();
								}
							}

							// Dont't add a marker for players with null
							// locations or one for myself.

							if (match.GetType() != Match.MatchType.Sandbox) {
								if (p.GetLocation() != null
										&& p.GetId() != player.GetId()
										&& p.GetRole() != Player.Role.Seeker) {
									googleMap.addMarker(new MarkerOptions()
											.position(
													new LatLng(p.GetLocation()
															.getLatitude(), p
															.GetLocation()
															.getLongitude()))
											.title(p.GetName()));
								}
							} else {
								if (p.GetLocation() != null
										&& p.GetId() != player.GetId()
										&& p.GetStatus() != Player.Status.Found) {
									googleMap.addMarker(new MarkerOptions()
											.position(
													new LatLng(p.GetLocation()
															.getLatitude(), p
															.GetLocation()
															.getLongitude()))
											.title(p.GetName()));
								}
							}
						}
					}
				};
				gplRequest.DoRequest(match);

				
				// Do request. No callback needed. Player location set by
				// Google Maps' onMyLocationChange
				PutGpsRequest pgRequest = new PutGpsRequest() {
					@Override
					protected void onException(Exception e) {
					}
				};
				pgRequest.DoRequest(player);

				if (isActive) {
					h2.postDelayed(this, callbackDelay);
				}
			}
		};
		callback.run(); // Begin periodic updating!
	
		
	}

	public void CheckForEndGame() {
		isActive=false;
			PutStopRequest psr = new PutStopRequest();
			psr.DoRequest(match);
			match.SetStatus(Match.Status.Complete);
			Intent end=new Intent(context,Home.class);
			startActivity(end);
	}

	public void scheduleAlarm() {
		// time at which alarm will be scheduled here alarm is scheduled at 1
		// day from current time,
		// we fetch the current time in milliseconds and added 1 day time
		// i.e. 24*60*60*1000= 86,400,000 milliseconds in a day
		Long time = new GregorianCalendar().getTimeInMillis()
				+ Long.parseLong(Timer)*1000 ;

		// create an Intent and set the class which will execute when Alarm
		// triggers, here we have
		// given AlarmReciever in the Intent, the onRecieve() method of this
		// class will execute when
		// alarm triggers and
		// we will write the code to send SMS inside onRecieve() method pf
		// Alarmreciever class
		
		Intent intentAlarm = new Intent(this, AlarmReciever.class);
		
		// create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		// set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent
				.getBroadcast(this, 1, intentAlarm,
						PendingIntent.FLAG_UPDATE_CURRENT));
		Toast.makeText(this, "Alarm Scheduled", Toast.LENGTH_LONG).show();
		

	}

	public void ShowSeeker() {
		sh = new ShowHider(showTime, 1000, temp);
		sh.startCountDown1();

	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status == ConnectionResult.SUCCESS) {
			// no problems just work
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(
					getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Toast.makeText(this, "Google Maps V2 is not available!",
					Toast.LENGTH_LONG).show();
			finish();
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DeletePlayingRequest dpRequest = new DeletePlayingRequest() {

			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
		};
		dpRequest.DoRequest(player);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ErrorDialogFragment extends DialogFragment {
		static final String ARG_STATUS = "status";

		static ErrorDialogFragment newInstance(int status) {
			Bundle args = new Bundle();
			args.putInt(ARG_STATUS, status);
			ErrorDialogFragment result = new ErrorDialogFragment();
			result.setArguments(args);
			return (result);
		}

		public void show(FragmentManager supportFragmentManager,
				String TAG_ERROR_DIALOG_FRAGMENT) {
			// TODO Auto-generated method stub

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			return GooglePlayServicesUtil.getErrorDialog(
					args.getInt(ARG_STATUS), getActivity(), 0);
		}

		@Override
		public void onDismiss(DialogInterface dlg) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		isActive = true;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.players, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.players_list) {
			Intent intent = new Intent(Active.this, CurrentPlayers.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}