package edu.cascadia.hidenseek;

import java.util.GregorianCalendar;

import com.cascadia.hidenseek.R;

import edu.cascadia.hidenseek.Match.MatchType;
import edu.cascadia.hidenseek.Match.Status;
import edu.cascadia.hidenseek.network.GetMatchRequest;
import edu.cascadia.hidenseek.network.GetPlayerListRequest;
import edu.cascadia.hidenseek.network.PutStartRequest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class HostConfig extends Activity {

	String username, counttime, seektime;
	ListView list;
	boolean isActive;
	SharedPreferences sh_Pref;
	Editor toEdit;
	String Timer;
	
	//Used for periodic callback.
    private Handler h2 = new Handler();
    //Millisecond delay between callbacks
    private final int callbackDelay = 500;
		
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_config);
		
		if(LoginManager.GetMatch() == null) {
			Dialog d = new Dialog(this);
			d.setTitle("Error: null match.");
			d.show();
			finish();
		}
		int temp=LoginManager.playerMe.GetId();
		Toast.makeText(this, Integer.toString(temp), Toast.LENGTH_LONG).show();
		LoginManager.playerMe.SetID(temp);
		Toast.makeText(this, "Id Changed to" + Integer.toString(LoginManager.playerMe.GetId()), Toast.LENGTH_LONG).show();
		initSettings();
		
		list=(ListView)findViewById(R.id.configPlayerList);
		isActive = true;
		
		ImageView countHelp = (ImageView) findViewById(R.id.configCountTimeHelp);
		countHelp.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				HelpDialog helpDialog = new HelpDialog("Enter the count time to begin the match.", "Count Time" );
				helpDialog.show(getFragmentManager(), "Help");
			}
		});
		ImageView searchHelp = (ImageView) findViewById(R.id.configSeekTimeHelp);
		searchHelp.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				HelpDialog helpDialog = new HelpDialog("Enter the time that would last for the game.", "Search Time");
				helpDialog.show(getFragmentManager(), "Help");
			}
		});
        ImageButton btnBeginMatch = (ImageButton) findViewById(R.id.btnConfigBegin);
        ImageButton btnCancelMatch = (ImageButton) findViewById(R.id.btnConfigCancel);
        
        btnBeginMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Set the match count time and seek time as specified, etc.
            	EditText countTime = (EditText) findViewById(R.id.configCountTimeInput);
            	EditText seekTime = (EditText) findViewById(R.id.configSeekTimeInput);
            	Match m = LoginManager.GetMatch();
            	
            	//Validate countTime and searchTime input unless this is a sandbox
            	if(LoginManager.GetMatch().GetType() != MatchType.Sandbox) {
                	String sCountTime = countTime.getText().toString();
            		String sSeekTime = seekTime.getText().toString();
            		
            		if(sSeekTime.length() == 0 || sCountTime.length() == 0) {
            			HelpDialog helpDialog = new HelpDialog("Please enter the count time and search time.", "Enter times" );
        				helpDialog.show(getFragmentManager(), "Help");
            			return;
            		}
	            	try {
		            	m.SetCountTime(Integer.parseInt(countTime.getText().toString()));
		            	m.SetSeekTime(Integer.parseInt(seekTime.getText().toString()));
		            	// Save if we go through
		            	sh_Pref = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE); 
	            		toEdit = sh_Pref.edit(); 
	            		toEdit.putString("Counttime", sCountTime);
	            		toEdit.putString("Seektime", sSeekTime);
	            		toEdit.commit(); 
	            	} catch(NumberFormatException e) {

	        			Dialog d = new Dialog(HostConfig.this);
	        			d.setTitle("Error: invalid value for count time and/or seek time");
	        			d.show();
	        			return;
	            	}
            	}
            	PutStartRequest request = new PutStartRequest() {
					@Override
					protected void onException(Exception e) {
						
					}
					@Override
					protected void onComplete(Match m) {
						Intent intent;
						if(LoginManager.GetMatch().GetType()==Match.MatchType.HideNSeek){
						Timer = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE)
									.getString("Seektime", null);
						scheduleAlarm();
						intent = new Intent(HostConfig.this, SplashActivity.class);
						
		    			startActivity(intent);
		    			isActive=false;
						}
						else{
							intent=new Intent(HostConfig.this, Active.class);
							startActivity(intent);
							isActive=false;
						}
							
					}
					
				};
				request.DoRequest(m);
            }
        });	
        
        btnCancelMatch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				confirmCancel();
			}
		});
        
        //Remove count time and search time things if this is a sandbox
        if(LoginManager.GetMatch().GetType() == MatchType.Sandbox||!LoginManager.isHost) {
        	findViewById(R.id.configTimeContainer).setVisibility(View.GONE);
        }
        
		//Change the buttons for joiners.
		if(!LoginManager.isHost) {
			btnBeginMatch.setEnabled(false);
			btnBeginMatch.setAlpha(100);
			btnCancelMatch.setImageDrawable(
					getResources().getDrawable(R.drawable.btn_leave_set));
		}
	    Runnable callback = new Runnable() {
	    	
	    	//This function gets called twice per second until the app is stopped.
	        @Override
	        public void run() {

				setPlayerList();
				
				//If joiner, then check if host has started yet.
				if(!LoginManager.isHost) {
					GetMatchRequest gmRequest = new GetMatchRequest() {
						@Override
						protected void onException(Exception e) {}
						@Override
						protected void onComplete(Match match) {
							if(match.GetStatus() == Status.Active) {
								isActive = false;
								String seek=String.valueOf(match.GetSeekTime());
								sh_Pref = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE); 
			            		toEdit = sh_Pref.edit(); 
			            		toEdit.putString("Seektime", seek);
			            		toEdit.commit(); 
			            		Timer=seek;
			            		scheduleAlarm();
				    			Intent intent = new Intent(HostConfig.this,Active.class);
				    			startActivity(intent);
							}
						}
					};
					gmRequest.DoRequest(LoginManager.GetMatch().GetId());
				}

				if(isActive) {
					h2.postDelayed(this, callbackDelay);
				}
	        }
	    };
	    callback.run(); //Begin periodic updating!

	}
	
	
	private void setPlayerList() {
		if(LoginManager.GetMatch() == null) {
			String[] titles = {"Failed to update match list.", "(null match)"};
			CustomList adapter = new CustomList(HostConfig.this, titles);
			list.setAdapter(adapter);
			return;
		}
		GetPlayerListRequest request = new GetPlayerListRequest() {
			
			@Override
			protected void onException(Exception e) {
				String[] titles = {"Failed to update match list."};
				CustomList adapter = new CustomList(HostConfig.this, titles);
				list.setAdapter(adapter);
			}
			
			@Override
			protected void onComplete(Match match) {
				String[] titles = new String[match.players.size()];
				int i = 0;
				for(Player p : match.players) {
					titles[i] = p.GetName();
					i++;
				}
				CustomList adapter = new CustomList(HostConfig.this, titles);
				list.setAdapter(adapter);
				
			}
		};
		request.DoRequest(LoginManager.GetMatch());
	}
	
	public void scheduleAlarm() {
		// time at which alarm will be scheduled here alarm is scheduled at 1
		// day from current time,
		// we fetch the current time in milliseconds and added 1 day time
		// i.e. 24*60*60*1000= 86,400,000 milliseconds in a day
		Long time = new GregorianCalendar().getTimeInMillis()
				+ Long.parseLong(Timer)*60000 ;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Shows a dialog to confirm whether the user wants to cancel/leave match
	 */
    private void confirmCancel() {
        final Dialog dialog;
        String message = LoginManager.isHost ? "Cancel this match?" : "Leave this match?";

        dialog = new AlertDialog.Builder(this).setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            })
            .setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).create();
        dialog.show();
    }
    
	/**
	 * Get any stored preferences and put them in the fields when form is loaded
	 */
	private void initSettings(){		
		counttime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Counttime", "");
		EditText cTime = (EditText)findViewById(R.id.configCountTimeInput);
		cTime.setText(counttime);
		
		seektime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Seektime", "");
		EditText sTime = (EditText)findViewById(R.id.configSeekTimeInput);
		sTime.setText(seektime);
	}
}
