package com.cascadia.hidenseek;

import com.cascadia.hidenseek.Match.MatchType;
import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.GetPlayerListRequest;
import com.cascadia.hidenseek.network.PutStartRequest;

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

import android.widget.ListView;


public class CurrentPlayers extends Activity {

	ListView list;
	boolean isActive;
	
	//Used for periodic callback.
    private Handler h2 = new Handler();
    //Millisecond delay between callbacks
    private final int callbackDelay = 500;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_players);
		
		if(LoginManager.GetMatch() == null) {
			Dialog d = new Dialog(this);
			d.setTitle("Error: null match.");
			d.show();
			finish();
		}
		
		
		
		list=(ListView)findViewById(R.id.configPlayerList1);
		isActive = true;
		
        
        
       
        
        //Remove count time and search time things if this is a sandbox
        if(LoginManager.GetMatch().GetType() == MatchType.Sandbox) {
        	findViewById(R.id.configTimeContainer).setVisibility(View.GONE);
        }
        
		//Change the buttons for joiners.
		
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
				    			Intent intent = new Intent(CurrentPlayers.this, Active.class);
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
			CustomPlayersList adapter = new CustomPlayersList(CurrentPlayers.this, titles);
			list.setAdapter(adapter);
			return;
		}
		GetPlayerListRequest request = new GetPlayerListRequest() {
			
			@Override
			protected void onException(Exception e) {
				String[] titles = {"Failed to update match list."};
				CustomPlayersList adapter = new CustomPlayersList(CurrentPlayers.this, titles);
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
				CustomPlayersList adapter = new CustomPlayersList(CurrentPlayers.this, titles);
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

    
	/**
	 * Get any stored preferences and put them in the fields when form is loaded
	 */
	
}
