package com.cascadia.hidenseek;

import com.cascadia.hidenseek.Match.MatchType;
import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.DeletePlayingRequest;
import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.GetPlayerListRequest;
import com.cascadia.hidenseek.network.PutGpsRequest;
import com.cascadia.hidenseek.network.PutStartRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class HostConfig extends Activity {

	String username, counttime, seektime;
	ListView list;
	boolean isActive;
	
	//Used for periodic callback.
    private Handler h2 = new Handler();
    //Millisecond delay between callbacks
    private final int callbackDelay = 500;
		
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
		
		initSettings();
		
		list=(ListView)findViewById(R.id.configPlayerList);
		isActive = true;
		
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
            			//Error!
            			return;
            		}
	            	try {
		            	m.SetCountTime(Integer.parseInt(countTime.getText().toString()));
		            	m.SetSeekTime(Integer.parseInt(seekTime.getText().toString()));
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
		    			Intent intent = new Intent(HostConfig.this, Active.class);
		    			startActivity(intent);
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
        if(LoginManager.GetMatch().GetType() == MatchType.Sandbox) {
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
				    			Intent intent = new Intent(HostConfig.this, Active.class);
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
